package ernestoyaquello.com.verticalstepperform;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm;
import ernestoyaquello.com.verticalstepperform.utils.Animations;

/**
 * Custom layout that implements a vertical stepper form
 */
@SuppressWarnings("unused")
public class VerticalStepperFormLayout extends RelativeLayout {

    // Style
    protected @ColorInt int stepNumberBackgroundColor = -1;
    protected @ColorInt int buttonBackgroundColor = -1;
    protected @ColorInt int buttonPressedBackgroundColor = -1;
    protected @ColorInt int stepNumberTextColor = -1;
    protected @ColorInt int stepTitleTextColor = -1;
    protected @ColorInt int stepSubtitleTextColor = -1;
    protected @ColorInt int buttonTextColor = -1;
    protected @ColorInt int buttonPressedTextColor = -1;
    protected @ColorInt int bottomNavigationBackgroundColor = -1;
    protected @ColorInt int errorMessageTextColor = -1;
    protected @DrawableRes int errorIcon = -1;
    protected @LayoutRes int customButtonLayout = -1;
    protected @IdRes int customButtonId = -1;
    protected float alphaOfDisabledElements = -1;
    protected boolean displayBottomNavigation = true;
    protected boolean materialDesignInDisabledSteps = false;
    protected boolean hideKeyboard = true;
    protected boolean showVerticalLineWhenStepsAreCollapsed = false;
    /**
     * true = Late validation, button always enabled but only takes action if step is valid
     * false =  on-step-open validation, button enabled once step is valid
     */
    protected boolean isValidateOnButtonPress = false;
    protected String customButtonText = null;

    // Views
    protected LayoutInflater mInflater;
    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected List<LinearLayout> stepLayouts;
    protected List<View> stepContentViews;
    protected List<TextView> stepsTitlesViews;
    protected List<TextView> stepsSubtitlesViews;
    protected List<AppCompatButton> nextStepButtonViews;
    protected AppCompatButton confirmationButton;
    protected ProgressBar progressBar;
    protected AppCompatImageButton previousStepButton, nextStepButton;
    protected RelativeLayout bottomNavigation;

    // Data
    protected List<String> steps;
    protected List<String> stepsSubtitles;
    @Nullable protected List<CharSequence> nextStepButtonTexts;

    // Logic
    protected int activeStep = 0;
    protected int numberOfSteps;
    protected boolean[] completedSteps;

    // Listeners and callbacks
    protected VerticalStepperForm verticalStepperFormImplementation;

    // Context
    protected Context context;
    protected Activity activity;
    protected NavigationClickListener navListener;

    public VerticalStepperFormLayout(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    protected void init(Context context, @Nullable AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.VerticalStepperFormLayout, 0, 0);
            if (a.hasValue(R.styleable.VerticalStepperFormLayout_stepValidationMode)) {
                int value = a.getInt(R.styleable.VerticalStepperFormLayout_stepValidationMode, 0);
                //noinspection RedundantIfStatement - so we can be explicit with comments
                if (value == 1) {
                    //NOTE: VALIDATE FROM BUTTON-PRESS
                    isValidateOnButtonPress = true;
                } else {
                    //NOTE: VALIDATE BY DEFAULT STEP-OPENING/USER-ACTIONS
                    isValidateOnButtonPress = false;
                }
            }
            initColoursFromAttrs(context, attrs, a);
            if (a.hasValue(R.styleable.VerticalStepperFormLayout_stepErrorIcon)) {
                @DrawableRes int res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepErrorIcon, -1);
                if (res != 0 && res != -1) {
                    errorIcon = res;
                }
            }
            initCustomButtonFromAttrs(context, attrs, a);
            initButtonTextFromAttrs(context, attrs, a);
            if (a.hasValue(R.styleable.VerticalStepperFormLayout_stepperDisplayBottomNavigation)) {
                displayBottomNavigation = a.getBoolean(R.styleable.VerticalStepperFormLayout_stepperDisplayBottomNavigation, true);
            }
            a.recycle();
        }
        navListener = onCreateNavListener();
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.vertical_stepper_form_layout, this, true);
    }

    protected void initCustomButtonFromAttrs(Context context, @Nullable AttributeSet attrs, TypedArray a) {
        boolean hasCustomLayout = a.hasValue(R.styleable.VerticalStepperFormLayout_stepButtonCustomLayout);
        boolean hasCustomId = a.hasValue(R.styleable.VerticalStepperFormLayout_stepButtonCustomId);
        if (hasCustomId != hasCustomLayout) {
            throw new RuntimeException("stepButtonCustomLayout and stepButtonCustomId must only be set together (you cannot set only one)");
        } else //noinspection ConstantConditions
            if (hasCustomLayout && hasCustomId) {
                @LayoutRes int layoutRes = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonCustomLayout, -1);
                @IdRes int idRes = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonCustomId, -1);
                if (layoutRes != 0 && layoutRes != -1 && idRes != 0 && idRes != -1) {
                    customButtonLayout = layoutRes;
                    customButtonId = idRes;
                } else {
                    throw new RuntimeException("stepButtonCustomLayout and stepButtonCustomId must only be set together (you cannot set or use -1 for only one)");
                }
            }
    }

    protected void initButtonTextFromAttrs(Context context, @Nullable AttributeSet attrs, TypedArray a) {
        if (a.hasValue(R.styleable.VerticalStepperFormLayout_stepButtonText)) {
            String string = a.getString(R.styleable.VerticalStepperFormLayout_stepButtonText);
            if (string != null && !"".equals(string)) {
                customButtonText = string;
            }
        }
        //NOTE: stepsButtonTexts (string array) overrides stepButtonText (single string)
        if (a.hasValue(R.styleable.VerticalStepperFormLayout_stepsButtonTexts)) {
            CharSequence[] array = a.getTextArray(R.styleable.VerticalStepperFormLayout_stepsButtonTexts);
            if (array != null) {
                nextStepButtonTexts = Arrays.asList(array);
            }
        }
    }

    protected void initColoursFromAttrs(Context context, @Nullable AttributeSet attrs, TypedArray a) {
        @ColorRes int res;
        //Primary colours
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepPrimaryColor, -1);
        if (res != 0 && res != -1) {
            @ColorInt int resolvedColour = ContextCompat.getColor(context, res);
            this.stepNumberBackgroundColor = resolvedColour;
            this.buttonBackgroundColor = resolvedColour;
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepPrimaryDarkColor, -1);
        if (res != 0 && res != -1) {
            @ColorInt int resolvedColour = ContextCompat.getColor(context, res);
            this.buttonPressedBackgroundColor = resolvedColour;
        }
        //Individual colours (override the primary colours)
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepErrorColor, -1);
        if (res != 0 && res != -1) { //Note: do NOT change to greater than - it's wrong for ColorRes
            errorMessageTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepNumberBackgroundColor, -1);
        if (res != 0 && res != -1) {
            stepNumberBackgroundColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonBackgroundColor, -1);
        if (res != 0 && res != -1) {
            buttonBackgroundColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonPressedBackgroundColor, -1);
        if (res != 0 && res != -1) {
            buttonPressedBackgroundColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepNumberTextColor, -1);
        if (res != 0 && res != -1) {
            stepNumberTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepTitleTextColor, -1);
        if (res != 0 && res != -1) {
            stepTitleTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepSubtitleTextColor, -1);
        if (res != 0 && res != -1) {
            stepSubtitleTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonTextColor, -1);
        if (res != 0 && res != -1) {
            buttonTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepButtonPressedTextColor, -1);
        if (res != 0 && res != -1) {
            buttonPressedTextColor = ContextCompat.getColor(context, res);
        }
        res = a.getResourceId(R.styleable.VerticalStepperFormLayout_stepBottomNavigationBackgroundColor, -1);
        if (res != 0 && res != -1) {
            bottomNavigationBackgroundColor = ContextCompat.getColor(context, res);
        }
    }

    protected NavigationClickListener onCreateNavListener() {
        return new NavigationClickListener(this);
    }

    /**
     * Returns the title of a step
     * @param stepNumber The step number (counting from 0)
     * @return the title string
     */
    public String getStepTitle(int stepNumber) {
        return steps.get(stepNumber);
    }

    /**
     * Returns the subtitle of a step
     * @param stepNumber The step number (counting from 0)
     * @return the subtitle string
     */
    public String getStepsSubtitles(int stepNumber) {
        if (stepsSubtitles != null) {
            return stepsSubtitles.get(stepNumber);
        }
        return null;
    }

    /**
     * Returns the text of a button
     * @param stepNumber The step number (counting from 0)
     * @return the button text string
     */
    public CharSequence getNextStepButtonText(int stepNumber) {
        if (nextStepButtonTexts != null) {
            return nextStepButtonTexts.get(stepNumber);
        }
        return null;
    }

    /**
     * Returns the active step number
     * @return the active step number (counting from 0)
     */
    public int getActiveStepNumber() {
        return activeStep;
    }

    /**
     * Set the title of certain step
     * @param stepNumber The step number (counting from 0)
     * @param title New title of the step
     */
    public void setStepTitle(int stepNumber, String title) {
        if(title != null && !title.equals("")) {
            steps.set(stepNumber, title);
            TextView titleView = stepsTitlesViews.get(stepNumber);
            if (titleView != null) {
                titleView.setText(title);
            }
        }
    }

    /**
     * Set the subtitle of certain step
     * @param stepNumber The step number (counting from 0)
     * @param subtitle New subtitle of the step
     */
    public void setStepSubtitle(int stepNumber, String subtitle) {
        if(stepsSubtitles != null && subtitle != null && !subtitle.equals("")) {
            stepsSubtitles.set(stepNumber, subtitle);
            TextView subtitleView = stepsSubtitlesViews.get(stepNumber);
            if (subtitleView != null) {
                subtitleView.setText(subtitle);
            }
        }
    }

    /**
     * Set the button text of certain step
     * @param stepNumber The step number (counting from 0)
     * @param buttonText New button text for the step
     */
    public void setNextStepButtonText(int stepNumber, String buttonText) {
        if (nextStepButtonTexts != null && buttonText != null && !buttonText.equals("")) {
            nextStepButtonTexts.set(stepNumber, buttonText);
            AppCompatButton btn = nextStepButtonViews.get(stepNumber);
            if (btn != null) {
                btn.setText(buttonText);
            }
        }
    }

    /**
     * Set the active step as completed
     */
    public void setActiveStepAsCompleted() {
        setStepAsCompleted(activeStep);
    }

    /**
     * Set the active step as not completed
     * @param errorMessage Error message that will be displayed (null for no message)
     */
    public void setActiveStepAsUncompleted(String errorMessage) {
        setStepAsUncompleted(activeStep, errorMessage);
    }

    private AppCompatButton getNextStepButton(@NonNull LinearLayout stepLayout) {
        return  (AppCompatButton) stepLayout.findViewById(customButtonLayout != -1 && customButtonId != -1? customButtonId : R.id.next_step);
    }

    /**
     * Set the step as completed
     * @param stepNumber the step number (counting from 0)
     */
    public void setStepAsCompleted(int stepNumber) {
        completedSteps[stepNumber] = true;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout errorContainer = (LinearLayout) stepLayout.findViewById(R.id.error_container);
        TextView errorTextView = (TextView) errorContainer.findViewById(R.id.error_message);


        enableStepHeader(stepLayout);

        setStepNextButtonAsCompleted(stepLayout);

        if (stepNumber != activeStep) {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
        } else {
            if (stepNumber != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }
        }

        errorTextView.setText("");
        //errorContainer.setVisibility(View.GONE);
        Animations.slideUp(errorContainer);

        displayCurrentProgress();
    }

    /**
     * Enable the button for current step without clearing anything else
     */
    public void setActiveNextStepButtonAsCompleted() {
        setStepNextButtonAsCompleted(activeStep);
    }

    protected void setStepNextButtonAsCompleted(int stepNumber) {
        setStepNextButtonAsCompleted(stepLayouts.get(stepNumber));
    }

    protected void setStepNextButtonAsCompleted(LinearLayout stepLayout) {
        AppCompatButton nextButton = getNextStepButton(stepLayout);
        nextButton.setEnabled(true);
        nextButton.setAlpha(1);
    }

    /**
     * Set the step as not completed
     * @param stepNumber the step number (counting from 0)
     * @param errorMessage Error message that will be displayed (null for no message)
     */
    public void setStepAsUncompleted(int stepNumber, String errorMessage) {
        completedSteps[stepNumber] = false;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        AppCompatButton nextButton = getNextStepButton(stepLayout);

        stepDone.setVisibility(View.INVISIBLE);
        stepNumberTextView.setVisibility(View.VISIBLE);

        nextButton.setEnabled(false);
        nextButton.setAlpha(alphaOfDisabledElements);

        if (stepNumber == activeStep) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            disableStepHeader(stepLayout);
        }

        if (stepNumber < numberOfSteps) {
            setStepAsUncompleted(numberOfSteps, null);
        }

        if (errorMessage != null && !errorMessage.equals("")) {
            LinearLayout errorContainer = (LinearLayout) stepLayout.findViewById(R.id.error_container);
            TextView errorTextView = (TextView) errorContainer.findViewById(R.id.error_message);

            errorTextView.setText(errorMessage);
            //errorContainer.setVisibility(View.VISIBLE);
            Animations.slideDown(errorContainer);
        }

        displayCurrentProgress();
    }

    /**
     * Determines whether the active step is completed or not
     * @return true if the active step is completed; false otherwise
     */
    public boolean isActiveStepCompleted() {
        return isStepCompleted(activeStep);
    }

    /**
     * Determines whether the given step is completed or not
     * @param stepNumber the step number (counting from 0)
     * @return true if the step is completed, false otherwise
     */
    public boolean isStepCompleted(int stepNumber) {
        return completedSteps[stepNumber];
    }

    /**
     * Determines if any step has been completed
     * @return true if at least 1 step has been completed; false otherwise
     */
    public boolean isAnyStepCompleted() {
        for (boolean completedStep : completedSteps) {
            if (completedStep) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the steps that are previous to the given one are completed
     * @param stepNumber the selected step number (counting from 0)
     * @return true if all the previous steps have been completed; false otherwise
     */
    public boolean arePreviousStepsCompleted(int stepNumber) {
        boolean previousStepsAreCompleted = true;
        for (int i = (stepNumber - 1); i >= 0 && previousStepsAreCompleted; i--) {
            previousStepsAreCompleted = completedSteps[i];
        }
        return previousStepsAreCompleted;
    }

    /**
     * Go to the next step
     */
    public void goToNextStep() {
        goToStep(activeStep + 1, false);
    }

    /**
     * Go to the previous step
     */
    public void goToPreviousStep() {
        goToStep(activeStep - 1, false);
    }

    /**
     * Go to the selected step
     * @param stepNumber the selected step number (counting from 0)
     * @param restoration true if the method has been called to restore the form; false otherwise
     */
    public void goToStep(int stepNumber, boolean restoration) {
        if (activeStep != stepNumber || restoration) {
            if(hideKeyboard) {
                hideSoftKeyboard();
            }
            boolean previousStepsAreCompleted = arePreviousStepsCompleted(stepNumber);
            if (stepNumber == 0 || previousStepsAreCompleted) {
                openStep(stepNumber, restoration);
            }
        }
    }

    /**
     * Set the active step as not completed
     * @deprecated use {@link #setActiveStepAsUncompleted(String)} instead
     */
    @Deprecated
    public void setActiveStepAsUncompleted() {
        setStepAsUncompleted(activeStep, null);
    }

    /**
     * Set the selected step as not completed
     * @param stepNumber the step number (counting from 0)
     * @deprecated use {@link #setStepAsUncompleted(int, String)} instead
     */
    @Deprecated
    public void setStepAsUncompleted(int stepNumber) {
        setStepAsUncompleted(stepNumber, null);
    }

    /**
     * Set up and initialize the form
     * @param stepsTitles names of the steps
     * @param colorPrimary primary color
     * @param colorPrimaryDark primary color (dark)
     * @param verticalStepperForm instance that implements the interface "VerticalStepperForm"
     * @param activity activity where the form is
     *
     * @deprecated use {@link Builder#newInstance(VerticalStepperFormLayout, String[], VerticalStepperForm, Activity)} instead like this:
     * <blockquote><pre>
     * VerticalStepperFormLayout.Builder.newInstance(verticalStepperFormLayout, stepsTitles, verticalStepperForm, activity)<br>
     *     .primaryColor(colorPrimary)<br>
     *     .primaryDarkColor(colorPrimaryDark)<br>
     *     .init();
     * </pre></blockquote>
     */
    @Deprecated
    public void initialiseVerticalStepperForm(String[] stepsTitles,
                                              @ColorInt int colorPrimary, @ColorInt int colorPrimaryDark,
                                              VerticalStepperForm verticalStepperForm, Activity activity) {
        this.alphaOfDisabledElements = 0.25f;
        this.buttonTextColor = Color.rgb(255, 255, 255);
        this.buttonPressedTextColor = Color.rgb(255, 255, 255);
        this.stepNumberTextColor = Color.rgb(255, 255, 255);
        this.stepTitleTextColor = Color.rgb(33, 33, 33);
        this.stepSubtitleTextColor = Color.rgb(162, 162, 162);
        this.stepNumberBackgroundColor = colorPrimary;
        this.buttonBackgroundColor = colorPrimary;
        this.buttonPressedBackgroundColor = colorPrimaryDark;
        this.errorMessageTextColor = Color.rgb(175, 18, 18);
        this.displayBottomNavigation = true;
        this.materialDesignInDisabledSteps = false;
        this.hideKeyboard = true;
        this.showVerticalLineWhenStepsAreCollapsed = false;
        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;
        initStepperForm(stepsTitles, null, null);
    }

    /**
     * Set up and initialize the form
     * @param stepsTitles names of the steps
     * @param buttonBackgroundColor background colour of the buttons
     * @param buttonTextColor text color of the buttons
     * @param buttonPressedBackgroundColor background color of the buttons when clicked
     * @param buttonPressedTextColor text color of the buttons when clicked
     * @param stepNumberBackgroundColor background color of the left circles
     * @param stepNumberTextColor text color of the left circles
     * @param verticalStepperForm instance that implements the interface "VerticalStepperForm"
     * @param activity activity where the form is
     *
     * @deprecated use {@link Builder#newInstance(VerticalStepperFormLayout, String[], VerticalStepperForm, Activity)} instead like this:
     * <blockquote><pre>
     * VerticalStepperFormLayout.Builder.newInstance(verticalStepperFormLayout, stepsTitles, verticalStepperForm, activity)<br>
     *     .buttonBackgroundColor(buttonBackgroundColor)<br>
     *     .buttonTextColor(buttonTextColor)<br>
     *     .buttonPressedBackgroundColor(buttonPressedBackgroundColor)<br>
     *     .buttonPressedTextColor(buttonPressedTextColor)<br>
     *     .stepNumberBackgroundColor(stepNumberBackgroundColor)<br>
     *     .stepNumberTextColor(stepNumberTextColor)<br>
     *     .init();
     * </pre></blockquote>
     */
    @Deprecated
    public void initialiseVerticalStepperForm(String[] stepsTitles,
                                              int buttonBackgroundColor, int buttonTextColor,
                                              int buttonPressedBackgroundColor, int buttonPressedTextColor,
                                              int stepNumberBackgroundColor, int stepNumberTextColor,
                                              VerticalStepperForm verticalStepperForm,
                                              Activity activity) {

        this.alphaOfDisabledElements = 0.25f;
        this.buttonBackgroundColor = buttonBackgroundColor;
        this.buttonTextColor = buttonTextColor;
        this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
        this.buttonPressedTextColor = buttonPressedTextColor;
        this.stepNumberBackgroundColor = stepNumberBackgroundColor;
        this.stepTitleTextColor = Color.rgb(33, 33, 33);
        this.stepSubtitleTextColor = Color.rgb(162, 162, 162);
        this.stepNumberTextColor = stepNumberTextColor;
        this.errorMessageTextColor = Color.rgb(175, 18, 18);
        this.displayBottomNavigation = true;
        this.materialDesignInDisabledSteps = false;
        this.hideKeyboard = true;
        this.showVerticalLineWhenStepsAreCollapsed = false;

        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;

        initStepperForm(stepsTitles, null, null);
    }

    protected void initialiseVerticalStepperForm(Builder builder) {
        //Required attributes
        this.activity = builder.activity;
        this.verticalStepperFormImplementation = builder.verticalStepperFormImplementation;
        //Truly TOptional attributes
        if (builder.errorIcon != -1) {
            this.errorIcon = builder.errorIcon;
        } else if (this.errorIcon == -1) {
            this.errorIcon = R.drawable.ic_error;
        }
        if (builder.customButtonLayout != -1) {
            this.customButtonLayout = builder.customButtonLayout;
        }
        if (builder.customButtonId != -1) {
            this.customButtonId = builder.customButtonId;
        }
        if (builder.alphaOfDisabledElements != -1) {
            this.alphaOfDisabledElements = builder.alphaOfDisabledElements;
        } else if (this.alphaOfDisabledElements == -1) {
            this.alphaOfDisabledElements = 0.25f;
        }
        //Optional color attributes
        if (builder.stepNumberBackgroundColor != -1) {
            this.stepNumberBackgroundColor = builder.stepNumberBackgroundColor;
        } else if (this.stepNumberBackgroundColor == -1) {
            this.stepNumberBackgroundColor = Color.rgb(63, 81, 181);
        }
        if (builder.buttonBackgroundColor != -1) {
            this.buttonBackgroundColor = builder.buttonBackgroundColor;
        } else if (this.buttonBackgroundColor == -1) {
            this.buttonBackgroundColor = Color.rgb(63, 81, 181);
        }
        if (builder.buttonPressedBackgroundColor != -1) {
            this.buttonPressedBackgroundColor = builder.buttonPressedBackgroundColor;
        } else if (this.buttonPressedBackgroundColor == -1) {
            this.buttonPressedBackgroundColor = Color.rgb(48, 63, 159);
        }
        if (builder.stepNumberTextColor != -1) {
            this.stepNumberTextColor = builder.stepNumberTextColor;
        } else if (this.stepNumberTextColor == -1) {
            this.stepNumberTextColor = Color.rgb(255, 255, 255);
        }
        if (builder.stepTitleTextColor != -1) {
            this.stepTitleTextColor = builder.stepTitleTextColor;
        } else if (this.stepTitleTextColor == -1) {
            this.stepTitleTextColor = Color.rgb(33, 33, 33);
        }
        if (builder.stepSubtitleTextColor != -1) {
            this.stepSubtitleTextColor = builder.stepSubtitleTextColor;
        } else if (this.stepSubtitleTextColor == -1) {
            this.stepSubtitleTextColor = Color.rgb(162, 162, 162);
        }
        if (builder.buttonTextColor != -1) {
            this.buttonTextColor = builder.buttonTextColor;
        } else if (this.buttonTextColor == -1) {
            this.buttonTextColor = Color.rgb(255, 255, 255);
        }
        //Log.d(getClass().getSimpleName(), "Button text colour is " + this.buttonTextColor + ". Builder said: " + builder.buttonTextColor);
        if (builder.buttonPressedTextColor != -1) {
            this.buttonPressedTextColor = builder.buttonPressedTextColor;
        } else if (this.buttonPressedTextColor == -1) {
            this.buttonPressedTextColor = Color.rgb(255, 255, 255);
        }
        if (builder.errorMessageTextColor != -1) {
            this.errorMessageTextColor = builder.errorMessageTextColor;
        } else if (this.errorMessageTextColor == -1) {
            this.errorMessageTextColor = Color.rgb(175, 18, 18);
        }
        if (builder.bottomNavigationBackgroundColor != -1) {
            this.bottomNavigationBackgroundColor = builder.bottomNavigationBackgroundColor;
        } else if (this.bottomNavigationBackgroundColor == -1) {
            this.bottomNavigationBackgroundColor = Color.parseColor("#EEEEEE");
        }
        //Non-resource attributes
        if (builder.displayBottomNavigation != null) {
            this.displayBottomNavigation = builder.displayBottomNavigation;
        }
        if (builder.materialDesignInDisabledSteps != null) {
            this.materialDesignInDisabledSteps = builder.materialDesignInDisabledSteps;
        }
        if (builder.hideKeyboard != null) {
            this.hideKeyboard = builder.hideKeyboard;
        }
        if (builder.showVerticalLineWhenStepsAreCollapsed != null) {
            this.showVerticalLineWhenStepsAreCollapsed = builder.showVerticalLineWhenStepsAreCollapsed;
        }
        initStepperForm(builder.steps, builder.stepsSubtitles, builder.stepButtonTexts);
    }

    protected void initStepperForm(String[] stepsTitles, String[] stepsSubtitles, @Nullable String[] stepsButtonTexts) {

        if (stepsButtonTexts != null) {
            this.nextStepButtonTexts = new ArrayList<>();
        }
        setSteps(stepsTitles, stepsSubtitles);

        List<View> stepContentLayouts = new ArrayList<>();
        for (int i = 0; i < numberOfSteps; i++) {
            View stepLayout = verticalStepperFormImplementation.createStepContentView(i);
            stepContentLayouts.add(stepLayout);
            if (stepsButtonTexts != null) {
                nextStepButtonTexts.add(stepsButtonTexts[i]);
            }
        }
        //Add a text to replace "Confirm Data", if we have it in the array
        if (stepsButtonTexts != null && stepsButtonTexts.length > numberOfSteps) {
            nextStepButtonTexts.add(stepsButtonTexts[numberOfSteps]);
        }
        stepContentViews = stepContentLayouts;

        initializeForm();

        verticalStepperFormImplementation.onStepOpening(activeStep);
    }

    protected void setSteps(String[] steps, String[] stepsSubtitles) {
        this.steps = new ArrayList<>(Arrays.asList(steps));
        if(stepsSubtitles != null) {
            this.stepsSubtitles = new ArrayList<>(Arrays.asList(stepsSubtitles));
        } else {
            this.stepsSubtitles = null;
        }
        numberOfSteps = steps.length;
        setAuxVars();
        addConfirmationStepToStepsList();
    }

    protected void registerListeners() {
        previousStepButton.setTag(NavigationClickListener.ACTION_PREVIOUS);
        previousStepButton.setOnClickListener(navListener);
        nextStepButton.setTag(NavigationClickListener.ACTION_NEXT);
        nextStepButton.setOnClickListener(navListener);
    }

    protected void initializeForm() {
        stepsTitlesViews = new ArrayList<>();
        stepsSubtitlesViews = new ArrayList<>();
        nextStepButtonViews = new ArrayList<>();
        setUpSteps();
        if (displayBottomNavigation) {
            styleBottomNavigation();
        } else {
            hideBottomNavigation();
        }
        goToStep(0, true);

        setObserverForKeyboard();
    }

    // http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
    protected void setObserverForKeyboard() {
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                content.getWindowVisibleDisplayFrame(r);

                int heightDiff = content.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 100) { // if more than 100 pixels, it is probably a keyboard...
                    scrollToActiveStep(true);
                }
            }
        });
    }

    protected void hideBottomNavigation() {
        bottomNavigation.setVisibility(View.GONE);
    }

    protected void styleBottomNavigation() {
        if (bottomNavigationBackgroundColor != -1) {
            bottomNavigation.setBackgroundColor(bottomNavigationBackgroundColor);
        }
    }

    protected void setUpSteps() {
        stepLayouts = new ArrayList<>();
        // Set up normal steps
        for (int i = 0; i < numberOfSteps; i++) {
            setUpStep(i);
        }
        // Set up confirmation step
        setUpStep(numberOfSteps);
    }

    protected void setUpStep(int stepNumber) {
        LinearLayout stepLayout = createStepLayout(stepNumber);
        if (stepNumber < numberOfSteps) {
            // The content of the step is the corresponding custom view previously created
            RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
            stepContent.addView(stepContentViews.get(stepNumber));
        } else {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        addStepToContent(stepLayout);
    }

    protected void addStepToContent(LinearLayout stepLayout) {
        content.addView(stepLayout);
    }

    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout) {
        LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line);
        LinearLayout stepLeftLine2 = (LinearLayout) stepLayout.findViewById(R.id.vertical_line_subtitle);
        confirmationButton = getNextStepButton(stepLayout);

        stepLeftLine.setVisibility(View.INVISIBLE);
        stepLeftLine2.setVisibility(View.INVISIBLE);

        disableConfirmationButton();

        if (nextStepButtonTexts != null && nextStepButtonTexts.size() > numberOfSteps && nextStepButtonTexts.get(numberOfSteps) != null && !"".contentEquals(nextStepButtonTexts.get(numberOfSteps))) {
            confirmationButton.setText(nextStepButtonTexts.get(numberOfSteps));
        } else {
            confirmationButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        }
        confirmationButton.setTag(NavigationClickListener.ACTION_COMPLETE_FORM);
        confirmationButton.setOnClickListener(navListener);

        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    protected LinearLayout createStepLayout(final int stepNumber) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout stepLayout = generateStepLayout(inflater);

        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        if (bg != null) {
            bg.setColorFilter(new PorterDuffColorFilter(
                    stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
            circle.setBackground(bg);
        } else {
            circle.setBackgroundResource(R.drawable.circle_step_done);
        }

        TextView stepTitle = (TextView) stepLayout.findViewById(R.id.step_title);
        stepTitle.setText(steps.get(stepNumber));
        stepTitle.setTextColor(stepTitleTextColor);
        stepsTitlesViews.add(stepNumber, stepTitle);

        TextView stepSubtitle = null;
        if (stepsSubtitles != null && stepNumber < stepsSubtitles.size()) {
            String subtitle = stepsSubtitles.get(stepNumber);
            if(subtitle != null && !subtitle.equals("")) {
                stepSubtitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);
                stepSubtitle.setText(subtitle);
                stepSubtitle.setTextColor(stepSubtitleTextColor);
                stepSubtitle.setVisibility(View.VISIBLE);
            }
        }
        stepsSubtitlesViews.add(stepNumber, stepSubtitle);

        TextView stepNumberTextView = (TextView) stepLayout.findViewById(R.id.step_number);
        stepNumberTextView.setText(String.valueOf(stepNumber + 1));
        stepNumberTextView.setTextColor(stepNumberTextColor);

        ImageView stepDoneImageView = (ImageView) stepLayout.findViewById(R.id.step_done);
        stepDoneImageView.setColorFilter(stepNumberTextColor);

        TextView errorMessage = (TextView) stepLayout.findViewById(R.id.error_message);
        ImageView errorIconView = (ImageView) stepLayout.findViewById(R.id.error_icon);
        if (errorIcon != -1) {
            errorIconView.setImageResource(errorIcon);
        }
        errorMessage.setTextColor(errorMessageTextColor);
        errorIconView.setColorFilter(errorMessageTextColor);

        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setTag(stepNumber);
        stepHeader.setOnClickListener(navListener);

        createNextButton(inflater, stepNumber, stepLayout);

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    /**
     * Create the next-step "continue" button for an individual step
     */
    protected void createNextButton(@NonNull LayoutInflater inflater, final int stepNumber, @NonNull LinearLayout stepLayout) {
        AppCompatButton nextButton;
        if (customButtonLayout == -1) {
            View v = inflater.inflate(R.layout.step_layout_next_button, (ViewGroup) stepLayout.findViewById(R.id.next_step_button_insert), true);
            verticalStepperFormImplementation.onNextStepViewInflated(v, stepNumber);
            nextButton = (AppCompatButton) v.findViewById(R.id.next_step);
        } else {
            View v = inflater.inflate(customButtonLayout, (ViewGroup) stepLayout.findViewById(R.id.next_step_button_insert), true);
            verticalStepperFormImplementation.onNextStepViewInflated(v, stepNumber);
            nextButton = (AppCompatButton) v.findViewById(customButtonId);
        }
        setButtonColor(nextButton, buttonBackgroundColor, buttonTextColor, buttonPressedBackgroundColor, buttonPressedTextColor);
        nextButton.setTag(NavigationClickListener.ACTION_NEXT);
        nextButton.setOnClickListener(navListener);
        if (customButtonText != null) { //NOTE: This ends up as the default if any array entry is null or blank
            nextButton.setText(customButtonText);
        }
        if (nextStepButtonTexts != null && nextStepButtonTexts.size() > stepNumber && nextStepButtonTexts.get(stepNumber) != null) {
            nextButton.setText(nextStepButtonTexts.get(stepNumber));
        }
        nextStepButtonViews.add(stepNumber, nextButton);
    }

    protected LinearLayout generateStepLayout(@NonNull LayoutInflater inflater) {
        return (LinearLayout) inflater.inflate(R.layout.step_layout, content, false);
    }

    protected void openStep(int stepNumber, boolean restoration) {
        if (stepNumber >= 0 && stepNumber <= numberOfSteps) {
            activeStep = stepNumber;
            //Log.d(getClass().getSimpleName(), "Opened step " + activeStep);

            if (stepNumber == 0) {
                disablePreviousButtonInBottomNavigationLayout();
            } else {
                enablePreviousButtonInBottomNavigationLayout();
            }

            if (completedSteps[stepNumber] && activeStep != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }

            for (int i = 0; i <= numberOfSteps; i++) {
                if (i != stepNumber) {
                    disableStepLayout(i, !restoration);
                } else {
                    enableStepLayout(i, !restoration);
                }
            }

            scrollToActiveStep(!restoration);

            if (isValidateOnButtonPress || stepNumber == numberOfSteps) {
                setStepAsCompleted(stepNumber);
            }

            verticalStepperFormImplementation.onStepOpening(stepNumber);
        }
    }

    protected void scrollToStep(final int stepNumber, boolean smoothScroll) {
        if (smoothScroll) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.smoothScrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        } else {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.scrollTo(0, stepLayouts.get(stepNumber).getTop());
                }
            });
        }
    }

    protected void scrollToActiveStep(boolean smoothScroll) {
        scrollToStep(activeStep, smoothScroll);
    }

    protected void findViews() {
        content = (LinearLayout) findViewById(R.id.content);
        stepsScrollView = (ScrollView) findViewById(R.id.steps_scroll);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        previousStepButton = (AppCompatImageButton) findViewById(R.id.down_previous);
        nextStepButton = (AppCompatImageButton) findViewById(R.id.down_next);
        bottomNavigation = (RelativeLayout) findViewById(R.id.bottom_navigation);
    }

    protected void disableStepLayout(int stepNumber, boolean smoothieDisabling) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);

        if (smoothieDisabling) {
            Animations.slideUp(button);
            Animations.slideUp(stepContent);
        } else {
            button.setVisibility(View.GONE);
            stepContent.setVisibility(View.GONE);
        }

        if (!completedSteps[stepNumber]) {
            disableStepHeader(stepLayout);
            stepDone.setVisibility(View.INVISIBLE);
            stepNumberTextView.setVisibility(View.VISIBLE);
        } else {
            enableStepHeader(stepLayout);
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
        }

        showVerticalLineInCollapsedStepIfNecessary(stepLayout);

    }

    protected void enableStepLayout(int stepNumber, boolean smoothieEnabling) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
        ImageView stepDone = (ImageView) stepHeader.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) stepHeader.findViewById(R.id.step_number);
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);

        enableStepHeader(stepLayout);

        if (smoothieEnabling) {
            Animations.slideDown(stepContent);
            Animations.slideDown(button);
        } else {
            stepContent.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }

        if (completedSteps[stepNumber] && activeStep != stepNumber) {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.INVISIBLE);
        } else {
            stepDone.setVisibility(View.INVISIBLE);
            stepNumberTextView.setVisibility(View.VISIBLE);
        }

        hideVerticalLineInCollapsedStepIfNecessary(stepLayout);

    }

    protected void enableStepHeader(LinearLayout stepLayout) {
        setHeaderAppearance(stepLayout, 1, buttonBackgroundColor);
    }

    protected void disableStepHeader(LinearLayout stepLayout) {
        setHeaderAppearance(stepLayout, alphaOfDisabledElements, Color.rgb(176, 176, 176));
    }

    protected void showVerticalLineInCollapsedStepIfNecessary(LinearLayout stepLayout) {
        // The height of the line will be 16dp when the subtitle textview is gone
        if(showVerticalLineWhenStepsAreCollapsed) {
            setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(stepLayout, 16);
        }
    }

    protected void hideVerticalLineInCollapsedStepIfNecessary(LinearLayout stepLayout) {
        // The height of the line will be 0 when the subtitle text is being shown
        if(showVerticalLineWhenStepsAreCollapsed) {
            setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(stepLayout, 0);
        }
    }

    protected void displayCurrentProgress() {
        int progress = 0;
        for (int i = 0; i < (completedSteps.length - 1); i++) {
            if (completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    protected void displayMaxProgress() {
        setProgress(numberOfSteps + 1);
    }

    protected void setAuxVars() {
        completedSteps = new boolean[numberOfSteps + 1];
        for (int i = 0; i < (numberOfSteps + 1); i++) {
            completedSteps[i] = false;
        }
        progressBar.setMax(numberOfSteps + 1);
    }

    protected void addConfirmationStepToStepsList() {
        String confirmationStepText = context.getString(R.string.vertical_form_stepper_form_last_step);
        steps.add(confirmationStepText);
    }

    protected void disablePreviousButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(previousStepButton);
    }

    protected void enablePreviousButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(previousStepButton);
    }

    protected void disableNextButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(nextStepButton);
    }

    protected void enableNextButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(nextStepButton);
    }

    protected void enableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(1f);
        button.setEnabled(true);
    }

    protected void disableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(alphaOfDisabledElements);
        button.setEnabled(false);
    }

    protected void setProgress(int progress) {
        if (progress > 0 && progress <= (numberOfSteps + 1)) {
            progressBar.setProgress(progress);
        }
    }

    protected void disableConfirmationButton() {
        confirmationButton.setEnabled(false);
        confirmationButton.setAlpha(alphaOfDisabledElements);
    }

    protected void hideSoftKeyboard() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    protected void prepareSendingAndSend() {
        displayDoneIconInConfirmationStep();
        disableConfirmationButton();
        displayMaxProgress();
        verticalStepperFormImplementation.sendData();
    }

    protected void displayDoneIconInConfirmationStep() {
        LinearLayout confirmationStepLayout = stepLayouts.get(stepLayouts.size() - 1);
        ImageView stepDone = (ImageView) confirmationStepLayout.findViewById(R.id.step_done);
        TextView stepNumberTextView = (TextView) confirmationStepLayout.findViewById(R.id.step_number);
        stepDone.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.INVISIBLE);
    }

    protected void restoreFormState() {
        goToStep(activeStep, true);
        displayCurrentProgress();
    }

    protected int convertDpToPixel(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int)px;
    }

    protected void setVerticalLineNearSubtitleHeightWhenSubtitleIsGone(LinearLayout stepLayout, int height) {
        TextView stepSubtitle = (TextView) stepLayout.findViewById(R.id.step_subtitle);
        if (stepSubtitle.getVisibility() == View.GONE) {
            LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line_subtitle);
            LayoutParams params = (LayoutParams) stepLeftLine.getLayoutParams();
            params.height = convertDpToPixel(height);
            stepLeftLine.setLayoutParams(params);
        }
    }

    protected void setHeaderAppearance(LinearLayout stepLayout, float alpha,
                                       int stepCircleBackgroundColor) {
        if(!materialDesignInDisabledSteps) {
            RelativeLayout stepHeader = (RelativeLayout) stepLayout.findViewById(R.id.step_header);
            TextView title = (TextView) stepHeader.findViewById(R.id.step_title);
            TextView subtitle = (TextView) stepHeader.findViewById(R.id.step_subtitle);
            LinearLayout circle = (LinearLayout) stepHeader.findViewById(R.id.circle);
            ImageView done = (ImageView) stepHeader.findViewById(R.id.step_done);

            title.setAlpha(alpha);
            circle.setAlpha(alpha);
            done.setAlpha(alpha);

            if(subtitle.getText() != null && !subtitle.getText().equals("")) {
                if(alpha == 1) {
                    subtitle.setVisibility(View.VISIBLE);
                } else {
                    subtitle.setVisibility(View.GONE);
                }
            }
        } else {
            setStepCircleBackgroundColor(stepLayout, stepCircleBackgroundColor);
        }
    }

    protected void setStepCircleBackgroundColor(LinearLayout stepLayout, int color) {
        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        if (bg != null) {
            bg.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        } else {
            circle.setBackgroundResource(R.drawable.circle_step_done);
        }
        circle.setBackground(bg);
    }

    protected void setButtonColor(AppCompatButton button, int buttonColor, int buttonTextColor,
                                  int buttonPressedColor, int buttonPressedTextColor) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{}
        };
        ColorStateList buttonColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedColor,
                        buttonPressedColor,
                        buttonColor
                });
        ColorStateList buttonTextColours = new ColorStateList(
                states,
                new int[]{
                        buttonPressedTextColor,
                        buttonPressedTextColor,
                        buttonTextColor
                });
        button.setSupportBackgroundTintList(buttonColours);
        button.setTextColor(buttonTextColours);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("activeStep", this.activeStep);
        bundle.putBooleanArray("completedSteps", this.completedSteps);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.activeStep = bundle.getInt("activeStep");
            this.completedSteps = bundle.getBooleanArray("completedSteps");
            state = bundle.getParcelable("superState");
            restoreFormState();
        }
        super.onRestoreInstanceState(state);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Builder {

        // Required parameters
        protected VerticalStepperFormLayout verticalStepperFormLayout;
        protected String[] steps;
        protected VerticalStepperForm verticalStepperFormImplementation;
        protected Activity activity;

        // Optional parameters
        protected String[] stepsSubtitles = null;
        protected String[] stepButtonTexts = null;
        protected float alphaOfDisabledElements = -1;
        protected @ColorInt int stepNumberBackgroundColor = -1;
        protected @ColorInt int buttonBackgroundColor = -1;
        protected @ColorInt int buttonPressedBackgroundColor = -1;
        protected @ColorInt int stepNumberTextColor = -1;
        protected @ColorInt int stepTitleTextColor = -1;
        protected @ColorInt int stepSubtitleTextColor = -1;
        protected @ColorInt int buttonTextColor = -1;
        protected @ColorInt int buttonPressedTextColor = -1;
        protected @ColorInt int errorMessageTextColor = -1;
        protected @ColorInt int bottomNavigationBackgroundColor = -1;
        protected @DrawableRes int errorIcon = -1;
        protected @LayoutRes int customButtonLayout = -1;
        protected @IdRes int customButtonId = -1;
        protected Boolean displayBottomNavigation = null;
        protected Boolean materialDesignInDisabledSteps = null;
        protected Boolean hideKeyboard = null;
        protected Boolean showVerticalLineWhenStepsAreCollapsed = null;

        protected Builder(VerticalStepperFormLayout stepperLayout,
                          String[] steps,
                          VerticalStepperForm stepperImplementation,
                          Activity activity) {
            this.verticalStepperFormLayout = stepperLayout;
            this.steps = steps;
            this.verticalStepperFormImplementation = stepperImplementation;
            this.activity = activity;
        }

        /**
         * Generates an instance of the builder that will set up and initialize the form (after
         * setting up the form it is mandatory to initialize it calling init())
         * @param stepperLayout the form layout
         * @param stepTitles a String array with the names of the steps
         * @param stepperImplementation The instance that implements "VerticalStepperForm" interface
         * @param activity The activity where the form is
         * @return an instance of the builder
         */
        public static Builder newInstance(VerticalStepperFormLayout stepperLayout,
                                          String[] stepTitles,
                                          VerticalStepperForm stepperImplementation,
                                          Activity activity) {

            return new Builder(stepperLayout, stepTitles, stepperImplementation, activity);
        }

        /**
         * Set the dark primary color resource (background color of the buttons when clicked)
         * @param colorPrimaryDark primary color resource (dark)
         * @return the builder instance
         */
        public Builder primaryDarkColorRes(@ColorRes int colorPrimaryDark) {
            this.buttonPressedBackgroundColor = ContextCompat.getColor(activity, colorPrimaryDark);
            return this;
        }

        /**
         * Set the primary color (background color of the left circles and buttons)
         * @param colorPrimary primary color
         * @return the builder instance
         */
        public Builder primaryColor(@ColorInt int colorPrimary) {
            this.stepNumberBackgroundColor = colorPrimary;
            this.buttonBackgroundColor = colorPrimary;
            return this;
        }

        /**
         * Set the dark primary color (background color of the buttons when clicked)
         * @param colorPrimaryDark primary color (dark)
         * @return the builder instance
         */
        public Builder primaryDarkColor(@ColorInt int colorPrimaryDark) {
            this.buttonPressedBackgroundColor = colorPrimaryDark;
            return this;
        }

        /**
         * Set the subtitles of the steps
         * @param stepsSubtitles a String array with the subtitles of the steps
         * @return the builder instance
         */
        public Builder stepsSubtitles(String[] stepsSubtitles) {
            this.stepsSubtitles = stepsSubtitles;
            return this;
        }

        /**
         * Set the button texts of the steps
         * @param stepButtonTexts a String array with the button texts for the steps
         * @return the builder instance
         */
        public Builder stepsButtonTexts(String[] stepButtonTexts) {
            this.stepButtonTexts = stepButtonTexts;
            return this;
        }

        /**
         * Set an bullet icon for when error messages are shown
         * @param errorIcon drawable for the error icon
         * @return the builder instance
         */
        public Builder errorIcon(@DrawableRes int errorIcon) {
            this.errorIcon = errorIcon;
            return this;
        }

        /**
         * Use a custom view in place of the usual "Continue" buttons
         * @param customLayoutRes a resource for a custom layout file
         * @param appCompatButtonId because the custom layout can be a compound view, you must specify
         *                       the id of the AppCompatButton to apply the click listener to.
         *                       Any colours set in the builder will also be applied.
         * @return the builder instance
         */
        public Builder buttonCustomLayout(@LayoutRes int customLayoutRes, @IdRes int appCompatButtonId) {
            this.customButtonLayout = customLayoutRes;
            this.customButtonId = appCompatButtonId;
            return this;
        }

        /**
         * Set the text color resource of the left circles
         * @param stepNumberTextColor text color resource of the left circles
         * @return the builder instance
         */
        public Builder stepNumberTextColorRes(@ColorRes int stepNumberTextColor) {
            this.stepNumberTextColor = ContextCompat.getColor(activity, stepNumberTextColor);
            return this;
        }

        /**
         * Set the text color resource of the step title
         * @param stepTitleTextColor the color resource of the step title
         * @return this builder instance
         */
        public Builder stepTitleTextColorRes(@ColorRes int stepTitleTextColor) {
            this.stepTitleTextColor = ContextCompat.getColor(activity, stepTitleTextColor);
            return this;
        }

        /**
         * Set the text color resource of the step subtitle
         * @param stepSubtitleTextColor the color resource of the step title
         * @return this builder instance
         */
        public Builder stepSubtitleTextColorRes(@ColorRes int stepSubtitleTextColor) {
            this.stepSubtitleTextColor = ContextCompat.getColor(activity, stepSubtitleTextColor);
            return this;
        }

        /**
         * Set the text color resource of the buttons
         * @param buttonTextColor text color resource of the buttons
         * @return the builder instance
         */
        public Builder buttonTextColorRes(@ColorRes int buttonTextColor) {
            this.buttonTextColor = ContextCompat.getColor(activity, buttonTextColor);
            return this;
        }

        /**
         * Set the text color of the buttons when clicked
         * @param buttonPressedTextColor text color of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedTextColorRes(@ColorRes int buttonPressedTextColor) {
            this.buttonPressedTextColor = ContextCompat.getColor(activity, buttonPressedTextColor);
            return this;
        }

        /**
         * Set the error message color resource
         * @param errorMessageTextColor error message color resource
         * @return the builder instance
         */
        public Builder errorMessageTextColorRes(@ColorRes int errorMessageTextColor) {
            this.errorMessageTextColor = ContextCompat.getColor(activity, errorMessageTextColor);
            return this;
        }

        /**
         * Set the color resource of the bottom navigation bar
         * @param color background color resource
         * @return the builder instance
         */
        public Builder bottomNavBackgroundColorRes(@ColorRes int color) {
            this.bottomNavigationBackgroundColor = ContextCompat.getColor(activity, color);
            return this;
        }

        /**
         * Set the background color resource of the left circles
         * @param stepNumberBackgroundColor background color resource of the left circles
         * @return the builder instance
         */
        public Builder stepNumberBackgroundColorRes(@ColorRes int stepNumberBackgroundColor) {
            this.stepNumberBackgroundColor = ContextCompat.getColor(activity, stepNumberBackgroundColor);
            return this;
        }

        /**
         * Set the background colour resource of the buttons
         * @param buttonBackgroundColor background color resource of the buttons
         * @return the builder instance
         */
        public Builder buttonBackgroundColorRes(@ColorRes int buttonBackgroundColor) {
            this.buttonBackgroundColor = ContextCompat.getColor(activity, buttonBackgroundColor);
            return this;
        }

        /**
         * Set the background color resource of the buttons when clicked
         * @param buttonPressedBackgroundColor background color resource of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedBackgroundColorRes(@ColorRes int buttonPressedBackgroundColor) {
            this.buttonPressedBackgroundColor = ContextCompat.getColor(activity, buttonPressedBackgroundColor);
            return this;
        }

        /**
         * Set the text color of the left circles
         * @param stepNumberTextColor text color of the left circles
         * @return the builder instance
         */
        public Builder stepNumberTextColor(@ColorInt int stepNumberTextColor) {
            this.stepNumberTextColor = stepNumberTextColor;
            return this;
        }

        /**
         * Set the text color of the step title
         * @param stepTitleTextColor the color of the step title
         * @return this builder instance
         */
        public Builder stepTitleTextColor(@ColorInt int stepTitleTextColor) {
            this.stepTitleTextColor = stepTitleTextColor;
            return this;
        }

        /**
         * Set the text color of the step subtitle
         * @param stepSubtitleTextColor the color of the step title
         * @return this builder instance
         */
        public Builder stepSubtitleTextColor(@ColorInt int stepSubtitleTextColor) {
            this.stepSubtitleTextColor = stepSubtitleTextColor;
            return this;
        }

        /**
         * Set the text color of the buttons
         * @param buttonTextColor text color of the buttons
         * @return the builder instance
         */
        public Builder buttonTextColor(@ColorInt int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        /**
         * Set the text color of the buttons when clicked
         * @param buttonPressedTextColor text color of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedTextColor(@ColorInt int buttonPressedTextColor) {
            this.buttonPressedTextColor = buttonPressedTextColor;
            return this;
        }

        /**
         * Set the error message color
         * @param errorMessageTextColor error message color
         * @return the builder instance
         */
        public Builder errorMessageTextColor(@ColorInt int errorMessageTextColor) {
            this.errorMessageTextColor = errorMessageTextColor;
            return this;
        }

        /**
         * Set the primary color resource (background color of the left circles and buttons)
         * @param colorPrimary primary color resource
         * @return the builder instance
         */
        public Builder primaryColorRes(@ColorRes int colorPrimary) {
            @ColorInt int resolvedColour = ContextCompat.getColor(activity, colorPrimary);
            this.stepNumberBackgroundColor = resolvedColour;
            this.buttonBackgroundColor = resolvedColour;
            return this;
        }

        /**
         * Set the color of the bottom navigation bar
         * @param color backgroudn color
         * @return the builder instance
         */
        public Builder bottomNavBackgroundColor(@ColorInt int color) {
            this.bottomNavigationBackgroundColor = color;
            return this;
        }

        /**
         * Set the background color of the left circles
         * @param stepNumberBackgroundColor background color of the left circles
         * @return the builder instance
         */
        public Builder stepNumberBackgroundColor(@ColorInt int stepNumberBackgroundColor) {
            this.stepNumberBackgroundColor = stepNumberBackgroundColor;
            return this;
        }

        /**
         * Set the background colour of the buttons
         * @param buttonBackgroundColor background color of the buttons
         * @return the builder instance
         */
        public Builder buttonBackgroundColor(@ColorInt int buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
            return this;
        }

        /**
         * Set the background color of the buttons when clicked
         * @param buttonPressedBackgroundColor background color of the buttons when clicked
         * @return the builder instance
         */
        public Builder buttonPressedBackgroundColor(@ColorInt int buttonPressedBackgroundColor) {
            this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
            return this;
        }

        /**
         * Set whether or not the bottom navigation bar will be displayed
         * @param displayBottomNavigationBar true to display it; false otherwise
         * @return the builder instance
         */
        public Builder displayBottomNavigation(boolean displayBottomNavigationBar) {
            this.displayBottomNavigation = displayBottomNavigationBar;
            return this;
        }

        /**
         * Set whether or not the disabled steps will have a Material Design look
         * @param materialDesignInDisabledSteps true to use Material Design for disabled steps; false otherwise
         * @return the builder instance
         */
        public Builder materialDesignInDisabledSteps(boolean materialDesignInDisabledSteps) {
            this.materialDesignInDisabledSteps = materialDesignInDisabledSteps;
            return this;
        }

        /**
         * Specify whether or not the keyboard should be hidden at the beginning
         * @param hideKeyboard true to hide the keyboard; false to not hide it
         * @return the builder instance
         */
        public Builder hideKeyboard(boolean hideKeyboard) {
            this.hideKeyboard = hideKeyboard;
            return this;
        }

        /**
         * Specify whether or not the vertical lines should be displayed when steps are collapsed
         * @param showVerticalLineWhenStepsAreCollapsed true to show the lines; false to not
         * @return the builder instance
         */
        public Builder showVerticalLineWhenStepsAreCollapsed(boolean showVerticalLineWhenStepsAreCollapsed) {
            this.showVerticalLineWhenStepsAreCollapsed = showVerticalLineWhenStepsAreCollapsed;
            return this;
        }

        /**
         * Set the alpha level of disabled elements
         * @param alpha alpha level of disabled elements (between 0 and 1)
         * @return the builder instance
         */
        public Builder alphaOfDisabledElements(float alpha) {
            this.alphaOfDisabledElements = alpha;
            return this;
        }

        /**
         * Set up the form and initialize it
         */
        public void init() {
            verticalStepperFormLayout.initialiseVerticalStepperForm(this);
        }

    }

    @SuppressWarnings("WeakerAccess")
    public static class NavigationClickListener implements View.OnClickListener {

        public static final int ACTION_NEXT = -4;
        public static final int ACTION_PREVIOUS = -6;
        public static final int ACTION_COMPLETE_FORM = -8;

        private final VerticalStepperFormLayout vsf;

        public NavigationClickListener(VerticalStepperFormLayout vsf) {
            this.vsf = vsf;
        }

        @Override
        public void onClick(View view) {
            int integ = (int) view.getTag();
            switch (integ) {
                case ACTION_COMPLETE_FORM:
                    onCompleteForm();
                    break;
                case ACTION_PREVIOUS:
                    onDoPrev();
                    break;
                case ACTION_NEXT:
                    onDoNext();
                    break;
                default:
                    onDoJump(integ);
            }

        }

        protected int getCurrentStep() {
            return vsf.activeStep;
        }

        protected void onCompleteForm() {
            vsf.prepareSendingAndSend();
        }

        protected void onDoPrev() {
            vsf.goToPreviousStep();
        }

        protected void onDoJump(int toStep) {
            vsf.goToStep(toStep, false);
        }

        protected void onDoNext() {
            if (vsf.verticalStepperFormImplementation != null) {
                //Note: at this point vsf.isActiveStepCompleted() can return false,
                //onClickNextStep will then revalidate, and by the following statement,
                //vsf.isActiveStepCompleted() may be true
                vsf.verticalStepperFormImplementation.onClickNextStep(getCurrentStep(), vsf.isActiveStepCompleted());
            }
            //goToStep((stepNumber + 1), false);
            if (vsf.isActiveStepCompleted()) {
                vsf.goToNextStep();
            } else if (vsf.isValidateOnButtonPress) {
                //NOTE: do NOT go to next step - but DO re-enable the button
                //NOTE: cannot just setActiveStepAsCompleted because it clears the error text!!
                vsf.setActiveNextStepButtonAsCompleted();
            }
        }

    }

}