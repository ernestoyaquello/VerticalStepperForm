package ernestoyaquello.com.verticalstepperform;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;

class UIHelper {

    private static final long MIN_DURATION_MILLIS = 150;

    private static final Map<View, ObjectAnimator> _runningObjectAnimators = new ConcurrentHashMap<>();

    static void setButtonColor(
            AppCompatButton button,
            int buttonColor,
            int buttonTextColor,
            int buttonPressedColor,
            int buttonPressedTextColor) {

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{}
        };
        ColorStateList buttonColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedColor,
                        buttonPressedColor,
                        buttonColor
                });
        ColorStateList buttonTextColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedTextColor,
                        buttonPressedTextColor,
                        buttonTextColor
                });
        ViewCompat.setBackgroundTintList(button, buttonColours);
        button.setTextColor(buttonTextColours);
    }

    static void slideDownIfNecessary(View view, boolean animate) {

        if (!animate) {
            endPreviousAnimationIfNecessary(view);
            onSlidingFinished(view, false);

            return;
        }

        performSlideAnimation(view, false);
    }

    static void slideUpIfNecessary(View view, boolean animate) {

        if (!animate) {
            endPreviousAnimationIfNecessary(view);
            onSlidingFinished(view, true);

            return;
        }

        performSlideAnimation(view, true);
    }

    private static void performSlideAnimation(final View view, final boolean slideUp) {

        int currentHeight = view.getHeight();
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int expandedHeight = view.getMeasuredHeight();

        if (currentHeight < 0) {
            // A negative current height means the view's height hasn't been measured yet, so we
            // assign this value manually depending on whether the animation slides up or down
            currentHeight = slideUp ? expandedHeight : 0;
            setViewHeight(view, currentHeight);
        }

        float initialValue = currentHeight / (float) expandedHeight;
        float finalValue = slideUp ? 0 : 1;
        final float correctedInitialValue = initialValue > 1 ? 1 : initialValue;
        if (correctedInitialValue == finalValue) {

            // No need to animate anything because initial value and final value match
            endPreviousAnimationIfNecessary(view);
            onSlidingFinished(view, slideUp);
            return;
        }

        float density = view.getContext().getResources().getDisplayMetrics().density;
        long durationMillis = ((int) (expandedHeight * (Math.abs(finalValue - correctedInitialValue)) / density)) * 2;
        durationMillis = durationMillis < MIN_DURATION_MILLIS ? MIN_DURATION_MILLIS : durationMillis;

        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, correctedInitialValue, finalValue);
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

                view.setAlpha(correctedInitialValue);
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                _runningObjectAnimators.remove(view);
                onSlidingFinished(view, slideUp);
            }
        });

        endPreviousAnimationIfNecessary(view);

        _runningObjectAnimators.put(view, animator);
        animator.start();
    }

    private static void onSlidingFinished(View view, boolean slideUp) {
        setViewHeight(view, slideUp ? 0 : ViewGroup.LayoutParams.WRAP_CONTENT);
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

    private static void setViewHeight(View view, int newHeight) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = newHeight;
        view.setLayoutParams(layoutParams);
    }
}
