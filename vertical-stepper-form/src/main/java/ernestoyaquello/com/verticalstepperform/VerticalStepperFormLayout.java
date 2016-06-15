package ernestoyaquello.com.verticalstepperform;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
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
import ernestoyaquello.com.verticalstepperform.utils.Appearance;

/**
 * Custom layout that implements a vertical stepper form
 */
public class VerticalStepperFormLayout extends RelativeLayout implements View.OnClickListener {

    // Style
    protected static float ALPHA_OF_DISABLED_ELEMENTS = 0.25f;
    protected int stepNumberBackgroundColor = Color.rgb(63, 81, 181);
    protected int buttonBackgroundColor = Color.rgb(63, 81, 181);
    protected int buttonPressedBackgroundColor = Color.rgb(48, 63, 159);
    protected int stepNumberTextColor = Color.rgb(255, 255, 255);
    protected int buttonTextColor = Color.rgb(255, 255, 255);
    protected int buttonPressedTextColor = Color.rgb(255, 255, 255);

    // Views
    protected LayoutInflater mInflater;
    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected List<LinearLayout> stepLayouts;
    protected List<View> stepContentViews;
    protected AppCompatButton confirmationButton;
    protected ProgressBar progressBar;
    protected AppCompatImageButton previousStepButton, nextStepButton;

    // Data
    protected List<String> steps;

    // Logic
    protected int activeStep = 0;
    protected int numberOfSteps;
    protected boolean[] completedSteps;

    // Listeners and callbacks
    protected VerticalStepperForm verticalStepperFormImplementation;

    // Context
    protected Context context;
    protected Activity activity;

    public VerticalStepperFormLayout(Context context) {
        super(context);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.vertical_stepper_form_layout, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    /**
     * This method initializes the form. It should be called only once from onCreate().
     */
    public void initialiseVerticalStepperForm(String[] stepsNames,
                                              int colorPrimary, int colorPrimaryDark,
                                              VerticalStepperForm verticalStepperForm,
                                              Activity activity) {

        this.stepNumberBackgroundColor = colorPrimary;
        this.buttonBackgroundColor = colorPrimary;
        this.buttonPressedBackgroundColor = colorPrimaryDark;

        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;

        initStepperForm(stepsNames);
    }

    /**
     * This method initializes the form. It should be called only once from onCreate().
     */
    public void initialiseVerticalStepperForm(String[] stepsNames,
                                              int buttonBackgroundColor, int buttonTextColor,
                                              int buttonPressedBackgroundColor, int buttonPressedTextColor,
                                              int stepNumberBackgroundColor, int stepNumberTextColor,
                                              VerticalStepperForm verticalStepperForm,
                                              Activity activity) {

        this.buttonBackgroundColor = buttonBackgroundColor;
        this.buttonTextColor = buttonTextColor;
        this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
        this.buttonPressedTextColor = buttonPressedTextColor;
        this.stepNumberBackgroundColor = stepNumberBackgroundColor;
        this.stepNumberTextColor = stepNumberTextColor;

        this.verticalStepperFormImplementation = verticalStepperForm;
        this.activity = activity;

        initStepperForm(stepsNames);
    }

    protected void initStepperForm(String[] stepsNames) {
        setSteps(stepsNames);

        List<View> stepContentLayouts = new ArrayList<View>();
        for(int i = 0; i < numberOfSteps; i++) {
            View stepLayout = verticalStepperFormImplementation.createStepContentView(i);
            stepContentLayouts.add(stepLayout);
        }
        stepContentViews = stepContentLayouts;

        initializeForm();

        verticalStepperFormImplementation.onStepOpening(getActiveStep());
    }

    public int getActiveStep() {
        return activeStep;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public List<String> getSteps() {
        return steps;
    }

    protected void setSteps(String[] steps) {
        this.steps = new ArrayList(Arrays.asList(steps));
        numberOfSteps = steps.length;
        setAuxVars();
        addConfirmationStepToStepsList();
    }

    protected void registerListeners() {
        previousStepButton.setOnClickListener(this);
        nextStepButton.setOnClickListener(this);
    }

    public void initializeForm() {
        setUpSteps();
    }

    protected void setUpSteps() {
        stepLayouts = new ArrayList<LinearLayout>();
        // Set up normal steps
        for(int i = 0; i < numberOfSteps; i++) {
            setUpStep(i);
        }
        // Set up confirmation step
        setUpStep(numberOfSteps);
    }

    protected void setUpStep(int stepNumber) {
        LinearLayout stepLayout = createStepLayout(stepNumber);
        if(stepNumber < numberOfSteps) {
            // The content of the step is the corresponding custom view previously created
            RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
            stepContent.addView(stepContentViews.get(stepNumber));
        } else {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        if(stepNumber > 0) {
            disableStepLayout(stepNumber);
        }
        addStepToContent(stepLayout);
    }

    protected void addStepToContent(LinearLayout stepLayout) {
        content.addView(stepLayout);
    }

    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout) {
        LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line);
        stepLeftLine.setVisibility(View.INVISIBLE);

        LinearLayout buttons = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        buttons.setVisibility(View.GONE);

        confirmationButton = (AppCompatButton) buttons.findViewById(R.id.next_step);
        confirmationButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        confirmationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDataSendingAndSend();
            }
        });

        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    protected LinearLayout createStepLayout(final int stepNumber) {
        LinearLayout stepLayout = generateStepLayout();

        AppCompatButton nextButton = (AppCompatButton) stepLayout.findViewById(R.id.next_step);
        Appearance.setButtonColor(nextButton,
                buttonBackgroundColor, buttonTextColor, buttonPressedBackgroundColor, buttonPressedTextColor);

        LinearLayout circle = (LinearLayout) stepLayout.findViewById(R.id.circle);
        Drawable bg = ContextCompat.getDrawable(context, R.drawable.circle_step_done);
        bg.setColorFilter(new PorterDuffColorFilter(
                stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        circle.setBackground(bg);

        TextView stepTitle = (TextView)stepLayout.findViewById(R.id.step_title);
        stepTitle.setText(steps.get(stepNumber));

        TextView stepNumberTextView = (TextView)stepLayout.findViewById(R.id.step_number);
        stepNumberTextView.setText(String.valueOf(stepNumber + 1));
        stepNumberTextView.setTextColor(stepNumberTextColor);

        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep(stepNumber, true, false);
            }
        });

        Button next = (Button) stepLayout.findViewById(R.id.next_step);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep((stepNumber + 1), true, false);
            }
        });

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    protected LinearLayout generateStepLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        return (LinearLayout) inflater.inflate(R.layout.step_layout, null, false);
    }

    protected void findViews() {
        content = (LinearLayout) findViewById(R.id.content);
        stepsScrollView = (ScrollView) findViewById(R.id.steps_scroll);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        previousStepButton = (AppCompatImageButton) findViewById(R.id.down_previous);
        nextStepButton = (AppCompatImageButton) findViewById(R.id.down_next);
    }

    public void enableActiveStepLayout() {
        enableStepLayout(getActiveStep());
    }

    public void disableActiveStepLayout() {
        disableStepLayout(activeStep);
    }

    public void disableStepLayout(int stepNumber) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        if(!completedSteps[stepNumber]) {
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        } else {
            stepHeader.setAlpha(1);
        }

        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        button.setVisibility(View.GONE);

        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        stepContent.setVisibility(View.GONE);
    }

    public void enableStepLayout(int stepNumber) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setAlpha(1);

        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        if(completedSteps[stepNumber]) {
            //button.setVisibility(View.VISIBLE);
            Animations.slideDown(button);
            enableNextButtonInBottomNavigationLayout();
        } else {
            button.setVisibility(View.GONE);
            disableNextButtonInBottomNavigationLayout();
        }

        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        //stepContent.setVisibility(View.VISIBLE);
        Animations.slideDown(stepContent);

        if(stepNumber > 0) {
            enablePreviousButtonInBottomNavigationLayout();
        } else {
            disablePreviousButtonInBottomNavigationLayout();
        }
    }

    public void displayCurrentProgress() {
        int progress = 0;
        for(int i = 0; i < (completedSteps.length - 1); i++) {
            if(completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    public void displayMaxProgress() {
        setProgress(numberOfSteps + 1);
    }

    public void setActiveStepAsCompleted() {
        setStepAsCompleted(activeStep);
    }

    public void setActiveStepAsUncompleted() {
        setStepAsUncompleted(activeStep);
    }

    public void setStepAsCompleted(int stepNumber) {
        completedSteps[stepNumber] = true;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        if(stepNumber == activeStep) {
            LinearLayout buttons = (LinearLayout)
                    stepLayout.findViewById(R.id.next_step_button_container);
            //buttons.setVisibility(View.VISIBLE);
            Animations.slideDown(buttons);
            enableNextButtonInBottomNavigationLayout();
        }

        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setAlpha(1);

        displayCurrentProgress();
    }

    public void setStepAsUncompleted(int stepNumber) {
        completedSteps[stepNumber] = false;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        LinearLayout buttons = (LinearLayout)
                stepLayout.findViewById(R.id.next_step_button_container);
        //buttons.setVisibility(View.GONE);
        Animations.slideUp(buttons);

        if (stepNumber == activeStep) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        }

        if(stepNumber < numberOfSteps) {
            setStepAsUncompleted(numberOfSteps);
        }

        displayCurrentProgress();
    }
    
    public boolean isActiveStepCompleted() {
        return isStepCompleted(activeStep);
    }

    public boolean isStepCompleted(int stepNumber) {
        return completedSteps[stepNumber];
    }

    public boolean previousStepsAreCompleted(int stepNumber) {
        boolean previousStepsAreCompleted = true;
        for(int i = (stepNumber-1); i >= 0 && previousStepsAreCompleted; i--) {
            previousStepsAreCompleted = completedSteps[i];
        }
        return previousStepsAreCompleted;
    }

    public void goToNextStep() {
        goToStep(activeStep + 1, true, false);
    }

    public void goToPreviousStep() {
        goToStep(activeStep - 1, true, false);
    }

    public void goToStep(int clickedStepNumber, boolean smoothScroll, boolean restoration) {
        if(activeStep != clickedStepNumber || restoration) {
            hideSoftKeyboard();
            boolean previousStepsAreCompleted =
                    previousStepsAreCompleted(clickedStepNumber);
            if (clickedStepNumber == 0 || previousStepsAreCompleted) {
                openStep(clickedStepNumber, smoothScroll);
            }
        }
        if(activeStep == numberOfSteps) {
            disableNextButtonInBottomNavigationLayout();
        }
    }

    public void scrollToStep(final int stepNumber, boolean smoothScroll) {
        if(smoothScroll) {
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

    public void scrollToActiveStep(boolean smoothScroll) {
        scrollToStep(activeStep, smoothScroll);
    }

    public void openStep(int stepNumber, boolean smoothScroll) {
        if(stepNumber >= 0 && stepNumber <= numberOfSteps) {
            disableActiveStepLayout();

            activeStep = stepNumber;
            enableActiveStepLayout();
            scrollToActiveStep(smoothScroll);

            if (stepNumber == numberOfSteps) {
                setStepAsCompleted(stepNumber);
            }

            verticalStepperFormImplementation.onStepOpening(stepNumber);
        }
    }

    protected void setAuxVars() {
        completedSteps = new boolean[numberOfSteps + 1];
        for(int i = 0; i < (numberOfSteps + 1); i++) {
            completedSteps[i] = false;
        }
        progressBar.setMax(numberOfSteps + 1);
    }

    protected void addConfirmationStepToStepsList() {
        String confirmationStepText = context.getString(R.string.vertical_form_stepper_form_last_step);
        steps.add(confirmationStepText);
    }

    public void disablePreviousButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(previousStepButton);
    }

    public void enablePreviousButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(previousStepButton);
    }

    public void disableNextButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(nextStepButton);
    }

    public void enableNextButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(nextStepButton);
    }

    protected void enableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(1f);
        //button.setClickable(true);
    }

    protected void disableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        //button.setClickable(false);
    }

    public void setProgress(int progress) {
        if(progress > 0 && progress <= (numberOfSteps+1)) {
            progressBar.setProgress(progress);
        }
    }

    public void disableConfirmationButton() {
        confirmationButton.setClickable(false);
        confirmationButton.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
    }

    protected void hideSoftKeyboard() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void prepareDataSendingAndSend() {
        disableConfirmationButton();
        displayMaxProgress();
        verticalStepperFormImplementation.sendData();
    }

    protected void restoreFormState() {
        for (int i = 0; i < completedSteps.length; i++) {
            if (completedSteps[i]) {
                disableStepLayout(i);
            }
        }
        goToStep(activeStep, false, true);
        displayCurrentProgress();
    }

    @Override
    public void onClick(View v) {
        String previousNavigationButtonTag =
                context.getString(R.string.vertical_form_stepper_form_down_previous);
        if(((String)v.getTag()).equals(previousNavigationButtonTag)) {
            goToPreviousStep();
        } else {
            if(isActiveStepCompleted()) {
                goToNextStep();
            }
        }
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

}
