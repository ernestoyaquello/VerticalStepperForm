package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView.FormStyle;

/**
 * This class holds a step instance and deals with updating its views so they reflect its state.
 * It can also handle the logic of the optional confirmation step.
 *
 * All this logic could certainly be in the base class of the step, but by keeping it here we make
 * that class cleaner, making it easier to understand for anyone taking a look at it.
 */
class StepHelper implements Step.InternalFormStepListener {

    private Step<?> step;
    private FormStyle formStyle;

    private View stepNumberCircleView;
    private TextView titleView;
    private TextView subtitleView;
    private TextView stepNumberTextView;
    private ImageView doneIconView;
    private TextView errorMessageView;
    private ImageView errorIconView;
    private View headerView;
    private MaterialButton nextButtonView;
    private MaterialButton cancelButtonView;
    private View lineView1;
    private View lineView2;
    private View stepAndButtonView;
    private View errorMessageContainerView;
    private View titleAndSubtitleContainerView;
    private View errorContentAndButtonContainerView;

    StepHelper(Step.InternalFormStepListener formListener, @NonNull Step<?> step) {
        this(formListener, step, false);
    }

    StepHelper(Step.InternalFormStepListener formListener, Step<?> step, boolean isConfirmationStep) {
        this.step = !isConfirmationStep ? step : new ConfirmationStep();
        this.step.addListenerInternal(this);
        this.step.addListenerInternal(formListener);
    }

    View initialize(VerticalStepperFormView form, ViewGroup parent, @LayoutRes int stepLayoutResourceId) {
        if (step.getEntireStepLayout() == null) {
            formStyle = form.style;

            Context context = form.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View stepLayout = inflater.inflate(stepLayoutResourceId, parent, false);

            step.initializeStepInternal(stepLayout, form);
            step.setContentLayoutInternal(step.createStepContentLayout());

            setupStepViews(form, stepLayout);
        } else {
            throw new IllegalStateException("This step has already been initialized");
        }

        return step.getEntireStepLayout();
    }

    private void setupStepViews(final VerticalStepperFormView form, View stepLayout) {

        if (step.getContentLayout() != null) {
            ViewGroup contentContainerLayout = step.getEntireStepLayout().findViewById(R.id.step_content);
            contentContainerLayout.addView(step.getContentLayout());
        }

        stepNumberCircleView = stepLayout.findViewById(R.id.step_number_circle);
        stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        titleView = stepLayout.findViewById(R.id.step_title);
        subtitleView = stepLayout.findViewById(R.id.step_subtitle);
        doneIconView = stepLayout.findViewById(R.id.step_done_icon);
        errorMessageView = stepLayout.findViewById(R.id.step_error_message);
        errorIconView = stepLayout.findViewById(R.id.step_error_icon);
        headerView = stepLayout.findViewById(R.id.step_header);
        nextButtonView = stepLayout.findViewById(R.id.step_button);
        cancelButtonView = stepLayout.findViewById(R.id.step_cancel_button);
        lineView1 = stepLayout.findViewById(R.id.line1);
        lineView2 = stepLayout.findViewById(R.id.line2);
        stepAndButtonView = step.getEntireStepLayout().findViewById(R.id.step_content_and_button);
        errorMessageContainerView = step.getEntireStepLayout().findViewById(R.id.step_error_container);
        titleAndSubtitleContainerView = step.getEntireStepLayout().findViewById(R.id.title_subtitle_container);
        errorContentAndButtonContainerView = step.getEntireStepLayout().findViewById(R.id.error_content_button_container);

        stepNumberTextView.setTypeface(formStyle.stepNumberFontFamily);
        titleView.setTypeface(formStyle.stepTitleFontFamily);
        subtitleView.setTypeface(formStyle.stepSubtitleFontFamily);
        errorMessageView.setTypeface(formStyle.stepErrorMessageFontFamily);

        titleView.setTextColor(formStyle.stepTitleTextColor);
        subtitleView.setTextColor(formStyle.stepSubtitleTextColor);
        stepNumberTextView.setTextColor(formStyle.stepNumberTextColor);
        doneIconView.setColorFilter(formStyle.stepNumberTextColor);
        errorMessageView.setTextColor(formStyle.errorMessageTextColor);
        errorIconView.setColorFilter(formStyle.errorMessageTextColor);

        Drawable circleDrawable = ContextCompat.getDrawable(form.getContext(), R.drawable.circle_step_done);
        circleDrawable.setColorFilter(
                new PorterDuffColorFilter(formStyle.stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        stepNumberCircleView.setBackground(circleDrawable);

        UIHelper.setButtonColor(
                nextButtonView,
                formStyle.nextButtonBackgroundColor,
                formStyle.nextButtonTextColor,
                formStyle.nextButtonPressedBackgroundColor,
                formStyle.nextButtonPressedTextColor);
        UIHelper.setButtonColor(
                cancelButtonView,
                formStyle.lastStepCancelButtonBackgroundColor,
                formStyle.lastStepCancelButtonTextColor,
                formStyle.lastStepCancelButtonPressedBackgroundColor,
                formStyle.lastStepCancelButtonPressedTextColor);

        ViewGroup.LayoutParams layoutParamsCircle = stepNumberCircleView.getLayoutParams();
        layoutParamsCircle.width = formStyle.leftCircleSizeInPx;
        layoutParamsCircle.height = formStyle.leftCircleSizeInPx;
        stepNumberCircleView.setLayoutParams(layoutParamsCircle);

        ViewGroup.LayoutParams layoutParamsLine1 = lineView1.getLayoutParams();
        layoutParamsLine1.width = formStyle.leftVerticalLineThicknessSizeInPx;
        lineView1.setLayoutParams(layoutParamsLine1);

        ViewGroup.LayoutParams layoutParamsLine2 = lineView2.getLayoutParams();
        layoutParamsLine2.width = formStyle.leftVerticalLineThicknessSizeInPx;
        lineView2.setLayoutParams(layoutParamsLine2);

        LinearLayout.LayoutParams titleAndSubtitleContainerLayoutParams =
                (LinearLayout.LayoutParams) titleAndSubtitleContainerView.getLayoutParams();
        titleAndSubtitleContainerLayoutParams.setMarginStart(formStyle.marginFromStepNumbersToContentInPx);
        titleAndSubtitleContainerView.setLayoutParams(titleAndSubtitleContainerLayoutParams);

        LinearLayout.LayoutParams errorContentAndButtonContainerLayoutParams =
                (LinearLayout.LayoutParams) errorContentAndButtonContainerView.getLayoutParams();
        errorContentAndButtonContainerLayoutParams.setMarginStart(formStyle.marginFromStepNumbersToContentInPx);
        errorContentAndButtonContainerView.setLayoutParams(errorContentAndButtonContainerLayoutParams);

        stepNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, formStyle.leftCircleTextSizeInPx);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, formStyle.stepTitleTextSizeInPx);
        subtitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, formStyle.stepSubtitleTextSizeInPx);
        errorMessageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, formStyle.stepErrorMessageTextSizeInPx);

        headerView.setOnClickListener(view -> {
            if (formStyle.allowStepOpeningOnHeaderClick) {
                form.goToStep(form.getStepPosition(step), true);
            }
        });
        nextButtonView.setOnClickListener(view -> form.goToStep(form.getStepPosition(step) + 1, true));
        cancelButtonView.setOnClickListener(view -> form.cancelForm());

        int position = form.getStepPosition(step);
        boolean isLast = (position + 1) == form.getTotalNumberOfSteps();

        String title = !isConfirmationStep()
                ? step.getTitle()
                : formStyle.confirmationStepTitle;
        String subtitle = !isConfirmationStep()
                ? step.getSubtitle()
                : formStyle.confirmationStepSubtitle;
        String stepNextButtonText = !step.getNextButtonText().isEmpty()
                ? step.getNextButtonText()
                : isLast ? formStyle.lastStepNextButtonText : formStyle.stepNextButtonText;

        stepNumberTextView.setText(String.valueOf(position + 1));
        step.updateTitle(title, false);
        step.updateSubtitle(subtitle, false);
        step.updateNextButtonText(stepNextButtonText, false);

        if (formStyle.displayCancelButtonInLastStep && isLast) {
            String cancelButtonText = formStyle.lastStepCancelButtonText == null
                    ? "" : formStyle.lastStepCancelButtonText;
            cancelButtonView.setText(cancelButtonText);
            cancelButtonView.setVisibility(View.VISIBLE);
        }

        if (!formStyle.displayNextButtonInLastStep && isLast) {
            nextButtonView.setVisibility(View.GONE);
        }

        if (!formStyle.displayStepButtons && !isConfirmationStep()) {
            nextButtonView.setVisibility(View.GONE);
        }

        if (isLast) {
            lineView1.setVisibility(View.GONE);
            lineView2.setVisibility(View.GONE);
        }

        onUpdatedStepCompletionState(position, false);
        onUpdatedStepVisibility(position, false);
    }

    void updateStepViewsAfterPositionChange(VerticalStepperFormView form) {
        int position = form.getStepPosition(step);
        boolean isLast = (position + 1) == form.getTotalNumberOfSteps();

        stepNumberTextView.setText(String.valueOf(position + 1));

        String stepNextButtonText = !step.getOriginalNextButtonText().isEmpty()
                ? step.getOriginalNextButtonText()
                : isLast ? formStyle.lastStepNextButtonText : formStyle.stepNextButtonText;
        step.updateNextButtonText(stepNextButtonText, false);

        if (formStyle.displayCancelButtonInLastStep && isLast) {
            String cancelButtonText = formStyle.lastStepCancelButtonText == null
                    ? "" : formStyle.lastStepCancelButtonText;
            cancelButtonView.setText(cancelButtonText);
            cancelButtonView.setVisibility(View.VISIBLE);
        } else {
            cancelButtonView.setVisibility(View.GONE);
        }

        if (formStyle.displayNextButtonInLastStep && isLast) {
            String nextButtonText = formStyle.lastStepNextButtonText == null
                    ? "" : formStyle.lastStepNextButtonText;
            nextButtonView.setText(nextButtonText);
            nextButtonView.setVisibility(View.VISIBLE);
        } else {
            nextButtonView.setVisibility(View.GONE);
        }

        lineView1.setVisibility(isLast ? View.GONE : View.VISIBLE);
        lineView2.setVisibility(isLast ? View.GONE : View.VISIBLE);

        onUpdatedStepCompletionState(position, false);
        onUpdatedStepVisibility(position, false);
    }

    public Step<?> getStepInstance() {
        return step;
    }

    @Override
    public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            updateTitleTextViewValue();
        }
    }

    @Override
    public void onUpdatedSubtitle(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (updateSubtitleTextViewValue()) {
                updateSubtitleVisibility(useAnimations);
            }
        }
    }

    @Override
    public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            updateButtonTextValue();
        }
    }

    @Override
    public void onUpdatedErrorMessage(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (updateErrorMessageTextViewValue()) {
                updateErrorMessageVisibility(useAnimations);
            }
        }
    }

    @Override
    public void onUpdatedStepVisibility(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (step.isOpen()) {
                UIHelper.slideDownIfNecessary(stepAndButtonView, useAnimations);

                // As soon as the step opens, we update its completion state
                boolean wasCompleted = step.isCompleted();
                boolean isCompleted = step.markAsCompletedOrUncompleted(useAnimations);
                if (isCompleted == wasCompleted) {
                    updateHeader(useAnimations);
                }
            } else {
                UIHelper.slideUpIfNecessary(stepAndButtonView, useAnimations);
                updateHeader(useAnimations);
            }
        }
    }

    @Override
    public void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations) {
        if (step.getEntireStepLayout() != null) {
            if (step.isCompleted()) {
                enableNextButton();
            } else {
                disableNextButton();
            }
            updateHeader(useAnimations);
        }
    }

    private void updateHeader(boolean useAnimations) {

        // Update alpha of header elements
        boolean enableHeader = step.isOpen() || step.isCompleted();
        float alpha = enableHeader ? 1f : formStyle.alphaOfDisabledElements;
        float subtitleAlpha = enableHeader ? 1f : 0f;
        titleView.setAlpha(alpha);
        subtitleView.setAlpha(subtitleAlpha);
        stepNumberCircleView.setAlpha(alpha);

        // Update background color of left circle
        int stepNumberBackgroundColor = !step.hasError()
                ? enableHeader
                    ? formStyle.stepNumberCompletedBackgroundColor
                    : formStyle.stepNumberBackgroundColor
                : formStyle.stepNumberErrorBackgroundColor;
        if (formStyle.displayDifferentBackgroundColorOnDisabledElements && !enableHeader) {
            stepNumberBackgroundColor = formStyle.backgroundColorOfDisabledElements;
        }

        Drawable circleDrawable = ContextCompat.getDrawable(stepNumberCircleView.getContext(),
                R.drawable.circle_step_done);
        circleDrawable.setColorFilter(
                new PorterDuffColorFilter(stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        stepNumberCircleView.setBackground(circleDrawable);

        // Update step position circle indicator layout
        if (step.isOpen() || !step.isCompleted()) {
            showStepNumberAndHideDoneIcon();
        } else {
            showDoneIconAndHideStepNumber();
        }

        updateSubtitleTextViewValue();
        updateSubtitleVisibility(useAnimations);
        updateErrorMessageVisibility(useAnimations);
    }

    private void showDoneIconAndHideStepNumber() {
        doneIconView.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.GONE);
    }

    private void showStepNumberAndHideDoneIcon() {
        doneIconView.setVisibility(View.GONE);
        stepNumberTextView.setVisibility(View.VISIBLE);
    }

    void enableNextButton() {
        nextButtonView.setEnabled(true);
        nextButtonView.setAlpha(1f);

        if (formStyle.displayDifferentBackgroundColorOnDisabledElements) {
            UIHelper.setButtonColor(
                    nextButtonView,
                    formStyle.nextButtonBackgroundColor,
                    formStyle.nextButtonTextColor,
                    formStyle.nextButtonPressedBackgroundColor,
                    formStyle.nextButtonPressedTextColor);
        }
    }

    void disableNextButton() {
        nextButtonView.setEnabled(false);
        nextButtonView.setAlpha(formStyle.alphaOfDisabledElements);

        if (formStyle.displayDifferentBackgroundColorOnDisabledElements) {
            UIHelper.setButtonColor(
                    nextButtonView,
                    formStyle.backgroundColorOfDisabledElements,
                    formStyle.nextButtonTextColor,
                    formStyle.backgroundColorOfDisabledElements,
                    formStyle.nextButtonPressedTextColor);
        }
    }

    void enableCancelButton() {
        cancelButtonView.setEnabled(true);
        cancelButtonView.setAlpha(1f);

        if (formStyle.displayDifferentBackgroundColorOnDisabledElements) {
            UIHelper.setButtonColor(
                    cancelButtonView,
                    formStyle.lastStepCancelButtonBackgroundColor,
                    formStyle.lastStepCancelButtonTextColor,
                    formStyle.lastStepCancelButtonPressedBackgroundColor,
                    formStyle.lastStepCancelButtonPressedTextColor);
        }
    }

    void disableCancelButton() {
        cancelButtonView.setEnabled(false);
        cancelButtonView.setAlpha(formStyle.alphaOfDisabledElements);

        if (formStyle.displayDifferentBackgroundColorOnDisabledElements) {
            UIHelper.setButtonColor(
                    cancelButtonView,
                    formStyle.backgroundColorOfDisabledElements,
                    formStyle.lastStepCancelButtonTextColor,
                    formStyle.backgroundColorOfDisabledElements,
                    formStyle.lastStepCancelButtonPressedTextColor);
        }
    }

    void enableAllButtons() {
        if (step.isCompleted()) {
            enableNextButton();
        }
        enableCancelButton();
    }

    void disableAllButtons() {
        disableNextButton();
        disableCancelButton();
    }

    private boolean updateTitleTextViewValue() {
        CharSequence previousValue = titleView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String title = step.getTitle();
        if (!title.equals(previousValueAsString)) {
            titleView.setText(title);
            return true;
        }

        return false;
    }

    private boolean updateSubtitleTextViewValue() {
        CharSequence previousValue = subtitleView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String subtitle = getActualSubtitleText();
        if (!subtitle.equals(previousValueAsString)) {
            if (!subtitle.isEmpty()) {
                // We don't update the text view if the subtitle is empty; instead, we leave the last
                // non-empty subtitle so the text view has text and can be seen while animating to hide
                subtitleView.setText(subtitle);
            }

            return true;
        }

        return false;
    }

    private boolean updateButtonTextValue() {
        CharSequence previousValue = nextButtonView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String buttonText = step.getNextButtonText();
        if (!buttonText.equals(previousValueAsString)) {
            nextButtonView.setText(buttonText);
            return true;
        }

        return false;
    }

    private boolean updateErrorMessageTextViewValue() {
        CharSequence previousValue = errorMessageView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String errorMessage = step.getErrorMessage();
        if (!errorMessage.equals(previousValueAsString)) {
            if (!errorMessage.isEmpty()) {
                // We don't update the text view if the error message is empty; instead, we leave the last
                // non-empty error message so the text view has text and can be seen while animating to hide
                errorMessageView.setText(errorMessage);
            }

            return true;
        }

        return false;
    }

    private void updateSubtitleVisibility(boolean useAnimations) {
        boolean showSubtitle = !getActualSubtitleText().isEmpty()
                && (step.isOpen() || step.isCompleted());
        if (showSubtitle) {
            UIHelper.slideDownIfNecessary(subtitleView, useAnimations);
        } else {
            UIHelper.slideUpIfNecessary(subtitleView, useAnimations);
        }
    }

    private void updateErrorMessageVisibility(boolean useAnimations) {
        if (step.isOpen() && !step.isCompleted() && !step.getErrorMessage().isEmpty()) {
            UIHelper.slideDownIfNecessary(errorMessageContainerView, useAnimations);
        } else {
            UIHelper.slideUpIfNecessary(errorMessageContainerView, useAnimations);
        }
    }

    private String getActualSubtitleText() {
        String subtitle = formStyle.displayStepDataInSubtitleOfClosedSteps && !step.isOpen()
                ? step.getStepDataAsHumanReadableString()
                : step.getSubtitle();
        subtitle = subtitle == null ? "" : subtitle;

        return subtitle;
    }

    boolean isConfirmationStep() {
        return step instanceof ConfirmationStep;
    }

    /**
     * This step will just display a button that the user will have to click to complete the form.
     */
    private class ConfirmationStep extends Step<Object> {

        ConfirmationStep() {
            super("");
        }

        @Override
        public Object getStepData() {
            return null;
        }

        @Override
        public String getStepDataAsHumanReadableString() {
            return getSubtitle();
        }

        @Override
        public void restoreStepData(Object data) {
            // No need to do anything here
        }

        @Override
        protected IsDataValid isStepDataValid(Object stepData) {
            return null;
        }

        @Override
        protected View createStepContentLayout() {
            return null;
        }

        @Override
        protected void onStepOpened(boolean animated) {
            // No need to do anything here
        }

        @Override
        protected void onStepClosed(boolean animated) {
            if (!getFormView().isFormCompleted()) {
                markAsUncompleted("", animated);
            }
        }

        @Override
        protected void onStepMarkedAsCompleted(boolean animated) {
            // No need to do anything here
        }

        @Override
        protected void onStepMarkedAsUncompleted(boolean animated) {
            // No need to do anything here
        }
    }
}
