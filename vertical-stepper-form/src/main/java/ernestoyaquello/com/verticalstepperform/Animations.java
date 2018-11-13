package ernestoyaquello.com.verticalstepperform;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

class Animations {

    private static final long MIN_DURATION_MILLIS = 150;

    void slideDownIfNecessary(final View v, boolean animate) {
        if (!animate) {
            v.setVisibility(View.VISIBLE);
            return;
        }

        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        int targetHeight = v.getMeasuredHeight();
        long durationMillis = ((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
        durationMillis = durationMillis < MIN_DURATION_MILLIS ? MIN_DURATION_MILLIS : durationMillis;
        Animation slideDownAnimation = getSlideDownAnimation(v, targetHeight, durationMillis);

        v.startAnimation(slideDownAnimation);
    }

    void slideUpIfNecessary(final View v, boolean animate) {
        if (!animate) {
            v.setVisibility(View.GONE);
            return;
        }

        int initialHeight = v.getMeasuredHeight();
        long durationMillis = ((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2;
        durationMillis = durationMillis < MIN_DURATION_MILLIS ? MIN_DURATION_MILLIS : durationMillis;
        Animation slideUpAnimation = getSlideUpAnimation(v, initialHeight, durationMillis);

        v.startAnimation(slideUpAnimation);
    }

    private Animation getSlideDownAnimation(final View v, final int targetHeight, long durationMillis) {

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

    private Animation getSlideUpAnimation(final View v, final int initialHeight, long durationMillis) {
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

    private void setHeight(View v, int newHeight) {
        v.setLayoutParams(new LinearLayout.LayoutParams(v.getLayoutParams().width, newHeight));
    }
}
