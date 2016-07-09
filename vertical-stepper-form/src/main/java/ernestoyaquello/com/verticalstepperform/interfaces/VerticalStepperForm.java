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

    /**
     * This method will be called when the user press the confirmation button
     */
    void sendData();

}
