package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.listener.VerticalStepperFormListener;
import ernestoyaquello.com.verticalstepperform.util.Animations;

/**
 * Custom layout that implements a vertical stepper form.
 */
public class VerticalStepperFormLayout extends LinearLayout {

    protected float alphaOfDisabledElements;
    protected int stepNumberBackgroundColor;
    protected int buttonBackgroundColor;
    protected int buttonPressedBackgroundColor;
    protected int stepNumberTextColor;
    protected int stepTitleTextColor;
    protected int stepSubtitleTextColor;
    protected int buttonTextColor;
    protected int buttonPressedTextColor;
    protected int errorMessageTextColor;
    protected boolean displayBottomNavigation;
    protected boolean showVerticalLineWhenStepsAreCollapsed;

    protected LayoutInflater mInflater;
    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected List<View> stepLayouts;
    protected List<View> stepContentViews;
    protected List<TextView> stepsTitlesViews;
    protected List<TextView> stepsSubtitlesViews;
    protected MaterialButton confirmationButton;
    protected ProgressBar progressBar;
    protected AppCompatImageButton previousStepButton, nextStepButton;
    protected View bottomNavigation;

    protected List<String> stepTitles;
    protected List<String> stepSubtitles;

    protected int activeStep = 0;
    protected int numberOfSteps;
    protected boolean[] completedSteps;

    protected VerticalStepperFormListener verticalStepperFormListener;

    public VerticalStepperFormLayout(Context context) {
        super(context);

        inflateLayout(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflateLayout(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflateLayout(context);
    }

    private void inflateLayout(Context context) {
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.vertical_stepper_form_layout, this, true);
    }

    /**
     * Returns the title of a specified step.
     * 
     * @param stepPosition The step position, counting from 0.
     * @return The title string.
     */
    public String getStepTitle(int stepPosition) {
        return stepTitles.get(stepPosition);
    }

    /**
     * Returns the subtitle of a specified step.
     * 
     * @param stepPosition The step position, counting from 0.
     * @return The subtitle string.
     */
    public String getStepsSubtitles(int stepPosition) {
        if(stepSubtitles != null) {
            return stepSubtitles.get(stepPosition);
        }
        return null;
    }

    /**
     * Returns the active step number.
     * 
     * @return The active step number, counting from 0.
     */
    public int getActiveStepNumber() {
        return activeStep;
    }

    /**
     * Sets the title of a specified step.
     * 
     * @param stepPosition The step position, counting from 0.
     * @param title New title of the step.
     */
    public void setStepTitle(int stepPosition, String title) {
        if(title != null && !title.equals("")) {
            stepTitles.set(stepPosition, title);
            TextView titleView = stepsTitlesViews.get(stepPosition);
            if (titleView != null) {
                titleView.setText(title);
            }
        }
    }

    /**
     * Sets the subtitle of a specified step.
     * 
     * @param stepPosition The step position, counting from 0.
     * @param subtitle New subtitle of the step.
     */
    public void setStepSubtitle(int stepPosition, String subtitle) {
        if(stepSubtitles != null && subtitle != null && !subtitle.equals("")) {
            stepSubtitles.set(stepPosition, subtitle);
            TextView subtitleView = stepsSubtitlesViews.get(stepPosition);
            if (subtitleView != null) {
                subtitleView.setText(subtitle);
            }
        }
    }

    /**
     * Sets the active step as completed.
     */
    public void setActiveStepAsCompleted() {
        setStepAsCompleted(activeStep);
    }

    /**
     * Sets the active step as not completed.
     * 
     * @param errorMessage Error message that will be displayed in the step. If null, no error
     *                     message will be displayed.
     */
    public void setActiveStepAsUncompleted(String errorMessage) {
        setStepAsUncompleted(activeStep, errorMessage);
    }

    /**
     * Sets the specified step as completed.
     * 
     * @param stepPosition The step position, counting from 0.
     */
    public void setStepAsCompleted(int stepPosition) {
        completedSteps[stepPosition] = true;

        View stepLayout = stepLayouts.get(stepPosition);
        View stepDone = stepLayout.findViewById(R.id.step_done);
        View stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        View errorContainer = stepLayout.findViewById(R.id.error_container);
        TextView errorTextView = errorContainer.findViewById(R.id.error_message);
        View nextButton = stepLayout.findViewById(R.id.next_step);

        enableStepHeader(stepLayout);

        nextButton.setEnabled(true);
        nextButton.setAlpha(1);

        if (stepPosition != activeStep) {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.GONE);
        } else {
            if (stepPosition != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }
        }

        errorTextView.setText("");
        Animations.slideUp(errorContainer);

        displayCurrentProgress();
    }

    /**
     * Sets the specified step as not completed.
     *
     * @param stepPosition The step position, counting from 0.
     * @param errorMessage Error message that will be displayed in the step. If null, no error
     *                     message will be displayed.
     */
    public void setStepAsUncompleted(int stepPosition, String errorMessage) {
        completedSteps[stepPosition] = false;

        View stepLayout = stepLayouts.get(stepPosition);
        View stepDone = stepLayout.findViewById(R.id.step_done);
        View stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        View nextButton = stepLayout.findViewById(R.id.next_step);

        stepDone.setVisibility(View.GONE);
        stepNumberTextView.setVisibility(View.VISIBLE);

        nextButton.setEnabled(false);
        nextButton.setAlpha(alphaOfDisabledElements);

        if (stepPosition == activeStep) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            disableStepHeader(stepLayout);
        }

        if (stepPosition < numberOfSteps) {
            setStepAsUncompleted(numberOfSteps, null);
        }

        if (errorMessage != null && !errorMessage.equals("")) {
            View errorContainer = stepLayout.findViewById(R.id.error_container);
            TextView errorTextView = errorContainer.findViewById(R.id.error_message);

            errorTextView.setText(errorMessage);
            Animations.slideDown(errorContainer);
        }

        displayCurrentProgress();
    }

    /**
     * Determines whether the active step is completed or not.
     *
     * @return True if the active step is completed; false otherwise
     */
    public boolean isActiveStepCompleted() {
        return isStepCompleted(activeStep);
    }

    /**
     * Determines whether the specified step is completed or not.
     *
     * @param stepPosition The step position, counting from 0.
     * @return True if the step is completed, false otherwise.
     */
    public boolean isStepCompleted(int stepPosition) {
        return completedSteps[stepPosition];
    }

    /**
     * Determines if any step has been completed.
     *
     * @return True if at least 1 step has been completed; false otherwise.
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
     * Determines if the stepTitles that are previous to the specified one are completed.
     *
     * @param stepPosition The step position, counting from 0.
     * @return True if all the previous stepTitles have been completed; false otherwise.
     */
    public boolean arePreviousStepsCompleted(int stepPosition) {
        boolean previousStepsAreCompleted = true;
        for (int i = (stepPosition - 1); i >= 0 && previousStepsAreCompleted; i--) {
            previousStepsAreCompleted = completedSteps[i];
        }
        return previousStepsAreCompleted;
    }

    /**
     * Goes to the next step and opens it.
     */
    public void goToNextStep() {
        goToStep(activeStep + 1, false);
    }

    /**
     * Goes to the previous step and opens it.
     */
    public void goToPreviousStep() {
        goToStep(activeStep - 1, false);
    }

    /**
     * Goes to the selected step and opens it.
     *
     * @param stepPosition The step position, counting from 0.
     * @param restoration True if the method has been called to restore the form; false otherwise.
     */
    public void goToStep(int stepPosition, boolean restoration) {
        if (activeStep != stepPosition || restoration) {
            boolean previousStepsAreCompleted =
                    arePreviousStepsCompleted(stepPosition);
            if (stepPosition == 0 || previousStepsAreCompleted) {
                openStep(stepPosition, restoration);
            }
        }
    }

    private void initialiseVerticalStepperForm(Builder builder) {

        this.verticalStepperFormListener = builder.verticalStepperFormListener;
        this.alphaOfDisabledElements = builder.alphaOfDisabledElements;
        this.stepNumberBackgroundColor = builder.stepNumberBackgroundColor;
        this.buttonBackgroundColor = builder.buttonBackgroundColor;
        this.buttonPressedBackgroundColor = builder.buttonPressedBackgroundColor;
        this.stepNumberTextColor = builder.stepNumberTextColor;
        this.stepTitleTextColor = builder.stepTitleTextColor;
        this.stepSubtitleTextColor = builder.stepSubtitleTextColor;
        this.buttonTextColor = builder.buttonTextColor;
        this.buttonPressedTextColor = builder.buttonPressedTextColor;
        this.errorMessageTextColor = builder.errorMessageTextColor;
        this.displayBottomNavigation = builder.displayBottomNavigation;
        this.showVerticalLineWhenStepsAreCollapsed = builder.showVerticalLineWhenStepsAreCollapsed;

        initStepperForm(builder.stepTitles, builder.stepSubtitles);
    }

    private void initStepperForm(String[] stepTitles, String[] stepSubtitles) {
        setSteps(stepTitles, stepSubtitles);

        List<View> stepContentLayouts = new ArrayList<>();
        for (int i = 0; i < numberOfSteps; i++) {
            View stepLayout = verticalStepperFormListener.getStepLayout(i);
            stepContentLayouts.add(stepLayout);
        }
        stepContentViews = stepContentLayouts;

        initializeForm();

        verticalStepperFormListener.onStepOpened(activeStep);
    }

    private void setSteps(String[] steps, String[] stepsSubtitles) {
        this.stepTitles = new ArrayList<>(Arrays.asList(steps));
        if(stepsSubtitles != null) {
            this.stepSubtitles = new ArrayList<>(Arrays.asList(stepsSubtitles));
        } else {
            this.stepSubtitles = null;
        }
        numberOfSteps = steps.length;
        setAuxVars();
        addConfirmationStepToStepsList();
    }

    private void initializeForm() {
        stepsTitlesViews = new ArrayList<>();
        stepsSubtitlesViews = new ArrayList<>();
        setUpSteps();
        if (!displayBottomNavigation) {
            hideBottomNavigation();
        }
        goToStep(0, true);

        setObserverForKeyboard();
    }

    private void setObserverForKeyboard() {
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                content.getWindowVisibleDisplayFrame(r);
                int screenHeight = content.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // The keyboard has probably been opened, so we scroll to the step
                    scrollToActiveStep(true);
                }
            }
        });
    }

    private void hideBottomNavigation() {
        bottomNavigation.setVisibility(View.GONE);
    }

    private void setUpSteps() {
        stepLayouts = new ArrayList<>();
        // Set up normal stepTitles
        for (int i = 0; i < numberOfSteps; i++) {
            setUpStep(i);
        }
        // Set up confirmation step
        setUpStep(numberOfSteps);
    }

    private void setUpStep(int stepPosition) {
        View stepLayout = createStepLayout(stepPosition);
        if (stepPosition < numberOfSteps) {
            // The content of the step is the corresponding custom view previously created
            ViewGroup stepContent = stepLayout.findViewById(R.id.step_content);
            stepContent.addView(stepContentViews.get(stepPosition));
        } else {
            setUpStepLayoutAsConfirmationStepLayout(stepLayout);
        }
        addStepToContent(stepLayout);
    }

    private void addStepToContent(View stepLayout) {
        content.addView(stepLayout);
    }

    private void setUpStepLayoutAsConfirmationStepLayout(View stepLayout) {
        confirmationButton = stepLayout.findViewById(R.id.next_step);

        disableConfirmationButton();

        confirmationButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        confirmationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareSendingAndSend();
            }
        });
    }

    protected View createStepLayout(final int stepPosition) {
        View stepLayout = generateStepLayout();

        View circle = stepLayout.findViewById(R.id.circle);
        Drawable bg = ContextCompat.getDrawable(getContext(), R.drawable.circle_step_done);
        bg.setColorFilter(new PorterDuffColorFilter(
                stepNumberBackgroundColor, PorterDuff.Mode.SRC_IN));
        circle.setBackground(bg);

        TextView stepTitle = stepLayout.findViewById(R.id.step_title);
        stepTitle.setText(stepTitles.get(stepPosition));
        stepTitle.setTextColor(stepTitleTextColor);
        stepsTitlesViews.add(stepPosition, stepTitle);

        TextView stepSubtitle = null;
        if(stepSubtitles != null && stepPosition < stepSubtitles.size()) {
            String subtitle = stepSubtitles.get(stepPosition);
            if(subtitle != null && !subtitle.equals("")) {
                stepSubtitle = stepLayout.findViewById(R.id.step_subtitle);
                stepSubtitle.setText(subtitle);
                stepSubtitle.setTextColor(stepSubtitleTextColor);
                stepSubtitle.setVisibility(View.VISIBLE);
            }
        }
        stepsSubtitlesViews.add(stepPosition, stepSubtitle);

        TextView stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        stepNumberTextView.setText(String.valueOf(stepPosition + 1));
        stepNumberTextView.setTextColor(stepNumberTextColor);

        ImageView stepDoneImageView = stepLayout.findViewById(R.id.step_done);
        stepDoneImageView.setColorFilter(stepNumberTextColor);

        TextView errorMessage = stepLayout.findViewById(R.id.error_message);
        ImageView errorIcon = stepLayout.findViewById(R.id.error_icon);
        errorMessage.setTextColor(errorMessageTextColor);
        errorIcon.setColorFilter(errorMessageTextColor);

        View stepHeader = stepLayout.findViewById(R.id.step_header);
        stepHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep(stepPosition, false);
            }
        });

        MaterialButton nextButton = stepLayout.findViewById(R.id.next_step);
        setButtonColor(nextButton,
                buttonBackgroundColor, buttonTextColor, buttonPressedBackgroundColor, buttonPressedTextColor);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStep((stepPosition + 1), false);
            }
        });

        stepLayouts.add(stepLayout);

        return stepLayout;
    }

    protected View generateStepLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return inflater.inflate(R.layout.step_layout, content, false);
    }

    private void openStep(int stepPosition, boolean restoration) {
        if (stepPosition >= 0 && stepPosition <= numberOfSteps) {
            activeStep = stepPosition;

            if (stepPosition == 0) {
                disablePreviousButtonInBottomNavigationLayout();
            } else {
                enablePreviousButtonInBottomNavigationLayout();
            }

            if (completedSteps[stepPosition] && activeStep != numberOfSteps) {
                enableNextButtonInBottomNavigationLayout();
            } else {
                disableNextButtonInBottomNavigationLayout();
            }

            for(int i = 0; i <= numberOfSteps; i++) {
                if(i != stepPosition) {
                    disableStepLayout(i, !restoration);
                } else {
                    enableStepLayout(i, !restoration);
                }
            }

            scrollToActiveStep(!restoration);

            if (stepPosition == numberOfSteps) {
                setStepAsCompleted(stepPosition);
            }

            verticalStepperFormListener.onStepOpened(stepPosition);
        }
    }

    private void scrollToStep(final int stepPosition, boolean smoothScroll) {
        if (smoothScroll) {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.smoothScrollTo(0, stepLayouts.get(stepPosition).getTop());
                }
            });
        } else {
            stepsScrollView.post(new Runnable() {
                public void run() {
                    stepsScrollView.scrollTo(0, stepLayouts.get(stepPosition).getTop());
                }
            });
        }
    }

    private void scrollToActiveStep(boolean smoothScroll) {
        scrollToStep(activeStep, smoothScroll);
    }
    
    private void disableStepLayout(int stepPosition, boolean useAnimations) {
        View stepLayout = stepLayouts.get(stepPosition);
        View stepDone = stepLayout.findViewById(R.id.step_done);
        TextView stepNumberTextView = stepLayout.findViewById(R.id.step_number);
        View stepAndButton = stepLayout.findViewById(R.id.step_content_and_button);

        if (useAnimations) {
            Animations.slideUp(stepAndButton);
        } else {
            stepAndButton.setVisibility(View.GONE);
        }

        if (!completedSteps[stepPosition]) {
            disableStepHeader(stepLayout);
            stepDone.setVisibility(View.GONE);
            stepNumberTextView.setVisibility(View.VISIBLE);
        } else {
            enableStepHeader(stepLayout);
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.GONE);
        }

        showVerticalLineInCollapsedStepIfNecessary(stepLayout);

    }

    private void enableStepLayout(int stepPosition, boolean smoothieEnabling) {
        View stepLayout = stepLayouts.get(stepPosition);
        View stepAndButton = stepLayout.findViewById(R.id.step_content_and_button);
        View stepDone = stepLayout.findViewById(R.id.step_done);
        View stepNumberTextView = stepLayout.findViewById(R.id.step_number);

        enableStepHeader(stepLayout);

        if (smoothieEnabling) {
            Animations.slideDown(stepAndButton);
        } else {
            stepAndButton.setVisibility(View.VISIBLE);
        }

        if (completedSteps[stepPosition] && activeStep != stepPosition) {
            stepDone.setVisibility(View.VISIBLE);
            stepNumberTextView.setVisibility(View.GONE);
        } else {
            stepDone.setVisibility(View.GONE);
            stepNumberTextView.setVisibility(View.VISIBLE);
        }

        hideVerticalLineInCollapsedStepIfNecessary(stepLayout);

    }

    private void enableStepHeader(View stepLayout) {
        setHeaderAppearance(stepLayout, 1, buttonBackgroundColor);
    }

    private void disableStepHeader(View stepLayout) {
        setHeaderAppearance(stepLayout, alphaOfDisabledElements, Color.rgb(176, 176, 176));
    }

    private void showVerticalLineInCollapsedStepIfNecessary(View stepLayout) {
        // The height of the line will be 16dp when the subtitle textview is gone
        if(showVerticalLineWhenStepsAreCollapsed) {
            // TODO
        }
    }

    private void hideVerticalLineInCollapsedStepIfNecessary(View stepLayout) {
        // The height of the line will be 0 when the subtitle text is being shown
        if(showVerticalLineWhenStepsAreCollapsed) {
            // TODO
        }
    }

    private void displayCurrentProgress() {
        int progress = 0;
        for (int i = 0; i < (completedSteps.length - 1); i++) {
            if (completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    private void displayMaxProgress() {
        setProgress(numberOfSteps + 1);
    }

    private void setAuxVars() {
        completedSteps = new boolean[numberOfSteps + 1];
        for (int i = 0; i < (numberOfSteps + 1); i++) {
            completedSteps[i] = false;
        }
        progressBar.setMax(numberOfSteps + 1);
    }

    private void addConfirmationStepToStepsList() {
        String confirmationStepText = getContext().getString(R.string.vertical_form_stepper_form_last_step);
        stepTitles.add(confirmationStepText);
    }

    private void disablePreviousButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(previousStepButton);
    }

    private void enablePreviousButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(previousStepButton);
    }

    private void disableNextButtonInBottomNavigationLayout() {
        disableBottomButtonNavigation(nextStepButton);
    }

    private void enableNextButtonInBottomNavigationLayout() {
        enableBottomButtonNavigation(nextStepButton);
    }

    private void enableBottomButtonNavigation(View button) {
        button.setAlpha(1f);
        button.setEnabled(true);
    }

    private void disableBottomButtonNavigation(View button) {
        button.setAlpha(alphaOfDisabledElements);
        button.setEnabled(false);
    }

    private void setProgress(int progress) {
        if (progress > 0 && progress <= (numberOfSteps + 1)) {
            progressBar.setProgress(progress);
        }
    }

    private void disableConfirmationButton() {
        confirmationButton.setEnabled(false);
        confirmationButton.setAlpha(alphaOfDisabledElements);
    }

    private void prepareSendingAndSend() {
        displayDoneIconInConfirmationStep();
        disableConfirmationButton();
        displayMaxProgress();

        verticalStepperFormListener.onCompletedForm();
    }

    private void displayDoneIconInConfirmationStep() {
        View confirmationStepLayout = stepLayouts.get(stepLayouts.size() - 1);
        View stepDone = confirmationStepLayout.findViewById(R.id.step_done);
        View stepNumberTextView = confirmationStepLayout.findViewById(R.id.step_number);
        stepDone.setVisibility(View.VISIBLE);
        stepNumberTextView.setVisibility(View.GONE);
    }

    private void restoreFormState() {
        goToStep(activeStep, true);
        displayCurrentProgress();
    }

    private void setHeaderAppearance(View stepLayout, float alpha, int stepCircleBackgroundColor) {
        TextView title = stepLayout.findViewById(R.id.step_title);
        TextView subtitle = stepLayout.findViewById(R.id.step_subtitle);
        View circle = stepLayout.findViewById(R.id.circle);
        ImageView done = stepLayout.findViewById(R.id.step_done);

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
    }

    private void setStepCircleBackgroundColor(View stepLayout, int color) {
        View circleLayout = stepLayout.findViewById(R.id.circle);
        ShapeDrawable circleBackground = (ShapeDrawable) ContextCompat.getDrawable(getContext(), R.drawable.circle_step_done);
        if (circleBackground != null) {
            circleBackground.getPaint().setColor(color);
            circleLayout.setBackground(circleBackground);
        }
    }

    private void setButtonColor(
            MaterialButton button,
            int buttonColor,
            int buttonTextColor,
            int buttonPressedColor,
            int buttonPressedTextColor) {

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
        button.setBackgroundTintList(buttonColours);
        button.setTextColor(buttonTextColours);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        findViews();
        registerListeners();
    }

    private void findViews() {
        content = findViewById(R.id.content);
        stepsScrollView = findViewById(R.id.steps_scroll);
        progressBar = findViewById(R.id.progress_bar);
        previousStepButton = findViewById(R.id.down_previous);
        nextStepButton = findViewById(R.id.down_next);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void registerListeners() {
        previousStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPreviousStep();
            }
        });
        
        nextStepButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNextStep();
            }
        });
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

    /**
     * Gets an instance of the builder that will be used to set up and initialize the form.
     *
     * @param stepTitles A String array with the names of the steps.
     * @param stepperImplementation The listener for the stepper form events.
     * @return An instance of the stepper form builder.
     */
    public Builder setup(
            String[] stepTitles,
            VerticalStepperFormListener stepperImplementation) {

        return new Builder(this, stepTitles, stepperImplementation);
    }

    public static class Builder {

        private VerticalStepperFormLayout verticalStepperFormLayout;
        private String[] stepTitles;
        private VerticalStepperFormListener verticalStepperFormListener;

        private String[] stepSubtitles = null;
        private float alphaOfDisabledElements = alphaOfDisabledElementsDefault;
        private int stepNumberBackgroundColor = stepNumberBackgroundColorDefault;
        private int buttonBackgroundColor = buttonBackgroundColorDefault;
        private int buttonPressedBackgroundColor = buttonPressedBackgroundColorDefault;
        private int stepNumberTextColor = stepNumberTextColorDefault;
        private int stepTitleTextColor = stepTitleTextColorDefault;
        private int stepSubtitleTextColor = stepSubtitleTextColorDefault;
        private int buttonTextColor = buttonTextColorDefault;
        private int buttonPressedTextColor = buttonPressedTextColorDefault;
        private int errorMessageTextColor = errorMessageTextColorDefault;
        private boolean displayBottomNavigation = displayBottomNavigationDefault;
        private boolean showVerticalLineWhenStepsAreCollapsed = showVerticalLineWhenStepsAreCollapsedDefault;

        private static float alphaOfDisabledElementsDefault = 0.25f;
        private static int stepNumberBackgroundColorDefault = Color.rgb(63, 81, 181);
        private static int buttonBackgroundColorDefault = Color.rgb(63, 81, 181);
        private static int buttonPressedBackgroundColorDefault = Color.rgb(48, 63, 159);
        private static int stepNumberTextColorDefault = Color.rgb(255, 255, 255);
        private static int stepTitleTextColorDefault = Color.rgb(33, 33, 33);
        private static int stepSubtitleTextColorDefault = Color.rgb(162, 162, 162);
        private static int buttonTextColorDefault = Color.rgb(255, 255, 255);
        private static int buttonPressedTextColorDefault = Color.rgb(255, 255, 255);
        private static int errorMessageTextColorDefault = Color.rgb(175, 18, 18);
        private static boolean displayBottomNavigationDefault = true;
        private static boolean showVerticalLineWhenStepsAreCollapsedDefault = false;

        private Builder(
                VerticalStepperFormLayout stepperLayout,
                String[] stepTitles,
                VerticalStepperFormListener stepperImplementation) {

            this.verticalStepperFormLayout = stepperLayout;
            this.stepTitles = stepTitles;
            this.verticalStepperFormListener = stepperImplementation;
        }

        /**
         * Sets the subtitles of the steps.
         *
         * @param stepsSubtitles A String array with the subtitles of the steps.
         * @return The builder instance.
         */
        public Builder stepsSubtitles(String[] stepsSubtitles) {
            this.stepSubtitles = stepsSubtitles;
            return this;
        }

        /**
         * Sets the primary color of the form. Will be used for the left circles and the buttons.
         * To set a different background color for buttons and left circles, please use
         * stepNumberBackgroundColor() and buttonBackgroundColor().
         *
         * @param colorPrimary The primary color.
         * @return The builder instance.
         */
        public Builder primaryColor(int colorPrimary) {
            this.stepNumberBackgroundColor = colorPrimary;
            this.buttonBackgroundColor = colorPrimary;
            return this;
        }

        /**
         * Sets the dark primary color. Will be displayed as the background color of the buttons
         * while clicked.
         *
         * @param colorPrimaryDark Primary color (dark)
         * @return The builder instance.
         */
        public Builder primaryDarkColor(int colorPrimaryDark) {
            this.buttonPressedBackgroundColor = colorPrimaryDark;
            return this;
        }

        /**
         * Sets the background color of the left circles.
         *
         * @param stepNumberBackgroundColor Background color of the left circles.
         * @return The builder instance.
         */
        public Builder stepNumberBackgroundColor(int stepNumberBackgroundColor) {
            this.stepNumberBackgroundColor = stepNumberBackgroundColor;
            return this;
        }

        /**
         * Sets the background color of the buttons.
         *
         * @param buttonBackgroundColor Background color of the buttons.
         * @return The builder instance.
         */
        public Builder buttonBackgroundColor(int buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
            return this;
        }

        /**
         * Sets the background color of the buttons when pressed.
         *
         * @param buttonPressedBackgroundColor Background color of the buttons when pressed.
         * @return The builder instance.
         */
        public Builder buttonPressedBackgroundColor(int buttonPressedBackgroundColor) {
            this.buttonPressedBackgroundColor = buttonPressedBackgroundColor;
            return this;
        }

        /**
         * Sets the text color of the left circles.
         *
         * @param stepNumberTextColor Text color of the left circles.
         * @return The builder instance.
         */
        public Builder stepNumberTextColor(int stepNumberTextColor) {
            this.stepNumberTextColor = stepNumberTextColor;
            return this;
        }

        /**
         * Sets the text color of the step title.
         *
         * @param stepTitleTextColor The color of the step title.
         * @return This builder instance.
         */
        public Builder stepTitleTextColor(int stepTitleTextColor) {
            this.stepTitleTextColor = stepTitleTextColor;
            return this;
        }

        /**
         * Sets the text color of the step subtitle.
         *
         * @param stepSubtitleTextColor The color of the step subtitle.
         * @return This builder instance.
         */
        public Builder stepSubtitleTextColor(int stepSubtitleTextColor) {
            this.stepSubtitleTextColor = stepSubtitleTextColor;
            return this;
        }

        /**
         * Sets the text color of the buttons.
         *
         * @param buttonTextColor Text color of the buttons.
         * @return The builder instance.
         */
        public Builder buttonTextColor(int buttonTextColor) {
            this.buttonTextColor = buttonTextColor;
            return this;
        }

        /**
         * Sets the text color of the buttons when clicked.
         *
         * @param buttonPressedTextColor Text color of the buttons when clicked.
         * @return The builder instance.
         */
        public Builder buttonPressedTextColor(int buttonPressedTextColor) {
            this.buttonPressedTextColor = buttonPressedTextColor;
            return this;
        }

        /**
         * Sets the error message color.
         *
         * @param errorMessageTextColor Error message color.
         * @return The builder instance.
         */
        public Builder errorMessageTextColor(int errorMessageTextColor) {
            this.errorMessageTextColor = errorMessageTextColor;
            return this;
        }

        /**
         * Specifies whether or not the bottom navigation bar will be displayed.
         *
         * @param displayBottomNavigationBar True to display it; false otherwise.
         * @return The builder instance.
         */
        public Builder displayBottomNavigation(boolean displayBottomNavigationBar) {
            this.displayBottomNavigation = displayBottomNavigationBar;
            return this;
        }

        /**
         * Specifies whether or not the vertical lines should be displayed when the steps are
         * collapsed.
         *
         * @param showVerticalLineWhenStepsAreCollapsed True to show the lines on collapsed steps;
         *                                              false to not.
         * @return The builder instance.
         */
        public Builder showVerticalLineWhenStepsAreCollapsed(boolean showVerticalLineWhenStepsAreCollapsed) {
            this.showVerticalLineWhenStepsAreCollapsed = showVerticalLineWhenStepsAreCollapsed;
            return this;
        }

        /**
         * Sets the alpha of the disabled elements.
         *
         * @param alpha Alpha level of disabled elements.
         * @return The builder instance.
         */
        public Builder alphaOfDisabledElements(float alpha) {
            this.alphaOfDisabledElements = alpha;
            return this;
        }

        /**
         * Sets up the form and initializes it.
         */
        public void init() {
            verticalStepperFormLayout.initialiseVerticalStepperForm(this);
        }

    }
}