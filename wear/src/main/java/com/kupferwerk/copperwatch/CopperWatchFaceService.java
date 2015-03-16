package com.kupferwerk.copperwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import java.util.TimeZone;

public class CopperWatchFaceService extends CanvasWatchFaceService {

   @Override
   public Engine onCreateEngine() {
      return new CopperEngine();
   }

   private class CopperEngine extends CanvasWatchFaceService.Engine {

      static final int MSG_UPDATE_TIME = 0;
      static final long INTERACTIVE_UPDATE_RATE_MS = 1000 * 1;
      // a time object
      Time mTime;

      // device features
      boolean mLowBitAmbient;
      boolean mBurnInProtection;

      // graphic objects
      Bitmap background;
      Bitmap mBackgroundScaledBitmap;
      Paint hourPaint;
      Paint minutePaint;
      Paint fillPaint;

      boolean mRegisteredTimeZoneReceiver;

      /* handler to update the time once a second in interactive mode */
      final Handler updateHandler = new Handler() {
         @Override
         public void handleMessage(Message message) {
            switch (message.what) {
               case MSG_UPDATE_TIME:
                  invalidate();
                  if (shouldTimerBeRunning()) {
                     long timeMs = System.currentTimeMillis();
                     long delayMs =
                           INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                     updateHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                  }
                  break;
            }
         }
      };

      private void updateTimer() {
         updateHandler.removeMessages(MSG_UPDATE_TIME);
         if (shouldTimerBeRunning()) {
            updateHandler.sendEmptyMessage(MSG_UPDATE_TIME);
         }
      }

      private boolean shouldTimerBeRunning() {
         return isVisible() && !isInAmbientMode();
      }

      // receiver to update the time zone
      final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            mTime.clear(intent.getStringExtra("time-zone"));
            mTime.setToNow();
         }
      };

      @Override
      public void onCreate(SurfaceHolder holder) {
         super.onCreate(holder);
         // configure the system UI (see next section)
         setWatchFaceStyle(new WatchFaceStyle.Builder(CopperWatchFaceService.this)
               .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
               .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
               .setShowSystemUiTime(false).build());

         // load the background image
         Resources resources = CopperWatchFaceService.this.getResources();
         Drawable backgroundDrawable = resources.getDrawable(R.drawable.watch_bg_round);
         background = ((BitmapDrawable) backgroundDrawable).getBitmap();

         // create graphic styles
         hourPaint = new Paint();
         hourPaint.setARGB(255, 33, 33, 33);
         hourPaint.setStrokeWidth(1.5f);
         hourPaint.setAntiAlias(true);
         hourPaint.setStyle(Paint.Style.STROKE);

         minutePaint = new Paint();
         minutePaint.setARGB(255, 255, 128, 0);
         minutePaint.setStrokeWidth(1.5f);
         minutePaint.setAntiAlias(true);
         minutePaint.setStyle(Paint.Style.STROKE);

         fillPaint = new Paint();
         fillPaint.setARGB(255, 255, 128, 0);
         fillPaint.setAntiAlias(true);
         fillPaint.setStyle(Paint.Style.FILL);

         // allocate an object to hold the time
         mTime = new Time();
      }

      @Override
      public void onPropertiesChanged(Bundle properties) {
         super.onPropertiesChanged(properties);
            /* get device features (burn-in, low-bit ambient) */
         mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
         mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
      }

      @Override
      public void onTimeTick() {
         super.onTimeTick();
            /* the time changed */
         invalidate();
      }

      @Override
      public void onAmbientModeChanged(boolean inAmbientMode) {
         super.onAmbientModeChanged(inAmbientMode);
            /* the wearable switched between modes */
         if (mLowBitAmbient) {
            boolean antiAlias = !inAmbientMode;
            hourPaint.setAntiAlias(antiAlias);
            minutePaint.setAntiAlias(antiAlias);
         }
         invalidate();
         updateTimer();
      }

      @Override
      public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
         mTime.setToNow();

         int width = bounds.width();
         int height = bounds.height();

         // Draw the background, scaled to fit.
         if (mBackgroundScaledBitmap == null || mBackgroundScaledBitmap.getWidth() != width ||
               mBackgroundScaledBitmap.getHeight() != height) {
            mBackgroundScaledBitmap =
                  Bitmap.createScaledBitmap(background, width, height, true /* filter */);
         }
         canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);

         // Find the center. Ignore the window insets so that, on round watches
         // with a "chin", the watch face is centered on the entire screen, not
         // just the usable portion.
         float centerX = width / 2f;
         float centerY = height / 2f;
         DateFormatter.getInstance(getApplicationContext()).setDate(canvas, centerX);

         // Compute rotations and lengths for the clock hands.
         //         float secRot = mTime.second / 30f * (float) Math.PI;
         int hour = mTime.hour;
         int minutes = mTime.minute;
         int seconds = mTime.second;

         // Only draw the second hand in interactive mode.
         if (!isInAmbientMode()) {
            //            float secX = (float) Math.sin(secRot) * secLength;
            //            float secY = (float) -Math.cos(secRot) * secLength;
            //            canvas.drawLine(centerX, centerY, centerX + secX, centerY +
            //                  secY, mSecondPaint);
         }

         if (hour > 11) {
            hour -= 12;
         }
         float degrees = ((hour * 60.f) + minutes) / 2f;

         canvas.save();
         canvas.rotate(degrees, centerX, centerY);
         fillPaint.setAlpha(255);
         canvas.drawRoundRect(centerX - 10f, -4f, centerX + 10f, 30, 6, 6, fillPaint);
         canvas.drawRoundRect(centerX - 10f, -4f, centerX + 10f, 30, 6, 6, hourPaint);
         canvas.restore();

         canvas.save();
         drawMinutes(canvas, centerX, centerY, minutes, seconds);
         canvas.restore();
      }

      private void drawMinutes(Canvas canvas, float centerX, float centerY, int minutes,
            int seconds) {
         float degrees = ((minutes * 60) + seconds) / 10;
         canvas.rotate(degrees, centerX, centerY);
         for (int i = 1; i <= 6; ++i) {
            drawSeconds(canvas, i, seconds, centerX - 7.5f, 2.5f, centerX + 7.5f, 12.5f);
         }
      }

      private void drawSeconds(Canvas canvas, int index, int seconds, float left, float top,
            float right, float bottom) {

         float alpha;
         if (index <= seconds / 10) {
            alpha = 1.f;
         } else if (index > (seconds / 10) + 1) {
            alpha = 0.f;
         } else {
            alpha = (float) seconds / (float) (index * 10);
         }
         fillPaint.setAlpha((int) (alpha * 255f));
         float offset = (index - 1) * (bottom + 4);
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, 2, 2, fillPaint);
         canvas.drawRoundRect(left, top + offset, right, bottom + offset, 2, 2, minutePaint);
      }

      @Override
      public void onVisibilityChanged(boolean visible) {
         super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */
         if (visible) {
            registerReceiver();

            // Update time zone in case it changed while we weren't visible.
            mTime.clear(TimeZone.getDefault().getID());
            mTime.setToNow();
         } else {
            unregisterReceiver();
         }

         // Whether the timer should be running depends on whether we're visible and
         // whether we're in ambient mode), so we may need to start or stop the timer
         updateTimer();
      }

      private void registerReceiver() {
         if (mRegisteredTimeZoneReceiver) {
            return;
         }
         mRegisteredTimeZoneReceiver = true;
         IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
         CopperWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
      }

      private void unregisterReceiver() {
         if (!mRegisteredTimeZoneReceiver) {
            return;
         }
         mRegisteredTimeZoneReceiver = false;
         CopperWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
      }
   }
}
