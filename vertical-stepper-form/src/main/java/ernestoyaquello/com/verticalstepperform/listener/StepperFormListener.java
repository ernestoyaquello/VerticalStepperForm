package ernestoyaquello.com.verticalstepperform.listener;

import ernestoyaquello.com.verticalstepperform.Step;

public interface StepperFormListener {

    /**
     * This method will be called when the user clicks on the last button after all the steps have
     * been marked as completed. It can be used to trigger showing loaders, sending the data, etc.
     *
     * Before this method gets called, the form disables the navigation between steps, as well as
     * all the buttons. To revert the form to normal, call cancelFormCompletionOrCancellationAttempt().
     */
    void onCompletedForm();

    /**
     * This method will be called when the form has been cancelled, which would generally mean that
     * the user has decided to not save/send the data (for example, by clicking on the cancellation
     * button of the confirmation step).
     *
     * Before this method gets called, the form disables the navigation between steps, as well as
     * all the buttons. To revert the form to normal, call cancelFormCompletionOrCancellationAttempt().
     */
    void onCancelledForm();

    /**
     * It will get called when a new step is added dynamically via the method addStep() of the form.
     *
     * @param index The index where the step was added.
     * @param addedStep The step that was added dynamically.
     */
    void onStepAdded(int index, Step<?> addedStep);

    /**
     * It will get called when a step is removed dynamically via the method removeStep() of the form.
     *
     * @param index The index of the step that was removed.
     */
    void onStepRemoved(int index);

}
