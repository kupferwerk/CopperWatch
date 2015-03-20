package com.kupferwerk.copperwatch.analog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;

import com.kupferwerk.copperwatch.DateFormatter;
import com.kupferwerk.copperwatch.Drawer;
import com.kupferwerk.copperwatch.R;

public class CopperAnalogDrawer implements Drawer {

   public static final float SCALE = 0.65f;

   private static final float HOUR_HAND_HEIGHT = 30;
   private static final float HOUR_HAND_WIDTH = 1.5f;
   private static final float MIN_HAND_HEIGHT = 3;
   private static final float MIN_HAND_RADIUS = 1;
   private static final float MIN_HAND_SPACEING = 2;
   private static final float MIN_HAND_WIDTH = 4;
   private static final float STROKE_WIDTH = 0.5f;
   private static final float START_ANGLE = -90;

   protected Paint bgPaint;
   protected Paint fillPaint;
   protected Paint strokePaint;
   protected Paint logoPaint;
   protected Paint minBgPaint;

   protected float centerX;
   protected float centerY;

   protected int height;
   protected int width;

   protected Bitmap logo;
   protected Bitmap scaledLogo;
   protected DateFormatter formatter;

   protected boolean inAmbientMode = false;

   @Override
   public void drawAdditionalText(Canvas canvas, Time time) {
      if (inAmbientMode) {
         return;
      }
      formatter.setDate(canvas, centerX, time);
      formatter.setWeekday(canvas, centerX, time);
      drawLogo(canvas);
   }

   @Override
   public void drawBackground(Canvas canvas, Rect bounds) {
      this.width = bounds.width();
      this.height = bounds.height();
      this.centerX = width / 2f;
      this.centerY = height / 2f;

      // Find the center. Ignore the window insets so that, on round watches
      // with a "chin", the watch face is centered on the entire screen, not
      // just the usable portion.
      canvas.drawRect(0, 0, width, height, bgPaint);
   }

   @Override
   public void drawHour(Canvas canvas, Time time) {
      float hourAngle = AnalogHelperUtils.getHourAngle(time);
      if (hourAngle > 360) {
         hourAngle -= 360;
      }
      if (!inAmbientMode) {
         drawOutline(canvas, hourAngle);
         drawClockFace(canvas, hourAngle);
         fillPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      } else {
         fillPaint.setARGB(ARGB.OPAQUE, ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
      }
      canvas.save();
      canvas.rotate(hourAngle, centerX, centerX);
      canvas.drawRect(centerX - 1.5f, 0, centerX + 1.5f, HOUR_HAND_HEIGHT, fillPaint);
      canvas.restore();
   }

   @Override
   public void drawMinutes(Canvas canvas, Time time) {
      canvas.save();
      canvas.rotate(AnalogHelperUtils.getMinuteAngle(time), centerX, centerY);
      strokePaint.setStrokeWidth(1);
      fillPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      for (int i = 1; i <= 6; ++i) {
         drawMinuteBlocks(canvas, i, time.second, centerX - MIN_HAND_WIDTH, MIN_HAND_SPACEING,
               centerX + MIN_HAND_WIDTH, MIN_HAND_SPACEING + MIN_HAND_HEIGHT);
      }
      canvas.restore();
   }

   @Override
   public void drawSeconds(Canvas canvas, Time time) {
      // nothing to do here
   }

   @Override
   public void init(Context context) {
      initBitmaps(context);
      initPaints();
      formatter = DateFormatter.getInstance(context);
   }

   @Override
   public void updateAmbientMode(boolean inAmbientMode) {
      this.inAmbientMode = inAmbientMode;
      if (inAmbientMode) {
         minBgPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
         fillPaint.setARGB(ARGB.WHITE[0], ARGB.WHITE[0], ARGB.WHITE[1], ARGB.WHITE[2]);
      } else {
         minBgPaint
               .setARGB(ARGB.OPAQUE, ARGB.WHITE_DIRTY[0], ARGB.WHITE_DIRTY[1], ARGB.WHITE_DIRTY[2]);
         fillPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      }
   }

   protected void drawLogo(Canvas canvas) {
      if (inAmbientMode) {
         return;
      }
      if (scaledLogo == null || scaledLogo.getWidth() != width ||
            scaledLogo.getHeight() != height) {
         scaledLogo = Bitmap.createScaledBitmap(logo, (int) (logo.getWidth() * SCALE),
               (int) (logo.getHeight() * SCALE), true);
      }
      canvas.drawBitmap(scaledLogo, centerX - (scaledLogo.getWidth() / 2),
            centerY + (scaledLogo.getHeight() * 2), logoPaint);
   }

   private void drawClockFace(Canvas canvas, float hourAngle) {
      canvas.save();
      for (int i = 1; i <= 12; ++i) {
         canvas.rotate(30, centerX, centerY);
         if (30 * i <= hourAngle) {
            fillPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
         } else {
            fillPaint
                  .setARGB(ARGB.OPAQUE, ARGB.GREY_LIGHT[0], ARGB.GREY_LIGHT[1], ARGB.GREY_LIGHT[2]);
         }
         canvas.drawRect(centerX - HOUR_HAND_WIDTH, 0, centerX + HOUR_HAND_WIDTH, 10, fillPaint);
      }
      canvas.restore();
   }

   private void drawMinuteBlocks(Canvas canvas, int index, int seconds, float left, float top,
         float right, float bottom) {

      float offset = (index - 1) * (bottom + MIN_HAND_SPACEING) + 6;
      canvas.drawRoundRect(left, top + offset, right, bottom + offset, MIN_HAND_RADIUS,
            MIN_HAND_RADIUS, minBgPaint);
      if (!inAmbientMode) {
         // do not draw seconds in ambient mode
         fillPaint
               .setAlpha((int) (AnalogHelperUtils.getAlpha(index, seconds) * (float) ARGB.OPAQUE));
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, MIN_HAND_RADIUS,
               MIN_HAND_RADIUS, fillPaint);
      }

      canvas.drawRoundRect(left - STROKE_WIDTH, top + offset - STROKE_WIDTH, right + STROKE_WIDTH,
            bottom + offset + STROKE_WIDTH, MIN_HAND_RADIUS, MIN_HAND_RADIUS, strokePaint);
   }

   private void drawOutline(Canvas canvas, float sweepAngle) {
      fillPaint.setARGB(ARGB.OPAQUE, ARGB.ORANGE[0], ARGB.ORANGE[1], ARGB.ORANGE[2]);
      canvas.drawArc(0, 0, width, height, START_ANGLE, sweepAngle, true, fillPaint);

      fillPaint.setARGB(ARGB.OPAQUE, ARGB.GREY_DARK[0], ARGB.GREY_DARK[1], ARGB.GREY_DARK[2]);
      canvas.drawArc(3, 3, width - 3, height - 3, START_ANGLE - 2, sweepAngle + 4, true, fillPaint);
   }

   private void initBitmaps(Context context) {
      // load the background image
      Resources resources = context.getResources();
      Drawable logoDrawable = resources.getDrawable(R.drawable.kw_logo);
      logo = ((BitmapDrawable) logoDrawable).getBitmap();
   }

   private void initPaints() {
      bgPaint = new Paint();
      bgPaint.setARGB(ARGB.OPAQUE, ARGB.GREY_DARK[0], ARGB.GREY_DARK[1], ARGB.GREY_DARK[2]);
      bgPaint.setAntiAlias(true);

      logoPaint = new Paint();
      logoPaint.setAntiAlias(true);

      strokePaint = new Paint();
      strokePaint.setAntiAlias(true);
      strokePaint.setStyle(Paint.Style.STROKE);

      fillPaint = new Paint();
      fillPaint.setAntiAlias(true);
      fillPaint.setStyle(Paint.Style.FILL);

      minBgPaint = new Paint();
      minBgPaint
            .setARGB(ARGB.OPAQUE, ARGB.WHITE_DIRTY[0], ARGB.WHITE_DIRTY[1], ARGB.WHITE_DIRTY[2]);
      minBgPaint.setAntiAlias(true);
      minBgPaint.setStyle(Paint.Style.FILL);
   }
}
