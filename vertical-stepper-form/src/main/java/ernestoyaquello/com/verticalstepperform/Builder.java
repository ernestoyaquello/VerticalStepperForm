package ernestoyaquello.com.verticalstepperform;

import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

/**
 * The builder to set up and initialize the form.
 */
public class Builder {

    private VerticalStepperFormView formView;
    private StepperFormListener listener;
    private StepHelper[] steps;

    Builder(VerticalStepperFormView formView, StepperFormListener listener, Step[] steps) {
        this.formView = formView;
        this.listener = listener;
        this.steps = new StepHelper[steps.length];
        for (int i = 0; i < steps.length; i++) {
            this.steps[i] = new StepHelper(formView.internalListener, steps[i]);
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
        formView.style.stepNextButtonText = stepNextButtonText;

        return this;
    }

    /**
     * Sets the text to be displayed in the "Confirm" button of the last step.
     *
     * @param lastStepNextButtonText The text to display in the "Confirm" button of the last step.
     * @return The builder instance.
     */
    public Builder lastStepNextButtonText(String lastStepNextButtonText) {
        formView.style.lastStepNextButtonText = lastStepNextButtonText;

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
        formView.style.lastStepCancelButtonText = lastStepCancelButtonText;

        return this;
    }

    /**
     * Sets the title to be displayed on the confirmation step.
     *
     * @param confirmationStepTitle The title of the confirmation step.
     * @return The builder instance.
     */
    public Builder confirmationStepTitle(String confirmationStepTitle) {
        formView.style.confirmationStepTitle = confirmationStepTitle;

        return this;
    }

    /**
     * Sets the subtitle to be displayed on the confirmation step.
     * Set it to be null or empty to not show a subtitle in the confirmation step.
     *
     * @param confirmationStepSubtitle The subtitle of the confirmation step.
     * @return The builder instance.
     */
    public Builder confirmationStepSubtitle(String confirmationStepSubtitle) {
        formView.style.confirmationStepSubtitle = confirmationStepSubtitle;

        return this;
    }

    /**
     * Sets the basic color scheme for buttons and left circles. For further customization, use
     * stepNumberColors() and nextButtonColors().
     *
     * @param primaryColor Background color of the "Continue" buttons and background color of the
     *                     left circles in which the step numbers are displayed.
     * @param primaryColorDark Background color of the "Continue" buttons when pressed.
     * @param textColorOfElementsDisplayedOverThePrimaryColor Text color of the text views displayed
     *                                                        on top of the primary color.
     *
     * @return The builder instance.
     */
    public Builder basicColorScheme(
            int primaryColor,
            int primaryColorDark,
            int textColorOfElementsDisplayedOverThePrimaryColor) {

        formView.style.stepNumberBackgroundColor = primaryColor;
        formView.style.stepNumberTextColor = textColorOfElementsDisplayedOverThePrimaryColor;
        formView.style.nextButtonBackgroundColor = primaryColor;
        formView.style.nextButtonTextColor = textColorOfElementsDisplayedOverThePrimaryColor;
        formView.style.nextButtonPressedBackgroundColor = primaryColorDark;
        formView.style.nextButtonPressedTextColor = textColorOfElementsDisplayedOverThePrimaryColor;

        return this;
    }

    /**
     * Sets the colors of the left circles in which the step numbers are displayed.
     *
     * @param stepNumberBackgroundColor Background color of the left circles.
     * @param stepNumberTextColor Text color for the step numbers displayed in the left circles.
     * @return The builder instance.
     */
    public Builder stepNumberColors(int stepNumberBackgroundColor, int stepNumberTextColor) {
        formView.style.stepNumberBackgroundColor = stepNumberBackgroundColor;
        formView.style.stepNumberTextColor = stepNumberTextColor;

        return this;
    }

    /**
     * Sets the colors of the "Continue" buttons.
     *
     * @param nextButtonBackgroundColor Background color of the "Continue" buttons.
     * @param nextButtonPressedBackgroundColor Background color of the "Continue" buttons when pressed.
     * @param nextButtonTextColor Text color of the "Continue" buttons.
     * @param nextButtonPressedTextColor Text color of the "Continue" buttons when pressed.
     *
     * @return The builder instance.
     */
    public Builder nextButtonColors(
            int nextButtonBackgroundColor,
            int nextButtonPressedBackgroundColor,
            int nextButtonTextColor,
            int nextButtonPressedTextColor) {

        formView.style.nextButtonBackgroundColor = nextButtonBackgroundColor;
        formView.style.nextButtonPressedBackgroundColor = nextButtonPressedBackgroundColor;
        formView.style.nextButtonTextColor = nextButtonTextColor;
        formView.style.nextButtonPressedTextColor = nextButtonPressedTextColor;

        return this;
    }

    /**
     * Sets the colors of the "Cancel" button of the last step.
     * To display the cancel button in the last step, use displayCancelButtonInLastStep().
     *
     * @param lastStepCancelButtonBackgroundColor Background color of the "Cancel" button.
     * @param lastStepCancelButtonPressedBackgroundColor Background color of the "Cancel" button when pressed.
     * @param lastStepCancelButtonTextColor Text color of the "Cancel" button.
     * @param lastStepCancelButtonPressedTextColor Text color of the "Cancel" button when pressed.
     *
     * @return The builder instance.
     */
    public Builder lastStepCancelButtonColors(
            int lastStepCancelButtonBackgroundColor,
            int lastStepCancelButtonPressedBackgroundColor,
            int lastStepCancelButtonTextColor,
            int lastStepCancelButtonPressedTextColor) {

        formView.style.lastStepCancelButtonBackgroundColor = lastStepCancelButtonBackgroundColor;
        formView.style.lastStepCancelButtonPressedBackgroundColor = lastStepCancelButtonPressedBackgroundColor;
        formView.style.lastStepCancelButtonTextColor = lastStepCancelButtonTextColor;
        formView.style.lastStepCancelButtonPressedTextColor = lastStepCancelButtonPressedTextColor;

        return this;
    }

    /**
     * Sets the text color of the step subtitle.
     *
     * @param stepSubtitleTextColor The color of the step subtitle.
     * @return This builder instance.
     */
    public Builder stepSubtitleTextColor(int stepSubtitleTextColor) {
        formView.style.stepSubtitleTextColor = stepSubtitleTextColor;

        return this;
    }

    /**
     * Sets the color of the error message and the error icon.
     *
     * @param errorMessageTextColor The color of both the error message and the error icon.
     * @return The builder instance.
     */
    public Builder errorMessageTextColor(int errorMessageTextColor) {
        formView.style.errorMessageTextColor = errorMessageTextColor;

        return this;
    }

    /**
     * Sets the size of the left circles in which the step numbers are displayed.
     *
     * @param leftCircleSizeInPx The size of the left circles in which the step numbers are displayed.
     * @return The builder instance.
     */
    public Builder leftCircleSizeInPx(int leftCircleSizeInPx) {
        formView.style.leftCircleSizeInPx = leftCircleSizeInPx;

        return this;
    }

    /**
     * Sets the text size of the step numbers that are displayed inside the left circles.
     *
     * @param leftCircleTextSizeInPx The text size of the step numbers that are displayed inside the
     *                               left circles.
     * @return The builder instance.
     */
    public Builder leftCircleTextSizeInPx(int leftCircleTextSizeInPx) {
        formView.style.leftCircleTextSizeInPx = leftCircleTextSizeInPx;

        return this;
    }

    /**
     * Sets the text size of the step titles.
     *
     * @param stepTitleTextSizeInPx The text size of the step titles.
     * @return The builder instance.
     */
    public Builder stepTitleTextSizeInPx(int stepTitleTextSizeInPx) {
        formView.style.stepTitleTextSizeInPx = stepTitleTextSizeInPx;

        return this;
    }

    /**
     * Sets the text size of the step subtitles.
     *
     * @param stepSubtitleTextSizeInPx The text size of the step subtitles.
     * @return The builder instance.
     */
    public Builder stepSubtitleTextSizeInPx(int stepSubtitleTextSizeInPx) {
        formView.style.stepSubtitleTextSizeInPx = stepSubtitleTextSizeInPx;

        return this;
    }

    /**
     * Sets the text size of the error messages.
     *
     * @param stepErrorMessageTextSizeInPx The text size of the error messages.
     * @return The builder instance.
     */
    public Builder stepErrorMessageTextSizeInPx(int stepErrorMessageTextSizeInPx) {
        formView.style.stepErrorMessageTextSizeInPx = stepErrorMessageTextSizeInPx;

        return this;
    }

    /**
     * Sets the width of the vertical lines that are displayed between step numbers.
     *
     * @param leftVerticalLineThicknessSizeInPx The width of the vertical lines that are displayed
     *                                          between step numbers.
     * @return The builder instance.
     */
    public Builder leftVerticalLineThicknessSizeInPx(int leftVerticalLineThicknessSizeInPx) {
        formView.style.leftVerticalLineThicknessSizeInPx = leftVerticalLineThicknessSizeInPx;

        return this;
    }

    /**
     * Specifies whether or not the bottom navigation bar will be displayed.
     *
     * @param displayBottomNavigationBar True to display it; false otherwise.
     * @return The builder instance.
     */
    public Builder displayBottomNavigation(boolean displayBottomNavigationBar) {
        formView.style.displayBottomNavigation = displayBottomNavigationBar;

        return this;
    }

    /**
     * Sets the alpha of the disabled elements.
     *
     * @param alpha Alpha level of disabled elements.
     * @return The builder instance.
     */
    public Builder alphaOfDisabledElements(float alpha) {
        formView.style.alphaOfDisabledElements = alpha;

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
        formView.style.backgroundColorOfDisabledElements = backgroundColorOfDisabledElements;

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

        formView.style.displayDifferentBackgroundColorOnDisabledElements
                = displayDifferentBackgroundColorOnDisabledElements;

        return this;
    }

    /**
     * Specifies whether or not a "Continue" button should be automatically displayed within each step.
     * If set to false, these "Continue" buttons will be missing and manual calls to
     * goToStep(stepPosition + 1, true) will be required in order to move to the next step.
     *
     * @param displayStepButtons True to display a button on each step; false to not.
     * @return The builder instance.
     */
    public Builder displayStepButtons(boolean displayStepButtons) {
        formView.style.displayStepButtons = displayStepButtons;

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
        formView.style.displayCancelButtonInLastStep = displayCancelButtonInLastStep;

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
        formView.style.includeConfirmationStep = includeConfirmationStep;

        return this;
    }

    /**
     * Specifies whether or not the step's data will be displayed as a human-readable string in the
     * step subtitle of the closed, completed steps.
     *
     * @param displayStepDataInSubtitleOfClosedSteps True to show the step's data as a string in the
     *                                               subtitle view of closed, completed steps; false
     *                                               to not.
     * @return The builder instance.
     */
    public Builder displayStepDataInSubtitleOfClosedSteps(boolean displayStepDataInSubtitleOfClosedSteps) {
        formView.style.displayStepDataInSubtitleOfClosedSteps = displayStepDataInSubtitleOfClosedSteps;

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
        formView.style.allowNonLinearNavigation = allowNonLinearNavigation;

        return this;
    }

    /**
     * Specifies whether or not the user will be able to open a step by clicking on its header.
     *
     * @param allowStepOpeningOnHeaderClick True to allow opening steps by clicking on their header;
     *                                      false to not.
     * @return The builder instance.
     */
    public Builder allowStepOpeningOnHeaderClick(boolean allowStepOpeningOnHeaderClick) {
        formView.style.allowStepOpeningOnHeaderClick = allowStepOpeningOnHeaderClick;

        return this;
    }

    /**
     * Sets up the form and initializes it.
     */
    public void init() {
        addConfirmationStepIfRequested();
        formView.initializeForm(listener, steps);
    }

    private void addConfirmationStepIfRequested() {
        if (formView.style.includeConfirmationStep) {
            StepHelper[] currentSteps = steps;
            steps = new StepHelper[steps.length + 1];
            System.arraycopy(currentSteps, 0, steps, 0, currentSteps.length);
            steps[currentSteps.length] = new StepHelper(formView.internalListener, null, true);
        }
    }
}
