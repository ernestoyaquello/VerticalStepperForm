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
     * Sets the text to be displayed in the "Confirm" button of the last step.
     *
     * @param lastStepNextButtonText The text to display in the "Confirm" button of the last step.
     * @return The builder instance.
     */
    public Builder lastStepNextButtonText(String lastStepNextButtonText) {
        style.lastStepNextButtonText = lastStepNextButtonText;

        return this;
    }

    /**
     * Sets the text to be displayed in the "Cancel" button of the last step.
     * To display this button in the last step, use displayCancelButtonInLastStep().
     *
     * @param lastStepCancelButtonText The text to display in the "Cancel" button of the last step.
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
     * Null to not show a subtitle in the confirmation step.
     *
     * @param confirmationStepSubtitle The subtitle of the confirmation step.
     * @return The builder instance.
     */
    public Builder confirmationStepSubtitle(String confirmationStepSubtitle) {
        style.confirmationStepSubtitle = confirmationStepSubtitle;

        return this;
    }

    /**
     * Sets the primary color of the form. It will be used for the left circles and the buttons.
     * To set a different background color for buttons and left circles, please use
     * stepNumberBackgroundColor() and nextButtonBackgroundColor().
     *
     * @param colorPrimary The primary color.
     * @return The builder instance.
     */
    public Builder primaryColor(int colorPrimary) {
        stepNumberBackgroundColor(colorPrimary);
        nextButtonBackgroundColor(colorPrimary);

        return this;
    }

    /**
     * Sets the dark primary color. It will be displayed as the background color of the "Continue"
     * buttons when clicked. Equivalent to nextButtonPressedBackgroundColor().
     *
     * @param colorPrimaryDark The primary color (dark).
     * @return The builder instance.
     */
    public Builder primaryDarkColor(int colorPrimaryDark) {
        nextButtonPressedBackgroundColor(colorPrimaryDark);

        return this;
    }

    /**
     * Sets the background color of the left circles in which the step numbers are displayed.
     *
     * @param stepNumberBackgroundColor Background color of the left circles.
     * @return The builder instance.
     */
    public Builder stepNumberBackgroundColor(int stepNumberBackgroundColor) {
        style.stepNumberBackgroundColor = stepNumberBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the "Continue" buttons.
     *
     * @param nextButtonBackgroundColor Background color of the "Continue" button.
     * @return The builder instance.
     */
    public Builder nextButtonBackgroundColor(int nextButtonBackgroundColor) {
        style.nextButtonBackgroundColor = nextButtonBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the "Continue" buttons when pressed.
     *
     * @param nextButtonPressedBackgroundColor Background color of the "Continue" buttons when pressed.
     * @return The builder instance.
     */
    public Builder nextButtonPressedBackgroundColor(int nextButtonPressedBackgroundColor) {
        style.nextButtonPressedBackgroundColor = nextButtonPressedBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the "Cancel" button of the last step.
     * To display the cancel button of the last step, use displayCancelButtonInLastStep().
     *
     * @param lastStepCancelButtonBackgroundColor Background color of the "Cancel" button of the
     *                                            last step.
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonBackgroundColor(int lastStepCancelButtonBackgroundColor) {
        style.lastStepCancelButtonBackgroundColor = lastStepCancelButtonBackgroundColor;

        return this;
    }

    /**
     * Sets the background color of the "Cancel" button of the last step when pressed.
     * To display the cancel button of the last step, use displayCancelButtonInLastStep().
     *
     * @param lastStepCancelButtonPressedBackgroundColor Background color of the "Cancel" button of
     *                                                   the last step when pressed.
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonPressedBackgroundColor(int lastStepCancelButtonPressedBackgroundColor) {
        style.lastStepCancelButtonPressedBackgroundColor = lastStepCancelButtonPressedBackgroundColor;

        return this;
    }

    /**
     * Sets the text color of the step numbers displayed inside the left circles.
     *
     * @param stepNumberTextColor Text color for the step numbers displayed in the left circles.
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
     * Sets the text color of the "Continue" buttons.
     *
     * @param nextButtonTextColor Text color of the "Continue" buttons.
     * @return The builder instance.
     */
    public Builder nextButtonTextColor(int nextButtonTextColor) {
        style.nextButtonTextColor = nextButtonTextColor;

        return this;
    }

    /**
     * Sets the text color of the "Continue" buttons when pressed.
     *
     * @param nextButtonPressedTextColor Text color of the "Continue" buttons when pressed.
     * @return The builder instance.
     */
    public Builder nextButtonPressedTextColor(int nextButtonPressedTextColor) {
        style.nextButtonPressedTextColor = nextButtonPressedTextColor;

        return this;
    }

    /**
     * Sets the text color of the "Cancel" button of the last step.
     *
     * @param lastStepCancelButtonTextColor Text color of the "Cancel" button of the last step.
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonTextColor(int lastStepCancelButtonTextColor) {
        style.lastStepCancelButtonTextColor = lastStepCancelButtonTextColor;

        return this;
    }

    /**
     * Sets the text color of the "Cancel" button of the last step when pressed.
     *
     * @param lastStepCancelButtonPressedTextColor Text color of the "Cancel" button of the last
     *                                             step when pressed.
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonPressedTextColor(int lastStepCancelButtonPressedTextColor) {
        style.lastStepCancelButtonPressedTextColor = lastStepCancelButtonPressedTextColor;

        return this;
    }

    /**
     * Sets the color of the error message.
     *
     * @param errorMessageTextColor The color of the error message.
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
     * Sets the background color of the disabled elements (buttons and left circles).
     * It will only work if used along with displayDifferentBackgroundColorOnDisabledElements().
     *
     * @param backgroundColorOfDisabledElements The background color of the disabled elements.
     * @return The builder instance.
     */
    public Builder backgroundColorOfDisabledElements(int backgroundColorOfDisabledElements) {
        style.backgroundColorOfDisabledElements = backgroundColorOfDisabledElements;

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
     * If displayed, this button will invoke the callback onCancelledForm() when clicked.
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
     * Specifies whether or not a different background color should be displayed on disabled elements.
     * Use backgroundColorOfDisabledElements() to specify what background color should be used.
     *
     * @param displayDifferentBackgroundColorOnDisabledElements True to show a different background
     *                                                          color on disabled elements.
     * @return The builder instance.
     */
    public Builder displayDifferentBackgroundColorOnDisabledElements(
            boolean displayDifferentBackgroundColorOnDisabledElements) {

        style.displayDifferentBackgroundColorOnDisabledElements
                = displayDifferentBackgroundColorOnDisabledElements;

        return this;
    }

    /**
     * Specifies whether or not the user will be able to jump to any step without having completed
     * the previous ones.
     *
     * @param allowNonLinearNavigation True to allow non-linear navigation between steps; false to not.
     * @return The builder instance.
     */
    public Builder allowNonLinearNavigation(boolean allowNonLinearNavigation) {
        style.allowNonLinearNavigation = allowNonLinearNavigation;

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
