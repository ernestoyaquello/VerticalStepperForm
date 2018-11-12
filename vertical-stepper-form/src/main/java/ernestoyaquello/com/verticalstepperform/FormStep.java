package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

public abstract class FormStep<T> {

    interface InternalFormStepListener {
        void onUpdatedTitle(int stepPosition, boolean useAnimations);
        void onUpdatedSubtitle(int stepPosition, boolean useAnimations);
        void onUpdatedButtonText(int stepPosition, boolean useAnimations);
    }

    InternalFormStepListener internalListener;

    private String title;
    private String subtitle;
    private String buttonText;

    protected FormStep(String title) {
        this(title, "");
    }

    protected FormStep(String title, String subtitle) {
        this(title, subtitle, "");
    }

    protected FormStep(String title, String subtitle, String buttonText) {
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
    }

    /**
     * Gets the data of this step (i.e., the information that the user has filled in for this field).
     *
     * @return The step data.
     */
    public abstract T getStepData();

    /**
     * Restores the step data. Useful for when restoring the state of the form.
     *
     * @param data The step data to restore.
     */
    public abstract void restoreStepData(T data);

    /**
     * Gets the title of this step.
     *
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the subtitle of this step.
     *
     * @return The subtitle.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Get the text for the step's button.
     *
     * @return The button text.
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * Returns an instance of IsDataValid that indicates whether the step data is valid or not.
     * This instance also contains an optional error message for when the data is not valid.
     *
     * @return An instance of IsDataValid with information about the validity of the data.
     */
    public IsDataValid isStepDataValid() {
        IsDataValid isDataValid = isStepDataValid(getStepData());
        isDataValid = isDataValid == null ? new IsDataValid(true) : isDataValid;

        return isDataValid;
    }

    /**
     * Returns an instance of IsDataValid that indicates whether the step data is valid or not.
     * This instance also contains an optional error message for when the data is not valid.
     *
     * @param stepData The data whose validity will be checked.
     * @return An instance of IsDataValid with information about the validity of the data.
     */
    protected abstract IsDataValid isStepDataValid(T stepData);

    /**
     * This method will be called automatically by the form in order to get the layout of the step.
     *
     * @param context The current context.
     * @param form The vertical stepper form.
     * @param stepPosition The step position withing the form, counting from 0.
     * @return The step's layout.
     */
    @NonNull
    protected abstract View getStepContentLayout(Context context, VerticalStepperFormLayout form, int stepPosition);

    /**
     * This method will be called every time the step is opened.
     *
     * @param form The vertical stepper form.
     * @param stepPosition The step position withing the form, counting from 0.
     * @param animated True if the step was opened using animations; false otherwise.
     *                 It will only be false if the step was opened on loading or on restoration.
     */
    protected abstract void onStepOpened(VerticalStepperFormLayout form, int stepPosition, boolean animated);

    /**
     * This method will be called every time the step is closed.
     *
     * @param form The vertical stepper form.
     * @param stepPosition The step position withing the form, counting from 0.
     * @param animated True if the step was closed using animations; false otherwise.
     *                 It will only be false if the step was closed on loading or on restoration.
     */
    protected abstract void onStepClosed(VerticalStepperFormLayout form, int stepPosition, boolean animated);

    protected void updateTitle(int stepPosition, String title, boolean useAnimations) {
        this.title = title;

        if (internalListener != null) {
            internalListener.onUpdatedTitle(stepPosition, useAnimations);
        }
    }

    protected void updateSubtitle(int stepPosition, String subtitle, boolean useAnimations) {
        this.subtitle = subtitle;

        if (internalListener != null) {
            internalListener.onUpdatedSubtitle(stepPosition, useAnimations);
        }
    }

    protected void updateButtonText(int stepPosition, String buttonText, boolean useAnimations) {
        this.buttonText = buttonText;

        if (internalListener != null) {
            internalListener.onUpdatedButtonText(stepPosition, useAnimations);
        }
    }

    void onStepOpenedImpl(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        IsDataValid dataValidation = isStepDataValid();
        if (dataValidation.isValid) {
            form.markStepAsCompleted(stepPosition, animated);
        } else {
            form.markStepAsUncompleted(stepPosition, dataValidation.errorMessage, animated);
        }

        onStepOpened(form, stepPosition, animated);
    }

    protected static class IsDataValid {

        private boolean isValid;
        private String errorMessage;

        public IsDataValid(boolean isValid) {
            this(isValid, "");
        }

        public IsDataValid(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
