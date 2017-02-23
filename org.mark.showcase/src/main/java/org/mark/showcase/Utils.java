package org.mark.showcase;

import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {

    public static float dpToPx(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
