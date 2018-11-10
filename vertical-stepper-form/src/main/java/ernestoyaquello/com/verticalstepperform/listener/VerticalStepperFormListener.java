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
    View getStepLayout(int stepPosition);

    /**
     * This method will be called every time a certain step is opened.
     *
     * @param stepPosition The step position, counting from 0.
     */
    void onStepOpened(int stepPosition);

    /**
     * This method will be called when the user press the confirmation button.
     */
    void onCompletedForm();

}
