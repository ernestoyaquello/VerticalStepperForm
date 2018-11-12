package ernestoyaquello.com.verticalstepperform.listener;

public interface StepperFormListener {

    /**
     * This method will be called when the user clicks on the last button after all the steps have
     * been marked as completed. It can be used to trigger showing loaders, sending the data, etc.
     */
    void onCompletedForm();

}
