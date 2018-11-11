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

import androidx.core.content.ContextCompat;
import ernestoyaquello.com.verticalstepperform.util.Animations;
import ernestoyaquello.com.verticalstepperform.util.UIHelpers;
import ernestoyaquello.com.verticalstepperform.util.model.Step;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout.FormStyle;

class ExtendedStep extends Step {

    private boolean isCompleted;
    private boolean isOpen;

    private View stepLayout;

    private FormStyle formStyle;

    ExtendedStep(Step step) {
        this(step.getTitle(), step.getSubtitle(), step.getButtonText());
    }

    ExtendedStep(String title, String subtitle) {
        this(title, subtitle, null);
    }

    ExtendedStep(String title, String subtitle, String buttonText) {
        this(title, subtitle, buttonText, false);
    }

    ExtendedStep(String title, String subtitle, String buttonText, boolean isCompleted) {
        super(title, subtitle, buttonText);

        this.isCompleted = isCompleted;
    }

    View initialize(
            FormStyle style,
            Context context,
            ViewGroup parent,
            View contentLayout,
            View.OnClickListener clickOnNextButton,
            View.OnClickListener clickOnHeader,
            int position,
            boolean isLast) {

        if (stepLayout == null) {
            this.formStyle = style;
            this.stepLayout = createAndSetupStepLayout(
                    context,
                    parent,
                    position,
                    clickOnNextButton,
                    clickOnHeader);

            init(style, isLast, contentLayout);
        } else {
            throw new IllegalStateException("This step has already been initialized");
        }

        return stepLayout;
    }

    private void init(FormStyle style, boolean isLast, View contentLayout) {

        // Update button view
        MaterialButton button = stepLayout.findViewById(R.id.step_button);
        if (style.displayStepButtons) {
            String stepButtonText = getButtonText() != null && !getButtonText().isEmpty()
                    ? getButtonText()
                    : isLast ? style.lastStepButtonText : style.stepButtonText;
            button.setText(stepButtonText);
        } else {
            button.setVisibility(View.GONE);
        }

        // Update line view
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

    private View createAndSetupStepLayout(
            Context context,
            ViewGroup parent,
            int position,
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

        stepTitle.setText(getTitle());
        stepSubtitle.setText(getSubtitle());
        stepNumberTextView.setText(String.valueOf(position + 1));

        stepHeader.setOnClickListener(clickOnHeader);
        stepNextButton.setOnClickListener(clickOnNextButton);

        return stepLayout;
    }

    private View createStepLayout(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.step_layout, parent, false);
    }

    public void setContentLayout(View contentLayout) {
        ViewGroup contentContainerLayout = stepLayout.findViewById(R.id.step_content);
        if (contentContainerLayout != null && contentLayout != null) {
            contentContainerLayout.removeAllViews();
            contentContainerLayout.addView(contentLayout);
        }
    }

    void markAsCompleted(boolean useAnimations) {
        updateStepCompletion(true, null, useAnimations);
    }

    void markAsUncompleted(String errorMessage, boolean useAnimations) {
        updateStepCompletion(false, errorMessage, useAnimations);
    }

    private void updateStepCompletion(boolean completed, String errorMessage, boolean useAnimations) {
        isCompleted = completed;

        TextView errorTextView = stepLayout.findViewById(R.id.step_error_message);
        View nextButton = stepLayout.findViewById(R.id.step_button);

        errorMessage = errorMessage != null ? errorMessage : "";
        errorTextView.setText(errorMessage);

        if (isCompleted) {
            enableNextButton();
        } else {
            disableNextButton();
        }

        updateHeaderAppearance(useAnimations);
    }

    void open(boolean useAnimations) {
        toggleStep(true, useAnimations);
    }

    void close(boolean useAnimations) {
        toggleStep(false, useAnimations);
    }

    private void toggleStep(boolean open, boolean useAnimations) {
        isOpen = open;

        View stepAndButton = stepLayout.findViewById(R.id.step_content_and_button);
        if (isOpen) {
            Animations.slideDownIfNecessary(stepAndButton, useAnimations);
        } else {
            Animations.slideUpIfNecessary(stepAndButton, useAnimations);
        }

        updateHeaderAppearance(useAnimations);
    }

    private void updateHeaderAppearance(boolean useAnimations) {
        TextView title = stepLayout.findViewById(R.id.step_title);
        TextView subtitle = stepLayout.findViewById(R.id.step_subtitle);
        TextView errorMessage = stepLayout.findViewById(R.id.step_error_message);
        View stepPosition = stepLayout.findViewById(R.id.step_number_circle);
        View spacingView = stepLayout.findViewById(R.id.spacing_to_show_left_line);
        View errorMessageContainer = stepLayout.findViewById(R.id.step_error_container);

        // Update alpha of header elements
        boolean enableHeader = isOpen || isCompleted;
        float alpha = enableHeader ? 1f : formStyle.alphaOfDisabledElements;
        float subtitleAlpha = enableHeader ? 1f : 0f;
        title.setAlpha(alpha);
        subtitle.setAlpha(subtitleAlpha);
        stepPosition.setAlpha(alpha);

        // Update subtitle visibility
        boolean showSubtitle = !getSubtitle().isEmpty() && (isOpen || isCompleted);
        if (showSubtitle) {
            Animations.slideDownIfNecessary(subtitle, useAnimations);
        } else {
            Animations.slideUpIfNecessary(subtitle, useAnimations);
        }

        // Update spacing view visibility to show/hide the left lines between collapsed steps
        boolean showLineBetweenCollapsedSteps = !showSubtitle && formStyle.showVerticalLineWhenStepsAreCollapsed && !isOpen;
        if (showLineBetweenCollapsedSteps) {
            Animations.slideDownIfNecessary(spacingView, useAnimations);
        } else {
            Animations.slideUpIfNecessary(spacingView, useAnimations);
        }

        // Update error message view visibility
        if (isOpen && !isCompleted && !errorMessage.getText().toString().isEmpty()) {
            Animations.slideDownIfNecessary(errorMessageContainer, useAnimations);
        } else {
            Animations.slideUpIfNecessary(errorMessageContainer, useAnimations);
        }

        // Update step position circle indicator layout
        if (isOpen || !isCompleted) {
            showStepNumberAndHideDoneIcon();
        } else {
            showDoneIconAndHideStepNumber();
        }
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

    boolean isCompleted() {
        return isCompleted;
    }

    boolean isOpen() {
        return isOpen;
    }

    View getStepLayout() {
        return stepLayout;
    }
}
