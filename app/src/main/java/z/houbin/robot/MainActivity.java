package z.houbin.robot;

import android.Manifest;
import android.app.usage.UsageStats;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import z.houbin.robot.device.DeviceManagerHandler;
import z.houbin.robot.handler.AppHandler;
import z.houbin.robot.tuling.Tuling;
import z.houbin.robot.util.AutoCheck;

public class MainActivity extends BaseActivity implements EventListener, SpeechSynthesizerListener {
    private String permissions[] = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private EventManager asr;
    private TextView log;
    private ScrollView scrollView;
    private Tuling tuling;
    private SpeechSynthesizer mSpeechSynthesizer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
//                AutoCheck autoCheck = (AutoCheck) msg.obj;
//                synchronized (autoCheck) {
//                    String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
//                    log(message + "\n");
//                    //可以用下面一行替代，在logcat中查看代码
//                    // Log.w("AutoCheckMessage", message);
//                }
            }
        }
    };
    private boolean isSpeak;

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

        log = findViewById(R.id.log);
        log.setMovementMethod(new ScrollingMovementMethod());
        scrollView = (ScrollView) this.log.getParent();
        tuling = new Tuling();

        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        mSpeechSynthesizer.setAppId("11221254");
        mSpeechSynthesizer.setApiKey("rXVnHC2XmWL9xqyo2cagGs1Z", "4e532bfba5e86ac62ab85d6cbf0bc6f6");

        new Thread() {
            @Override
            public void run() {
                super.run();
                mSpeechSynthesizer.auth(TtsMode.ONLINE);
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "4");
                mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "3");
                mSpeechSynthesizer.setStereoVolume(0.5f, 0.5f);
                mSpeechSynthesizer.initTts(TtsMode.ONLINE);
            }
        }.start();
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        try {
            //log("\r\n" + name);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        switch (name) {
            case SpeechConstant.CALLBACK_EVENT_ASR_EXIT:
                if (!isSpeak) {
                    startInMain();
                }
                break;
            case SpeechConstant.ASR_CANCEL:
                break;
        }
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL) && !TextUtils.isEmpty(params)) {
            try {
                JSONObject jsonObject = new JSONObject(params);
                String type = jsonObject.getString("result_type");
                //log("\r\ntype:" + type);
                switch (type) {
                    case "final_result":
                        final String speak = jsonObject.getString("best_result");
                        log("\r\n发:" + speak);
                        if (filter(speak)) {
                            return;
                        }
                        isSpeak = true;
                        tuling.request(speak, new Tuling.CallBack() {
                            @Override
                            public void onCall(List<String> text) {
                                if (text.isEmpty()) {
                                    isSpeak = false;
                                    startInMain();
                                    return;
                                }
                                List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
                                for (final String str : text) {
                                    log("\r\n收:" + str);
                                    SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
                                    //需要合成的文本text的长度不能超过1024个GBK字节。
                                    speechSynthesizeBag.setText(str);
                                    speechSynthesizeBag.setUtteranceId("0");
                                    bags.add(speechSynthesizeBag);
                                }
                                int result = mSpeechSynthesizer.batchSpeak(bags);
                                log(result + "");
                            }
                        });
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean filter(String speak) {
        //播放音乐什么都不做
        AudioManager m = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (m != null && m.isMusicActive()) {
            return true;
        }
        if (speak.equals("获取正在运行的程序")) {
            AppHandler handler = AppHandler.getInstance(getApplicationContext());
            if (!handler.canGetRunningProcess()) {
                handler.openUsageSeetings();
            } else {
                List<UsageStats> runningProcess = handler.getRunningProcess();
                StringBuilder builder = new StringBuilder();
                for (UsageStats process : runningProcess) {
                    builder.append(process.getPackageName());
                    builder.append("\t");
                    builder.append(process.getTotalTimeInForeground());
                    builder.append("\r\n");
                }
                log(builder.toString());
            }
            return true;
        } else if (speak.contains("解锁")) {
            DeviceManagerHandler handler = new DeviceManagerHandler(getApplicationContext());
            handler.unLock();
            return true;
        } else if (speak.contains("锁屏")) {
            DeviceManagerHandler handler = new DeviceManagerHandler(MainActivity.this);
            handler.lock();
            return true;
        } else if (speak.contains("打开")) {
            int start = speak.indexOf("打开");
            String name = speak.substring(start + 2, speak.length());
            if (TextUtils.isEmpty(name) || name.length() < 2) {
                return false;
            } else {
                Data data = AppHandler.getInstance(getApplicationContext()).openApp(name);
                if (data.getCode() == Data.SUCCESS) {
                    return true;
                } else if (data.getMsg() != null) {
                    speak(data.getMsg());
                    return true;
                }
            }
        }
        return false;
    }

    public void wakeup(View view) {
    }

    public void start(View view) {
        start();
    }

    private void startInMain() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    private void speak(String text) {
        mSpeechSynthesizer.speak(text);
    }

    private void start() {
        System.out.println("MainActivity.start");
        log("\r\nstart !!!");
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        String event = SpeechConstant.ASR_START; //替换成测试的event
        params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
        //params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0); // 长语音
        params.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 400); // 静音400毫秒后断句返回识别结果
        params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
        (new AutoCheck(getApplicationContext(), handler, false)).checkAsr(params);
        String json = new JSONObject(params).toString(); // 这里可以替换成你需要测试的json
        asr.send(event, json, null, 0, 0);
    }

    private void log(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.log.append(log);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
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

    @Override
    public void onSynthesizeStart(String s) {
        isSpeak = true;
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

    }

    @Override
    public void onSynthesizeFinish(String s) {
        isSpeak = false;
    }

    @Override
    public void onSpeechStart(String s) {
        System.out.println("MainActivity.onSpeechStart");
        isSpeak = true;
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {
        System.out.println("MainActivity.onSpeechFinish " + s);
        isSpeak = false;
        startInMain();
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        System.out.println("MainActivity.onError " + s);
        isSpeak = false;
        startInMain();
    }
}
