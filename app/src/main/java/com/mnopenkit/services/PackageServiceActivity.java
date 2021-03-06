package com.mnopenkit.services;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import mn.h5kit.activity.ShopWebActivity;
import mn.h5kit.key.H5Parameter;


public class PackageServiceActivity extends AppCompatActivity {

    String STR_APP_KEY = "8Wa227sQ00S33p4y";
    String STR_APP_SECRET = "RlA8aCPlsuATT227kKTg003ncP35HYRI";
    String STR_APP_ACCESS_TOKEN = "u_b2c777c965be46918e2c23dc22a94959.cn";
    String STR_USER_ID = "13949127706";
    String STR_H5_HOST = "https://mallcn.bullyun.com";
    String DeviceId_4G = "424684214722498560";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_service);
    }

    public void gotoServices(View view) {
        // 增值服务
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, DeviceId_4G);
        intent.putExtra(H5Parameter.INT_SERVICE_TYPE, 1);
        startActivity(intent);
    }

    public void gotoFaceLibrary(View view) {
        // 人脸库扩容
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.INT_SERVICE_TYPE, 3);
        startActivity(intent);
    }

    public void gotoBuy4GTraffic(View view) {
        // 4G 流量购买
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.INT_SERVICE_TYPE, 2);
        startActivity(intent);
    }

    public void gotoReceiveStoragePackage(View view) {
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.B_IS_RECEIVE_CLOUD, true);
        intent.putExtra(H5Parameter.B_IS_SUPPORT_24_CLOUD, false);
        startActivity(intent);
    }

    public void gotoMobileServiceCenter(View view) {
        // 移动服务中心
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.B_IS_MOBILE_CENTER, true);
        startActivity(intent);
    }


    public void goto4GNetworkMonitoring(View view) {
        // 4G网络监测
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.B_IS_NETWORK_MONOTORING, true);
        startActivity(intent);
    }

    public void goto4GTrafficQuery(View view) {
        // 4G 设备流量查询
        Intent intent = new Intent(this, ShopWebActivity.class);
        intent.putExtra(H5Parameter.STR_H5_HOST, STR_H5_HOST);
        intent.putExtra(H5Parameter.STR_APP_KEY, STR_APP_KEY);
        intent.putExtra(H5Parameter.STR_APP_SECRET, STR_APP_SECRET);
        intent.putExtra(H5Parameter.STR_APP_ACCESS_TOKEN, STR_APP_ACCESS_TOKEN);
        intent.putExtra(H5Parameter.STR_USER_ID, STR_USER_ID);
        intent.putExtra(H5Parameter.STR_DEVICE_ID, DeviceId_4G);
        intent.putExtra(H5Parameter.B_IS_4G_TRAFFIC_QUERY, true);
        intent.putExtra(H5Parameter.STR_DEVICE_NAME, "4G太阳能相机摄像机");
        startActivity(intent);
    }

}
