package ernestoyaquello.com.verticalstepperform;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.interfaces.Callback;
import ernestoyaquello.com.verticalstepperform.interfaces.OnBottomNavigationButtonClickListener;
import ernestoyaquello.com.verticalstepperform.interfaces.OnStepSelectedListener;

public abstract class VerticalStepperFormBaseActivity extends AppCompatActivity
        implements OnBottomNavigationButtonClickListener, OnStepSelectedListener {

    public static final String STATE_ACTIVE_STEP = "active_step";
    public static final String STATE_COMPLETED_STEPS = "completed_step";

    protected static boolean confirmBack = true;
    protected ProgressDialog progressDialog;
    protected VerticalStepperFormLayout verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeActivity();
    }

    protected void initialiseVerticalStepperForm(int stepperLayoutResourceId, String[] stepsNames,
                                                 int colorPrimary, int colorPrimaryDark) {

        verticalStepperForm = (VerticalStepperFormLayout) findViewById(stepperLayoutResourceId);

        if(verticalStepperForm != null) {
            verticalStepperForm.setColorPrimary(colorPrimary);
            verticalStepperForm.setColorPrimaryDark(colorPrimaryDark);

            initStepperForm(stepsNames);
        }
    }

    protected void initialiseVerticalStepperForm(int stepperLayoutResourceId, String[] stepsNames,
                                                 int buttonBackgroundColor, int buttonTextColor,
                                                 int buttonPressedBackgroundColor, int buttonPressedTextColor,
                                                 int stepNumberBackgroundColor, int stepNumberTextColor) {

        verticalStepperForm = (VerticalStepperFormLayout) findViewById(stepperLayoutResourceId);

        if(verticalStepperForm != null) {
            verticalStepperForm.setButtonBackgroundColor(buttonBackgroundColor);
            verticalStepperForm.setButtonTextColor(buttonTextColor);
            verticalStepperForm.setButtonPressedBackgroundColor(buttonPressedBackgroundColor);
            verticalStepperForm.setButtonPressedTextColor(buttonPressedTextColor);
            verticalStepperForm.setStepNumberBackgroundColor(stepNumberBackgroundColor);
            verticalStepperForm.setStepNumberTextColor(stepNumberTextColor);

            initStepperForm(stepsNames);
        }
    }

    private void initStepperForm(String[] stepsNames) {
        verticalStepperForm.setSteps(stepsNames);
        verticalStepperForm.setOnButtonClickListener(this);
        verticalStepperForm.setOnStepSelectedListener(this);
        verticalStepperForm.setDataSendingConfirmationCallback(new Callback() {
            @Override
            public void executeCallback() {
                prepareDataSendingAndSend();
            }
        });

        List<View> stepContentLayouts = new ArrayList<View>();
        for(int i = 0; i < verticalStepperForm.getNumberOfSteps(); i++) {
            View stepLayout = createCustomStep(i);
            stepContentLayouts.add(stepLayout);
        }
        verticalStepperForm.setStepContentViews(stepContentLayouts);

        verticalStepperForm.initializeForm();

        stepsCheckingOnStepOpening();
    }

    protected void initializeActivity() {
        confirmBack = true;
    }

    protected void goToNextStep() {
        int activeStep = verticalStepperForm.getActiveStep();
        goToStep(activeStep + 1, true);
    }

    protected void goToPreviousStep() {
        int activeStep = verticalStepperForm.getActiveStep();
        goToStep(activeStep - 1, true);
    }

    protected void goToStep(int clickedStepNumber, boolean smoothScroll) {
        if(verticalStepperForm.getActiveStep() != clickedStepNumber) {
            hideSoftKeyboard();
            boolean previousStepsAreCompleted =
                    verticalStepperForm.previousStepsAreCompleted(clickedStepNumber);
            if (clickedStepNumber == 0 || previousStepsAreCompleted) {
                verticalStepperForm.moveToStep(clickedStepNumber, smoothScroll);
                stepsCheckingOnStepOpening();
            } else {
                userCantGoToOtherStepBeforeCompletingCurrentOneAlert();
            }
        }
    }

    protected void userCantGoToOtherStepBeforeCompletingCurrentOneAlert() {
        /*Toast.makeText(getApplicationContext(),
                R.string.vertical_form_stepper_form_current_step_uncompleted_alert,
                Toast.LENGTH_SHORT)
                    .show();*/
    }

    protected void setActiveStepAsCompleted() {
        verticalStepperForm.setStepAsCompleted(verticalStepperForm.getActiveStep());
    }

    protected void setActiveStepAsUncompleted() {
        verticalStepperForm.setStepAsUncompleted(verticalStepperForm.getActiveStep());
    }

    protected void confirmBack() {
        if(verticalStepperForm.isStepCompleted(0)) {
            DialogFragment backConfirmation = new BackConfirmationFragment();
            backConfirmation.show(getSupportFragmentManager(), null);
        } else {
            confirmBack = false;
            finish();
        }
    }

    protected void prepareDataSendingAndSend() {
        verticalStepperForm.disableConfirmationButton();
        verticalStepperForm.setMaxProgress();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.vertical_form_stepper_form_sending_data_message));

        sendData();
    }

    protected void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt(STATE_ACTIVE_STEP, verticalStepperForm.getActiveStep());
        savedInstanceState.putBooleanArray(STATE_COMPLETED_STEPS, verticalStepperForm.getCompletedSteps());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boolean[] completedSteps = savedInstanceState.getBooleanArray(STATE_COMPLETED_STEPS);
        if(completedSteps != null) {
            for (int i = 0; i < completedSteps.length; i++) {
                if (completedSteps[i]) {
                    verticalStepperForm.setStepAsCompleted(i);
                    verticalStepperForm.disableStepLayout(i);
                }
            }
        }

        int previouslyActiveStep = savedInstanceState.getInt(STATE_ACTIVE_STEP);
        goToStep(previouslyActiveStep, false);

        verticalStepperForm.setCurrentProgress();
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
    public void onStepSelected(int stepNum, boolean smoothScrolling) {
        goToStep(stepNum, smoothScrolling);
    }

    @Override
    public void onNavigationButtonClick(ButtonType buttonType) {
        if(buttonType == ButtonType.PREVIOUS) {
            goToPreviousStep();
        } else {
            if(verticalStepperForm.isActiveStepCompleted()) {
                goToNextStep();
            } else {
                userCantGoToOtherStepBeforeCompletingCurrentOneAlert();
            }
        }
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

    /**
     * The content of the layout of the corresponding step must be generated here. The system will
     * automatically call this method for every step
     * @param numStep the number of the step
     * @return The view that will be automatically added as the content of the step
     */
    protected abstract View createCustomStep(int numStep);

    /**
     * This method will be called every time a step is open
     */
    protected abstract void stepsCheckingOnStepOpening();

    /**
     * This method will be called when the user confirms the information of the form
      */
    protected abstract void sendData();

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

}