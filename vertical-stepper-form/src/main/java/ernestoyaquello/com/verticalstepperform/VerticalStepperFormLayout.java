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
     * If possible, goes to the step that is positioned after the currently open one, closing the
     * current one and opening the next one.
     * It is only possible to navigate to a certain step if all the previous ones are marked as completed.
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
     * It is only possible to navigate to a certain step if all the previous ones are marked as completed.
     *
     * @param useAnimations Indicates whether or not the affected steps will be opened/closed using
     *                      animations.
     * @return True if the navigation to the step was performed; false otherwise.
     */
    public synchronized boolean goToPreviousStep(boolean useAnimations) {
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
        if (openStepPosition != stepPosition && stepPosition >= 0 && stepPosition <= stepHelpers.size()) {
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
     * Scrolls to the specified step.
     *
     * @param stepPosition The step position.
     * @param smoothScroll Determines whether the scrolling should be smooth or abrupt.
     */
    public void scrollToStep(final int stepPosition, final boolean smoothScroll) {
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    if (smoothScroll) {
                        stepsScrollView.smoothScrollTo(0, stepHelpers.get(stepPosition).getStepInstance().getEntireStepLayout().getTop());
                    } else {
                        stepsScrollView.scrollTo(0, stepHelpers.get(stepPosition).getStepInstance().getEntireStepLayout().getTop());
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
    public synchronized void scrollToOpenStep(boolean smoothScroll) {
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
        return goToStep(stepHelpers.size(), true);
    }

    /**
     * To be used after a failed form completion attempt, this method re-activates the navigation to
     * other steps and re-enables the button of the currently open step.
     * Useful when saving the form data fails and you want to allow the user to use the form again
     * in order to re-send the data.
     */
    public synchronized void cancelFormCompletionAttempt() {
        int openedStepPosition = getOpenStepPosition();
        if (openedStepPosition >= 0 && openedStepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(openedStepPosition);
            stepHelper.enableNextButton();

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
        FormStyle.defaultDisplayStepDataInSubtitleOfClosedSteps = true;

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

        setObserverForKeyboard();

        for (int i = 0; i < stepHelpers.size(); i++) {
            View stepLayout = initializeStepHelper(i);
            formContentView.addView(stepLayout);
        }

        goToStep(0, false);
    }

    private View initializeStepHelper(int position) {
        final int stepPosition = position;
        StepHelper stepHelper = stepHelpers.get(stepPosition);

        OnClickListener clickOnNextButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep((stepPosition + 1), true);
            }
        };
        OnClickListener clickOnHeaderListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep(stepPosition, true);
            }
        };

        boolean isLast = (stepPosition + 1) == stepHelpers.size();

        return stepHelper.initialize(this, style, formContentView, stepPosition, isLast);
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
                    // The keyboard has probably been opened, so we scroll to the step to keep it in sight
                    scrollToOpenStep(true);
                }
            }
        });
    }

    protected synchronized void updateBottomNavigationButtons() {
        int stepPosition = getOpenStepPosition();
        if (stepPosition >= 0 && stepPosition < stepHelpers.size()) {
            StepHelper stepHelper = stepHelpers.get(stepPosition);

            if (stepPosition == 0 || formCompleted) {
                disablePreviousButtonInBottomNavigation();
            } else {
                enablePreviousButtonInBottomNavigation();
            }

            if (!formCompleted && (stepHelper.getStepInstance().isCompleted() && (stepPosition + 1) < stepHelpers.size())) {
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

    private synchronized void attemptToCompleteForm() {
        if (formCompleted) {
            return;
        }

        int openStepPosition = getOpenStepPosition();
        if (openStepPosition >= 0 && openStepPosition < stepHelpers.size()) {
            formCompleted = true;
            stepHelpers.get(openStepPosition).disableNextButton();
            updateBottomNavigationButtons();

            if (listener != null) {
                listener.onCompletedForm();
            }
        }
    }

    private void restoreFromState(
            int positionToOpen,
            boolean[] completedSteps,
            String[] titles,
            String[] subtitles,
            String[] buttonTexts,
            String[] errorMessages) {

        for (int i = 0; i < completedSteps.length; i++) {
            StepHelper stepHelper = stepHelpers.get(i);

            if (completedSteps[i]) {
                stepHelper.getStepInstance().markAsCompleted(false);
            } else {
                stepHelper.getStepInstance().markAsUncompleted(errorMessages[i], false);
            }
            stepHelper.getStepInstance().updateTitle(titles[i], false);
            stepHelper.getStepInstance().updateSubtitle(subtitles[i], false);
            stepHelper.getStepInstance().updateButtonText(buttonTexts[i], false);
        }

        for (int i = 0; i <= positionToOpen; i++) {
            goToStep(i, false);
        }

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
            buttonTexts[i] = stepHelper.getStepInstance().getButtonText();
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

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            String[] errorMessages = bundle.getStringArray("errorMessages");
            String[] buttonTexts = bundle.getStringArray("buttonTexts");
            String[] subtitles = bundle.getStringArray("subtitles");
            String[] titles = bundle.getStringArray("titles");
            boolean[] completedSteps = bundle.getBooleanArray("completedSteps");
            int positionToOpen = bundle.getInt("openStep");
            state = bundle.getParcelable("superState");

            restoreFromState(positionToOpen, completedSteps, titles, subtitles, buttonTexts, errorMessages);
        }
        super.onRestoreInstanceState(state);
    }

    class FormStepListener implements Step.InternalFormStepListener {

        @Override
        public void onUpdatedTitle(int stepPosition, boolean useAnimations) {
            // Do nothing
        }

        @Override
        public void onUpdatedSubtitle(int stepPosition, boolean useAnimations) {
            // Do nothing
        }

        @Override
        public void onUpdatedButtonText(int stepPosition, boolean useAnimations) {
            // Do nothing
        }

        @Override
        public void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            refreshFormProgress();
        }

        @Override
        public void onUpdatedStepVisibility(int stepPosition, boolean useAnimations) {
            updateBottomNavigationButtons();
            scrollToStep(stepPosition, useAnimations);
        }
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
        private static boolean defaultDisplayStepDataInSubtitleOfClosedSteps;

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

        // TODO Add method in builder
        boolean displayStepDataInSubtitleOfClosedSteps;

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
            this.displayStepDataInSubtitleOfClosedSteps = defaultDisplayStepDataInSubtitleOfClosedSteps;
        }
    }
}