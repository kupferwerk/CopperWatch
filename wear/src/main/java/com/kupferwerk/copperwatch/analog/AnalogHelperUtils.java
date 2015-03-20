package com.kupferwerk.copperwatch.analog;

import android.text.format.Time;

public class AnalogHelperUtils {

   /**
    * Calculating angle for hour
    *
    * @param time current time
    * @return angle of hour hand
    */
   public static float getHourAngle(Time time) {
      return ((time.hour * 60.f) + time.minute) / 2f;
   }

   /**
    * Calculating angle for minute
    *
    * @param time current time
    * @return angle of hour minute
    */
   public static float getMinuteAngle(Time time) {
      return ((time.minute * 60) + time.second) / 10f;
   }

   /**
    * Calculating alpha value for minute block
    *
    * @param index   index of current block
    * @param seconds amount of seconds
    */
   public static float getAlpha(int index, int seconds) {
      if (seconds < (index - 1) * 10) {
         return 0.0f;
      }
      if (seconds >= index * 10) {
         return 1.0f;
      }
      return ((float) seconds - index * 10) / 10.f;
   }
}
