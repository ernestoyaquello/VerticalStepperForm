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

    private FormStyle style;
    private StepperFormListener listener;
    private List<StepWrapper> steps;

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
    public FormBuilder setup(StepperFormListener stepperFormListener, FormStep... steps) {
        return new FormBuilder(this, stepperFormListener, steps);
    }

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepperFormListener The listener for the stepper form events.
     * @param steps A list with the steps that will be displayed in the form.
     * @return An instance of the stepper form builder. Use it to configure and initialize the form.
     */
    public FormBuilder setup(StepperFormListener stepperFormListener, List<FormStep> steps) {
        FormStep[] stepsArray = steps.toArray(new FormStep[steps.size()]);
        return new FormBuilder(this, stepperFormListener, stepsArray);
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
            StepWrapper step = steps.get(stepPosition);
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
            StepWrapper step = steps.get(stepPosition);
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
            StepWrapper step = steps.get(i);
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
            StepWrapper step = steps.get(openedStepPosition);
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

    private void initializeForm(StepperFormListener listener, FormStyle style, StepWrapper[] stepsArray) {
        this.listener = listener;
        this.style = style;
        this.steps = Arrays.asList(stepsArray);

        progressBar.setMax(steps.size());

        if (!style.displayBottomNavigation) {
            hideBottomNavigation();
        }

        setObserverForKeyboard();

        for (int i = 0; i < steps.size(); i++) {
            View stepLayout = initializeStep(i);
            formContentView.addView(stepLayout);
        }

        goToStep(0, false);
    }

    private View initializeStep(int position) {
        final int stepPosition = position;
        StepWrapper step = steps.get(stepPosition);

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
        boolean isLast = (stepPosition + 1) == steps.size();

        return step.initialize(
                this,
                style,
                getContext(),
                formContentView,
                clickOnNextButtonListener,
                clickOnHeaderListener,
                stepPosition,
                isLast);
    }

    private void openStep(int stepToOpenPosition, boolean useAnimations) {
        if (stepToOpenPosition >= 0 && stepToOpenPosition < steps.size()) {

            int stepToClosePosition = getOpenStepPosition();
            if (stepToClosePosition != -1) {
                StepWrapper stepToClose = steps.get(stepToClosePosition);
                stepToClose.close(this, stepToClosePosition, useAnimations);
            }

            StepWrapper stepToOpen = steps.get(stepToOpenPosition);
            stepToOpen.open(this, stepToOpenPosition, useAnimations);

            updateBottomNavigationButtons();
            scrollToOpenStep(useAnimations);

            if (stepToOpen.isConfirmationStep()) {
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
                    // The keyboard has probably been opened, so we scroll to the step to keep it in sight
                    scrollToOpenStep(true);
                }
            }
        });
    }

    protected void updateBottomNavigationButtons() {
        int stepPosition = getOpenStepPosition();
        StepWrapper step = steps.get(stepPosition);

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
        if (numberOfCompletedSteps >= 0 && numberOfCompletedSteps <= steps.size()) {
            progressBar.setProgress(numberOfCompletedSteps);
        }
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
            StepWrapper step = steps.get(i);

            step.restoreTitle(i, titles[i]);
            step.restoreSubtitle(i, subtitles[i]);
            step.restoreButtonText(i, buttonTexts[i]);

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
        String[] titles = new String[steps.size()];
        String[] subtitles = new String[steps.size()];
        String[] buttonTexts = new String[steps.size()];
        String[] errorMessages = new String[steps.size()];
        for (int i = 0; i < completedSteps.length; i++) {
            StepWrapper step = steps.get(i);
            completedSteps[i] = step.isCompleted();
            titles[i] = step.getTitle();
            subtitles[i] = step.getSubtitle();
            buttonTexts[i] = step.getButtonText();
            if (!step.isCompleted()) {
                errorMessages[i] = step.getCurrentErrorMessage();
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

    public class FormBuilder {

        private VerticalStepperFormLayout formLayout;
        private StepperFormListener listener;
        private StepWrapper[] steps;

        private VerticalStepperFormLayout.FormStyle style;

        FormBuilder(VerticalStepperFormLayout formLayout, StepperFormListener listener, FormStep[] steps) {
            this.formLayout = formLayout;
            this.listener = listener;
            this.style = new VerticalStepperFormLayout.FormStyle();
            this.steps = new StepWrapper[steps.length];
            for (int i = 0; i < steps.length; i++) {
                this.steps[i] = new StepWrapper(steps[i]);
            }
        }

        /**
         * Sets the text to be displayed in the button of all the steps but the last one.
         *
         * @param stepButtonText The text to display in the button of all the steps but the last one.
         * @return The builder instance.
         */
        public FormBuilder stepButtonText(String stepButtonText) {
            style.stepButtonText = stepButtonText;

            return this;
        }

        /**
         * Sets the text to be displayed in the last step's button.
         *
         * @param lastStepButtonText The text to display in the last step's button.
         * @return The builder instance.
         */
        public FormBuilder lastStepButtonText(String lastStepButtonText) {
            style.lastStepButtonText = lastStepButtonText;

            return this;
        }

        /**
         * Sets the title to be displayed on the confirmation step.
         *
         * @param confirmationStepTitle The title of the confirmation step.
         * @return The builder instance.
         */
        public FormBuilder confirmationStepTitle(String confirmationStepTitle) {
            style.confirmationStepTitle = confirmationStepTitle;

            return this;
        }

        /**
         * Sets the primary color of the form. Will be used for the left circles and the buttons.
         * To set a different background color for buttons and left circles, please use
         * stepNumberBackgroundColor() and buttonBackgroundColor().
         *
         * @param colorPrimary The primary color.
         * @return The builder instance.
         */
        public FormBuilder primaryColor(int colorPrimary) {
            style.stepNumberBackgroundColor = colorPrimary;
            style.buttonBackgroundColor = colorPrimary;

            return this;
        }

        /**
         * Sets the dark primary color. Will be displayed as the background color of the buttons
         * while clicked.
         *
         * @param colorPrimaryDark Primary color (dark)
         * @return The builder instance.
         */
        public FormBuilder primaryDarkColor(int colorPrimaryDark) {
            style.buttonPressedBackgroundColor = colorPrimaryDark;

            return this;
        }

        /**
         * Sets the background color of the left circles.
         *
         * @param stepNumberBackgroundColor Background color of the left circles.
         * @return The builder instance.
         */
        public FormBuilder stepNumberBackgroundColor(int stepNumberBackgroundColor) {
            style.stepNumberBackgroundColor = stepNumberBackgroundColor;

            return this;
        }

        /**
         * Sets the background color of the buttons.
         *
         * @param buttonBackgroundColor Background color of the buttons.
         * @return The builder instance.
         */
        public FormBuilder buttonBackgroundColor(int buttonBackgroundColor) {
            style.buttonBackgroundColor = buttonBackgroundColor;

            return this;
        }

        /**
         * Sets the background color of the buttons when pressed.
         *
         * @param buttonPressedBackgroundColor Background color of the buttons when pressed.
         * @return The builder instance.
         */
        public FormBuilder buttonPressedBackgroundColor(int buttonPressedBackgroundColor) {
            style.buttonPressedBackgroundColor = buttonPressedBackgroundColor;

            return this;
        }

        /**
         * Sets the text color of the left circles.
         *
         * @param stepNumberTextColor Text color of the left circles.
         * @return The builder instance.
         */
        public FormBuilder stepNumberTextColor(int stepNumberTextColor) {
            style.stepNumberTextColor = stepNumberTextColor;

            return this;
        }

        /**
         * Sets the text color of the step title.
         *
         * @param stepTitleTextColor The color of the step title.
         * @return This builder instance.
         */
        public FormBuilder stepTitleTextColor(int stepTitleTextColor) {
            style.stepTitleTextColor = stepTitleTextColor;

            return this;
        }

        /**
         * Sets the text color of the step subtitle.
         *
         * @param stepSubtitleTextColor The color of the step subtitle.
         * @return This builder instance.
         */
        public FormBuilder stepSubtitleTextColor(int stepSubtitleTextColor) {
            style.stepSubtitleTextColor = stepSubtitleTextColor;

            return this;
        }

        /**
         * Sets the text color of the buttons.
         *
         * @param buttonTextColor Text color of the buttons.
         * @return The builder instance.
         */
        public FormBuilder buttonTextColor(int buttonTextColor) {
            style.buttonTextColor = buttonTextColor;

            return this;
        }

        /**
         * Sets the text color of the buttons when clicked.
         *
         * @param buttonPressedTextColor Text color of the buttons when clicked.
         * @return The builder instance.
         */
        public FormBuilder buttonPressedTextColor(int buttonPressedTextColor) {
            style.buttonPressedTextColor = buttonPressedTextColor;

            return this;
        }

        /**
         * Sets the error message color.
         *
         * @param errorMessageTextColor Error message color.
         * @return The builder instance.
         */
        public FormBuilder errorMessageTextColor(int errorMessageTextColor) {
            style.errorMessageTextColor = errorMessageTextColor;

            return this;
        }

        /**
         * Specifies whether or not the bottom navigation bar will be displayed.
         *
         * @param displayBottomNavigationBar True to display it; false otherwise.
         * @return The builder instance.
         */
        public FormBuilder displayBottomNavigation(boolean displayBottomNavigationBar) {
            style.displayBottomNavigation = displayBottomNavigationBar;

            return this;
        }

        /**
         * Specifies whether or not the vertical lines should be displayed when the steps are
         * collapsed.
         *
         * @param displayVerticalLineWhenStepsAreCollapsed True to show the lines on collapsed steps;
         *                                                 false to not.
         * @return The builder instance.
         */
        public FormBuilder displayVerticalLineWhenStepsAreCollapsed(boolean displayVerticalLineWhenStepsAreCollapsed) {
            style.displayVerticalLineWhenStepsAreCollapsed = displayVerticalLineWhenStepsAreCollapsed;

            return this;
        }

        /**
         * Sets the alpha of the disabled elements.
         *
         * @param alpha Alpha level of disabled elements.
         * @return The builder instance.
         */
        public FormBuilder alphaOfDisabledElements(float alpha) {
            style.alphaOfDisabledElements = alpha;

            return this;
        }

        /**
         * Specifies whether or not a "Next" button should be automatically displayed within each step.
         * If set to false, the step buttons will be missing and manual calls to
         * goToStep(stepPosition + 1, true) will be required in order to move to the next step.
         *
         * @param displayStepButtons True to display a button on each step; false to not.
         * @return The builder instance.
         */
        public FormBuilder displayStepButtons(boolean displayStepButtons) {
            style.displayStepButtons = displayStepButtons;

            return this;
        }

        /**
         * Specifies whether or not a confirmation step should be added as an extra step at the end of
         * the form.
         *
         * @param includeConfirmationStep True to add a confirmation step as the final step of the form;
         *                                false to not.
         * @return The builder instance.
         */
        public FormBuilder includeConfirmationStep(boolean includeConfirmationStep) {
            style.includeConfirmationStep = includeConfirmationStep;

            return this;
        }

        /**
         * Sets up the form and initializes it.
         */
        public void init() {

            if (style.includeConfirmationStep) {
                StepWrapper[] currentSteps = steps;

                steps = new StepWrapper[steps.length + 1];
                for (int i = 0; i < currentSteps.length; i++) {
                    steps[i] = currentSteps[i];
                }

                steps[currentSteps.length] = new StepWrapper(null, true);
            }

            formLayout.initializeForm(listener, style, steps);
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