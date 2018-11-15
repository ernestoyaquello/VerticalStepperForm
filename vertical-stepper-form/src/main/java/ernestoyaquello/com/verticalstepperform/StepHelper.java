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

/**
 * This class holds a step instance and deals with updating its views so they reflect its state.
 * It can also handle the logic of the optional confirmation step.
 *
 * All this logic could certainly be in the base class of the step, but by keeping it here we make
 * that class cleaner, helping anyone taking a look at it to understand it better.
 */
class StepHelper implements Step.InternalFormStepListener {

    private Step step;
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
    private View lineView1;
    private View lineView2;
    private View stepAndButtonView;
    private View errorMessageContainerView;

    StepHelper(Step.InternalFormStepListener formListener, @NonNull Step step) {
        this(formListener, step, false);
    }

    StepHelper(Step.InternalFormStepListener formListener, Step step, boolean isConfirmationStep) {
        this.step = !isConfirmationStep ? step : new ConfirmationStep();
        this.step.addListenerInternal(formListener);
        this.step.addListenerInternal(this);
    }

    View initialize(
            VerticalStepperFormLayout form,
            FormStyle style,
            ViewGroup parent,
            int position,
            boolean isLast) {

        if (step.getEntireStepLayout() == null) {
            formStyle = style;

            Context context = form.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View stepLayout = inflater.inflate(R.layout.step_layout, parent, false);

            step.initializeStepInternal(stepLayout, form, position);
            step.setContentLayoutInternal(step.createStepContentLayout());

            setupStepViews(form, context, stepLayout, position, isLast);
        } else {
            throw new IllegalStateException("This step has already been initialized");
        }

        return step.getEntireStepLayout();
    }

    private void setupStepViews(
            final VerticalStepperFormLayout form,
            Context context,
            View stepLayout,
            final int position,
            boolean isLast) {

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
        lineView1 = stepLayout.findViewById(R.id.line1);
        lineView2 = stepLayout.findViewById(R.id.line2);
        stepAndButtonView = step.getEntireStepLayout().findViewById(R.id.step_content_and_button);
        errorMessageContainerView = step.getEntireStepLayout().findViewById(R.id.step_error_container);

        titleView.setTextColor(formStyle.stepTitleTextColor);
        subtitleView.setTextColor(formStyle.stepSubtitleTextColor);
        stepNumberTextView.setTextColor(formStyle.stepNumberTextColor);
        doneIconView.setColorFilter(formStyle.stepNumberTextColor);
        errorMessageView.setTextColor(formStyle.errorMessageTextColor);
        errorIconView.setColorFilter(formStyle.errorMessageTextColor);
        Drawable circleDrawable = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        circleDrawable.setColorFilter(new PorterDuffColorFilter(formStyle.stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        stepNumberCircleView.setBackground(circleDrawable);
        UIHelpers.setButtonColor(
                nextButtonView,
                formStyle.buttonBackgroundColor,
                formStyle.buttonTextColor,
                formStyle.buttonPressedBackgroundColor,
                formStyle.buttonPressedTextColor);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.goToStep(position, true);
            }
        });
        nextButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.goToStep(position + 1, true);
            }
        });

        String title = !isConfirmationStep()
                ? step.getTitle()
                : formStyle.confirmationStepTitle;
        String subtitle = step.getSubtitle();
        String stepButtonText = !step.getButtonText().isEmpty()
                ? step.getButtonText()
                : isLast ? formStyle.lastStepButtonText : formStyle.stepButtonText;

        stepNumberTextView.setText(String.valueOf(position + 1));
        step.updateTitle(title, false);
        step.updateSubtitle(subtitle, false);
        step.updateButtonText(stepButtonText, false);

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

    public Step getStepInstance() {
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
                Animations.slideDownIfNecessary(stepAndButtonView, useAnimations);
            } else {
                Animations.slideUpIfNecessary(stepAndButtonView, useAnimations);
            }

            if (step.isOpen()) {
                // As soon as the step opens, we update its completion state
                boolean wasCompleted = step.isCompleted();
                boolean isCompleted = step.markAsCompletedOrUncompleted(useAnimations);
                if (isCompleted == wasCompleted) {
                    updateHeaderAppearance(useAnimations);
                }
            } else {
                updateHeaderAppearance(useAnimations);
            }

            // Update the text to show in the subtitle view if necessary
            String textToShowOnSubtitleView = formStyle.displayStepDataInSubtitleOfClosedSteps && !step.isOpen()
                    ? step.getStepDataAsHumanReadableString()
                    : step.getSubtitle();
            textToShowOnSubtitleView = textToShowOnSubtitleView == null ? "" : textToShowOnSubtitleView;
            subtitleView.setText(textToShowOnSubtitleView);
            updateSubtitleVisibility(useAnimations);
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
            updateHeaderAppearance(useAnimations);
            updateSubtitleVisibility(useAnimations);
        }
    }

    private void updateHeaderAppearance(boolean useAnimations) {

        // Update alpha of header elements
        boolean enableHeader = step.isOpen() || step.isCompleted();
        float alpha = enableHeader ? 1f : formStyle.alphaOfDisabledElements;
        float subtitleAlpha = enableHeader ? 1f : 0f;
        titleView.setAlpha(alpha);
        subtitleView.setAlpha(subtitleAlpha);
        stepNumberCircleView.setAlpha(alpha);

        // Update step position circle indicator layout
        if (step.isOpen() || !step.isCompleted()) {
            showStepNumberAndHideDoneIcon();
        } else {
            showDoneIconAndHideStepNumber();
        }
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
    }

    void disableNextButton() {
        nextButtonView.setEnabled(false);
        nextButtonView.setAlpha(formStyle.alphaOfDisabledElements);
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

        String subtitle = step.getSubtitle();
        subtitle = subtitle == null ? "" : subtitle;
        if (!subtitle.equals(previousValueAsString)) {
            subtitleView.setText(subtitle);
            return true;
        }

        return false;
    }

    private boolean updateButtonTextValue() {
        CharSequence previousValue = nextButtonView.getText();
        String previousValueAsString = previousValue == null ? "" : previousValue.toString();

        String buttonText = step.getButtonText();
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
            errorMessageView.setText(errorMessage);
            return true;
        }

        return false;
    }

    private void updateSubtitleVisibility(boolean useAnimations) {
        boolean showSubtitle = !subtitleView.getText().toString().isEmpty()
                && (step.isOpen() || step.isCompleted());
        if (showSubtitle) {
            Animations.slideDownIfNecessary(subtitleView, useAnimations);
        } else {
            Animations.slideUpIfNecessary(subtitleView, useAnimations);
        }
    }

    private void updateErrorMessageVisibility(boolean useAnimations) {
        if (step.isOpen()
                && !step.isCompleted()
                && step.getErrorMessage() != null
                && !errorMessageView.getText().toString().isEmpty()) {
            Animations.slideDownIfNecessary(errorMessageContainerView, useAnimations);
        } else {
            Animations.slideUpIfNecessary(errorMessageContainerView, useAnimations);
        }
    }

    private boolean isConfirmationStep() {
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
            return null;
        }

        @Override
        public void restoreStepData(Object data) {
            // No need to do anything here
        }

        @Override
        protected IsDataValid isStepDataValid(Object stepData) {
            return null;
        }

        @NonNull
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
            // No need to do anything here
        }
    }
}
