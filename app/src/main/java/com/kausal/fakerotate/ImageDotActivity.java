package com.kausal.fakerotate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.ArrayList;
import java.util.List;
import com.kausal.fakerotate.ImageDotLayout;
public class ImageDotActivity extends AppCompatActivity {

    private ImageDotLayout imageDotLayout;
    public int fg=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_dot);
        imageDotLayout = (ImageDotLayout) findViewById(R.id.idl_idl_photo);
        imageDotLayout.setOnImageClickListener(new ImageDotLayout.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDotLayout.IconBean bean) {
                //可以一系列处理后再添加标签
                imageDotLayout.addIcon(bean);
                List<ImageDotLayout.IconBean> rectBeans = new ArrayList<>();
                if (imageDotLayout.iconList != null && imageDotLayout.iconList.size() > 0) {
                    for (ImageView ic : imageDotLayout.iconList) {
                        ImageDotLayout.IconBean rectBean = (ImageDotLayout.IconBean) ic.getTag();
                        rectBeans.add(rectBean);
                    }
                }

                double dis=0;
                MapUtils mp=new MapUtils();
                float topx= (float) 27.8950248644;
                float topy=(float)112.8657982302;
                float downx=(float)27.8781445823;
                float downy=(float)112.8521836590;
                //Log.d("Super1", String.valueOf(imageDotLayout.iconList.size()));
                //Log.d("Super", String.valueOf(rectBeans.size()));
                for(int i=1;i<rectBeans.size();i++){
                    //top右上 down左下 向右增加y 向上增加x  x:上下 y：左右
                    double x0=topx-(topx-downx)*(rectBeans.get(i-1).sy);
                    double y0=(topy-downy)*(rectBeans.get(i-1).sx)+downy;
                    double x1=topx-(topx-downx)*(rectBeans.get(i).sy);
                    double y1=(topy-downy)*(rectBeans.get(i).sx)+downy;
                    dis+=mp.GetDistance(x0,y0,x1,y1);
                    Log.d("Super", "xixi");
                }
                TextView tv=(TextView)findViewById(R.id.count);
                String s="总路程长度为："+String.valueOf((int)dis)+"(m)";
                tv.setText(s);
            }
        });
        //设置背景图片

        imageDotLayout.setImage(R.drawable.sc);
        initIcon();
        imageDotLayout.setOnIconClickListener(new ImageDotLayout.OnIconClickListener() {
            @Override
            public void onIconClick(View v) {
                ImageDotLayout.IconBean bean= (ImageDotLayout.IconBean) v.getTag();
                Toast.makeText(ImageDotActivity.this,"位置="+(bean.id+1),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initIcon() {
        final List<ImageDotLayout.IconBean> iconBeanList = new ArrayList<>();
        BitmapActivity b=new BitmapActivity();
        Drawable d =new BitmapDrawable(b.setTextToImg(String.valueOf(fg++)));
        //ImageDotLayout.IconBean bean = new ImageDotLayout.IconBean(0, 0.3f, 0.4f,d );
        //iconBeanList.add(bean);
        //监听图片是否加载完成
        imageDotLayout.setOnLayoutReadyListener(new ImageDotLayout.OnLayoutReadyListener() {
            @Override
            public void onLayoutReady() {
                imageDotLayout.addIcons(iconBeanList);
            }
        });
    }
    public void save(View v){
        //final Context a=this;
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageDotActivity.this);
        builder.setTitle("请输入的路线名称(重名则会覆盖)");
        final EditText e=new EditText(ImageDotActivity.this);
        builder.setView(e);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Toast.makeText(ImageDotActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
                String name=e.getText().toString()+".txt";
                SaveFileService S=new SaveFileService();
                if(S.saveFile(ImageDotActivity.this,name,imageDotLayout.iconList)){
                    Toast.makeText(ImageDotActivity.this,"成功创建了新路线！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ImageDotActivity.this,"创建路线失败！",Toast.LENGTH_SHORT).show();
                }
                ;

            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    public void cancel(View v){

        imageDotLayout.fg--;
        if(imageDotLayout.fg<1)
            imageDotLayout.fg=1;
        //list : iconList
        //
        for(int i = 0;i < imageDotLayout.iconList.size();i++){
            if(i==imageDotLayout.iconList.size()-1){
                imageDotLayout.removeIcon(imageDotLayout.iconList.get(i));
                imageDotLayout.iconList.remove(i);
            }
        }


        List<ImageDotLayout.IconBean> rectBeans = new ArrayList<>();
        if (imageDotLayout.iconList != null && imageDotLayout.iconList.size() > 0) {
            for (ImageView icon : imageDotLayout.iconList) {
                ImageDotLayout.IconBean rectBean = (ImageDotLayout.IconBean) icon.getTag();
                rectBeans.add(rectBean);
            }
        }
        double dis=0;
        MapUtils mp=new MapUtils();
        float topx= (float) 27.8950248644;
        float topy=(float)112.8657982302;
        float downx=(float)27.8781445823;
        float downy=(float)112.8521836590;
        for(int i=1;i<rectBeans.size();i++){
            //top右上 down左下 向右增加y 向上增加x  x:上下 y：左右
            double x0=topx-(topx-downx)*(rectBeans.get(i-1).sy);
            double y0=(topy-downy)*(rectBeans.get(i-1).sx)+downy;
            double x1=topx-(topx-downx)*(rectBeans.get(i).sy);
            double y1=(topy-downy)*(rectBeans.get(i).sx)+downy;
            dis+=mp.GetDistance(x0,y0,x1,y1);
        }
        TextView tv=(TextView)findViewById(R.id.count);
        tv.setText("总路程长度为："+String.valueOf((int)dis)+"(m)");

        Log.d("AK", "SBAAAAAAA: ");
    }
}
