package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout.FormStyle;

class StepWrapper implements FormStep.InternalFormStepListener {

    private FormStep step;
    private boolean isCompleted;
    private boolean isOpen;
    private boolean isConfirmationStep;

    private View stepLayout;
    private View contentLayout;

    private FormStyle formStyle;

    StepWrapper(@NonNull FormStep step) {
        this(step, false);
    }

    StepWrapper(FormStep step, boolean isConfirmationStep) {
        this.step = !isConfirmationStep ? step : new EmptyFormStep();
        this.isConfirmationStep = isConfirmationStep;
        this.step.internalListener = this;
    }

    View initialize(
            VerticalStepperFormLayout form,
            FormStyle style,
            Context context,
            ViewGroup parent,
            View.OnClickListener clickOnNextButton,
            View.OnClickListener clickOnHeader,
            int position,
            boolean isLast) {

        if (stepLayout == null) {
            this.formStyle = style;
            this.stepLayout = createStepLayout(context, parent, clickOnNextButton, clickOnHeader);
            this.contentLayout = step.getStepContentLayout(context, form, position);

            finishInitialization(position, isLast);
        } else {
            throw new IllegalStateException("This step has already been initialized");
        }

        return stepLayout;
    }

    private void finishInitialization(int position, boolean isLast) {

        // Setup step number text
        TextView stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        stepNumberTextView.setText(String.valueOf(position + 1));

        // Setup title and subtitle
        String title = !isConfirmationStep ? step.getTitle() : formStyle.confirmationStepTitle;
        String subtitle = step.getSubtitle() != null ? step.getSubtitle() : "";
        step.updateTitle(position, title, false);
        step.updateSubtitle(position, subtitle, false);

        // Setup button view
        MaterialButton button = stepLayout.findViewById(R.id.step_button);
        if (formStyle.displayStepButtons || isConfirmationStep) {
            String stepButtonText = step.getButtonText() != null && !step.getButtonText().isEmpty()
                    ? step.getButtonText()
                    : isLast ? formStyle.lastStepButtonText : formStyle.stepButtonText;
            step.updateButtonText(position, stepButtonText, false);
        } else {
            button.setVisibility(View.GONE);
        }

        // Setup line view
        if (isLast) {
            View lineView1 = stepLayout.findViewById(R.id.line1);
            View lineView2 = stepLayout.findViewById(R.id.line2);
            lineView1.setVisibility(View.GONE);
            lineView2.setVisibility(View.GONE);
        }

        // Set the step state
        updateStepCompletion(isCompleted, null, false);
        toggleStep(isOpen, false);

        // Set the content, if any
        if (contentLayout != null) {
            ViewGroup contentContainerLayout = stepLayout.findViewById(R.id.step_content);
            contentContainerLayout.addView(contentLayout);
        }
    }

    private View createStepLayout(
            Context context,
            ViewGroup parent,
            View.OnClickListener clickOnNextButton,
            View.OnClickListener clickOnHeader) {

        View stepLayout = createStepLayout(context, parent);

        View stepPositionCircle = stepLayout.findViewById(R.id.step_number_circle);
        TextView stepTitle = stepLayout.findViewById(R.id.step_title);
        TextView stepSubtitle = stepLayout.findViewById(R.id.step_subtitle);
        TextView stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        ImageView stepDoneImageView = stepLayout.findViewById(R.id.step_done_icon);
        TextView stepErrorMessage = stepLayout.findViewById(R.id.step_error_message);
        ImageView stepErrorIcon = stepLayout.findViewById(R.id.step_error_icon);
        View stepHeader = stepLayout.findViewById(R.id.step_header);
        MaterialButton stepNextButton = stepLayout.findViewById(R.id.step_button);

        Drawable circleDrawable = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        circleDrawable.setColorFilter(new PorterDuffColorFilter(formStyle.stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        stepPositionCircle.setBackground(circleDrawable);

        stepTitle.setTextColor(formStyle.stepTitleTextColor);
        stepSubtitle.setTextColor(formStyle.stepSubtitleTextColor);
        stepNumberTextView.setTextColor(formStyle.stepNumberTextColor);
        stepDoneImageView.setColorFilter(formStyle.stepNumberTextColor);
        stepErrorMessage.setTextColor(formStyle.errorMessageTextColor);
        stepErrorIcon.setColorFilter(formStyle.errorMessageTextColor);

        UIHelpers.setButtonColor(
                stepNextButton,
                formStyle.buttonBackgroundColor,
                formStyle.buttonTextColor,
                formStyle.buttonPressedBackgroundColor,
                formStyle.buttonPressedTextColor);

        stepHeader.setOnClickListener(clickOnHeader);
        stepNextButton.setOnClickListener(clickOnNextButton);

        return stepLayout;
    }

    private View createStepLayout(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(VerticalStepperFormLayout.getInternalStepLayout(), parent, false);
    }

    void markAsCompleted(boolean useAnimations) {
        updateStepCompletion(true, null, useAnimations);
    }

    void markAsUncompleted(String errorMessage, boolean useAnimations) {
        updateStepCompletion(false, errorMessage, useAnimations);
    }

    private void updateStepCompletion(
            boolean completed,
            String errorMessage,
            boolean useAnimations) {

        isCompleted = completed;

        TextView errorTextView = stepLayout.findViewById(R.id.step_error_message);
        errorMessage = errorMessage != null ? errorMessage : "";
        errorTextView.setText(errorMessage);

        if (isCompleted) {
            enableNextButton();
        } else {
            disableNextButton();
        }

        updateHeaderAppearance(useAnimations);
    }

    void open(VerticalStepperFormLayout form, int position, boolean useAnimations) {
        toggleStep(true, useAnimations);

        if (!isConfirmationStep) {
            step.onStepOpenedImpl(form, position,useAnimations);
        }
    }

    void close(VerticalStepperFormLayout form, int position, boolean useAnimations) {
        toggleStep(false, useAnimations);

        if (!isConfirmationStep) {
            step.onStepClosed(form, position, useAnimations);
        }
    }

    private void toggleStep(boolean open, boolean useAnimations) {
        isOpen = open;

        View stepAndButton = stepLayout.findViewById(R.id.step_content_and_button);
        if (isOpen) {
            Animations.slideDownIfNecessary(stepAndButton, useAnimations);
        } else {
            Animations.slideUpIfNecessary(stepAndButton, useAnimations);
        }

        if (isOpen && isConfirmationStep && !isCompleted) {
            // If the user has been able to open the confirmation step, then the form is completed,
            // so we just mark the confirmation step as completed immediately to activate its button
            markAsCompleted(useAnimations);
        } else {
            updateHeaderAppearance(useAnimations);
        }
    }

    private void updateHeaderAppearance(boolean useAnimations) {
        TextView title = stepLayout.findViewById(R.id.step_title);
        TextView subtitle = stepLayout.findViewById(R.id.step_subtitle);
        View stepPosition = stepLayout.findViewById(R.id.step_number_circle);

        // Update alpha of header elements
        boolean enableHeader = isOpen || isCompleted;
        float alpha = enableHeader ? 1f : formStyle.alphaOfDisabledElements;
        float subtitleAlpha = enableHeader ? 1f : 0f;
        title.setAlpha(alpha);
        subtitle.setAlpha(subtitleAlpha);
        stepPosition.setAlpha(alpha);

        updateSubtitleAndSpacingVisibility(useAnimations);
        updateErrorMessageVisibility(useAnimations);

        // Update step position circle indicator layout
        if (isOpen || !isCompleted) {
            showStepNumberAndHideDoneIcon();
        } else {
            showDoneIconAndHideStepNumber();
        }
    }

    String getCurrentErrorMessage() {
        if (stepLayout != null) {
            TextView errorMessage = stepLayout.findViewById(R.id.step_error_message);
            if (errorMessage != null) {
                return errorMessage.getText().toString();
            }
        }

        return "";
    }

    private void showDoneIconAndHideStepNumber() {
        View stepDone = stepLayout.findViewById(R.id.step_done_icon);
        View stepNumberTextView = stepLayout.findViewById(R.id.step_number);

        stepDone.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.GONE);
    }

    private void showStepNumberAndHideDoneIcon() {
        View stepDone = stepLayout.findViewById(R.id.step_done_icon);
        View stepNumberTextView = stepLayout.findViewById(R.id.step_number);

        stepDone.setVisibility(View.GONE);
        stepNumberTextView.setVisibility(View.VISIBLE);
    }

    void enableNextButton() {
        View nextButton = stepLayout.findViewById(R.id.step_button);

        nextButton.setEnabled(true);
        nextButton.setAlpha(1f);
    }

    void disableNextButton() {
        View nextButton = stepLayout.findViewById(R.id.step_button);

        nextButton.setEnabled(false);
        nextButton.setAlpha(formStyle.alphaOfDisabledElements);
    }

    private void updateSubtitleAndSpacingVisibility(boolean useAnimations) {
        boolean showSubtitle = updateSubtitleVisibility(useAnimations);
        updateSpacingViewVisibility(useAnimations, showSubtitle);
    }

    private boolean updateSubtitleVisibility(boolean useAnimations) {
        TextView subtitle = stepLayout.findViewById(R.id.step_subtitle);

        boolean showSubtitle = step.getSubtitle() != null
                && !step.getSubtitle().isEmpty()
                && (isOpen || isCompleted);
        if (showSubtitle) {
            Animations.slideDownIfNecessary(subtitle, useAnimations);
        } else {
            Animations.slideUpIfNecessary(subtitle, useAnimations);
        }

        return showSubtitle;
    }

    private void updateSpacingViewVisibility(boolean useAnimations, boolean showSubtitle) {
        View spacingView = stepLayout.findViewById(R.id.spacing_to_show_left_line);

        boolean showLineBetweenCollapsedSteps =
                !showSubtitle && formStyle.displayVerticalLineWhenStepsAreCollapsed && !isOpen;
        if (showLineBetweenCollapsedSteps) {
            Animations.slideDownIfNecessary(spacingView, useAnimations);
        } else {
            Animations.slideUpIfNecessary(spacingView, useAnimations);
        }
    }

    private void updateErrorMessageVisibility(boolean useAnimations) {
        View errorMessageContainer = stepLayout.findViewById(R.id.step_error_container);

        if (isOpen && !isCompleted && !getCurrentErrorMessage().isEmpty()) {
            Animations.slideDownIfNecessary(errorMessageContainer, useAnimations);
        } else {
            Animations.slideUpIfNecessary(errorMessageContainer, useAnimations);
        }
    }

    boolean isCompleted() {
        return isCompleted;
    }

    boolean isOpen() {
        return isOpen;
    }

    boolean isConfirmationStep() {
        return isConfirmationStep;
    }

    View getStepLayout() {
        return stepLayout;
    }

    View getContentLayout() {
        return contentLayout;
    }

    String getTitle() {
        return step.getTitle();
    }

    String getSubtitle() {
        return step.getSubtitle();
    }

    String getButtonText() {
        return step.getButtonText();
    }

    void restoreTitle(int stepPosition, String title) {
        step.updateTitle(stepPosition, title, false);
    }

    void restoreSubtitle(int stepPosition, String subtitle) {
        step.updateSubtitle(stepPosition, subtitle, false);
    }

    void restoreButtonText(int stepPosition, String buttonText) {
        step.updateButtonText(stepPosition, buttonText, false);
    }

    @Override
    public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
        String title = step.getTitle();

        if (stepLayout != null) {
            TextView subtitleView = stepLayout.findViewById(R.id.step_title);
            subtitleView.setText(title);
        }
    }

    @Override
    public void onUpdatedSubtitle(int stepPosition, boolean useAnimations) {
        String subtitle = step.getSubtitle();
        subtitle = subtitle == null ? "" : subtitle;

        if (stepLayout != null) {
            TextView subtitleView = stepLayout.findViewById(R.id.step_subtitle);
            subtitleView.setText(subtitle);

            updateSubtitleAndSpacingVisibility(useAnimations);
        }
    }

    @Override
    public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
        String buttonText = step.getButtonText();

        if (stepLayout != null) {
            MaterialButton subtitleView = stepLayout.findViewById(R.id.step_button);
            subtitleView.setText(buttonText);
        }
    }

    // Useless, empty implementation of FormStep to be used for the confirmation step
    private class EmptyFormStep extends FormStep<Object> {

        EmptyFormStep() {
            super("");
        }

        @Override
        public Object getStepData() {
            return null;
        }

        @Override
        public void restoreStepData(Object data) {
            // Do nothing
        }

        @Override
        protected IsDataValid isStepDataValid(Object stepData) {
            return null;
        }

        @NonNull
        @Override
        protected View getStepContentLayout(Context context, VerticalStepperFormLayout form, int stepPosition) {
            return null;
        }

        @Override
        protected void onStepOpened(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
            // Do nothing
        }

        @Override
        protected void onStepClosed(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
            // Do nothing
        }
    }
}
