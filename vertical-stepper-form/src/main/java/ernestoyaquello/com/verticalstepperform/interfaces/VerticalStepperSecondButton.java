package ernestoyaquello.com.verticalstepperform.interfaces;

public interface VerticalStepperSecondButton {

    /**
     * This method will be called when the user press the second button in last step. That
     * button can be added via {@link ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout.Builder#addSecondButtonLastStep(String, VerticalStepperSecondButton) addSecondButtonLastStep}
     */
    void onClickSecondButton();
}
