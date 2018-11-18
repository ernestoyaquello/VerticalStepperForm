package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;

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

import java.util.Arrays;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

/**
 * Custom layout that implements a vertical stepper form.
 */
public class VerticalStepperFormLayout extends LinearLayout {

    FormStepListener internalListener;

    private FormStyle style;
    private StepperFormListener listener;
    private List<StepHelper> stepHelpers;

    private LinearLayout formContentView;
    private ScrollView stepsScrollView;
    private ProgressBar progressBar;
    private AppCompatImageButton previousStepButton, nextStepButton;
    private View bottomNavigationView;

    private boolean formCompleted;
    private boolean keyboardIsOpen;

    public VerticalStepperFormLayout(Context context) {
        super(context);

        onConstructed(context, null, 0);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        onConstructed(context, attrs, 0);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
        Step[] stepsArray = steps.toArray(new Step[steps.size()]);
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
        for (int i = 0; i < stepHelpers.size(); i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            if (stepHelper.getStepInstance().isOpen()) {
                return stepHelper.getStepInstance();
            }
        }

        return null;
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
        if (openedStepPosition >= 0 && openedStepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(openedStepPosition);
            if ((openedStepPosition + 1) < stepHelpers.size() || areAllStepsCompleted()) {
                stepHelper.enableAllButtons();
            } else {
                stepHelper.enableCancelButton();
            }

            formCompleted = false;
            updateBottomNavigationButtons();
        }
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
     * Gets the total number of steps of the form.
     *
     * @return The total number of steps, including the confirmation step, if any.
     */
    public int getTotalNumberOfSteps() {
        return stepHelpers.size();
    }

    private void onConstructed(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.vertical_stepper_form_layout, this, true);

        // TODO Move all these default style values to resource files
        // TODO Get these values from XML attributes whenever possible
        // TODO Add sizes
        FormStyle.defaultNextStepButtonText =
                getResources().getString(R.string.vertical_form_stepper_form_continue_button);
        FormStyle.defaultLastStepNextButtonText =
                getResources().getString(R.string.vertical_form_stepper_form_confirm_button);
        FormStyle.defaultLastStepCancelButtonText =
                getResources().getString(R.string.vertical_form_stepper_form_cancel_button);
        FormStyle.defaultConfirmationStepTitle =
                getResources().getString(R.string.vertical_form_stepper_form_confirmation_step_title);
        FormStyle.defaultConfirmationStepSubtitle =
                getResources().getString(R.string.vertical_form_stepper_form_confirmation_step_subtitle);
        FormStyle.defaultAlphaOfDisabledElements = 0.25f;
        FormStyle.defaultBackgroundColorOfDisabledElements = Color.rgb(200, 200, 200);
        FormStyle.defaultStepNumberBackgroundColor = Color.rgb(63, 81, 181);
        FormStyle.defaultNextButtonBackgroundColor = Color.rgb(63, 81, 181);
        FormStyle.defaultNextButtonPressedBackgroundColor = Color.rgb(48, 63, 159);
        FormStyle.defaultLastStepCancelButtonBackgroundColor = Color.rgb(155, 155, 155);
        FormStyle.defaultLastStepCancelButtonPressedBackgroundColor = Color.rgb(135, 135, 135);
        FormStyle.defaultStepNumberTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultStepTitleTextColor = Color.rgb(33, 33, 33);
        FormStyle.defaultStepSubtitleTextColor = Color.rgb(162, 162, 162);
        FormStyle.defaultNextButtonTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultNextButtonPressedTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultLastStepCancelButtonTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultLastStepCancelButtonPressedTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultErrorMessageTextColor = Color.rgb(175, 18, 18);
        FormStyle.defaultDisplayBottomNavigation = true;
        FormStyle.defaultDisplayStepButtons = true;
        FormStyle.defaultDisplayCancelButtonInLastStep = false;
        FormStyle.defaultIncludeConfirmationStep = true;
        FormStyle.defaultDisplayStepDataInSubtitleOfClosedSteps = false;
        FormStyle.defaultDisplayDifferentBackgroundColorOnDisabledElements = false;
        FormStyle.defaultAllowNonLinearNavigation = false;

        internalListener = new FormStepListener();
    }

    void initializeForm(StepperFormListener listener, FormStyle style, StepHelper[] stepsArray) {
        this.listener = listener;
        this.style = style;
        this.stepHelpers = Arrays.asList(stepsArray);

        progressBar.setMax(stepHelpers.size());

        if (!style.displayBottomNavigation) {
            hideBottomNavigation();
        }

        keyboardIsOpen = isKeyboardOpen();
        setObserverForKeyboard();

        for (int i = 0; i < stepHelpers.size(); i++) {
            View stepLayout = initializeStepHelper(i);
            formContentView.addView(stepLayout);
        }

        goToStep(0, false);
    }

    private View initializeStepHelper(int position) {
        StepHelper stepHelper = stepHelpers.get(position);
        boolean isLast = (position + 1) == stepHelpers.size();

        return stepHelper.initialize(this, style, formContentView, position, isLast);
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

    private void setObserverForKeyboard() {
        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean keyboardWasOpen = keyboardIsOpen;
                keyboardIsOpen = isKeyboardOpen();
                if (keyboardIsOpen != keyboardWasOpen) {
                    scrollToOpenStepIfNecessary(true);
                }
            }
        });
    }

    private boolean isKeyboardOpen() {
        Rect r = new Rect();
        formContentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = formContentView.getRootView().getHeight();
        int keyboardHeight = screenHeight - r.bottom;

        return keyboardHeight > screenHeight * 0.2;
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
            StepHelper stepHelper = stepHelpers.get(i);

            stepHelper.getStepInstance().updateTitle(titles[i], false);
            stepHelper.getStepInstance().updateSubtitle(subtitles[i], false);
            stepHelper.getStepInstance().updateNextButtonText(buttonTexts[i], false);
            if (completedSteps[i]) {
                stepHelper.getStepInstance().markAsCompleted(false);
            } else {
                stepHelper.getStepInstance().markAsUncompleted(errorMessages[i], false);
            }
        }

        goToStep(positionToOpen, false);

        if (formCompleted) {
            this.formCompleted = true;
            stepHelpers.get(getOpenStepPosition()).disableAllButtons();
            updateBottomNavigationButtons();
        }

        refreshFormProgress();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        boolean[] completedSteps = new boolean[stepHelpers.size()];
        String[] titles = new String[stepHelpers.size()];
        String[] subtitles = new String[stepHelpers.size()];
        String[] buttonTexts = new String[stepHelpers.size()];
        String[] errorMessages = new String[stepHelpers.size()];
        for (int i = 0; i < completedSteps.length; i++) {
            StepHelper stepHelper = stepHelpers.get(i);
            completedSteps[i] = stepHelper.getStepInstance().isCompleted();
            titles[i] = stepHelper.getStepInstance().getTitle();
            subtitles[i] = stepHelper.getStepInstance().getSubtitle();
            buttonTexts[i] = stepHelper.getStepInstance().getNextButtonText();
            if (!stepHelper.getStepInstance().isCompleted()) {
                errorMessages[i] = stepHelper.getStepInstance().getErrorMessage();
            }
        }

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("openStep", this.getOpenStepPosition());
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

    static class FormStyle {

        private static String defaultNextStepButtonText;
        private static String defaultLastStepNextButtonText;
        private static String defaultLastStepCancelButtonText;
        private static String defaultConfirmationStepTitle;
        private static String defaultConfirmationStepSubtitle;
        private static float defaultAlphaOfDisabledElements;
        private static int defaultBackgroundColorOfDisabledElements;
        private static int defaultStepNumberBackgroundColor;
        private static int defaultNextButtonBackgroundColor;
        private static int defaultNextButtonPressedBackgroundColor;
        private static int defaultLastStepCancelButtonBackgroundColor;
        private static int defaultLastStepCancelButtonPressedBackgroundColor;
        private static int defaultStepNumberTextColor;
        private static int defaultStepTitleTextColor;
        private static int defaultStepSubtitleTextColor;
        private static int defaultNextButtonTextColor;
        private static int defaultNextButtonPressedTextColor;
        private static int defaultLastStepCancelButtonTextColor;
        private static int defaultLastStepCancelButtonPressedTextColor;
        private static int defaultErrorMessageTextColor;
        private static boolean defaultDisplayBottomNavigation;
        private static boolean defaultDisplayStepButtons;
        private static boolean defaultDisplayCancelButtonInLastStep;
        private static boolean defaultIncludeConfirmationStep;
        private static boolean defaultDisplayStepDataInSubtitleOfClosedSteps;
        private static boolean defaultDisplayDifferentBackgroundColorOnDisabledElements;
        private static boolean defaultAllowNonLinearNavigation;

        String stepNextButtonText;
        String lastStepNextButtonText;
        String lastStepCancelButtonText;
        String confirmationStepTitle;
        String confirmationStepSubtitle;
        float alphaOfDisabledElements;
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
        boolean displayBottomNavigation;
        boolean displayStepButtons;
        boolean displayCancelButtonInLastStep;
        boolean includeConfirmationStep;
        boolean displayStepDataInSubtitleOfClosedSteps;
        boolean displayDifferentBackgroundColorOnDisabledElements;
        boolean allowNonLinearNavigation;

        FormStyle() {
            this.stepNextButtonText = defaultNextStepButtonText;
            this.lastStepNextButtonText = defaultLastStepNextButtonText;
            this.lastStepCancelButtonText = defaultLastStepCancelButtonText;
            this.confirmationStepTitle = defaultConfirmationStepTitle;
            this.confirmationStepSubtitle = defaultConfirmationStepSubtitle;
            this.alphaOfDisabledElements = defaultAlphaOfDisabledElements;
            this.backgroundColorOfDisabledElements = defaultBackgroundColorOfDisabledElements;
            this.stepNumberBackgroundColor = defaultStepNumberBackgroundColor;
            this.nextButtonBackgroundColor = defaultNextButtonBackgroundColor;
            this.nextButtonPressedBackgroundColor = defaultNextButtonPressedBackgroundColor;
            this.lastStepCancelButtonBackgroundColor = defaultLastStepCancelButtonBackgroundColor;
            this.lastStepCancelButtonPressedBackgroundColor = defaultLastStepCancelButtonPressedBackgroundColor;
            this.stepNumberTextColor = defaultStepNumberTextColor;
            this.stepTitleTextColor = defaultStepTitleTextColor;
            this.stepSubtitleTextColor = defaultStepSubtitleTextColor;
            this.nextButtonTextColor = defaultNextButtonTextColor;
            this.nextButtonPressedTextColor = defaultNextButtonPressedTextColor;
            this.lastStepCancelButtonTextColor = defaultLastStepCancelButtonTextColor;
            this.lastStepCancelButtonPressedTextColor = defaultLastStepCancelButtonPressedTextColor;
            this.errorMessageTextColor = defaultErrorMessageTextColor;
            this.displayBottomNavigation = defaultDisplayBottomNavigation;
            this.displayStepButtons = defaultDisplayStepButtons;
            this.displayCancelButtonInLastStep = defaultDisplayCancelButtonInLastStep;
            this.includeConfirmationStep = defaultIncludeConfirmationStep;
            this.displayStepDataInSubtitleOfClosedSteps = defaultDisplayStepDataInSubtitleOfClosedSteps;
            this.displayDifferentBackgroundColorOnDisabledElements = defaultDisplayDifferentBackgroundColorOnDisabledElements;
            this.allowNonLinearNavigation = defaultAllowNonLinearNavigation;
        }
    }
}