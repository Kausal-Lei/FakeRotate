package com.kausal.fakerotate;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ChooseRotateActivity extends AppCompatActivity {
    private ListView testLv;//ListView组件
    private ArrayAdapter<String> arrayAdapter;
    private final int SDK_PERMISSION_REQUEST = 127;
    public static String latLngInfo = "104.06121778639009&30.544111926165282";
    private String permissionInfo;
    private boolean isGPSOpen = false;
    private boolean isMockLocOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooserotate);
        final String s[]=ChooseRotateActivity.this.fileList();
        final ArrayList<String>  ss = new ArrayList<>();
        for(int i=0;i<s.length;i++){
            Log.d("傻逼", s[i]);
            ss.add(s[i].substring(0, s[i].length()-4));
        }
        testLv = (ListView) findViewById(R.id.test_lv);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,ss);
        testLv.setAdapter(arrayAdapter);
        testLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String result = parent.getItemAtPosition(position).toString();//获取选择项的值
                //Toast.makeText(ChooseRotateActivity.this,"您点击了"+result,Toast.LENGTH_SHORT).show();
                final String[] items = new String[] { "运行此路线", "删除此路线", "取消" };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseRotateActivity.this);
                // 设置参数
                builder.setTitle(result).setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("傻逼", String.valueOf(which));
                                if(which==0){
                                    //运行
                                    SaveFileService sav=new SaveFileService();

                                    int fg=0;
                                    getPersimmions();
                                    if (!(isGPSOpen = isGpsOpened())) {
                                        DisplayToast("GPS定位未开启，请先打开GPS定位服务");
                                        showGpsDialog();
                                        fg=1;
                                    }
                                    // 是否开启位置模拟
                                    isMockLocOpen = isAllowMockLocation();
                                    //提醒用户开启位置模拟
                                    if (!isMockLocOpen) {
                                        setDialog();
                                        fg=1;
                                    }
                                    if(fg==1){
                                        Log.d("嘤嘤嘤", "傻逼了");
                                        DisplayToast("请在开启上述服务后重试");
                                        return;
                                    }






                                    final ArrayList<String> arr=sav.getrotate(ChooseRotateActivity.this,String.valueOf(result+".txt"));
                                    final Intent mockLocServiceIntent = new Intent(ChooseRotateActivity.this, fakeService.class);
                                    final ArrayList<String> arr2=new ArrayList<String>();
                                    String s=arr.get(0);
                                    String[] res = s.split("&");
                                    double prex=Double.parseDouble(res[0]);
                                    double prey=Double.parseDouble(res[1]);
                                    double ra1 = Math.random() * 2.0 - 1.0;
                                    prex = prex + ra1/ 100000.0;
                                    ra1 = Math.random() * 2.0 - 1.0;
                                    prey = prey + ra1/ 100000.0;
                                    arr2.add(String.valueOf(prex)+"&"+String.valueOf(prey));
                                    for(int i=1;i<arr.size();i++){
                                        s=arr.get(i-1);
                                        res = s.split("&");
                                        prex=Double.parseDouble(res[0]);
                                        prey=Double.parseDouble(res[1]);
                                        ra1 = Math.random() * 2.0 - 1.0;
                                        prex = prex + ra1/ 100000.0;
                                        ra1 = Math.random() * 2.0 - 1.0;
                                        prey = prey + ra1/ 100000.0;
                                        s=arr.get(i);
                                        res = s.split("&");
                                        double x=Double.parseDouble(res[0]);
                                        double y=Double.parseDouble(res[1]);
                                        ra1 = Math.random() * 2.0 - 1.0;
                                        x = x + ra1/ 100000.0;
                                        ra1 = Math.random() * 2.0 - 1.0;
                                        y = y + ra1/ 100000.0;
                                        double dis=MapUtils.GetDistance(prex,prey,x,y);
                                        int cnt=(int)Math.floor(dis/2.5);
                                        double changex=x-prex;
                                        double changey=y-prey;
                                        for(int j=1;j<=cnt;j++){
                                            if(j==cnt){
                                                if(i!=arr.size()-1){
                                                    String nex=arr.get(i+1),bef;
                                                    res = nex.split("&");
                                                    double nexx=Double.parseDouble(res[0]);
                                                    double nexy=Double.parseDouble(res[1]);
                                                    double dis2=MapUtils.GetDistance(x,y,nexx,nexy);
                                                    int cnt2=(int)Math.floor(dis2/2.5);
                                                    double nex2x=(nexx-x)/cnt2*1.5+x;
                                                    double nex2y=(nexy-y)/cnt2*1.5+y;
                                                    bef=arr2.get(arr2.size()-1);
                                                    res = bef.split("&");
                                                    double befx = Double.parseDouble(res[0]);
                                                    double befy = Double.parseDouble(res[1]);
                                                    double avgx=(nex2x+befx)/2;
                                                    double avgy=(nex2y+befy)/2;
                                                    arr2.add(String.valueOf(avgx)+"&"+String.valueOf(avgy));
                                                }else
                                                    arr2.add(String.valueOf(x)+"&"+String.valueOf(y));
                                            }else{
                                                double disx=MapUtils.GetDistance(x,y,prex,y);//纬度差
                                                double disy=MapUtils.GetDistance(prex,prey,prex,y);//精度度差
                                                double nowx=prex+changex/(1.0*cnt)*j;
                                                double nowy=prey+changey/(1.0*cnt)*j;
                                                if(disx>disy){//竖着
                                                    ra1 = Math.random() * 2 - 1.0;
                                                    nowx = nowx + ra1/ 100000.0;
                                                    ra1 = Math.random() * 2.5 - 1.0;
                                                    nowy = nowy + ra1/ 47000.0;
                                                    //Log.d("HAHA", "①");
                                                }else{//横着
                                                    ra1 = Math.random() * 2.5 - 1.0;
                                                    nowx = nowx + ra1/ 47000.0;
                                                    ra1 = Math.random() * 2 - 1.0;
                                                    nowy = nowy + ra1/ 100000.0;
                                                    //Log.d("HAHA", "②");
                                                }

                                                arr2.add(String.valueOf(nowx)+"&"+String.valueOf(nowy));
                                            }
                                        }
                                    }
                                    latLngInfo=arr2.get(0);
                                    mockLocServiceIntent.putExtra("key", latLngInfo);
                                    if (Build.VERSION.SDK_INT >= 26) {
                                        startForegroundService(mockLocServiceIntent);
                                        Log.d("DEBUG", "startForegroundService:MOCK_GPS");
                                    } else {
                                        startService(mockLocServiceIntent);
                                        Log.d("DEBUG", "startService:MOCK_GPS");
                                    }
                                    Log.d("arr2", String.valueOf(arr2.size()));
                                    Toast.makeText(ChooseRotateActivity.this,"请在1分钟内打开运动世界校园并开启跑步！",Toast.LENGTH_LONG).show();
                                    int time=60000;
                                    for(int i=0;i<arr2.size();i++){
                                        final int finalI = i;
                                        new Handler().postDelayed(new Runnable(){
                                            public void run(){
                                                //stopService(mockLocServiceIntent);
                                                latLngInfo=arr2.get(finalI);
                                                mockLocServiceIntent.putExtra("key", latLngInfo);
                                                if (Build.VERSION.SDK_INT >= 26) {
                                                    startForegroundService(mockLocServiceIntent);
                                                    Log.d("DEBUG", "startForegroundService:MOCK_GPS");
                                                } else {
                                                    startService(mockLocServiceIntent);
                                                    Log.d("DEBUG", "startService:MOCK_GPS");
                                                }
                                            }
                                        },time);
                                        time+=Math.random()*(200)+795;
                                        Log.d("嘤嘤嘤", arr2.get(i));
                                    }

                                    //stopService(mockLocServiceIntent);
                                }else if(which==1){
                                    //删除此路线
                                    File file_file = new File(getFilesDir(), String.valueOf(result+".txt"));
                                    if(file_file.delete()){
                                        for(int i=0;i<ss.size();i++){
                                            if(result.compareTo(ss.get(i))==0){
                                                ss.remove(i);
                                            }
                                        }
                                        arrayAdapter = new ArrayAdapter<String>(ChooseRotateActivity.this,android.R.layout.simple_list_item_1,ss);
                                        testLv.setAdapter(arrayAdapter);
                                        Toast.makeText(ChooseRotateActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ChooseRotateActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        });
                builder.create().show();
            }
        });
    }
    //显示开启GPS的提示
    private void showGpsDialog() {
        new AlertDialog.Builder(ChooseRotateActivity.this)
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
        Toast toast = Toast.makeText(ChooseRotateActivity.this, str, Toast.LENGTH_LONG);
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
