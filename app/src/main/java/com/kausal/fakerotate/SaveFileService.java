package com.kausal.fakerotate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

public class SaveFileService {
    //保存数据
    public static boolean saveFile(Context context, String FileName, List<ImageView> iconList){
        //创建文件对象
       // File file = new File("/data/com.kausal.fakerotate", FileName);

        //创建文件对象 通过file目录
        File file = new File(context.getFilesDir(),FileName);

        //创建文件对象 通过cache目录
        //File file = new File(context.getCacheDir(), "info.txt");

        try {
            Log.d("傻逼", "嘤嘤嘤");
            //文件输出流
            FileOutputStream fos = new FileOutputStream(file);
            List<ImageDotLayout.IconBean> rectBeans = new ArrayList<>();
            if (iconList != null && iconList.size() > 0) {
                for (ImageView icon : iconList) {
                    ImageDotLayout.IconBean rectBean = (ImageDotLayout.IconBean) icon.getTag();
                    rectBeans.add(rectBean);
                }
            }
            //写数据
            for(int i=0;i<rectBeans.size();i++){
                //top右上 down左下 向右增加y 向上增加x  x:上下 y：左右
                float topx= (float) 27.8950248644;
                float topy=(float)112.8657982302;
                float downx=(float)27.8781445823;
                float downy=(float)112.8521836590;
                float x=topx-(topx-downx)*(rectBeans.get(i).sy);
                float y=(topy-downy)*(rectBeans.get(i).sx)+downy;
                fos.write((x+ "&" + y+"\n").getBytes());
                Log.d("傻逼", (rectBeans.get(i).sx+ "&" + (rectBeans.get(i).sy)));
            }


            //关闭文件流
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //数据回显
    public static ArrayList<String> getrotate(Context context,String Name){
        //获取文件对象
        //File file = new File("/data/data/cn.yzx.login", "info.txt");

        //获取文件对象
        File file = new File(context.getFilesDir(), Name);
        ArrayList<String>  ss = new ArrayList<>();
        try {
            //输入流
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            //读取文件中的内容
            String line;
            while ((line = br.readLine()) != null) {
                //拆分成String[]
                ss.add(line);
            }
            return ss;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
}
