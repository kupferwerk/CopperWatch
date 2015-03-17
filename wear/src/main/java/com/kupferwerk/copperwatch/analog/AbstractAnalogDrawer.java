package com.kupferwerk.copperwatch.analog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.format.Time;

import com.kupferwerk.copperwatch.DateFormatter;
import com.kupferwerk.copperwatch.Drawer;

public abstract class AbstractAnalogDrawer implements Drawer {

   protected Paint bgPaint;
   protected Paint logoPaint;
   protected Paint strokePaint;
   protected Paint minBgPaint;
   protected Paint minFgPaint;
   protected Paint hourPaint;

   protected Bitmap logo;
   protected Bitmap scaledLogo;
   protected Bitmap background;
   protected Bitmap scaledBackground;

   protected boolean inAmbientMode = false;
   protected DateFormatter formatter;

   protected float centerX;
   protected float centerY;

   protected float hourHandWidth;
   protected float hourHandHeight;
   protected float hourHandSpacing;
   protected float hourHandRadius;

   protected float minHandWidth;
   protected float minHandHeight;
   protected float minHandSpacing;
   protected float minHandRadius;

   @Override
   public abstract void init(Context context);

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

      if (scaledLogo == null || scaledLogo.getWidth() != width ||
            scaledLogo.getHeight() != height) {
         scaledLogo = Bitmap.createScaledBitmap(logo, (int) (logo.getWidth() * 0.65f),
               (int) (logo.getHeight() * 0.65f), true /* filter */);
      }
      if (!inAmbientMode) {
         canvas.drawBitmap(scaledBackground, 0, 0, null);
         canvas.drawBitmap(scaledLogo, centerX - (scaledLogo.getWidth() / 2),
               centerY + (scaledLogo.getHeight() * 2), logoPaint);
      } else {
         canvas.drawRect(0, 0, width, height, bgPaint);
      }
   }

   @Override
   public void drawSeconds(Canvas canvas, Time time) {
      // nothing to do here
   }

   @Override
   public void drawAdditionalText(Canvas canvas) {
      if (inAmbientMode) {
         return;
      }
      formatter.setDate(canvas, centerX);
      formatter.setWeekday(canvas, centerX);
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

   protected float getHourAngle(Time time) {
      return ((time.hour * 60.f) + time.minute) / 2f;
   }

   protected float getMinuteAngle(Time time) {
      return ((time.minute * 60) + time.second) / 10f;
   }

   protected float getAlpha(int index, int seconds) {
      if (seconds < (index - 1) * 10) {
         return 0.0f;
      }
      if (seconds >= index * 10) {
         return 1.0f;
      }
      return ((float) seconds - index * 10) / 10.f;
   }
}
