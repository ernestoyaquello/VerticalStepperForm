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

    private String title;
    private String subtitle;
    private String nextButtonText;
    private String errorMessage;
    private boolean completed;
    private boolean open;
    private View stepLayout;
    private View contentLayout;
    private int position;
    private VerticalStepperFormView formView;

    private List<InternalFormStepListener> internalListeners;

    protected Step(String title) {
        this(title, "");
    }

    protected Step(String title, String subtitle) {
        this(title, subtitle, "");
    }

    protected Step(String title, String subtitle, String nextButtonText) {
        this.title = title;
        this.subtitle = subtitle;
        this.nextButtonText = nextButtonText;
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
     * @return The step data as a human-readable string.
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
     *                 Generally, it will only be false if the step was opened on loading or on
     *                 restoration.
     */
    protected abstract void onStepOpened(boolean animated);

    /**
     * This method will be called every time the step is closed.
     *
     * @param animated True if the step was closed using animations; false otherwise.
     *                 Generally, it will only be false if the step was closed on loading or on
     *                 restoration.
     */
    protected abstract void onStepClosed(boolean animated);

    /**
     * This method will be called every time the step is marked as completed.
     *
     * @param animated True if the step was marked as completed using animations; false otherwise.
     *                 Generally, it will only be false if the step was marked as completed on
     *                 loading or on restoration.
     */
    protected abstract void onStepMarkedAsCompleted(boolean animated);

    /**
     * This method will be called every time the step is marked as uncompleted.
     *
     * @param animated True if the step was marked as uncompleted using animations; false otherwise.
     *                 Generally, it will only be false if the step was marked as uncompleted on
     *                 loading or on restoration.
     */
    protected abstract void onStepMarkedAsUncompleted(boolean animated);

    /**
     * Gets the title of this step.
     *
     * @return The title.
     */
    public String getTitle() {
        return title == null ? "" : title;
    }

    /**
     * Gets the subtitle of this step.
     *
     * @return The subtitle.
     */
    public String getSubtitle() {
        return subtitle == null ? "" : subtitle;
    }

    /**
     * Gets the text for the step's button.
     *
     * @return The button text.
     */
    public String getNextButtonText() {
        return nextButtonText == null ? "" : nextButtonText;
    }

    /**
     * Gets the current error message of this step.
     *
     * @return The error message.
     */
    public String getErrorMessage() {
        return errorMessage == null ? "" : errorMessage;
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
    public VerticalStepperFormView getFormView() {
        return formView;
    }

    /**
     * Gets the context of the form.
     *
     * @return The context.
     */
    public Context getContext() {
        return formView.getContext();
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
     * Marks the step as completed or uncompleted depending on whether the step data is valid or not.
     * It should be called every time the step data changes.
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
            updateErrorMessage(isDataValid.isValid() ? "" : isDataValid.getErrorMessage(), useAnimations);
        }

        return isDataValid.isValid();
    }

    /**
     * Marks the step as completed.
     *
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markAsCompleted(boolean useAnimations) {
        updateStepCompletionState(true, "", useAnimations);
    }

    /**
     * Marks the step as uncompleted.
     *
     * @param errorMessage The optional error message that explains why the step is uncompleted.
     * @param useAnimations True to animate the changes in the views, false to not.
     */
    public void markAsUncompleted(String errorMessage, boolean useAnimations) {
        updateStepCompletionState(false, errorMessage, useAnimations);
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
    protected void updateNextButtonText(String buttonText, boolean useAnimations) {
        this.nextButtonText = buttonText == null ? "" : buttonText;

        onUpdatedButtonText(useAnimations);
    }

    private void updateErrorMessage(String errorMessage, boolean useAnimations) {
        this.errorMessage = errorMessage == null ? "" : errorMessage;

        onUpdatedErrorMessage(useAnimations);
    }

    private void updateStepCompletionState(boolean completed, String errorMessage, boolean useAnimations) {
        this.completed = completed;

        updateErrorMessage(errorMessage, useAnimations);
        onUpdatedStepCompletionState(useAnimations);
        if (completed) {
            onStepMarkedAsCompleted(useAnimations);
        } else {
            onStepMarkedAsUncompleted(useAnimations);
        }
    }

    private void updateStepVisibility(boolean visibility, boolean useAnimations) {
        open = visibility;

        onUpdatedStepVisibility(useAnimations);
        if (visibility) {
            onStepOpened(useAnimations);
        } else {
            onStepClosed(useAnimations);
        }
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

    private void onUpdatedErrorMessage(boolean useAnimations) {
        for (InternalFormStepListener listener: internalListeners) {
            listener.onUpdatedErrorMessage(getPosition(), useAnimations);
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
            updateStepVisibility(true, useAnimations);
        }
    }

    void closeInternal(boolean useAnimations) {
        if (open) {
            updateStepVisibility(false, useAnimations);
        }
    }

    void initializeStepInternal(View stepLayout, VerticalStepperFormView formView, int position) {
        this.stepLayout = stepLayout;
        this.formView = formView;
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
        void onUpdatedErrorMessage(int stepPosition, boolean useAnimations);
        void onUpdatedStepCompletionState(int stepPosition, boolean useAnimations);
        void onUpdatedStepVisibility(int stepPosition, boolean useAnimations);
    }
}
