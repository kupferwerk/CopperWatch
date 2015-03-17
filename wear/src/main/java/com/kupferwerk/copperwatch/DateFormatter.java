package com.kupferwerk.copperwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

   public static DateFormatter instance;

   private Paint paint;
   private SimpleDateFormat dateFormat;
   private SimpleDateFormat weekdayFormat;

   public static DateFormatter getInstance(Context context) {
      if (instance == null) {
         instance = new DateFormatter(context);
      }
      return instance;
   }

   private DateFormatter(Context context) {
      paint = new Paint();
      paint.setTextSize(32);
      paint.setARGB(Drawer.ARGB.OPAQUE, Drawer.ARGB.GREY_LIGHT[0], Drawer.ARGB.GREY_LIGHT[1],
            Drawer.ARGB.GREY_LIGHT[2]);
      paint.setAntiAlias(true);
      Typeface kwConduit = Typeface.createFromAsset(context.getApplicationContext().getAssets(),
            "fonts/ConduitITCStd.otf");
      paint.setTypeface(kwConduit);
      paint.setTextAlign(Paint.Align.CENTER);
      dateFormat = new SimpleDateFormat("dd. MMM");
      weekdayFormat = new SimpleDateFormat("EEEE");
   }

   public void setDate(Canvas canvas, float centerX) {
      Date now = new Date();
      paint.setTextSize(28);
      canvas.drawText(dateFormat.format(now), centerX, 90, paint);
   }

   public void setWeekday(Canvas canvas, float centerX) {
      Date now = new Date();
      paint.setTextSize(22);
      canvas.drawText(weekdayFormat.format(now), centerX, 118, paint);
   }
}
