package ernestoyaquello.com.verticalstepperform.listener;

public interface StepperFormListener {

    /**
     * This method will be called when the user clicks on the last button after all the steps have
     * been marked as completed. It can be used to trigger showing loaders, sending the data, etc.
     */
    void onCompletedForm();

    /**
     * This method will be called when the form has been cancelled, which would generally mean that
     * the user has decided to not save/send the data (for example, by clicking on the cancellation
     * button of the confirmation step).
     */
    // TODO Implement the button that will invoke this call
    void onCancelledForm();

}
