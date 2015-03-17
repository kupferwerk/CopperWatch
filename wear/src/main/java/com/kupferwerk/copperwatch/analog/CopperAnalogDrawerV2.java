package com.kupferwerk.copperwatch.analog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;

import com.kupferwerk.copperwatch.DateFormatter;
import com.kupferwerk.copperwatch.R;

public class CopperAnalogDrawerV2 extends AbstractAnalogDrawer {

   static final float MINUTE_BLOCK_HEIGHT = 8;
   static final float MINUTE_BLOCK_WIDTH = 5;
   static final int MINUTE_BLOCK_RADIUS = 2;
   static final float HOUR_HAND_WIDTH = 5f;
   static final float HOUR_HAND_HEIGHT = 32f;
   static final int HOUR_HAND_RADIUS = 4;
   static final float SPACEING = 2;
   static final float STROKE_WIDTH = 1.5f;

   @Override
   public void init(Context context) {
      // load the background image
      Resources resources = context.getResources();
      Drawable backgroundDrawable = resources.getDrawable(R.drawable.watch_bg);
      background = ((BitmapDrawable) backgroundDrawable).getBitmap();

      Drawable logoDrawable = resources.getDrawable(R.drawable.kw_logo);
      logo = ((BitmapDrawable) logoDrawable).getBitmap();
      logoPaint = new Paint();
      logoPaint.setAntiAlias(true);

      formatter = DateFormatter.getInstance(context);
      // create graphic styles
      hourPaint = new Paint();
      hourPaint.setARGB(ARGB.OPAQUE, ARGB.GREY_DARK[0], ARGB.GREY_DARK[1], ARGB.GREY_DARK[2]);
      hourPaint.setStrokeWidth(STROKE_WIDTH);
      hourPaint.setAntiAlias(true);
      hourPaint.setStyle(Paint.Style.STROKE);

      minFgPaint = new Paint();
      minFgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      minFgPaint.setStrokeWidth(STROKE_WIDTH);
      minFgPaint.setAntiAlias(true);
      minFgPaint.setStyle(Paint.Style.STROKE);

      minBgPaint = new Paint();
      minBgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      minBgPaint.setAntiAlias(true);
      minBgPaint.setStyle(Paint.Style.FILL);
   }

   @Override
   public void drawHour(Canvas canvas, Time time) {
      canvas.save();
      canvas.rotate(getHourAngle(time), centerX, centerY);
      minFgPaint.setAlpha(255);
      canvas.drawRoundRect(centerX - HOUR_HAND_WIDTH, SPACEING, centerX + HOUR_HAND_WIDTH,
            SPACEING + HOUR_HAND_HEIGHT, HOUR_HAND_RADIUS, HOUR_HAND_RADIUS, minFgPaint);
      canvas.drawRoundRect(centerX - HOUR_HAND_WIDTH, SPACEING, centerX + HOUR_HAND_WIDTH,
            SPACEING + HOUR_HAND_HEIGHT, HOUR_HAND_RADIUS, HOUR_HAND_RADIUS, hourPaint);
      canvas.restore();
   }

   @Override
   public void drawMinutes(Canvas canvas, Time time) {
      canvas.save();
      float degrees = ((time.minute * 60) + time.second) / 10;
      canvas.rotate(degrees, centerX, centerY);
      for (int i = 1; i <= 6; ++i) {
         drawSeconds(canvas, i, time.second, centerX - MINUTE_BLOCK_WIDTH, SPACEING,
               centerX + MINUTE_BLOCK_WIDTH, SPACEING + MINUTE_BLOCK_HEIGHT);
      }
      canvas.restore();
   }

   private void drawSeconds(Canvas canvas, int index, int seconds, float left, float top,
         float right, float bottom) {

      float offset = (index - 1) * (bottom + SPACEING);
      if (!inAmbientMode) {
         // do not draw seconds in ambient mode
         minFgPaint.setAlpha((int) (getAlpha(index, seconds) * (float) ARGB.OPAQUE));
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, MINUTE_BLOCK_RADIUS,
               MINUTE_BLOCK_RADIUS, minFgPaint);
         canvas.drawRoundRect(left - STROKE_WIDTH, top + offset - STROKE_WIDTH,
               right + STROKE_WIDTH, bottom + offset + STROKE_WIDTH, MINUTE_BLOCK_RADIUS,
               MINUTE_BLOCK_RADIUS, hourPaint);
      }
      canvas.drawRoundRect(left, top + offset, right, bottom + offset, MINUTE_BLOCK_RADIUS,
            MINUTE_BLOCK_RADIUS, minBgPaint);
   }

   @Override
   public void updateAmbientMode(boolean inAmbientMode) {
      this.inAmbientMode = inAmbientMode;
      if (inAmbientMode) {
         minFgPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
         minBgPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
      } else {
         minFgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
         minBgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      }
   }
}
