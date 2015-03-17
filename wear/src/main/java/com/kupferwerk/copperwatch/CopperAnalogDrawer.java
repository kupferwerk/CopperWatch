package com.kupferwerk.copperwatch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;

public class CopperAnalogDrawer implements Drawer {

   static final float MINUTE_BLOCK_HEIGHT = 8;
   static final float MINUTE_BLOCK_WIDTH = 5;
   static final int MINUTE_BLOCK_RADIUS = 2;
   static final float HOUR_HAND_WIDTH = 10f;
   static final float HOUR_HAND_HEIGHT = 24f;
   static final int HOUR_HAND_RADIUS = 6;
   static final float SPACEING = 2;
   static final float STROKE_WIDTH = 1.5f;

   private Paint hourPaint;
   private Paint minutePaint;
   private Paint fillPaint;

   private Bitmap background;
   private Bitmap scaledBackground;

   private boolean inAmbientMode;

   private float centerX;
   private float centerY;

   @Override
   public void init(Context context) {
      // load the background image
      Resources resources = context.getResources();
      Drawable backgroundDrawable = resources.getDrawable(R.drawable.watch_bg_round);
      background = ((BitmapDrawable) backgroundDrawable).getBitmap();

      // create graphic styles
      hourPaint = new Paint();
      hourPaint.setARGB(255, 33, 33, 33);
      hourPaint.setStrokeWidth(STROKE_WIDTH);
      hourPaint.setAntiAlias(true);
      hourPaint.setStyle(Paint.Style.STROKE);

      minutePaint = new Paint();
      minutePaint.setARGB(255, 255, 128, 0);
      minutePaint.setStrokeWidth(STROKE_WIDTH);
      minutePaint.setAntiAlias(true);
      minutePaint.setStyle(Paint.Style.STROKE);

      fillPaint = new Paint();
      fillPaint.setARGB(255, 255, 128, 0);
      fillPaint.setAntiAlias(true);
      fillPaint.setStyle(Paint.Style.FILL);
   }

   @Override
   public void drawBackground(Canvas canvas, Rect bounds) {
      int width = bounds.width();
      int height = bounds.height();

      // Find the center. Ignore the window insets so that, on round watches
      // with a "chin", the watch face is centered on the entire screen, not
      // just the usable portion.
      this.centerX = width / 2f;
      this.centerY = height / 2f;

      // Draw the background, scaled to fit.
      if (scaledBackground == null || scaledBackground.getWidth() != width ||
            scaledBackground.getHeight() != height) {
         scaledBackground = Bitmap.createScaledBitmap(background, width, height, true /* filter */);
      }
      canvas.drawBitmap(scaledBackground, 0, 0, null);
   }

   @Override
   public void drawHour(Canvas canvas, Time time) {
      float degrees = ((time.hour * 60.f) + time.minute) / 2f;
      canvas.save();
      canvas.rotate(degrees, centerX, centerY);
      fillPaint.setAlpha(255);
      canvas.drawRoundRect(centerX - HOUR_HAND_WIDTH, SPACEING, centerX + HOUR_HAND_WIDTH,
            SPACEING + HOUR_HAND_HEIGHT, HOUR_HAND_RADIUS, HOUR_HAND_RADIUS, fillPaint);
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
         fillPaint.setAlpha((int) (getAlpha(index, seconds) * 255f));
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, MINUTE_BLOCK_RADIUS,
               MINUTE_BLOCK_RADIUS, fillPaint);
         canvas.drawRoundRect(left - STROKE_WIDTH, top + offset - STROKE_WIDTH,
               right + STROKE_WIDTH, bottom + offset + STROKE_WIDTH, MINUTE_BLOCK_RADIUS,
               MINUTE_BLOCK_RADIUS, hourPaint);
      }
      canvas.drawRoundRect(left, top + offset, right, bottom + offset, MINUTE_BLOCK_RADIUS,
            MINUTE_BLOCK_RADIUS, minutePaint);
   }

   @Override
   public void drawSeconds(Canvas canvas, Time time) {

   }

   @Override
   public void drawAdditionalText() {

   }

   @Override
   public void updateAmbientMode(boolean inAmbientMode) {
      this.inAmbientMode = inAmbientMode;
      if (inAmbientMode) {
         minutePaint.setARGB(255, 255, 255, 255);
         minutePaint.setAntiAlias(false);
         fillPaint.setARGB(255, 255, 255, 255);
         fillPaint.setAntiAlias(false);
      } else {
         minutePaint.setARGB(255, 255, 128, 0);
         minutePaint.setAntiAlias(true);
         fillPaint.setARGB(255, 255, 128, 0);
         fillPaint.setAntiAlias(true);
      }
   }

   private float getAlpha(int index, int seconds) {
      if (seconds < (index - 1) * 10) {
         return 0.0f;
      }
      if (seconds >= index * 10) {
         return 1.0f;
      }
      return ((float) seconds - index * 10) / 10.f;
   }
}
