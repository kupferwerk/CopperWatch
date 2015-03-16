package com.kupferwerk.copperwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

   public static DateFormatter instance;

   private Paint paint;
   private SimpleDateFormat dateFormat;

   public static DateFormatter getInstance(Context context) {
      if (instance == null) {
         instance = new DateFormatter(context);
      }
      return instance;
   }

   private DateFormatter(Context context) {
      paint = new Paint();
      paint.setTextSize(32);
      paint.setColor(Color.WHITE);
      paint.setAntiAlias(true);
      Typeface kwConduit = Typeface
            .createFromAsset(context.getApplicationContext().getAssets(), "fonts/ConduitITCStd.otf");
      paint.setTypeface(kwConduit);
      paint.setTextAlign(Paint.Align.CENTER);
      dateFormat = new SimpleDateFormat("dd. MMM");
   }

   public void setDate(Canvas canvas, float centerX) {
      Date now = new Date();
      canvas.drawText(dateFormat.format(now), centerX, 105, paint);
   }
}
