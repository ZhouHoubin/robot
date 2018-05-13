package z.houbin.robot;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    private final int CODE_REQUEST_PERMISSION = 507;
    private PermissionCallBack permissionCallBack;

    public void requestPermission(String[] requestPermissions, PermissionCallBack permissionCallBack) {
        this.permissionCallBack = permissionCallBack;
        if(requestPermissions.length != 0){
            ActivityCompat.requestPermissions(this, requestPermissions, CODE_REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PERMISSION) {
            if (permissionCallBack != null) {
                permissionCallBack.onRequestPermissionsResult(permissions, grantResults);
            }
        }
        permissionCallBack = null;
    }

    public interface PermissionCallBack {
        void onRequestPermissionsResult(String[] permissions, int[] results);
    }
}
