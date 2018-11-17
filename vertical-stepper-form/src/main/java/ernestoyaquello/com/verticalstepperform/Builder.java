package ernestoyaquello.com.verticalstepperform;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

/**
 * The builder to set up and initialize the form.
 */
public class Builder {

    private VerticalStepperFormLayout formLayout;
    private StepperFormListener listener;
    private StepHelper[] steps;

    private VerticalStepperFormLayout.FormStyle style;

    Builder(VerticalStepperFormLayout formLayout, StepperFormListener listener, Step[] steps) {
        this.formLayout = formLayout;
        this.listener = listener;
        this.style = new VerticalStepperFormLayout.FormStyle();
        this.steps = new StepHelper[steps.length];
        for (int i = 0; i < steps.length; i++) {
            this.steps[i] = new StepHelper(formLayout.internalListener, steps[i]);
        }
    }

    /**
     * Sets the text to be displayed in the "Continue" button of all the steps but the last one.
     * To set up the text of the last step's button, use lastStepNextButtonText().
     *
     * @param stepNextButtonText The text to display in the "Continue" button of all the steps but
     *                           the last one.
     * @return The builder instance.
     */
    public Builder stepNextButtonText(String stepNextButtonText) {
        style.stepNextButtonText = stepNextButtonText;

        return this;
    }

    /**
     * Sets the text to be displayed in the last step's "Confirm" button.
     *
     * @param lastStepNextButtonText The text to display in the last step's "Confirm" button.
     * @return The builder instance.
     */
    public Builder lastStepNextButtonText(String lastStepNextButtonText) {
        style.lastStepNextButtonText = lastStepNextButtonText;

        return this;
    }

    /**
     * Sets the text to be displayed in the last step's "Cancel" button.
     * To display this button in the last step, set displayCancelButtonInLastStep to true.
     *
     * @param lastStepCancelButtonText The text to display in the last step's "Cancel" button.
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonText(String lastStepCancelButtonText) {
        style.lastStepCancelButtonText = lastStepCancelButtonText;

        return this;
    }

    /**
     * Sets the title to be displayed on the confirmation step.
     *
     * @param confirmationStepTitle The title of the confirmation step.
     * @return The builder instance.
     */
    public Builder confirmationStepTitle(String confirmationStepTitle) {
        style.confirmationStepTitle = confirmationStepTitle;

        return this;
    }

    /**
     * Sets the subtitle to be displayed on the confirmation step.
     *
     * @param confirmationStepSubtitle The subtitle of the confirmation step.
     * @return The builder instance.
     */
    public Builder confirmationStepSubtitle(String confirmationStepSubtitle) {
        style.confirmationStepSubtitle = confirmationStepSubtitle;

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
    public Builder primaryColor(int colorPrimary) {
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
    public Builder primaryDarkColor(int colorPrimaryDark) {
        style.buttonPressedBackgroundColor = colorPrimaryDark;

        return this;
    }

    /**
     * Sets the background color of the left circles.
     *
     * @param stepNumberBackgroundColor Background color of the left circles.
     * @return The builder instance.
     */
    public Builder stepNumberBackgroundColor(int stepNumberBackgroundColor) {
        style.stepNumberBackgroundColor = stepNumberBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the buttons.
     *
     * @param buttonBackgroundColor Background color of the buttons.
     * @return The builder instance.
     */
    public Builder buttonBackgroundColor(int buttonBackgroundColor) {
        style.buttonBackgroundColor = buttonBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the buttons when pressed.
     *
     * @param buttonPressedBackgroundColor Background color of the buttons when pressed.
     * @return The builder instance.
     */
    public Builder buttonPressedBackgroundColor(int buttonPressedBackgroundColor) {
        style.buttonPressedBackgroundColor = buttonPressedBackgroundColor;

        return this;
    }

    /**
     * Sets the text color of the left circles.
     *
     * @param stepNumberTextColor Text color of the left circles.
     * @return The builder instance.
     */
    public Builder stepNumberTextColor(int stepNumberTextColor) {
        style.stepNumberTextColor = stepNumberTextColor;

        return this;
    }

    /**
     * Sets the text color of the step title.
     *
     * @param stepTitleTextColor The color of the step title.
     * @return This builder instance.
     */
    public Builder stepTitleTextColor(int stepTitleTextColor) {
        style.stepTitleTextColor = stepTitleTextColor;

        return this;
    }

    /**
     * Sets the text color of the step subtitle.
     *
     * @param stepSubtitleTextColor The color of the step subtitle.
     * @return This builder instance.
     */
    public Builder stepSubtitleTextColor(int stepSubtitleTextColor) {
        style.stepSubtitleTextColor = stepSubtitleTextColor;

        return this;
    }

    /**
     * Sets the text color of the buttons.
     *
     * @param buttonTextColor Text color of the buttons.
     * @return The builder instance.
     */
    public Builder buttonTextColor(int buttonTextColor) {
        style.buttonTextColor = buttonTextColor;

        return this;
    }

    /**
     * Sets the text color of the buttons when clicked.
     *
     * @param buttonPressedTextColor Text color of the buttons when clicked.
     * @return The builder instance.
     */
    public Builder buttonPressedTextColor(int buttonPressedTextColor) {
        style.buttonPressedTextColor = buttonPressedTextColor;

        return this;
    }

    /**
     * Sets the error message color.
     *
     * @param errorMessageTextColor Error message color.
     * @return The builder instance.
     */
    public Builder errorMessageTextColor(int errorMessageTextColor) {
        style.errorMessageTextColor = errorMessageTextColor;

        return this;
    }

    /**
     * Specifies whether or not the bottom navigation bar will be displayed.
     *
     * @param displayBottomNavigationBar True to display it; false otherwise.
     * @return The builder instance.
     */
    public Builder displayBottomNavigation(boolean displayBottomNavigationBar) {
        style.displayBottomNavigation = displayBottomNavigationBar;

        return this;
    }

    /**
     * Sets the alpha of the disabled elements.
     *
     * @param alpha Alpha level of disabled elements.
     * @return The builder instance.
     */
    public Builder alphaOfDisabledElements(float alpha) {
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
    public Builder displayStepButtons(boolean displayStepButtons) {
        style.displayStepButtons = displayStepButtons;

        return this;
    }

    /**
     * Specifies whether or not a cancellation button should be displayed in the last step.
     * If displayed, this button will invoke the callback onCancelledForm().
     *
     * @param displayCancelButtonInLastStep True to display a cancellation button in the last step;
     *                                      false to not.
     * @return The builder instance.
     */
    public Builder displayCancelButtonInLastStep(boolean displayCancelButtonInLastStep) {
        style.displayCancelButtonInLastStep = displayCancelButtonInLastStep;

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
    public Builder includeConfirmationStep(boolean includeConfirmationStep) {
        style.includeConfirmationStep = includeConfirmationStep;

        return this;
    }

    /**
     * Specifies whether or not the step's data will be displayed as a human-readable string in the
     * step subtitle of the closed, completed steps.
     *
     * @param displayStepDataInSubtitleOfClosedSteps True to show the step's data as a string in the
     *                                               subtitle view of closed, completed steps.
     * @return The builder instance.
     */
    public Builder displayStepDataInSubtitleOfClosedSteps(boolean displayStepDataInSubtitleOfClosedSteps) {
        style.displayStepDataInSubtitleOfClosedSteps = displayStepDataInSubtitleOfClosedSteps;

        return this;
    }

    /**
     * Sets up the form and initializes it.
     */
    public void init() {
        addConfirmationStepIfRequested();
        formLayout.initializeForm(listener, style, steps);
    }

    private void addConfirmationStepIfRequested() {
        if (style.includeConfirmationStep) {
            StepHelper[] currentSteps = steps;

            steps = new StepHelper[steps.length + 1];
            for (int i = 0; i < currentSteps.length; i++) {
                steps[i] = currentSteps[i];
            }

            steps[currentSteps.length] = new StepHelper(formLayout.internalListener, null, true);
        }
    }
}
