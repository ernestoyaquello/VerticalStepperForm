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

import ernestoyaquello.com.verticalstepperform.listener.VerticalStepperFormListener;
import ernestoyaquello.com.verticalstepperform.util.model.Step;

/**
 * Custom layout that implements a vertical stepper form.
 */
public class VerticalStepperFormLayout extends LinearLayout {

    private FormStyle style;
    private VerticalStepperFormListener listener;
    private List<ExtendedStep> steps;

    private LinearLayout formContentView;
    private ScrollView stepsScrollView;
    private ProgressBar progressBar;
    private AppCompatImageButton previousStepButton, nextStepButton;
    private View bottomNavigationView;

    private boolean formCompleted;

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
    public FormBuilder setup(VerticalStepperFormListener stepperFormListener, Step[] steps) {
        return new FormBuilder(this, stepperFormListener, steps);
    }

    void initialiseVerticalStepperForm(VerticalStepperFormListener listener, FormStyle style, ExtendedStep[] steps) {
        this.listener = listener;
        this.style = style;
        this.steps = Arrays.asList(steps);

        initialize();
    }

    /**
     * Marks the currently open step, if any, as completed.
     *
     * @param useAnimations Indicates whether or not the step layout will be updated using animations.
     */
    public void markOpenStepAsCompleted(boolean useAnimations) {
        markStepAsCompleted(getOpenStepPosition(), useAnimations);
    }

    /**
     * Marks the currently open step, if any, as uncompleted.
     *
     * @param errorMessage The error message to display. Null or empty to not show any.
     * @param useAnimations Indicates whether or not the step layout will be updated using animations.
     */
    public void markOpenStepAsUncompleted(String errorMessage, boolean useAnimations) {
        markStepAsUncompleted(getOpenStepPosition(), errorMessage, useAnimations);
    }

    /**
     * Marks the specified step as completed.
     *
     * @param stepPosition The step position.
     * @param useAnimations Indicates whether or not the step layout will be updated using animations.
     */
    public void markStepAsCompleted(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            ExtendedStep step = steps.get(stepPosition);
            step.markAsCompleted(useAnimations);

            updateBottomNavigationButtons();
            refreshFormProgress();
        }
    }

    /**
     * Marks the specified step as uncompleted and displays an error message if required.
     *
     * @param stepPosition The step position.
     * @param errorMessage The error message to display. Null or empty to not show any.
     * @param useAnimations Indicates whether or not the step layout will be updated using animations.
     */
    public void markStepAsUncompleted(int stepPosition, String errorMessage, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            ExtendedStep step = steps.get(stepPosition);
            step.markAsUncompleted(errorMessage, useAnimations);

            updateBottomNavigationButtons();
            refreshFormProgress();
        }
    }

    /**
     * Determines whether the open step is marked as completed or not.
     *
     * @return True if the open step is currently marked as completed; false otherwise.
     */
    public boolean isOpenStepCompleted() {
        return isStepCompleted(getOpenStepPosition());
    }

    /**
     * Determines whether the specified step is marked as completed or not.
     *
     * @param stepPosition The step position.
     * @return True if the step is currently marked as completed; false otherwise.
     */
    public boolean isStepCompleted(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            return steps.get(stepPosition).isCompleted();
        }

        return false;
    }

    /**
     * Determines whether there is at least one step marked as completed.
     *
     * @return True if at least one step has been marked as completed; false otherwise.
     */
    public boolean isAnyStepCompleted() {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).isCompleted()) {
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
            previousStepsAreCompleted &= steps.get(i).isCompleted();
        }

        return previousStepsAreCompleted;
    }

    /**
     * If possible, goes to the step that is positioned after the currently open one, closing the
     * current one and opening the next one.
     * It is only possible to navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public boolean goToNextStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() + 1, useAnimations);
    }

    /**
     * If possible, goes to the step that is positioned before the currently open one, closing the
     * current one and opening the previous one.
     * It is only possible to navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public boolean goToPreviousStep(boolean useAnimations) {
        return goToStep(getOpenStepPosition() - 1, useAnimations);
    }

    /**
     * If possible, goes to a certain step, closing the currently open one and opening the target one.
     * It is only possible to navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param stepPosition The step position.
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToStep(int stepPosition, boolean useAnimations) {
        if (formCompleted) {
            return false;
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition != stepPosition && stepPosition >= 0 && stepPosition <= steps.size()) {
            boolean previousStepsAreCompleted = areAllPreviousStepsCompleted(stepPosition);
            if (previousStepsAreCompleted) {
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
    public int getOpenStepPosition() {
        for (int i = 0; i < steps.size(); i++) {
            ExtendedStep step = steps.get(i);
            if (step.isOpen()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the content layout of the currently open step (i.e., the layout which was provided at
     * start to setup the step).
     *
     * @return If found, the layout. If not, null.
     */
    public View getOpenStepContentLayout() {
        return getStepContentLayout(getOpenStepPosition());
    }

    /**
     * Gets the content layout of the specified step (i.e., the layout which was provided at start
     * to setup the step).
     *
     * @param stepPosition The step position.
     * @return If found, the layout. If not, null.
     */
    public View getStepContentLayout(int stepPosition) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            return steps.get(stepPosition).getContentLayout();
        }

        return null;
    }

    /**
     * Updates the subtitle of the currently open step. If the provided subtitle is null or empty,
     * no subtitle will be displayed on the step.
     *
     * @param subtitle The subtitle to display in the step.
     * @param useAnimations Indicates whether or not the subtitle will be show/hidden with animations.
     */
    public void updateOpenStepSubtitle(String subtitle, boolean useAnimations) {
        updateStepSubtitle(getOpenStepPosition(), subtitle, useAnimations);
    }

    /**
     * Updates the subtitle of the specified step. If the provided subtitle is null or empty, no
     * subtitle will be displayed on the step.
     *
     * @param stepPosition The step position.
     * @param subtitle The subtitle to display in the step.
     * @param useAnimations Indicates whether or not the subtitle will be show/hidden with animations.
     */
    public void updateStepSubtitle(int stepPosition, String subtitle, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            ExtendedStep step = steps.get(stepPosition);
            step.updateSubtitle(subtitle, useAnimations);
        }
    }

    /**
     * Removes and hides the subtitle of the currently open step.
     *
     * @param useAnimations Indicates whether or not the subtitle will be hidden using animations.
     */
    public void removeOpenStepSubtitle(boolean useAnimations) {
        removeStepSubtitle(getOpenStepPosition(), useAnimations);
    }

    /**
     * Removes and hides the subtitle of the step located at the specified position.
     *
     * @param stepPosition The step position.
     * @param useAnimations Indicates whether or not the subtitle will be hidden using animations.
     */
    public void removeStepSubtitle(int stepPosition, boolean useAnimations) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            updateStepSubtitle(stepPosition, "", useAnimations);
        }
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
     * Scrolls to the specified step.
     *
     * @param stepPosition The step position.
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public void scrollToStep(final int stepPosition, final boolean smoothScroll) {
        if (stepPosition >= 0 && stepPosition < steps.size()) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    if (smoothScroll) {
                        stepsScrollView.smoothScrollTo(0, steps.get(stepPosition).getStepLayout().getTop());
                    } else {
                        stepsScrollView.scrollTo(0, steps.get(stepPosition).getStepLayout().getTop());
                    }
                }
            });
        }
    }

    /**
     * Scrolls to the open step.
     *
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public void scrollToOpenStep(boolean smoothScroll) {
        scrollToStep(getOpenStepPosition(), smoothScroll);
    }

    /**
     * If all the steps are currently marked as completed, it completes the form, disabling the
     * step navigation and the button of the currently open step. To revert these changes, call
     * cancelFormCompletionAttempt().
     *
     * @return True if the form was completed; false otherwise.
     */
    public boolean completeForm() {
        return goToStep(steps.size(), true);
    }

    /**
     * To be used after a failed form completion attempt, this method re-activates the navigation to
     * other steps and re-enables the button of the currently open step.
     * Useful when saving the form data fails and you want to allow the user to use the form again
     * in order to re-send the data.
     */
    public void cancelFormCompletionAttempt() {
        int openedStepPosition = getOpenStepPosition();
        if (openedStepPosition >= 0 && openedStepPosition < steps.size()) {
            ExtendedStep step = steps.get(openedStepPosition);
            step.enableNextButton();

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
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).isCompleted()) {
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
        return steps.size();
    }

    private void onConstructed(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.vertical_stepper_form_layout, this, true);

        // TODO Move all these default style values to resource files
        // TODO Get these values from XML attributes whenever possible
        FormStyle.defaultStepButtonText =
                getResources().getString(R.string.vertical_form_stepper_form_continue_button);
        FormStyle.defaultLastStepButtonText =
                getResources().getString(R.string.vertical_form_stepper_form_confirm_button);
        FormStyle.defaultConfirmationStepTitle =
                getResources().getString(R.string.vertical_form_stepper_form_confirmation_step_title);
        FormStyle.defaultAlphaOfDisabledElements = 0.25f;
        FormStyle.defaultStepNumberBackgroundColor = Color.rgb(63, 81, 181);
        FormStyle.defaultButtonBackgroundColor = Color.rgb(63, 81, 181);
        FormStyle.defaultButtonPressedBackgroundColor = Color.rgb(48, 63, 159);
        FormStyle.defaultStepNumberTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultStepTitleTextColor = Color.rgb(33, 33, 33);
        FormStyle.defaultStepSubtitleTextColor = Color.rgb(162, 162, 162);
        FormStyle.defaultButtonTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultButtonPressedTextColor = Color.rgb(255, 255, 255);
        FormStyle.defaultErrorMessageTextColor = Color.rgb(175, 18, 18);
        FormStyle.defaultDisplayBottomNavigation = true;
        FormStyle.defaultDisplayVerticalLineWhenStepsAreCollapsed = true;
        FormStyle.defaultDisplayStepButtons = true;
        FormStyle.defaultIncludeConfirmationStep = true;
    }

    private void initialize() {
        progressBar.setMax(steps.size());

        if (!style.displayBottomNavigation) {
            hideBottomNavigation();
        }

        setObserverForKeyboard();

        for (int i = 0; i < steps.size(); i++) {
            final int stepPosition = i;
            ExtendedStep step = steps.get(stepPosition);
            boolean isLast = (i + 1) == steps.size();

            View.OnClickListener clickOnNextButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToStep((stepPosition + 1), true);
                }
            };
            View.OnClickListener clickOnHeaderListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToStep(stepPosition, true);
                }
            };
            View stepContentLayout = !step.isConfirmationStep()
                    ? listener.getStepContentLayout(i)
                    : null;

            View stepLayout = step.initialize(
                    style,
                    getContext(),
                    formContentView,
                    stepContentLayout,
                    clickOnNextButtonListener,
                    clickOnHeaderListener,
                    stepPosition,
                    isLast);

            formContentView.addView(stepLayout);
        }

        goToStep(0, false);
    }

    private void openStep(int stepToOpenPosition, boolean useAnimations) {
        if (stepToOpenPosition >= 0 && stepToOpenPosition < steps.size()) {

            int stepToClosePosition = getOpenStepPosition();
            if (stepToClosePosition != -1) {
                ExtendedStep stepToClose = steps.get(stepToClosePosition);
                stepToClose.close(useAnimations);

                if (!stepToClose.isConfirmationStep()) {
                    listener.onStepClosed(stepToClosePosition, useAnimations);
                }
            }

            ExtendedStep stepToOpen = steps.get(stepToOpenPosition);
            stepToOpen.open(useAnimations);

            updateBottomNavigationButtons();
            scrollToOpenStep(useAnimations);

            if (!stepToOpen.isConfirmationStep()) {
                listener.onStepOpened(stepToOpenPosition, useAnimations);
            } else {
                refreshFormProgress();
            }
        } else if (stepToOpenPosition == steps.size()) {
            attemptToCompleteForm();
        }
    }

    private void setObserverForKeyboard() {
        formContentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                formContentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = formContentView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // The keyboard has probably been opened, so we scroll to the step
                    scrollToOpenStep(true);
                }
            }
        });
    }

    protected void updateBottomNavigationButtons() {
        int stepPosition = getOpenStepPosition();
        ExtendedStep step = steps.get(stepPosition);

        if (stepPosition == 0 || formCompleted) {
            disablePreviousButtonInBottomNavigation();
        } else {
            enablePreviousButtonInBottomNavigation();
        }

        if (!formCompleted && (step.isCompleted() && (stepPosition + 1) < steps.size())) {
            enableNextButtonInBottomNavigation();
        } else {
            disableNextButtonInBottomNavigation();
        }
    }

    private void setProgress(int numberOfCompletedSteps) {
        if (numberOfCompletedSteps >= 0 && numberOfCompletedSteps <= steps.size()) {
            progressBar.setProgress(numberOfCompletedSteps);
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

    private void attemptToCompleteForm() {
        if (formCompleted) {
            return;
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition >= 0 && openStepPosition < steps.size()) {
            formCompleted = true;
            steps.get(openStepPosition).disableNextButton();
            updateBottomNavigationButtons();

            listener.onCompletedForm();
        }
    }

    private void restoreFromState(
            int positionToOpen,
            boolean[] completedSteps,
            String[] subtitles,
            String[] errorMessages) {

        for (int i = 0; i < completedSteps.length; i++) {
            updateStepSubtitle(i, subtitles[i], false);
            if (completedSteps[i]) {
                markStepAsCompleted(i, false);
            } else {
                markStepAsUncompleted(i, errorMessages[i], false);
            }
        }

        goToStep(positionToOpen, false);
        refreshFormProgress();
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

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        boolean[] completedSteps = new boolean[steps.size()];
        String[] subtitles = new String[steps.size()];
        String[] errorMessages = new String[steps.size()];
        for (int i = 0; i < completedSteps.length; i++) {
            ExtendedStep step = steps.get(i);

            completedSteps[i] = step.isCompleted();
            subtitles[i] = step.getSubtitle();
            if (!step.isCompleted()) {
                errorMessages[i] = step.getCurrentErrorMessage();
            }
        }

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("openStep", this.getOpenStepPosition());
        bundle.putBooleanArray("completedSteps", completedSteps);
        bundle.putStringArray("subtitles", subtitles);
        bundle.putStringArray("errorMessages", errorMessages);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            String[] errorMessages = bundle.getStringArray("errorMessages");
            String[] subtitles = bundle.getStringArray("subtitles");
            boolean[] completedSteps = bundle.getBooleanArray("completedSteps");
            int positionToOpen = bundle.getInt("openStep");
            state = bundle.getParcelable("superState");

            restoreFromState(positionToOpen, completedSteps, subtitles, errorMessages);
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * This method returns the layout ID that will be used to inflate the entire step layout.
     *
     * Please note that this is not the layout ID of the step's content layout; this layout ID
     * references the entire step layout, which includes the header, the "Next" button, etc.
     *
     * Even though it is only for internal use of the library, it can be extended to force the use
     * of a modified version of the original layout.
     *
     * @return The step layout ID.
     */
    protected static int getInternalStepLayout() {
        return R.layout.step_layout;
    }

    static class FormStyle {

        private static String defaultStepButtonText;
        private static String defaultLastStepButtonText;
        private static String defaultConfirmationStepTitle;
        private static float defaultAlphaOfDisabledElements;
        private static int defaultStepNumberBackgroundColor;
        private static int defaultButtonBackgroundColor;
        private static int defaultButtonPressedBackgroundColor;
        private static int defaultStepNumberTextColor;
        private static int defaultStepTitleTextColor;
        private static int defaultStepSubtitleTextColor;
        private static int defaultButtonTextColor;
        private static int defaultButtonPressedTextColor;
        private static int defaultErrorMessageTextColor;
        private static boolean defaultDisplayBottomNavigation;
        private static boolean defaultDisplayVerticalLineWhenStepsAreCollapsed;
        private static boolean defaultDisplayStepButtons;
        private static boolean defaultIncludeConfirmationStep;

        String stepButtonText;
        String lastStepButtonText;
        String confirmationStepTitle;
        float alphaOfDisabledElements;
        int stepNumberBackgroundColor;
        int buttonBackgroundColor;
        int buttonPressedBackgroundColor;
        int stepNumberTextColor;
        int stepTitleTextColor;
        int stepSubtitleTextColor;
        int buttonTextColor;
        int buttonPressedTextColor;
        int errorMessageTextColor;
        boolean displayBottomNavigation;
        boolean displayVerticalLineWhenStepsAreCollapsed;
        boolean displayStepButtons;
        boolean includeConfirmationStep;

        FormStyle() {
            this.stepButtonText = defaultStepButtonText;
            this.lastStepButtonText = defaultLastStepButtonText;
            this.confirmationStepTitle = defaultConfirmationStepTitle;
            this.alphaOfDisabledElements = defaultAlphaOfDisabledElements;
            this.stepNumberBackgroundColor = defaultStepNumberBackgroundColor;
            this.buttonBackgroundColor = defaultButtonBackgroundColor;
            this.buttonPressedBackgroundColor = defaultButtonPressedBackgroundColor;
            this.stepNumberTextColor = defaultStepNumberTextColor;
            this.stepTitleTextColor = defaultStepTitleTextColor;
            this.stepSubtitleTextColor = defaultStepSubtitleTextColor;
            this.buttonTextColor = defaultButtonTextColor;
            this.buttonPressedTextColor = defaultButtonPressedTextColor;
            this.errorMessageTextColor = defaultErrorMessageTextColor;
            this.displayBottomNavigation = defaultDisplayBottomNavigation;
            this.displayVerticalLineWhenStepsAreCollapsed = defaultDisplayVerticalLineWhenStepsAreCollapsed;
            this.displayStepButtons = defaultDisplayStepButtons;
            this.includeConfirmationStep = defaultIncludeConfirmationStep;
        }
    }
}