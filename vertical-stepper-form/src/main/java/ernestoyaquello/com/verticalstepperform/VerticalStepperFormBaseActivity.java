package ernestoyaquello.com.verticalstepperform;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public abstract class VerticalStepperFormBaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected static int NUMBER_OF_STEPS;
    protected static float ALPHA_OF_DISABLED_ELEMENTS = 0.25f;
    public static final String STATE_ACTIVE_STEP = "active_step";
    public static final String STATE_COMPLETED_STEPS = "completed_step";

    protected LinearLayout content;
    protected ScrollView stepsScrollView;
    protected ImageButton previousStepButton, nextStepButton;
    protected ProgressBar progressBar;
    protected static boolean confirmBack = true;
    protected List<LinearLayout> stepLayouts;
    protected int activeStep = 0;
    protected String[] steps;
    protected boolean[] completedSteps;
    protected ProgressDialog progressDialog;
    protected Button finalButton;
    protected VerticalStepperFormLayout verticalStepperForm;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_ACTIVE_STEP, activeStep);
        savedInstanceState.putBooleanArray(STATE_COMPLETED_STEPS, completedSteps);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        completedSteps = savedInstanceState.getBooleanArray(STATE_COMPLETED_STEPS);
        if(completedSteps != null) {
            for (int i = 0; i < completedSteps.length; i++) {
                if (completedSteps[i]) {
                    setStepAsCompleted(i);
                    disableStepLayout(i);
                }
            }
        }
        activeStep = savedInstanceState.getInt(STATE_ACTIVE_STEP);
        goToStep(activeStep, false);
        setCurrentProgress();
    }

    protected void initStepperForm() {
        setInitialVariables();
        addSteps();
    }

    protected void setInitialVariables() {
        activeStep = 0;
        confirmBack = true;
        stepLayouts = new ArrayList<LinearLayout>();
        steps = addElementToStringVector(verticalStepperForm.getSteps(),
                getString(R.string.vertical_form_stepper_form_last_step));
        NUMBER_OF_STEPS = (steps.length - 1);
        completedSteps = new boolean[NUMBER_OF_STEPS + 1];
        for(int i = 0; i < (NUMBER_OF_STEPS + 1); i++) {
            completedSteps[i] = false;
        }
        stepsScrollView = (ScrollView) verticalStepperForm.findViewById(R.id.steps_scroll);
        progressBar = (ProgressBar) verticalStepperForm.findViewById(R.id.progress_bar);
        progressBar.setMax(NUMBER_OF_STEPS + 1);
        previousStepButton = (ImageButton) verticalStepperForm.findViewById(R.id.down_previous);
        previousStepButton.setOnClickListener(this);
        nextStepButton = (ImageButton) verticalStepperForm.findViewById(R.id.down_next);
        nextStepButton.setOnClickListener(this);
        content = (LinearLayout) verticalStepperForm.findViewById(R.id.content);
    }

    private static String[] addElementToStringVector(String[] a, String e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    private void addSteps() {
        for(int i = 0; i < NUMBER_OF_STEPS; i++) {
            addStep(i);
        }
        addStep(NUMBER_OF_STEPS);
    }

    private void addStep(int numStep) {
        LinearLayout stepLayout = createStepLayout(numStep);
        if(numStep < NUMBER_OF_STEPS) {
            RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
            View view = createCustomStep(numStep, stepContent);
            if(view != null) {
                stepContent.addView(view);
            }
        } else {
            addFinalStep(stepLayout);
        }
        if(numStep > 0) {
            disableStepLayout(numStep);
        }
        addStepToContent(stepLayout);
    }

    protected void addFinalStep(LinearLayout stepLayout) {
        LinearLayout stepLeftLine = (LinearLayout) stepLayout.findViewById(R.id.vertical_line);
        stepLeftLine.setVisibility(View.INVISIBLE);
        LinearLayout buttons = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        buttons.setVisibility(View.GONE);
        finalButton = (Button) buttons.findViewById(R.id.next_step);
        finalButton.setText(R.string.vertical_form_stepper_form_confirm_button);
        // Some content could be added to the final step inside stepContent layout
        // RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
    }

    private LinearLayout createStepLayout(int stepNum) {
        LinearLayout stepLayout = getStepLayout();
        stepLayout.setTag(stepNum);
        stepLayouts.add(stepLayout);
        TextView stepTitle = (TextView)stepLayout.findViewById(R.id.step_title);
        TextView stepNumber = (TextView)stepLayout.findViewById(R.id.step_number);
        stepTitle.setText(steps[stepNum]);
        stepNumber.setText(String.valueOf(stepNum + 1));
        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setTag(stepNum);
        stepHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedStepNumber = (int) v.getTag();
                goToStep(clickedStepNumber, true);
            }
        });
        Button next = (Button) stepLayout.findViewById(R.id.next_step);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextStep();
            }
        });
        return stepLayout;
    }

    protected void goToNextStep() {
        hideSoftKeyboard();
        if(activeStep == NUMBER_OF_STEPS) {
            confirmForm();
            return;
        }
        if (completedSteps[activeStep]) {
            disableActiveStepLayout();
            ++activeStep;
            enableActiveStepLayout();
            smoothScrollToCurrentStep();
        } else {
            userCantGoToOtherStepBeforeCompletingCurrentOneAlert();
        }

        setConfirmStepIfNecessary();

        // Custom steps checking
        customStepsCheckingOnStepOpening();
    }
    protected void goToPreviousStep() {
        hideSoftKeyboard();
        if(activeStep > 0) {
            disableActiveStepLayout();
            --activeStep;
            enableActiveStepLayout();
            smoothScrollToCurrentStep();
        }
    }

    protected void goToStep(int clickedStepNumber, boolean SmoothScrolling) {
        hideSoftKeyboard();
        boolean previousStepsAreCompleted = true;
        for(int i = (clickedStepNumber-1); i >= 0 && previousStepsAreCompleted; i--) {
            if(!completedSteps[i]) {
                previousStepsAreCompleted = false;
            }
        }
        if (clickedStepNumber == 0 || previousStepsAreCompleted) {
            disableActiveStepLayout();
            activeStep = clickedStepNumber;
            enableActiveStepLayout();
            if(SmoothScrolling) {
                smoothScrollToCurrentStep();
            } else {
                scrollToCurrentStep();
            }
        } else {
            userCantGoToOtherStepBeforeCompletingCurrentOneAlert();
        }

        setConfirmStepIfNecessary();

        // Custom steps checking
        customStepsCheckingOnStepOpening();
    }

    protected void userCantGoToOtherStepBeforeCompletingCurrentOneAlert() {
        /*Toast.makeText(getApplicationContext(),
                R.string.vertical_form_stepper_form_current_step_uncompleted_alert,
                Toast.LENGTH_SHORT)
                    .show();*/
    }

    private void setConfirmStepIfNecessary() {
        if(activeStep == (NUMBER_OF_STEPS)) {
            completedSteps[activeStep] = true;
            setCurrentProgress();
            enableActiveStepLayout();
            LinearLayout stepLayout = stepLayouts.get(activeStep);
            LinearLayout buttonContainer = (LinearLayout) stepLayout.findViewById(
                    R.id.next_step_button_container);
            buttonContainer.setVisibility(View.VISIBLE);
            enableNextButtonInBottomNavigationLayout();
        }
    }

    private void confirmForm() {
        completedSteps[activeStep] = true;
        setCurrentProgress();
        prepareDataSending();
    }

    private void scrollToCurrentStep() {
        stepsScrollView.post(new Runnable() {
            public void run() {
                stepsScrollView.scrollTo(0, stepLayouts.get(activeStep).getTop());
            }
        });
    }

    private void smoothScrollToCurrentStep() {
        stepsScrollView.post(new Runnable() {
            public void run() {
                stepsScrollView.smoothScrollTo(0, stepLayouts.get(activeStep).getTop());
            }
        });
    }

    private void enableActiveStepLayout() {
        enableStepLayout(activeStep);
    }

    private void disableActiveStepLayout() {
        disableStepLayout(activeStep);
    }

    protected void disableStepLayout(int stepNum) {
        LinearLayout stepLayout = stepLayouts.get(stepNum);
        if(!completedSteps[stepNum]) {
            LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        }
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        button.setVisibility(View.GONE);
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        stepContent.setVisibility(View.GONE);
    }

    protected void enableStepLayout(int stepNum) {
        LinearLayout stepLayout = stepLayouts.get(stepNum);
        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setAlpha(1);
        LinearLayout button = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        if(completedSteps[stepNum]) {
            button.setVisibility(View.VISIBLE);
            enableNextButtonInBottomNavigationLayout();
        } else {
            button.setVisibility(View.GONE);
            disableNextButtonInBottomNavigationLayout();
        }
        RelativeLayout stepContent = (RelativeLayout) stepLayout.findViewById(R.id.step_content);
        stepContent.setVisibility(View.VISIBLE);
        if(stepNum > 0) {
            enablePreviousButtonInBottomNavigationLayout();
        } else {
            disablePreviousButtonInBottomNavigationLayout();
        }
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

    private void enableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(1f);
        //button.setClickable(true);
    }

    private void disableBottomButtonNavigation(ImageButton button) {
        button.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        //button.setClickable(false);
    }

    private void addStepToContent(LinearLayout stepLayout) {
        content.addView(stepLayout);
    }

    private LinearLayout getStepLayout() {
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        return (LinearLayout) inflater.inflate(R.layout.step_layout, null, false);
    }

    protected void setActiveStepAsCompleted() {
        setStepAsCompleted(activeStep);
    }

    protected void setActiveStepAsUncompleted() {
        setStepAsUncompleted(activeStep);
    }

    protected void setStepAsCompleted(int stepNum) {
        completedSteps[stepNum] = true;
        LinearLayout stepLayout = stepLayouts.get(stepNum);
        if(stepNum == activeStep) {
            LinearLayout buttons = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
            buttons.setVisibility(View.VISIBLE);
            enableNextButtonInBottomNavigationLayout();
        }
        LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
        stepHeader.setAlpha(1);
        setCurrentProgress();
    }

    protected void setStepAsUncompleted(int stepNum) {
        completedSteps[stepNum] = false;
        LinearLayout stepLayout = stepLayouts.get(stepNum);
        LinearLayout buttons = (LinearLayout) stepLayout.findViewById(R.id.next_step_button_container);
        buttons.setVisibility(View.GONE);
        if (stepNum == activeStep) {
            disableNextButtonInBottomNavigationLayout();
        } else {
            LinearLayout stepHeader = (LinearLayout) stepLayout.findViewById(R.id.step_header);
            stepHeader.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        }
        if(stepNum < NUMBER_OF_STEPS) {
            completedSteps[NUMBER_OF_STEPS] = false;
            setStepAsUncompleted(NUMBER_OF_STEPS);
        }
        setCurrentProgress();
    }

    protected void setCurrentProgress() {
        int progress = 0;
        for(int i = 0; i < (completedSteps.length - 1); i++) {
            if(completedSteps[i]) {
                ++progress;
            }
        }
        progressBar.setProgress(progress);
    }

    protected void confirmBack() {
        if(completedSteps[0]) {
            DialogFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.show(getSupportFragmentManager(), null);
        } else {
            confirmBack = false;
            finish();
        }
    }

    public static class BackConfirmationFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.vertical_form_stepper_form_discard_question)
                    .setMessage(R.string.vertical_form_stepper_form_info_will_be_lost)
                    .setNegativeButton(R.string.vertical_form_stepper_form_discard_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    confirmBack = true;
                                }
                            })
                    .setPositiveButton(R.string.vertical_form_stepper_form_discard,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    confirmBack = false;
                                    getActivity().finish();
                                }
                            });
            return builder.create();
        }
    }

    protected void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        if(((String)v.getTag()).equals(getString(R.string.vertical_form_stepper_form_down_previous))) {
            goToPreviousStep();
        } else {
            if(completedSteps[activeStep]) {
                goToNextStep();
            } else {
                userCantGoToOtherStepBeforeCompletingCurrentOneAlert();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home && confirmBack) {
            confirmBack();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        if(confirmBack) {
            confirmBack();
        } else {
            confirmBack = true;
            super.onBackPressed();
        }
    }

    protected void prepareDataSending() {
        finalButton.setClickable(false);
        finalButton.setAlpha(ALPHA_OF_DISABLED_ELEMENTS);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressBar.setProgress(completedSteps.length);
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));
        sendData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    protected void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * The content of the layout of the corresponding step must be generated here. The system will
     * automatically call this method for every step
     * @param numStep the number of the step
     * @param stepContent the layout of the step
     * @return The view that will be automatically added by the system to "stepContent"
     *         (if the view is manually added inside the method, then it must return null)
     */
    protected abstract View createCustomStep(int numStep, RelativeLayout stepContent);

    /**
     * This method will be called every time a step is open
     */
    protected abstract void customStepsCheckingOnStepOpening();

    /**
     * This method will be called when the user confirms the information of the form
      */
    protected abstract void sendData();

}