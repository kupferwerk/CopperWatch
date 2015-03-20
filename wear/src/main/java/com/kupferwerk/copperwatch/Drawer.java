package com.kupferwerk.copperwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.format.Time;

public interface Drawer {

   interface ARGB {

      int OPAQUE = 255;

      int[] GREY_DARK = {34, 34, 34};
      int[] GREY_LIGHT = {119, 119, 119};
      int[] ORANGE = {255, 128, 0};
      int[] WHITE = {255, 255, 255};
      int[] WHITE_DIRTY = {204, 204, 204};
   }

   void init(Context context);

   void drawBackground(Canvas canvas, Rect bounds);

   void drawHour(Canvas canvas, Time time);

   void drawMinutes(Canvas canvas, Time time);

   void drawSeconds(Canvas canvas, Time time);

   void drawAdditionalText(Canvas canvas, Time time);

   void updateAmbientMode(boolean inAmbientMode);
}
