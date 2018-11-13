package ernestoyaquello.com.verticalstepperform;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

class Animations {

    private static long minDurationMillis = 150;

    static Animation slideDownIfNecessary(final View v, boolean animate) {
        if (!animate) {
            v.setVisibility(View.VISIBLE);
            return null;
        }

        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int targetHeight = v.getMeasuredHeight();
        long durationMillis = ((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
        durationMillis = durationMillis < minDurationMillis ? minDurationMillis : durationMillis;
        Animation slideDownAnimation = getSlideDownAnimation(v, targetHeight, durationMillis);

        v.startAnimation(slideDownAnimation);

        return slideDownAnimation;
    }

    static Animation slideUpIfNecessary(final View v, boolean animate) {
        if (!animate) {
            v.setVisibility(View.GONE);
            return null;
        }

        int initialHeight = v.getMeasuredHeight();
        long durationMillis = ((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
        durationMillis = durationMillis < minDurationMillis ? minDurationMillis : durationMillis;
        Animation slideUpAnimation = getSlideUpAnimation(v, initialHeight, durationMillis);

        v.startAnimation(slideUpAnimation);

        return slideUpAnimation;
    }

    static Animation getSlideDownAnimation(final View v, final int targetHeight, long durationMillis) {

        setHeight(v, v.getVisibility() != View.VISIBLE ? 1 : v.getMeasuredHeight());
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

    static Animation getSlideUpAnimation(final View v, final int initialHeight, long durationMillis) {
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
