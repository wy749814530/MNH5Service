package mn.h5kit.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.zxingx.library.activity.BaseScanActivity;

import mn.h5kit.services.R;
import mn.h5kit.views.RuleAlertDialog;

public class ScanQRcodeActivity extends BaseScanActivity {
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleLay(R.layout.qr_title_lay);
    }

    @Override
    protected void onQrAnalyzeFailed() {
        Log.i(TAG, "==  识别二维码失败  ==");
    }

    @Override
    protected void onQrAnalyzeSuccess(String result, Bitmap barcode) {
        analyzeQrcodeData(result);
    }

    @Override
    protected void onDeniedPermission(String permission) {
        new RuleAlertDialog(this).builder().setCancelable(false).
                setTitle(getString(R.string.add_wifi_tip)).
                setMsg(getString(R.string.add_camera_per)).
                setPositiveButton(getString(R.string.go_to_settings), v1 -> {
                    toPermissionSetting();
                }).setNegativeButton(getString(R.string.label_cancel), v2 -> {
        }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 跳转到权限设置界面
     */
    public void toPermissionSetting() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(intent);
    }

    private void analyzeQrcodeData(String result) {
        try {
            if (result.contains("sn")) {
                //解析二维码返回结果
                String snResult = null;
                String vnString = result.split(";")[0];
                String snString = result.split(";")[1];
                if (vnString.startsWith("sn:")) {
                    snResult = vnString.split(":")[1];
                }
                if (snString.startsWith("sn:")) {
                    snResult = snString.split(":")[1];
                }
                if (!TextUtils.isEmpty(snResult)) {
                    Intent intent = new Intent();
                    intent.putExtra("snResult", snResult);
                    setResult(100, intent);
                    finish();
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra("snResult", result);
                setResult(100, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
