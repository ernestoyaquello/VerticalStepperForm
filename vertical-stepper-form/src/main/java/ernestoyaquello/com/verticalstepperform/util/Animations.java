package ernestoyaquello.com.verticalstepperform.util;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class Animations {

    private static long minDurationMillis = 150;

    public static Animation slideDownIfNecessary(final View v, boolean animate) {
        return slideDownIfNecessary(v, animate, true, 0);
    }

    public static Animation slideDownIfNecessary(final View v, boolean animate, long durationMillis) {
        return slideDownIfNecessary(v, animate, true, durationMillis);
    }

    public static Animation slideDownIfNecessary(final View v, boolean animate, boolean startAnimation, long durationMillis) {
        if(v.getVisibility() != View.VISIBLE) {

            if (!animate) {
                v.setVisibility(View.VISIBLE);
                return null;
            }

            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            int targetHeight = v.getMeasuredHeight();
            durationMillis = durationMillis != 0
                    ? durationMillis
                    : ((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
            durationMillis = durationMillis < minDurationMillis ? minDurationMillis : durationMillis;
            Animation slideDownAnimation = getSlideDownAnimation(v, targetHeight, durationMillis);

            if (startAnimation) {
                v.startAnimation(slideDownAnimation);
            }

            return slideDownAnimation;
        }

        return null;
    }

    public static Animation slideUpIfNecessary(final View v, boolean animate) {
        return slideUpIfNecessary(v, animate, true, 0);
    }

    public static Animation slideUpIfNecessary(final View v, boolean animate, long durationMillis) {
        return slideUpIfNecessary(v, animate, true, durationMillis);
    }

    public static Animation slideUpIfNecessary(final View v, boolean animate, boolean startAnimation, long durationMillis) {
        if(v.getVisibility() == View.VISIBLE) {

            if (!animate) {
                v.setVisibility(View.GONE);
                return null;
            }

            int initialHeight = v.getMeasuredHeight();
            durationMillis = durationMillis != 0
                    ? durationMillis
                    : ((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
            durationMillis = durationMillis < minDurationMillis ? minDurationMillis : durationMillis;
            Animation slideUpAnimation = getSlideUpAnimation(v, initialHeight, durationMillis);

            if (startAnimation) {
                v.startAnimation(slideUpAnimation);
            }

            return slideUpAnimation;
        }

        return null;
    }

    public static Animation getSlideDownAnimation(final View v, final int targetHeight, long durationMillis) {

        // Older versions of android (pre API 21) cancel animations for views with a height of 0,
        // so we set it to 1 instead
        setHeight(v, 1);

        v.setVisibility(View.VISIBLE);
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                setHeight(v, newHeight);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing here
            }
        });
        animation.setDuration(durationMillis);

        return animation;
    }

    public static Animation getSlideUpAnimation(final View v, final int initialHeight, long durationMillis) {
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime != 1) {
                    int newHeight = initialHeight - (int) (initialHeight * interpolatedTime);
                    setHeight(v, newHeight);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing here
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing here
            }
        });
        animation.setDuration(durationMillis);

        return animation;
    }

    private static void setHeight(View v, int newHeight) {
        v.setLayoutParams(new LinearLayout.LayoutParams(v.getLayoutParams().width, newHeight));
    }
}
