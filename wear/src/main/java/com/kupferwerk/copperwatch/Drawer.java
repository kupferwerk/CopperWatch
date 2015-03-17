package com.kupferwerk.copperwatch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.format.Time;

public interface Drawer {

   void init(Context context);

   void drawBackground(Canvas canvas, Rect bounds);

   void drawHour(Canvas canvas, Time time);

   void drawMinutes(Canvas canvas, Time time);

   void drawSeconds(Canvas canvas, Time time);

   void drawAdditionalText();

   void updateAmbientMode(boolean inAmbientMode);
}
