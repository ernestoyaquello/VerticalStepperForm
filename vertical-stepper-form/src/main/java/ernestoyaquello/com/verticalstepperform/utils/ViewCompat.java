package ernestoyaquello.com.verticalstepperform.utils;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.view.View;

public final class ViewCompat {

    private ViewCompat() {
        // utility class
    }

    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecated")
    public static void setBackground(View view, Drawable drawable) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(drawable);
        } else {
            view.setBackground(drawable);
        }
    }
}
