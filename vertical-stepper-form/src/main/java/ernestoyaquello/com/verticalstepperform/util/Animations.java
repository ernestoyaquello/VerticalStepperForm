package ernestoyaquello.com.verticalstepperform.util;

import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class Animations {

    public static void slideDownIfNecessary(final View v, boolean animate) {
        if(v.getVisibility() != View.VISIBLE) {

            if (!animate) {
                v.setVisibility(View.VISIBLE);
                return;
            }

            v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            final int targetHeight = v.getMeasuredHeight();

            // Older versions of android (pre API 21) cancel animations for views with a height of 0,
            // so we set it to 1 instead
            setHeight(v, 1);
            v.setVisibility(View.VISIBLE);

            Animation a = new Animation() {
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

            a.setAnimationListener(new Animation.AnimationListener() {
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

            // 1dp/ms
            a.setDuration(((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2);
            v.startAnimation(a);
        }
    }

    public static void slideUpIfNecessary(final View v, boolean animate) {
        if(v.getVisibility() == View.VISIBLE) {

            if (!animate) {
                v.setVisibility(View.GONE);
                return;
            }

            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
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

            a.setAnimationListener(new Animation.AnimationListener() {
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

            // 1dp/ms
            a.setDuration(((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 2);
            v.startAnimation(a);
        }
    }

    private static void setHeight(View v, int newHeight) {
        v.setLayoutParams(new LinearLayout.LayoutParams(v.getLayoutParams().width, newHeight));
    }
}
