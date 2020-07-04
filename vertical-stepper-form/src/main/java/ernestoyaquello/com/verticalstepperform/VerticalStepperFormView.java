package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;

import androidx.annotation.LayoutRes;
import androidx.appcompat.widget.AppCompatImageButton;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

/**
 * Custom layout that implements a vertical stepper form.
 */
public class VerticalStepperFormView extends LinearLayout {

    FormStepListener internalListener;
    FormStyle style;

    private StepperFormListener listener;
    private KeyboardTogglingObserver keyboardTogglingObserver;
    private List<StepHelper> stepHelpers;
    private List<StepHelper> originalStepHelpers;
    private boolean initialized;

    private LinearLayout formContentView;
    private ScrollView stepsScrollView;
    private ProgressBar progressBar;
    private AppCompatImageButton previousStepButton, nextStepButton;
    private View bottomNavigationView;

    private boolean formCompleted;
    private boolean keyboardIsOpen;

    public VerticalStepperFormView(Context context) {
        super(context);

        onConstructed(context, null, 0);
    }

    public VerticalStepperFormView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onConstructed(context, attrs, 0);
    }

    public VerticalStepperFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        onConstructed(context, attrs, defStyleAttr);
    }

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepperFormListener The listener for the stepper form events.
     * @param steps An array with the steps that will be displayed in the form.
     * @return An instance of the stepper form builder. Use it to configure and initialize the form.
     */
    public Builder setup(StepperFormListener stepperFormListener, Step... steps) {
        return new Builder(this, stepperFormListener, steps);
    }

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepperFormListener The listener for the stepper form events.
     * @param steps A list with the steps that will be displayed in the form.
     * @return An instance of the stepper form builder. Use it to configure and initialize the form.
     */
    public Builder setup(StepperFormListener stepperFormListener, List<Step> steps) {
        Step[] stepsArray = steps.toArray(new Step[0]);
        return new Builder(this, stepperFormListener, stepsArray);
    }

    /**
     * Marks the currently open step as completed or uncompleted depending on whether the step data
     * is valid or not.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     * @return True if the step was found and marked as completed; false otherwise.
     */
    public synchronized boolean markOpenStepAsCompletedOrUncompleted(boolean useAnimations) {
        return markStepAsCompletedOrUncompleted(getOpenStepPosition(), useAnimations);
    }

    /**
     * Marks the specified step as completed or uncompleted depending on whether the step data is 
     * valid or not.
     *
     * @param stepPosition The step position.
     * @param useAnimations True to animate the changes in the views, false to not.
     * @return True if the step was found and marked as completed; false otherwise.
     */
    public boolean markStepAsCompletedOrUncompleted(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            return stepHelper.getStepInstance().markAsCompletedOrUncompleted(useAnimations);
        }

        return false;
    }

    /**
     * Marks the currently open step as completed.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public synchronized void markOpenStepAsCompleted(boolean useAnimations) {
        markStepAsCompleted(getOpenStepPosition(), useAnimations);
    }

    /**
     * Marks the specified step as completed.
     *
     * @param stepPosition The step position.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markStepAsCompleted(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            stepHelper.getStepInstance().markAsCompleted(useAnimations);
        }
    }

    /**
     * Marks the currently open step as uncompleted.
     *
     * @param errorMessage The error message.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public synchronized void markOpenStepAsUncompleted(boolean useAnimations, String errorMessage) {
        markStepAsUncompleted(getOpenStepPosition(), errorMessage, useAnimations);
    }

    /**
     * Marks the specified step as uncompleted.
     *
     * @param stepPosition The step position.
     * @param errorMessage The error message.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markStepAsUncompleted(int stepPosition, String errorMessage, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);
            stepHelper.getStepInstance().markAsUncompleted(errorMessage, useAnimations);
        }
    }

    /**
     * Determines whether the open step is marked as completed or not.
     *
     * @return True if the open step is currently marked as completed; false otherwise.
     */
    public synchronized boolean isOpenStepCompleted() {
        return isStepCompleted(getOpenStepPosition());
    }

    /**
     * Determines whether the specified step is marked as completed or not.
     *
     * @param stepPosition The step position.
     * @return True if the step is currently marked as completed; false otherwise.
     */
    public boolean isStepCompleted(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            return stepHelpers.get(stepPosition).getStepInstance().isCompleted();
        }

        return false;
    }

    /**
     * Determines whether there is at least one step marked as completed.
     *
     * @return True if at least one step has been marked as completed; false otherwise.
     */
    public boolean isAnyStepCompleted() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (stepHelpers.get(i).getStepInstance().isCompleted()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether all the steps previous to the specified one are currently marked as completed.
     *
     * @param stepPosition The step position.
     * @return True if all the steps previous to the specified one are marked as completed; false otherwise.
     */
    public boolean areAllPreviousStepsCompleted(int stepPosition) {
        boolean previousStepsAreCompleted = true;
        for (int i = stepPosition - 1; i >= 0; i--) {
            previousStepsAreCompleted &= stepHelpers.get(i).getStepInstance().isCompleted();
        }

        return previousStepsAreCompleted;
    }

    /**
     * Determines whether all the steps are currently marked as completed.
     *
     * @return True if all the steps are marked as completed; false otherwise.
     */
    public boolean areAllStepsCompleted() {
        return areAllPreviousStepsCompleted(stepHelpers.size());
    }

    /**
     * Determines whether the form has already been completed or cancelled.
     * Please note that this could return false even if all the steps are completed (for example,
     * if the user has filled in all the required data but hasn't submitted the form yet).
     *
     * @return True if the form has been completed or cancelled; false otherwise.
     */
    public boolean isFormCompleted() {
        return formCompleted;
    }

    /**
     * If possible, goes to the step that is positioned after the currently open one, closing the
     * current one and opening the next one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToNextStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() + 1, useAnimations);
    }

    /**
     * If possible, goes to the step that is positioned before the currently open one, closing the
     * current one and opening the previous one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToPreviousStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() - 1, useAnimations);
    }

    /**
     * If possible, goes to the specified step, closing the currently open one and opening the
     * target one.
     * Please note that, unless allowNonLinearNavigation is set to true, it will only be possible to
     * navigate to a certain step if all the previous ones are marked as completed.
     * In case the navigation is possible and the specified position to go to is the last one + 1,
     * the form will attempt to complete.
     *
     * @param stepPosition The step position to go to. If it is the next one to the actual last one,
     *                     the form will attempt to complete.
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToStep(int stepPosition, boolean useAnimations) {
        if (formCompleted) {
            return false;
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition != stepPosition && stepPosition >= 0 && stepPosition <= stepHelpers.size()) {
            boolean previousStepsAreCompleted = areAllPreviousStepsCompleted(stepPosition);
            if ((style.allowNonLinearNavigation && stepPosition < stepHelpers.size()) || previousStepsAreCompleted) {
                openStep(stepPosition, useAnimations);

                return true;
            }
        }

        return false;
    }

    /**
     * Gets the position of the currently open step.
     *
     * @return The position of the currently open step, counting from 0. -1 if not found.
     */
    public synchronized int getOpenStepPosition() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            if (stepHelper.getStepInstance().isOpen()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the currently open step.
     *
     * @return The currently open step, or null if not found.
     */
    public synchronized Step getOpenStep() {
        StepHelper openStepHelper = getOpenStepHelper();
        return openStepHelper != null ? openStepHelper.getStepInstance() : null;
    }

    /**
     * Gets the content layout of the specified step (i.e., the layout which was provided at start
     * to setup the step).
     *
     * @param stepPosition The step position.
     * @return If found, the layout. If not, null.
     */
    public View getStepContentLayout(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            return stepHelpers.get(stepPosition).getStepInstance().getContentLayout();
        }

        return null;
    }

    /**
     * Shows the bottom navigation bar.
     */
    public void showBottomNavigation() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the bottom navigation bar.
     */
    public void hideBottomNavigation() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    /**
     * Scrolls to the top of the specified step, but only in case its content is not visible.
     *
     * @param stepPosition The step position.
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public void scrollToStepIfNecessary(final int stepPosition, final boolean smoothScroll) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    Step stepInstance = stepHelpers.get(stepPosition).getStepInstance();
                    View stepEntireLayout = stepInstance.getEntireStepLayout();
                    View stepContentLayout = stepInstance.getContentLayout();
                    Rect scrollBounds = new Rect();
                    stepsScrollView.getDrawingRect(scrollBounds);
                    if (stepContentLayout == null || scrollBounds.top > stepContentLayout.getY()) {
                        if (smoothScroll) {
                            stepsScrollView.smoothScrollTo(0, stepEntireLayout.getTop());
                        } else {
                            stepsScrollView.scrollTo(0, stepEntireLayout.getTop());
                        }
                    }
                }
            });
        }
    }

    /**
     * Scrolls to the top of the currently open step, but only in case its content is not visible.
     *
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public synchronized void scrollToOpenStepIfNecessary(boolean smoothScroll) {
        scrollToStepIfNecessary(getOpenStepPosition(), smoothScroll);
    }

    /**
     * If all the steps are currently marked as completed, completes the form, disabling the step
     * navigation and the button(s) of the last step, and invoking onCompletedForm() on the listener.
     * To revert these changes (for example, because saving or sending the data has failed and you
     * want the form to go back to normal so the user can use it), call
     * cancelFormCompletionOrCancellationAttempt().
     */
    public void completeForm() {
        attemptToCompleteForm(false);
    }

    /**
     * Cancels the form, disabling the step navigation and the button(s) of the currently open step,
     * and invoking onCancelledForm() on the listener.
     * To revert these changes (for example, because the user has dismissed the cancellation and you
     * want the form to go back to normal), call cancelFormCompletionOrCancellationAttempt().
     */
    public void cancelForm() {
        attemptToCompleteForm(true);
    }

    /**
     * To be used after a failed form completion attempt or after a dismissed cancellation attempt,
     * this method re-activates the navigation to other steps and re-enables the button(s) of the
     * currently open step.
     * Useful when saving the form data fails and you want to allow the user to use the form again
     * in order to re-send the data.
     */
    public synchronized void cancelFormCompletionOrCancellationAttempt() {
        if (!formCompleted) {
            return;
        }

        int openedStepPosition = getOpenStepPosition();
        openedStepPosition = openedStepPosition == -1 ? stepHelpers.size() - 1 : openedStepPosition;
        StepHelper stepHelper = stepHelpers.get(openedStepPosition);

        if (style.closeLastStepOnCompletion) {
            Step step = stepHelper.getStepInstance();
            if (!step.isOpen()) {
                step.openInternal(true);
            }
        }

        if ((openedStepPosition + 1) < stepHelpers.size() || areAllStepsCompleted()) {
            stepHelper.enableAllButtons();
        } else {
            stepHelper.enableCancelButton();
        }

        formCompleted = false;
        updateBottomNavigationButtons();
    }

    /**
     * Refreshes the progress bar of the bottom navigation depending on the number of steps marked
     * as completed, returning the number of completed steps.
     *
     * @return The number of steps that are currently marked as completed.
     */
    public int refreshFormProgress() {
        int numberOfCompletedSteps = 0;
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (stepHelpers.get(i).getStepInstance().isCompleted()) {
                ++numberOfCompletedSteps;
            }
        }
        setProgress(numberOfCompletedSteps);

        return numberOfCompletedSteps;
    }

    /**
     * Adds a step to the form in the specified position.
     * Please note that this change will be lost on restoration events like rotation. Hence, it
     * will be up to you to re-add the step (and its state) AFTER the form is restored without it.
     *
     * @param index The index where the step will be added.
     * @param stepToAdd The step to add.
     * @return True if the step was added successfully; false otherwise.
     */
    public boolean addStep(int index, Step stepToAdd) {
        StepHelper lastStep = stepHelpers.get(stepHelpers.size() - 1);
        int lastAllowedIndex = lastStep.isConfirmationStep() ? stepHelpers.size() - 1 : stepHelpers.size();
        if (!initialized || formCompleted || index < 0 || index > lastAllowedIndex) {
            return false;
        }

        StepHelper stepHelper = new StepHelper(internalListener, stepToAdd);
        stepHelpers.add(index, stepHelper);
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (i != index) {
                StepHelper previouslyExistingStepHelper = stepHelpers.get(i);
                previouslyExistingStepHelper.updateStepsViewAfterPositionChange(this);
            }
        }

        View stepLayout = initializeStepHelper(index);
        stepToAdd.markAsCompletedOrUncompleted(false);

        progressBar.setMax(stepHelpers.size());
        refreshFormProgress();
        updateBottomNavigationButtons();

        formContentView.addView(stepLayout, index);
        int openStepPosition = getOpenStepPosition();
        if (!style.allowNonLinearNavigation && !isStepCompleted(index) && index < openStepPosition) {
            goToStep(index, true);
        }

        return true;
    }

    /**
     * Removes the step that is placed at the specified position.
     * Please note that this change will be lost on restoration events like rotation. Hence, it
     * will be up to you to remove the step again AFTER the form is restored with it on it.
     *
     * @param index The index where the step to delete is.
     * @return True if the step was deleted successfully; false otherwise.
     */
    public boolean removeStep(int index) {
        StepHelper lastStep = stepHelpers.get(stepHelpers.size() - 1);
        int lastAllowedIndex = lastStep.isConfirmationStep() ? stepHelpers.size() - 2 : stepHelpers.size() - 1;
        if (!initialized || formCompleted || index < 0 || index > lastAllowedIndex || stepHelpers.size() <= 1) {
            return false;
        }

        stepHelpers.remove(index);
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper previouslyExistingStepHelper = stepHelpers.get(i);
            previouslyExistingStepHelper.updateStepsViewAfterPositionChange(this);
        }

        progressBar.setMax(stepHelpers.size());
        refreshFormProgress();
        updateBottomNavigationButtons();

        formContentView.removeViewAt(index);
        int openStepPosition = getOpenStepPosition();
        if (openStepPosition == -1) {
            int replacementStep = index > 0 ? index - 1 : 0;
            goToStep(replacementStep, true);
        }

        return true;
    }

    /**
     * Gets the total number of steps of the form.
     *
     * @return The total number of steps, including the confirmation step, if any.
     */
    public int getTotalNumberOfSteps() {
        return stepHelpers.size();
    }

    /**
     * Gets the position of the specified step within the list of steps of the form.
     *
     * @param step The step to find the position of.
     * @return The position of the step, or -1 if the step is not found.
     */
    public int getStepPosition(Step step) {
        for (int i = 0; i < stepHelpers.size(); i++) {
            if (stepHelpers.get(i).getStepInstance() == step)
                return i;
        }

        return -1;
    }

    private void onConstructed(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.vertical_stepper_form_layout, this, true);

        keyboardTogglingObserver = new KeyboardTogglingObserver();

        style = new FormStyle();

        // Set the default values for all the style properties
        style.stepNextButtonText =
                getResources().getString(R.string.vertical_stepper_form_continue_button);
        style.lastStepNextButtonText =
                getResources().getString(R.string.vertical_stepper_form_confirm_button);
        style.lastStepCancelButtonText =
                getResources().getString(R.string.vertical_stepper_form_cancel_button);
        style.confirmationStepTitle =
                getResources().getString(R.string.vertical_stepper_form_confirmation_step_title);
        style.confirmationStepSubtitle = "";
        style.leftCircleSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_width_circle);
        style.leftCircleTextSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_text_size_circle);
        style.stepTitleTextSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_text_size_title);
        style.stepSubtitleTextSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_text_size_subtitle);
        style.stepErrorMessageTextSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_text_size_error_message);
        style.leftVerticalLineThicknessSizeInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_width_vertical_line);
        style.marginFromStepNumbersToContentInPx =
                getResources().getDimensionPixelSize(R.dimen.vertical_stepper_form_space_between_numbers_and_content);
        style.backgroundColorOfDisabledElements =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_disabled_elements);
        style.stepNumberBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_circle);
        style.nextButtonBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_next_button);
        style.nextButtonPressedBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_next_button_pressed);
        style.lastStepCancelButtonBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_cancel_button);
        style.lastStepCancelButtonPressedBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_cancel_button_pressed);
        style.stepNumberTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_circle);
        style.stepTitleTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_title);
        style.stepSubtitleTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_subtitle);
        style.nextButtonTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_next_button);
        style.nextButtonPressedTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_next_button_pressed);
        style.lastStepCancelButtonTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_cancel_button);
        style.lastStepCancelButtonPressedTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_cancel_button_pressed);
        style.errorMessageTextColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_text_color_error_message);
        style.bottomNavigationBackgroundColor =
                ContextCompat.getColor(context, R.color.vertical_stepper_form_background_color_bottom_navigation);
        style.displayBottomNavigation = true;
        style.displayStepButtons = true;
        style.displayCancelButtonInLastStep = false;
        style.displayStepDataInSubtitleOfClosedSteps = true;
        style.displayDifferentBackgroundColorOnDisabledElements = false;
        style.includeConfirmationStep = true;
        style.allowNonLinearNavigation = false;
        style.allowStepOpeningOnHeaderClick = true;
        style.closeLastStepOnCompletion = false;
        style.alphaOfDisabledElements = 0.3f;

        // Try to get the user values for the style properties to replace the default ones
        TypedArray vars;
        if (attrs != null) {
            vars = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.VerticalStepperFormView,
                    defStyleAttr,
                    0);

            if (vars.hasValue(R.styleable.VerticalStepperFormView_form_next_button_text)) {
                style.stepNextButtonText = vars.getString(
                        R.styleable.VerticalStepperFormView_form_next_button_text);
            }
            if (vars.hasValue(R.styleable.VerticalStepperFormView_form_last_button_text)) {
                style.lastStepNextButtonText = vars.getString(
                        R.styleable.VerticalStepperFormView_form_last_button_text);
            }
            if (vars.hasValue(R.styleable.VerticalStepperFormView_form_cancel_button_text)) {
                style.lastStepCancelButtonText = vars.getString(
                        R.styleable.VerticalStepperFormView_form_cancel_button_text);
            }
            if (vars.hasValue(R.styleable.VerticalStepperFormView_form_confirmation_step_title_text)) {
                style.confirmationStepTitle = vars.getString(
                        R.styleable.VerticalStepperFormView_form_confirmation_step_title_text);
            }
            if (vars.hasValue(R.styleable.VerticalStepperFormView_form_confirmation_step_subtitle_text)) {
                style.confirmationStepSubtitle = vars.getString(
                        R.styleable.VerticalStepperFormView_form_confirmation_step_subtitle_text);
            }
            style.leftCircleSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_circle_size,
                    style.leftCircleSizeInPx);
            style.leftCircleTextSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_circle_text_size,
                    style.leftCircleTextSizeInPx);
            style.stepTitleTextSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_title_text_size,
                    style.stepTitleTextSizeInPx);
            style.stepSubtitleTextSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_subtitle_text_size,
                    style.stepSubtitleTextSizeInPx);
            style.stepErrorMessageTextSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_error_message_text_size,
                    style.stepErrorMessageTextSizeInPx);
            style.leftVerticalLineThicknessSizeInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_vertical_line_width,
                    style.leftVerticalLineThicknessSizeInPx);
            style.marginFromStepNumbersToContentInPx = vars.getDimensionPixelSize(
                    R.styleable.VerticalStepperFormView_form_horizontal_margin_from_step_numbers_to_content,
                    style.marginFromStepNumbersToContentInPx);
            style.backgroundColorOfDisabledElements = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_disabled_elements_background_color,
                    style.backgroundColorOfDisabledElements);
            style.stepNumberBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_circle_background_color,
                    style.stepNumberBackgroundColor);
            style.nextButtonBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_next_button_background_color,
                    style.nextButtonBackgroundColor);
            style.nextButtonPressedBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_next_button_pressed_background_color,
                    style.nextButtonPressedBackgroundColor);
            style.lastStepCancelButtonBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_cancel_button_background_color,
                    style.lastStepCancelButtonBackgroundColor);
            style.lastStepCancelButtonPressedBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_cancel_button_pressed_background_color,
                    style.lastStepCancelButtonPressedBackgroundColor);
            style.stepNumberTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_circle_text_color,
                    style.stepNumberTextColor);
            style.stepTitleTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_title_text_color,
                    style.stepTitleTextColor);
            style.stepSubtitleTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_subtitle_text_color,
                    style.stepSubtitleTextColor);
            style.nextButtonTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_next_button_text_color,
                    style.nextButtonTextColor);
            style.nextButtonPressedTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_next_button_pressed_text_color,
                    style.nextButtonPressedTextColor);
            style.lastStepCancelButtonTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_cancel_button_text_color,
                    style.lastStepCancelButtonTextColor);
            style.lastStepCancelButtonPressedTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_cancel_button_pressed_text_color,
                    style.lastStepCancelButtonPressedTextColor);
            style.errorMessageTextColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_error_message_text_color,
                    style.errorMessageTextColor);
            style.bottomNavigationBackgroundColor = vars.getColor(
                    R.styleable.VerticalStepperFormView_form_bottom_navigation_background_color,
                    style.bottomNavigationBackgroundColor);
            style.displayBottomNavigation = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_bottom_navigation,
                    style.displayBottomNavigation);
            style.displayStepButtons = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_step_buttons,
                    style.displayStepButtons);
            style.displayCancelButtonInLastStep = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_cancel_button_in_last_step,
                    style.displayCancelButtonInLastStep);
            style.displayStepDataInSubtitleOfClosedSteps = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_step_data_in_subtitle_of_closed_steps,
                    style.displayStepDataInSubtitleOfClosedSteps);
            style.displayDifferentBackgroundColorOnDisabledElements = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_display_different_background_color_on_disabled_elements,
                    style.displayDifferentBackgroundColorOnDisabledElements);
            style.includeConfirmationStep = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_include_confirmation_step,
                    style.includeConfirmationStep);
            style.allowNonLinearNavigation = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_allow_non_linear_navigation,
                    style.allowNonLinearNavigation);
            style.allowStepOpeningOnHeaderClick = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_allow_step_opening_on_header_click,
                    style.allowStepOpeningOnHeaderClick);
            style.closeLastStepOnCompletion = vars.getBoolean(
                    R.styleable.VerticalStepperFormView_form_close_last_step_on_completion,
                    style.closeLastStepOnCompletion);
            style.alphaOfDisabledElements = vars.getFloat(
                    R.styleable.VerticalStepperFormView_form_alpha_of_disabled_elements,
                    style.alphaOfDisabledElements);

            vars.recycle();
        }

        internalListener = new FormStepListener();
    }

    void initializeForm(StepperFormListener listener, StepHelper[] stepsArray) {
        this.listener = listener;
        this.originalStepHelpers = Arrays.asList(stepsArray);
        this.stepHelpers = new ArrayList<StepHelper>(originalStepHelpers);

        progressBar.setMax(stepHelpers.size());

        bottomNavigationView.setBackgroundColor(style.bottomNavigationBackgroundColor);
        if (!style.displayBottomNavigation) {
            hideBottomNavigation();
        }

        for (int i = 0; i < stepHelpers.size(); i++) {
            View stepLayout = initializeStepHelper(i);
            formContentView.addView(stepLayout);
        }

        goToStep(0, false);

        initialized = true;
    }

    private View initializeStepHelper(int position) {
        StepHelper stepHelper = stepHelpers.get(position);
        boolean isLast = (position + 1) == stepHelpers.size();
        int stepLayoutResourceId = getStepLayoutResourceId(position, isLast);

        return stepHelper.initialize(this, formContentView, stepLayoutResourceId);
    }

    @LayoutRes
    protected int getStepLayoutResourceId(int position, boolean isLast) {
        // This could be overridden to use a custom step layout
        return R.layout.step_layout;
    }

    private StepHelper getOpenStepHelper() {
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            if (stepHelper.getStepInstance().isOpen()) {
                return stepHelper;
            }
        }

        return null;
    }

    private synchronized void openStep(int stepToOpenPosition, boolean useAnimations) {
        if (stepToOpenPosition >= 0 && stepToOpenPosition < stepHelpers.size()) {

            int stepToClosePosition = getOpenStepPosition();
            if (stepToClosePosition != -1) {
                StepHelper stepToClose = stepHelpers.get(stepToClosePosition);
                stepToClose.getStepInstance().closeInternal(useAnimations);
            }

            StepHelper stepToOpen = stepHelpers.get(stepToOpenPosition);
            stepToOpen.getStepInstance().openInternal(useAnimations);
        } else if (stepToOpenPosition == stepHelpers.size()) {
            attemptToCompleteForm(false);
        }
    }

    protected synchronized void updateBottomNavigationButtons() {
        int stepPosition = getOpenStepPosition();
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);

            if (!formCompleted && stepPosition > 0) {
                enablePreviousButtonInBottomNavigation();
            } else {
                disablePreviousButtonInBottomNavigation();
            }

            if (!formCompleted
                    && (stepPosition + 1) < stepHelpers.size()
                    && (style.allowNonLinearNavigation || stepHelper.getStepInstance().isCompleted())) {
                enableNextButtonInBottomNavigation();
            } else {
                disableNextButtonInBottomNavigation();
            }
        }
    }

    protected void disablePreviousButtonInBottomNavigation() {
        disableBottomButtonNavigation(previousStepButton);
    }

    protected void enablePreviousButtonInBottomNavigation() {
        enableBottomButtonNavigation(previousStepButton);
    }

    protected void disableNextButtonInBottomNavigation() {
        disableBottomButtonNavigation(nextStepButton);
    }

    protected void enableNextButtonInBottomNavigation() {
        enableBottomButtonNavigation(nextStepButton);
    }

    private void enableBottomButtonNavigation(View button) {
        button.setAlpha(1f);
        button.setEnabled(true);
    }

    private void disableBottomButtonNavigation(View button) {
        button.setAlpha(style.alphaOfDisabledElements);
        button.setEnabled(false);
    }

    private void setProgress(int numberOfCompletedSteps) {
        if (numberOfCompletedSteps >= 0 && numberOfCompletedSteps <= stepHelpers.size()) {
            progressBar.setProgress(numberOfCompletedSteps);
        }
    }

    private void enableOrDisableLastStepNextButton() {
        if (!areAllStepsCompleted()) {
            stepHelpers.get(stepHelpers.size() - 1).disableNextButton();
        } else {
            stepHelpers.get(stepHelpers.size() - 1).enableNextButton();
        }
    }

    private synchronized void attemptToCompleteForm(boolean isCancellation) {
        if (formCompleted) {
            return;
        }

        // If the last step is a confirmation step that happens to be marked as uncompleted,
        // here we attempt to mark it as completed so the form can be completed
        boolean markedConfirmationStepAsCompleted = false;
        String confirmationStepErrorMessage = "";
        StepHelper lastStepHelper = stepHelpers.get(stepHelpers.size() - 1);
        Step lastStep = lastStepHelper.getStepInstance();
        if (!isCancellation) {
            if (!lastStep.isCompleted() && lastStepHelper.isConfirmationStep()) {
                confirmationStepErrorMessage = lastStep.getErrorMessage();
                lastStep.markAsCompletedOrUncompleted(true);
                if (lastStep.isCompleted()) {
                    markedConfirmationStepAsCompleted = true;
                }
            }
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition >= 0 && openStepPosition < stepHelpers.size() && (isCancellation || areAllStepsCompleted())) {
            formCompleted = true;
            stepHelpers.get(openStepPosition).disableAllButtons();
            updateBottomNavigationButtons();

            if (listener != null) {
                if (!isCancellation) {
                    listener.onCompletedForm();
                } else {
                    listener.onCancelledForm();
                }
            }
        } else if (markedConfirmationStepAsCompleted) {
            // If the completion attempt fails, we restore the confirmation step to its previous state
            lastStep.markAsUncompleted(confirmationStepErrorMessage, true);
        }

        if (!isCancellation && style.closeLastStepOnCompletion) {
            lastStep.closeInternal(true);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    private void findViews() {
        formContentView = findViewById(R.id.content);
        stepsScrollView = findViewById(R.id.steps_scroll);
        progressBar = findViewById(R.id.progress_bar);
        previousStepButton = findViewById(R.id.down_previous);
        nextStepButton = findViewById(R.id.down_next);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void registerListeners() {
        previousStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviousStep(true);
            }
        });
        nextStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextStep(true);
            }
        });

        addObserverForKeyboard();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeObserverForKeyboard();

        super.onDetachedFromWindow();
    }

    private void addObserverForKeyboard() {
        keyboardIsOpen = isKeyboardOpen();
        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardTogglingObserver);
    }

    private void removeObserverForKeyboard() {
        getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardTogglingObserver);
    }

    private boolean isKeyboardOpen() {
        Rect r = new Rect();
        formContentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = formContentView.getRootView().getHeight();
        int keyboardHeight = screenHeight - r.bottom;

        return keyboardHeight > screenHeight * 0.2;
    }

    private void restoreFromState(
            int positionToOpen,
            boolean[] completedSteps,
            String[] titles,
            String[] subtitles,
            String[] buttonTexts,
            String[] errorMessages,
            boolean formCompleted) {

        for (int i = 0; i < completedSteps.length; i++) {
            StepHelper stepHelper = originalStepHelpers.get(i);
            Step step = stepHelper.getStepInstance();
            step.updateTitle(titles[i], false);
            step.updateSubtitle(subtitles[i], false);
            step.updateNextButtonText(buttonTexts[i], false);
            if (completedSteps[i]) {
                step.markAsCompleted(false);
            } else {
                step.markAsUncompleted(errorMessages[i], false);
            }
        }

        goToStep(positionToOpen, false);

        if (formCompleted) {
            this.formCompleted = true;
            originalStepHelpers.get(getOpenStepPosition()).disableAllButtons();
            updateBottomNavigationButtons();
        }

        refreshFormProgress();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        boolean[] completedSteps = new boolean[originalStepHelpers.size()];
        String[] titles = new String[originalStepHelpers.size()];
        String[] subtitles = new String[originalStepHelpers.size()];
        String[] buttonTexts = new String[originalStepHelpers.size()];
        String[] errorMessages = new String[originalStepHelpers.size()];
        for (int i = 0; i < completedSteps.length; i++) {
            StepHelper stepHelper = originalStepHelpers.get(i);
            completedSteps[i] = stepHelper.getStepInstance().isCompleted();
            titles[i] = stepHelper.getStepInstance().getTitle();
            subtitles[i] = stepHelper.getStepInstance().getSubtitle();
            buttonTexts[i] = stepHelper.getStepInstance().getNextButtonText();
            if (!stepHelper.getStepInstance().isCompleted()) {
                errorMessages[i] = stepHelper.getStepInstance().getErrorMessage();
            }
        }

        StepHelper openStepHelper = getOpenStepHelper();
        int openStepPosition = originalStepHelpers.indexOf(openStepHelper);

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("openStep", openStepPosition);
        bundle.putBooleanArray("completedSteps", completedSteps);
        bundle.putStringArray("titles", titles);
        bundle.putStringArray("subtitles", subtitles);
        bundle.putStringArray("buttonTexts", buttonTexts);
        bundle.putStringArray("errorMessages", errorMessages);
        bundle.putBoolean("formCompleted", formCompleted);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            boolean formCompleted = bundle.getBoolean("formCompleted");
            String[] errorMessages = bundle.getStringArray("errorMessages");
            String[] buttonTexts = bundle.getStringArray("buttonTexts");
            String[] subtitles = bundle.getStringArray("subtitles");
            String[] titles = bundle.getStringArray("titles");
            boolean[] completedSteps = bundle.getBooleanArray("completedSteps");
            int positionToOpen = bundle.getInt("openStep");
            state = bundle.getParcelable("superState");

            restoreFromState(
                    positionToOpen,
                    completedSteps,
                    titles,
                    subtitles,
                    buttonTexts,
                    errorMessages,
                    formCompleted);
        }
        super.onRestoreInstanceState(state);
    }

    class FormStepListener implements Step.InternalFormStepListener {

        @Override
        public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedSubtitle(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedErrorMessage(int stepPosition, boolean useAnimations) {
            // No need to do anything here
        }

        @Override
        public void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            refreshFormProgress();
            enableOrDisableLastStepNextButton();
        }

        @Override
        public void onUpdatedStepVisibility(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            scrollToOpenStepIfNecessary(useAnimations);
            enableOrDisableLastStepNextButton();
        }
    }

    class FormStyle {
        String stepNextButtonText;
        String lastStepNextButtonText;
        String lastStepCancelButtonText;
        String confirmationStepTitle;
        String confirmationStepSubtitle;
        int leftCircleSizeInPx;
        int leftCircleTextSizeInPx;
        int stepTitleTextSizeInPx;
        int stepSubtitleTextSizeInPx;
        int stepErrorMessageTextSizeInPx;
        int leftVerticalLineThicknessSizeInPx;
        int marginFromStepNumbersToContentInPx;
        int backgroundColorOfDisabledElements;
        int stepNumberBackgroundColor;
        int nextButtonBackgroundColor;
        int nextButtonPressedBackgroundColor;
        int lastStepCancelButtonBackgroundColor;
        int lastStepCancelButtonPressedBackgroundColor;
        int stepNumberTextColor;
        int stepTitleTextColor;
        int stepSubtitleTextColor;
        int nextButtonTextColor;
        int nextButtonPressedTextColor;
        int lastStepCancelButtonTextColor;
        int lastStepCancelButtonPressedTextColor;
        int errorMessageTextColor;
        int bottomNavigationBackgroundColor;
        boolean displayBottomNavigation;
        boolean displayStepButtons;
        boolean displayCancelButtonInLastStep;
        boolean displayStepDataInSubtitleOfClosedSteps;
        boolean displayDifferentBackgroundColorOnDisabledElements;
        boolean includeConfirmationStep;
        boolean allowNonLinearNavigation;
        boolean allowStepOpeningOnHeaderClick;
        boolean closeLastStepOnCompletion;
        float alphaOfDisabledElements;
    }

    private class KeyboardTogglingObserver implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            boolean keyboardWasOpen = keyboardIsOpen;
            keyboardIsOpen = isKeyboardOpen();
            if (initialized && keyboardIsOpen != keyboardWasOpen) {
                scrollToOpenStepIfNecessary(true);
            }
        }
    }
}