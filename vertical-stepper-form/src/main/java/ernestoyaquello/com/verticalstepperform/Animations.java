package ernestoyaquello.com.verticalstepperform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Animations {

    private static final long MIN_DURATION_MILLIS = 150;

    private static final Map<View, ObjectAnimator> _runningObjectAnimators = new ConcurrentHashMap<>();

    static void slideDownIfNecessary(View view, boolean animate) {

        if (!animate) {
            endPreviousAnimationIfNecessary(view);
            view.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            setViewHeight(view, view.getMeasuredHeight());
            setFinalAlphaAndVisibility(view, false);

            return;
        }

        performSlideAnimation(view, false);
    }

    static void slideUpIfNecessary(View view, boolean animate) {

        if (!animate) {
            endPreviousAnimationIfNecessary(view);
            setViewHeight(view, 0);
            setFinalAlphaAndVisibility(view, true);

            return;
        }

        performSlideAnimation(view, true);
    }

    private static void performSlideAnimation(final View view, final boolean slideUp) {

        int currentHeight = view.getHeight();
        view.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int expandedHeight = view.getMeasuredHeight();

        if (currentHeight < 0) {
            // A negative current height means the view's height hasn't been measured yet, so we
            // assign this value manually depending on whether the animation slides up or down
            currentHeight = slideUp ? expandedHeight : 0;
            setViewHeight(view, currentHeight);
        }

        final float initialValue = currentHeight / (float) expandedHeight;
        final float finalValue = slideUp ? 0 : 1;

        if (initialValue == finalValue) {

            // No need to animate anything because initial value and final value match
            endPreviousAnimationIfNecessary(view);
            setViewHeight(view, (int) (finalValue * expandedHeight));
            setFinalAlphaAndVisibility(view, slideUp);

            return;
        }

        long durationMillis = ((int) (expandedHeight * (Math.abs(finalValue - initialValue)) / view.getContext().getResources().getDisplayMetrics().density)) * 2;
        durationMillis = durationMillis < MIN_DURATION_MILLIS ? MIN_DURATION_MILLIS : durationMillis;

        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, initialValue, finalValue);
        animator.setDuration(durationMillis);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scaleYCurrentValue = (float) valueAnimator.getAnimatedValue();
                int newHeight = (int) (expandedHeight * scaleYCurrentValue);

                setViewHeight(view, newHeight);
                view.requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);

                view.setAlpha(initialValue);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                _runningObjectAnimators.remove(view);
                setFinalAlphaAndVisibility(view, slideUp);
            }
        });

        endPreviousAnimationIfNecessary(view);

        _runningObjectAnimators.put(view, animator);
        animator.start();
    }

    private static void setFinalAlphaAndVisibility(View view, boolean slideUp) {
        view.setAlpha(slideUp ? 0f : 1f);
        view.setVisibility(slideUp ? View.GONE : View.VISIBLE);
    }

    private static void endPreviousAnimationIfNecessary(View view) {
        if (_runningObjectAnimators.containsKey(view)) {
            ObjectAnimator previousAnimator = _runningObjectAnimators.get(view);
            if (previousAnimator != null) {
                previousAnimator.end();
            }
        }
    }

    private static void setViewHeight(View view, int currentHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = currentHeight;
        view.setLayoutParams(layoutParams);
    }
}
