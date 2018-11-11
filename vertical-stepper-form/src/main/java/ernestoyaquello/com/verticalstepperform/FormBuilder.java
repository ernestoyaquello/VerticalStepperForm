package ernestoyaquello.com.verticalstepperform;

import ernestoyaquello.com.verticalstepperform.listener.VerticalStepperFormListener;
import ernestoyaquello.com.verticalstepperform.util.model.Step;

public class FormBuilder {

    private VerticalStepperFormLayout formLayout;
    private VerticalStepperFormListener listener;
    private ExtendedStep[] steps;

    private VerticalStepperFormLayout.FormStyle style;

    FormBuilder(VerticalStepperFormLayout formLayout, VerticalStepperFormListener listener, Step[] steps) {
        this.formLayout = formLayout;
        this.listener = listener;
        this.style = new VerticalStepperFormLayout.FormStyle();
        this.steps = new ExtendedStep[steps.length];
        for (int i = 0; i < steps.length; i++) {
            this.steps[i] = new ExtendedStep(steps[i]);
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
     * @param showVerticalLineWhenStepsAreCollapsed True to show the lines on collapsed steps;
     *                                              false to not.
     * @return The builder instance.
     */
    public FormBuilder showVerticalLineWhenStepsAreCollapsed(boolean showVerticalLineWhenStepsAreCollapsed) {
        style.showVerticalLineWhenStepsAreCollapsed = showVerticalLineWhenStepsAreCollapsed;

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
     * Sets up the form and initializes it.
     */
    public void init() {
        formLayout.initialiseVerticalStepperForm(listener, style, steps);
    }
}
