package ernestoyaquello.com.verticalstepperform.interfaces;

import android.view.View;

public interface VerticalStepperForm {

    /**
     * The content of the layout of the corresponding step must be generated here. The system will
     * automatically call this method for every step
     * @param stepNumber the number of the step
     * @return The view that will be automatically added as the content of the step
     */
    View createStepContentView(int stepNumber);

    /**
     * This method will be called every time a certain step is open
     * @param stepNumber the number of the step
     */
    void onStepOpening(int stepNumber);

    /***
     * This method will be called every time a step continue button is pressed
     * Return true or false to go on next step step or stay on same with an error message, accordingly
     * @param stepNumber the number of the step
     * @return return true will go on next step otherwise stay on same step
     */
    boolean onContinue(int stepNumber);

    /**
     * This method will be called when the user press the confirmation button
     */
    void sendData();

}
