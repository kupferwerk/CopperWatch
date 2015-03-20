package com.kupferwerk.copperwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.Time;

public class DateFormatter {

   public static final String FONTS_CONDUIT = "fonts/ConduitITCStd.otf";
   public static DateFormatter instance;

   private Paint paint;

   public static DateFormatter getInstance(Context context) {
      if (instance == null) {
         instance = new DateFormatter(context);
      }
      return instance;
   }

   private DateFormatter(Context context) {
      paint = new Paint();
      paint.setARGB(Drawer.ARGB.OPAQUE, Drawer.ARGB.GREY_LIGHT[0], Drawer.ARGB.GREY_LIGHT[1],
            Drawer.ARGB.GREY_LIGHT[2]);
      paint.setAntiAlias(true);
      paint.setTypeface(
            Typeface.createFromAsset(context.getApplicationContext().getAssets(), FONTS_CONDUIT));
      paint.setTextAlign(Paint.Align.CENTER);
   }

   public void setDate(Canvas canvas, float centerX, Time time) {
      paint.setTextSize(28);
      canvas.drawText(time.format("%d. %b"), centerX, 90, paint);
   }

   public void setWeekday(Canvas canvas, float centerX, Time time) {
      paint.setTextSize(22);
      canvas.drawText(time.format("%A"), centerX, 118, paint);
   }
}
