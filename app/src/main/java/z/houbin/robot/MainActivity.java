package z.houbin.robot;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import z.houbin.robot.util.AutoCheck;

public class MainActivity extends BaseActivity implements EventListener {
    private String permissions[] = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private EventManager asr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission(permissions, new PermissionCallBack() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] results) {
                System.out.println("权限申请:" + Arrays.toString(results));
            }
        });

        asr = EventManagerFactory.create(this, "asr");
        asr.registerListener(this);
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        String logTxt = "name: " + name;
        if (params != null && !params.isEmpty()) {
            logTxt += " ;params :" + params;
        }
        switch (name){
            case SpeechConstant.CALLBACK_EVENT_ASR_EXIT:
            case SpeechConstant.ASR_CANCEL:
                start();
                break;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            if (params.contains("\"nlu_result\"")) {
                if (length > 0 && data.length > 0) {
                    logTxt += ", 语义解析结果：" + new String(data, offset, length);
                }
            }
        } else if (data != null) {
            logTxt += " ;data length=" + data.length;
        }
        log(logTxt);
    }

    public void wakeup(View view) {
    }

    public void start(View view) {
        start();
    }

    private void start() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = SpeechConstant.ASR_START; // 替换成测试的event
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        //params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 800); // 静音800毫秒后断句返回识别结果
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        (new AutoCheck(getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
                        log(message + "\n");
                        ; // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }
        }, false)).checkAsr(params);

        String json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
        log("输入参数：" + json);
    }

    private void log(String log) {
        Log.d("Robot", log);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
    }

    public void stop(View view) {
        log("停止识别：ASR_STOP");
        asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0); //
    }
}
