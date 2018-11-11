package ernestoyaquello.com.verticalstepperform.listener;

import android.view.View;

import androidx.annotation.NonNull;

public interface VerticalStepperFormListener {

    /**
     * The layout of the corresponding step must be generated here.
     * The form will automatically call this method for each step.
     *
     * @param stepPosition The step position.
     * @return The step's layout.
     */
    @NonNull
    View getStepContentLayout(int stepPosition);

    /**
     * This method will be called every time a certain step is opened.
     *
     * @param stepPosition The step position, counting from 0.
     * @param animated True if the step was opened using animations; false otherwise.
     *                 It will only be false if the step was opened on loading or on restoration.
     */
    void onStepOpened(int stepPosition, boolean animated);

    /**
     * This method will be called every time a certain step is closed.
     *
     * @param stepPosition The step position, counting from 0.
     * @param animated True if the step was closed using animations; false otherwise.
     *                 It will only be false if the step was closed on loading or on restoration.
     */
    void onStepClosed(int stepPosition, boolean animated);

    /**
     * This method will be called when the user press the confirmation button.
     */
    void onCompletedForm();

}
