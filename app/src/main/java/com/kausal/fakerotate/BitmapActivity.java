package com.kausal.fakerotate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapActivity extends Activity {
    public Bitmap setTextToImg(String text) {
        //BitmapDrawable icon = (BitmapDrawable) getResources().getDrawable(R.drawable.score_bg);

        Bitmap bitmap = Bitmap.createBitmap(24, 24, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        canvas.translate((float)24.0 / 2, (float)24 / 2);
        paint.setDither(true);
        //paint.setStrokeWidth(2);
        paint.setTextSize(13);
        canvas.drawCircle(0,0,(float)12,paint);
        paint.setColor(Color.parseColor("#ffffff"));
        float textWidth = paint.measureText(text);
        float baseLineY = Math.abs(paint.ascent() + paint.descent()) / 2;
        canvas.drawText(text, -textWidth / 2, baseLineY, paint);

        return bitmap;
    }
}