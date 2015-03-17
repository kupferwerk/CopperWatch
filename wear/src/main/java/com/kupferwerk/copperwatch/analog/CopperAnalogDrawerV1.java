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

public class CopperAnalogDrawerV1 extends AbstractAnalogDrawer {

   static final float HOUR_HAND_WIDTH = 8;
   static final float HOUR_HAND_HEIGHT = 20;
   static final float HOUR_HAND_RADIUS = 3;
   static final float HOUR_HAND_SPACEING = 4;

   static final float MIN_HAND_HEIGHT = 3;
   static final float MIN_HAND_WIDTH = 8;
   static final float MIN_HAND_RADIUS = 1;
   static final float MIN_HAND_SPACEING = 2;

   static final float STROKE_WIDTH = 0.5f;

   @Override
   public void init(Context context) {
      initSpecs();
      initBitmaps(context);
      initPaints();
      formatter = DateFormatter.getInstance(context);
   }

   private void initBitmaps(Context context) {
      // load the background image
      Resources resources = context.getResources();
      Drawable backgroundDrawable = resources.getDrawable(R.drawable.watch_bg);
      background = ((BitmapDrawable) backgroundDrawable).getBitmap();

      Drawable logoDrawable = resources.getDrawable(R.drawable.kw_logo);
      logo = ((BitmapDrawable) logoDrawable).getBitmap();
   }

   private void initPaints() {
      bgPaint = new Paint();
      bgPaint.setARGB(ARGB.OPAQUE, ARGB.GREY_DARK[0], ARGB.GREY_DARK[1], ARGB.GREY_DARK[2]);

      logoPaint = new Paint();
      logoPaint.setAntiAlias(true);

      strokePaint = new Paint();
      strokePaint.setARGB(ARGB.OPAQUE, ARGB.GREY_DARK[0], ARGB.GREY_DARK[1], ARGB.GREY_DARK[2]);

      strokePaint.setAntiAlias(true);
      strokePaint.setStyle(Paint.Style.STROKE);

      minBgPaint = new Paint();
      minBgPaint
            .setARGB(ARGB.OPAQUE, ARGB.WHITE_DIRTY[0], ARGB.WHITE_DIRTY[1], ARGB.WHITE_DIRTY[2]);
      minBgPaint.setAntiAlias(true);
      minBgPaint.setStyle(Paint.Style.FILL);

      minFgPaint = new Paint();
      minFgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      minFgPaint.setAntiAlias(true);
      minFgPaint.setStyle(Paint.Style.FILL);

      hourPaint = new Paint();
      hourPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      hourPaint.setAntiAlias(true);
      hourPaint.setStrokeWidth(4);
      hourPaint.setStyle(Paint.Style.FILL_AND_STROKE);
   }

   private void initSpecs() {
      hourHandWidth = HOUR_HAND_WIDTH;
      hourHandHeight = HOUR_HAND_HEIGHT;
      hourHandSpacing = HOUR_HAND_SPACEING;
      hourHandRadius = HOUR_HAND_RADIUS;

      minHandWidth = MIN_HAND_WIDTH;
      minHandHeight = MIN_HAND_HEIGHT;
      minHandSpacing = MIN_HAND_SPACEING;
      minHandRadius = MIN_HAND_RADIUS;
   }

   @Override
   public void drawHour(Canvas canvas, Time time) {
      strokePaint.setStrokeWidth(3);
      canvas.save();
      canvas.rotate(getHourAngle(time), centerX, centerY);
      canvas.drawRoundRect(centerX - hourHandWidth, hourHandSpacing, centerX + hourHandWidth,
            hourHandSpacing + hourHandHeight, hourHandRadius, hourHandRadius, hourPaint);
      canvas.drawRoundRect(centerX - hourHandWidth + 1, hourHandSpacing + 1,
            centerX + hourHandWidth - 1, hourHandSpacing + hourHandHeight - 1, hourHandRadius,
            hourHandRadius, strokePaint);
      canvas.restore();
   }

   @Override
   public void drawMinutes(Canvas canvas, Time time) {
      canvas.save();
      canvas.rotate(getMinuteAngle(time), centerX, centerY);
      strokePaint.setStrokeWidth(1);
      for (int i = 1; i <= 6; ++i) {
         drawMinuteBlocks(canvas, i, time.second, centerX - minHandWidth, minHandSpacing,
               centerX + minHandWidth, minHandSpacing + minHandHeight);
      }
      canvas.restore();
   }

   private void drawMinuteBlocks(Canvas canvas, int index, int seconds, float left, float top,
         float right, float bottom) {

      float offset = (index - 1) * (bottom + minHandSpacing);
      canvas.drawRoundRect(left, top + offset, right, bottom + offset, minHandRadius, minHandRadius,
            minBgPaint);
      if (!inAmbientMode) {
         // do not draw seconds in ambient mode
         minFgPaint.setAlpha((int) (getAlpha(index, seconds) * (float) ARGB.OPAQUE));
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, minHandRadius,
               minHandRadius, minFgPaint);
      }

      canvas.drawRoundRect(left - STROKE_WIDTH, top + offset - STROKE_WIDTH, right + STROKE_WIDTH,
            bottom + offset + STROKE_WIDTH, minHandRadius, minHandRadius, strokePaint);
   }

   @Override
   public void drawSeconds(Canvas canvas, Time time) {
      // nothing to do here
   }

   @Override
   public void updateAmbientMode(boolean inAmbientMode) {
      this.inAmbientMode = inAmbientMode;
      if (inAmbientMode) {
         minBgPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
         minFgPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
         hourPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
      } else {
         minBgPaint
               .setARGB(ARGB.OPAQUE, ARGB.WHITE_DIRTY[0], ARGB.WHITE_DIRTY[1], ARGB.WHITE_DIRTY[2]);
         minFgPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
         hourPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      }
   }
}
