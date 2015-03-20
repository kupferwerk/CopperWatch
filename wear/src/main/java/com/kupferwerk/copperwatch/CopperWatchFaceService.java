package com.kupferwerk.copperwatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import com.kupferwerk.copperwatch.analog.CopperAnalogDrawer;

import java.util.TimeZone;

public class CopperWatchFaceService extends CanvasWatchFaceService {

   @Override
   public Engine onCreateEngine() {
      return new CopperEngine();
   }

   private class CopperEngine extends CanvasWatchFaceService.Engine {

      static final int MSG_UPDATE_TIME = 0;
      static final long INTERACTIVE_UPDATE_RATE_MS = 1000 * 1;
      static final long AMBIENT_UPDATE_RATE_MS = 1000 * 60;
      // a time object
      Time time;

      // device features
      boolean mLowBitAmbient;
      boolean mBurnInProtection;
      long updateRate;

      // graphic objects

      private Drawer drawer;

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
                     long delayMs = updateRate - (timeMs % updateRate);
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
            time.clear(intent.getStringExtra("time-zone"));
            time.setToNow();
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

         updateRate = INTERACTIVE_UPDATE_RATE_MS;
         drawer = new CopperAnalogDrawer();
         drawer.init(getBaseContext());

         // allocate an object to hold the time
         time = new Time();
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
         drawer.updateAmbientMode(inAmbientMode);
         if (inAmbientMode) {
            updateRate = AMBIENT_UPDATE_RATE_MS;
         } else {
            updateRate = INTERACTIVE_UPDATE_RATE_MS;
         }
         invalidate();
         updateTimer();
      }

      @Override
      public void onDraw(Canvas canvas, Rect bounds) {
            /* draw your watch face */
         time.setToNow();

         drawer.drawBackground(canvas, bounds);
         drawer.drawHour(canvas, time);
         drawer.drawAdditionalText(canvas, time);
         drawer.drawMinutes(canvas, time);
      }

      @Override
      public void onVisibilityChanged(boolean visible) {
         super.onVisibilityChanged(visible);
            /* the watch face became visible or invisible */
         if (visible) {
            registerReceiver();

            // Update time zone in case it changed while we weren't visible.
            time.clear(TimeZone.getDefault().getID());
            time.setToNow();
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
