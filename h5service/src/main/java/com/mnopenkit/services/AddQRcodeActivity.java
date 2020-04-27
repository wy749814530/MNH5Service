package com.mnopenkit.services;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.mining.zxing.activity.BaseScanActivity;
import com.mining.zxing.activity.CodeUtils;
import com.mining.zxing.helper.ImageAnalyzeLinstener;
import com.mining.zxing.utils.ImageUtil;
import com.mining.zxing.view.ViewfinderView;
import com.mnopenkit.views.RuleAlertDialog;

/**
 * Created by Administrator on 2019/10/24 0024.
 */

public class AddQRcodeActivity extends BaseScanActivity implements ImageAnalyzeLinstener {
    private String TAG = AddQRcodeActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 112;
    SurfaceView previewView;
    ViewfinderView viewfinderView;
    ImageView ivFinder;
    Button scanBack;
    TextView ivFlight;
    TextView qrcodePhoto;


    private static AddQRcodeActivity mActivity;
    private boolean isClickSetting;
    private boolean initCarameSuc = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_camera);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
            }
        }
        previewView = findViewById(R.id.preview_view);
        viewfinderView = findViewById(R.id.viewfinder_view);
        ivFinder = findViewById(R.id.iv_finder);
        scanBack = findViewById(R.id.scan_back);
        ivFlight = findViewById(R.id.iv_flight);
        qrcodePhoto = findViewById(R.id.qrcode_photo);
        Log.i("ScanActivity", "--- onCreate ---");

        initCameraPerview(previewView, viewfinderView);
        mActivity = this;

//        if (gotype == 1 || isScanBind) {
//            addManual.setVisibility(View.GONE);
//            tvTitle.setText(getString(R.string.scan_code_recognition));
//            tvScan.setText(getString(R.string.put_code_in_the_box));
//            tvExample.setText(getString(R.string.code_on_the_device_body));
//            tvExample.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.tool_scan_img_qr, 0, 0);
//        }
    }

    public static AddQRcodeActivity getInstance() {
        return mActivity;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("ScanActivity", "--- onResume ---");
        if (initCarameSuc) {
            ivFinder.setVisibility(View.GONE);
        }

        if (isClickSetting) {
            isClickSetting = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ivFinder.setVisibility(View.VISIBLE);
        Log.i("ScanActivity", "--- onPause ---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]);
                if (showRequestPermission) {
                    //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
                } else {
                    //被禁止且点了不再询问按钮
                    new RuleAlertDialog(this).builder().setCancelable(false).
                            setTitle(getString(R.string.add_wifi_tip)).
                            setMsg(getString(R.string.add_camera_per)).
                            setPositiveButton(getString(R.string.go_to_settings), v1 -> {
                                toPermissionSetting();
                                isClickSetting = true;
                            }).setNegativeButton(getString(R.string.label_cancel), v2 -> {
                    }).show();
                }

            }
        }
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

    @Override
    public void onInitCameraSuccess() {
        Log.i("ScanActivity", "--- onInitCameraSuccess ---");
        initCarameSuc = true;
        ivFinder.setVisibility(View.GONE);
    }

    @Override
    public void onInitCameraFailed(Exception e) {
        initCarameSuc = false;
        Log.i("ScanActivity", "--- onInitCameraFailed ---" + e.getMessage());
        ivFinder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQrAnalyzeFailed() {
        restartScanCode();
        Log.i("ScanActivity", "--- onQrAnalyzeFailed ---");
        Toast.makeText(this, getString(R.string.unrecognized_QR), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onQrAnalyzeSuccess(Result result, Bitmap barcode) {
        Log.i("ScanActivity", "--- onQrAnalyzeSuccess ---" + result.toString() + " , " + barcode);
        analyzeQrcodeData(result.toString());
    }

    @Override
    public void onImageAnalyzeSuccess(Result result, Bitmap barcode) {
        Log.i("ScanActivity", "--- onImageAnalyzeSuccess ---" + result.toString() + " , " + barcode);
        analyzeQrcodeData(result.toString());
        playBeepSoundAndVibrate();
    }

    @Override
    public void onImageAnalyzeFailed() {
        Log.i("ScanActivity", "--- onQrAnalyzeFailed ---");
        playBeepSoundAndVibrate();
        Toast.makeText(this, getString(R.string.unrecognized_QR), Toast.LENGTH_LONG).show();
    }

    private Handler handler = new Handler();

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
                } else {
                    if (handler != null) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                restartScanCode();
                            }
                        }, 2500);
                    }
                }
            } else {
                Intent intent = new Intent();
                intent.putExtra("snResult", result);
                setResult(100, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restartScanCode();
                    }
                }, 2500);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 选择系统图片并解析
         */
        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isOpen = false;

    public void onViewClicked(View view) {
        int i = view.getId();
        if (i == R.id.scan_back) {
            finish();

        } else if (i == R.id.iv_flight) {
            if (!isOpen) {
                CodeUtils.isLightEnable(true);
                isOpen = true;
                ivFlight.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.add_scan_btn_colse, 0, 0);
            } else {
                CodeUtils.isLightEnable(false);
                isOpen = false;
                ivFlight.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.mipmap.add_scan_btn_opne, 0, 0);
            }

        } else if (i == R.id.qrcode_photo) {
            Intent innerIntent = new Intent();
            if (Build.VERSION.SDK_INT < 19) {
                innerIntent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                innerIntent.setAction(Intent.ACTION_PICK);
            }
            innerIntent.setType("image/*");
            //  innerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
            startActivityForResult(wrapperIntent, REQUEST_IMAGE);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
