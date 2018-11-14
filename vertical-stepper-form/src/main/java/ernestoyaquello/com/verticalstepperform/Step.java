package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a step of the form (e.g., user email). It holds the step's data and offers
 * useful methods. It should be implemented and passed to the form builder as a parameter.
 *
 * @param <T> The type of the data that the step will hold. E.g., For a user email, T = String
 */
public abstract class Step<T> {

    private List<InternalFormStepListener> internalListeners;

    private String title;
    private String subtitle;
    private String buttonText;
    private String errorMessage;
    private boolean completed;
    private boolean open;
    private View stepLayout;
    private View contentLayout;
    private int position;
    private VerticalStepperFormLayout formLayout;

    protected Step(String title) {
        this(title, "");
    }

    protected Step(String title, String subtitle) {
        this(title, subtitle, "");
    }

    protected Step(String title, String subtitle, String buttonText) {
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
        this.errorMessage = "";
        this.internalListeners = new ArrayList<>();
    }

    /**
     * Gets the data of this step (i.e., the information that the user has filled in for this field).
     *
     * @return The step data.
     */
    public abstract T getStepData();

    /**
     * Gets the data of this step (i.e., the information that the user has filled in for this field)
     * as a human-readable string. When the option displayStepDataInSubtitleOfClosedSteps is
     * activated, the text returned by this method will be the one displayed in the step's subtitle.
     *
     * @return The step data as a string.
     */
    public abstract String getStepDataAsHumanReadableString();

    /**
     * Restores the step data. Useful for when restoring the state of the form.
     *
     * @param data The step data to restore.
     */
    public abstract void restoreStepData(T data);

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
     * @return The step's layout.
     */
    protected abstract View createStepContentLayout();

    /**
     * This method will be called every time the step is opened.
     *
     * @param animated True if the step was opened using animations; false otherwise.
     *                 It will only be false if the step was opened on loading or on restoration.
     */
    protected abstract void onStepOpened(boolean animated);

    /**
     * This method will be called every time the step is closed.
     *
     * @param animated True if the step was closed using animations; false otherwise.
     *                 It will only be false if the step was closed on loading or on restoration.
     */
    protected abstract void onStepClosed(boolean animated);

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
     * Gets the text for the step's button.
     *
     * @return The button text.
     */
    public String getButtonText() {
        return buttonText;
    }

    /**
     * Gets the current error message of this step.
     *
     * @return The error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Determines whether the step is marked as completed or not.
     *
     * @return True if the step is marked as completed; false otherwise.
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Determines whether the step is open or not.
     *
     * @return True if the step is open; false otherwise.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Gets the content layout of the step, which was generated on createStepContentLayout(), if any.
     *
     * @return The step's content layout.
     */
    public View getContentLayout() {
        return contentLayout;
    }

    /**
     * This method returns the entire step layout.
     *
     * Please note that this is not the layout of the step's content; this layout is for the entire
     * step and includes the header, the "Next" button, etc.
     *
     * @return The entire step layout.
     */
    public View getEntireStepLayout() {
        return stepLayout;
    }

    /**
     * Gets the position of the step within the form, counting from 0.
     *
     * @return The position of the step.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the instance of the vertical stepper form that this step belongs to.
     *
     * @return The instance of the form.
     */
    public VerticalStepperFormLayout getFormLayout() {
        return formLayout;
    }

    /**
     * Gets the context of the form.
     *
     * @return The context.
     */
    public Context getContext() {
        return formLayout.getContext();
    }

    /**
     * Determines whether the step data (i.e., the information the user has filled up in this field)
     * is valid or not.
     *
     * @return True if the data is valid; false otherwise.
     */
    public boolean isStepDataValid() {
        IsDataValid isDataValid = isStepDataValid(getStepData());
        isDataValid = isDataValid == null ? new IsDataValid(true) : isDataValid;

        return isDataValid.isValid();
    }

    /**
     * Marks the step as completed or uncompleted automatically depending on whether the step data
     * is valid or not.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     * @return True if the step was marked as completed; false otherwise.
     */
    public boolean markAsCompletedOrUncompleted(boolean useAnimations) {
        IsDataValid isDataValid = isStepDataValid(getStepData());
        isDataValid = isDataValid == null ? new IsDataValid(true) : isDataValid;

        if (completed != isDataValid.isValid()) {
            if(isDataValid.isValid()) {
                markAsCompleted(useAnimations);
            } else {
                markAsUncompleted(isDataValid.getErrorMessage(), useAnimations);
            }
        } else {
            updateErrorMessage(isDataValid.isValid() ? "" : isDataValid.getErrorMessage());
        }

        return isDataValid.isValid();
    }

    /**
     * Marks the step as completed.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markAsCompleted(boolean useAnimations) {
        updateErrorMessage("");
        updateStepCompletionState(true, useAnimations);
    }

    /**
     * Marks the step as uncompleted.
     *
     * @param errorMessage The optional error message that explains why the step is uncompleted.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markAsUncompleted(String errorMessage, boolean useAnimations) {
        updateErrorMessage(errorMessage);
        updateStepCompletionState(false, useAnimations);
    }

    /**
     * Sets the title of the step, updating the view if necessary,
     *
     * @param title The new title of the step.
     * @param useAnimations Determines whether or not the necessary layout changes should be animated.
     */
    protected void updateTitle(String title, boolean useAnimations) {
        this.title = title == null ? "" : title;

        onUpdatedTitle(useAnimations);
    }

    /**
     * Sets the subtitle of the step, updating the view if necessary,
     *
     * @param subtitle The new subtitle of the step.
     * @param useAnimations Determines whether or not the necessary layout changes should be animated.
     */
    protected void updateSubtitle(String subtitle, boolean useAnimations) {
        this.subtitle = subtitle == null ? "" : subtitle;

        onUpdatedSubtitle(useAnimations);
    }

    /**
     * Sets the text of the of the step's button, updating the view if necessary,
     *
     * @param buttonText The new text for the button of the step.
     * @param useAnimations Determines whether or not the necessary layout changes should be animated.
     */
    protected void updateButtonText(String buttonText, boolean useAnimations) {
        this.buttonText = buttonText == null ? "" : buttonText;

        onUpdatedButtonText(useAnimations);
    }

    private void updateStepCompletionState(boolean completed, boolean useAnimations) {
        this.completed = completed;

        onUpdatedStepCompletionState(useAnimations);
    }

    private void updateErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    private void onUpdatedTitle(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedTitle(getPosition(), useAnimations);
        }
    }

    private void onUpdatedSubtitle(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedSubtitle(getPosition(), useAnimations);
        }
    }

    private void onUpdatedButtonText(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedButtonText(getPosition(), useAnimations);
        }
    }

    private void onUpdatedStepCompletionState(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedStepCompletionState(getPosition(), useAnimations);
        }
    }

    private void onUpdatedStepVisibility(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedStepVisibility(getPosition(), useAnimations);
        }
    }

    void addListenerInternal(InternalFormStepListener listener) {
        if (!internalListeners.contains(listener)) {
            internalListeners.add(listener);
        }
    }

    void openInternal(boolean useAnimations) {
        if (!open) {
            open = true;

            onUpdatedStepVisibility(useAnimations);
            onStepOpened(useAnimations);
        }
    }

    void closeInternal(boolean useAnimations) {
        if (open) {
            open = false;

            onUpdatedStepVisibility(useAnimations);
            onStepClosed(useAnimations);
        }
    }

    void initializeStepInternal(View stepLayout, VerticalStepperFormLayout formLayout, int position) {
        this.stepLayout = stepLayout;
        this.formLayout = formLayout;
        this.position = position;
    }

    void setContentLayoutInternal(View contentLayout) {
        this.contentLayout = contentLayout;
    }

    /**
     * This class holds information about whether the data is valid in a boolean. It also includes
     * an optional error message for when the data turns out to be invalid.
     */
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

        /**
         * Determines whether the data is valid or not.
         *
         * @return True if the data is valid; false otherwise.
         */
        public boolean isValid() {
            return isValid;
        }

        /**
         * Gets the optional error message, if any.
         *
         * @return The optional error message, or null if none.
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    /**
     * Internal listener that will be used to notify both the form and the step helper
     * about any changes on this step so they can update accordingly.
     */
    interface InternalFormStepListener {
        void onUpdatedTitle(int stepPosition, boolean useAnimations);
        void onUpdatedSubtitle(int stepPosition, boolean useAnimations);
        void onUpdatedButtonText(int stepPosition, boolean useAnimations);
        void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations);
        void onUpdatedStepVisibility(int stepPosition, boolean useAnimations);
    }
}
