package com.example.pong;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
    public static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.getDisplayMetrics());
    }
}
