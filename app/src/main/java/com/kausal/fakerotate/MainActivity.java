package com.kausal.fakerotate;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String latLngInfo = "104.06121778639009&30.544111926165282";
    private final int SDK_PERMISSION_REQUEST = 127;

    private String permissionInfo;
    private boolean isGPSOpen = false;
    private boolean isMockLocOpen = false;
    public void toImageDot(View v){
        startActivity(new Intent(this, ImageDotActivity.class));
    }
    public void chooserotate(View v){
        startActivity(new Intent(this, ChooseRotateActivity.class));
    }
    public void donate(View v){
        startActivity(new Intent(this, DonateActivity.class));
    }
    //显示开启GPS的提示
    private void showGpsDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Tips")//这里是表头的内容
                .setMessage("是否开启GPS定位服务?")//这里是中间显示的具体信息
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0);
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gps是否开启
//        isGPSOpen=isGpsOpened();
        getPersimmions();
        if (!(isGPSOpen = isGpsOpened())) {
            DisplayToast("GPS定位未开启，请先打开GPS定位服务");
            showGpsDialog();
        }
        // 是否开启位置模拟
        isMockLocOpen = isAllowMockLocation();
        //提醒用户开启位置模拟
        if (!isMockLocOpen) {
            setDialog();
        }
        //MyImageView map = null;
        //map.initListener();
        setContentView(R.layout.activity_main);
    }
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            //悬浮窗
//            if (checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED) {
//                permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
//            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    public void DisplayToast(String str) {
        Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }
    private boolean isGpsOpened() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public boolean isAllowMockLocation() {
        boolean canMockPosition = false;
        if (Build.VERSION.SDK_INT <= 22) {//6.0以下
            canMockPosition = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
        } else {
            try {
                LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);//获得LocationManager引用
                String providerStr = LocationManager.GPS_PROVIDER;
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                // 模拟位置可用
                canMockPosition = true;
                locationManager.setTestProviderEnabled(providerStr, false);
                locationManager.removeTestProvider(providerStr);
            } catch (SecurityException e) {
                canMockPosition = false;
            }
        }
        return canMockPosition;
    }
    private void setDialog() {
        //判断是否开启开发者选项
//        boolean enableAdb = (Settings.Secure.getInt(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) > 0);
//        if (!enableAdb) {
//            DisplayToast("请打先开开发者选项");
//            return;
//        }
        new AlertDialog.Builder(this)
                .setTitle("启用位置模拟")//这里是表头的内容
                .setMessage("请在开发者选项->选择模拟位置信息应用中进行设置")//这里是中间显示的具体信息
                .setPositiveButton("设置",//这个string是设置左边按钮的文字
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                    startActivity(intent);
                                } catch (Exception e) {
                                    DisplayToast("无法跳转到开发者选项,请先确保您的设备已处于开发者模式");
                                    e.printStackTrace();
                                }
                            }
                        })//setPositiveButton里面的onClick执行的是左边按钮
                .setNegativeButton("取消",//这个string是设置右边按钮的文字
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })//setNegativeButton里面的onClick执行的是右边的按钮的操作
                .show();
    }
}
