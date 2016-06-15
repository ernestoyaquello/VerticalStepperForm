package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

import ernestoyaquello.com.verticalstepperform.interfaces.Callback;
import ernestoyaquello.com.verticalstepperform.interfaces.OnBottomNavigationButtonClickListener;
import ernestoyaquello.com.verticalstepperform.interfaces.OnStepSelectedListener;
import ernestoyaquello.com.verticalstepperform.utils.Animations;
import ernestoyaquello.com.verticalstepperform.utils.Appearance;

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
    protected OnBottomNavigationButtonClickListener onButtonClickListener;
    protected OnStepSelectedListener onStepSelectedListener;
    protected Callback dataSendingConfirmation;

    // Others
    protected Context context;

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

    public void setStepContentViews(List<View> stepContentViews) {
        this.stepContentViews = stepContentViews;
    }

    public boolean[] getCompletedSteps() {
        return completedSteps;
    }

    public void setOnButtonClickListener(
            OnBottomNavigationButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public void setOnStepSelectedListener(
            OnStepSelectedListener onStepSelectedListener) {
        this.onStepSelectedListener = onStepSelectedListener;
    }

    public void setDataSendingConfirmationCallback(Callback callback) {
        dataSendingConfirmation = callback;
    }

    public void setColorPrimary(int colorPrimary) {
        stepNumberBackgroundColor = colorPrimary;
        buttonBackgroundColor = colorPrimary;
    }

    public void setColorPrimaryDark(int colorPrimaryDark) {
        buttonPressedBackgroundColor = colorPrimaryDark;
    }

    public void setButtonBackgroundColor(int buttonBackgroundColor) {
        this.buttonBackgroundColor = buttonBackgroundColor;
    }

    public void setButtonTextColor(int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public void setButtonPressedBackgroundColor(int buttonPressedBackgroundColor) {
        this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
    }

    public void setButtonPressedTextColor(int buttonPressedTextColor) {
        this.buttonPressedTextColor = buttonPressedTextColor;
    }

    public void setStepNumberBackgroundColor(int stepNumberBackgroundColor) {
        this.stepNumberBackgroundColor = stepNumberBackgroundColor;
    }

    public void setStepNumberTextColor(int stepNumberTextColor) {
        this.stepNumberTextColor = stepNumberTextColor;
    }

    public int getActiveStep() {
        return activeStep;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
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

    protected void setUpStep(int numStep) {
        LinearLayout stepLayout = createStepLayout(numStep);
        if(numStep < numberOfSteps) {
            // The content of the step is the corresponding custom view previously created
            RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
            stepContent.addView(stepContentViews.get(numStep));
        } else {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        if(numStep > 0) {
            disableStepLayout(numStep);
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
                dataSendingConfirmation.executeCallback();
                confirmationButton.setClickable(false);
                confirmationButton.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
            }
        });

        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    protected LinearLayout createStepLayout(final int stepNumber) {
        LinearLayout stepLayout = getStepLayout();

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
                onStepSelectedListener.onStepSelected(stepNumber, true);
            }
        });

        Button next = (Button) stepLayout.findViewById(R.id.next_step);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStepSelectedListener.onStepSelected((stepNumber + 1), true);
            }
        });

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    protected LinearLayout getStepLayout() {
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
        disableStepLayout(getActiveStep());
    }

    public void disableStepLayout(int stepNumber) {
        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        if(!completedSteps[stepNumber]) {
            LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
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

    public void setCurrentProgress() {
        int progress = 0;
        for(int i = 0; i < (completedSteps.length - 1); i++) {
            if(completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    public void setMaxProgress() {
        setProgress(completedSteps.length);
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

        if(stepNumber == getActiveStep()) {
            LinearLayout buttons = (LinearLayout)
                    stepLayout.findViewById(R.id.next_step_button_container);
            //buttons.setVisibility(View.VISIBLE);
            Animations.slideDown(buttons);
            enableNextButtonInBottomNavigationLayout();
        }

        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setAlpha(1);

        setCurrentProgress();
    }

    public void setStepAsUncompleted(int stepNumber) {
        completedSteps[stepNumber] = false;

        LinearLayout stepLayout = stepLayouts.get(stepNumber);

        LinearLayout buttons = (LinearLayout)
                stepLayout.findViewById(R.id.next_step_button_container);
        //buttons.setVisibility(View.GONE);
        Animations.slideUp(buttons);

        if (stepNumber == getActiveStep()) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        }

        if(stepNumber < numberOfSteps) {
            completedSteps[getNumberOfSteps()] = false;
            setStepAsUncompleted(getNumberOfSteps());
        }

        setCurrentProgress();
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

    public void moveToStep(int stepNumber, boolean smoothScroll) {
        if(stepNumber >= 0 && stepNumber <= numberOfSteps) {
            disableActiveStepLayout();

            activeStep = stepNumber;
            enableActiveStepLayout();
            scrollToActiveStep(smoothScroll);

            if (stepNumber == numberOfSteps) {
                setStepAsCompleted(stepNumber);
            }
        }
    }

    protected void setAuxVars() {
        completedSteps = new boolean[numberOfSteps + 1];
        for(int i = 0; i < (numberOfSteps + 1); i++) {
            completedSteps[i] = false;
        }
        progressBar.setMax(numberOfSteps + 1);
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(String[] steps) {
        this.steps = new ArrayList(Arrays.asList(steps));
        numberOfSteps = steps.length;
        setAuxVars();
        addConfirmationStepToStepsList();
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
        numberOfSteps = steps.size();
        setAuxVars();
        addConfirmationStepToStepsList();
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

    public void enableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(1f);
        //button.setClickable(true);
    }

    public void disableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        //button.setClickable(false);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void disableConfirmationButton() {
        confirmationButton.setClickable(false);
        confirmationButton.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
    }

    @Override
    public void onClick(View v) {
        String previousNavigationButtonTag =
                context.getString(R.string.vertical_form_stepper_form_down_previous);
        if(((String)v.getTag()).equals(previousNavigationButtonTag)) {
            onButtonClickListener.onNavigationButtonClick(
                    OnBottomNavigationButtonClickListener.ButtonType.PREVIOUS);
        } else {
            onButtonClickListener.onNavigationButtonClick(
                    OnBottomNavigationButtonClickListener.ButtonType.NEXT);
        }
    }
}
