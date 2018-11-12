package verticalstepperform.ernestoyaquello.com.verticalstepperform;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.DialogFragment;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps.AlarmDaysStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps.AlarmDescriptionStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps.AlarmNameStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps.AlarmTimeStep;

public class NewAlarmFormActivity extends AppCompatActivity implements StepperFormListener {
    
    public static final String STATE_NEW_ALARM_ADDED = "new_alarm_added";
    public static final String STATE_TITLE = "title";
    public static final String STATE_DESCRIPTION = "description";
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";
    public static final String STATE_WEEK_DAYS = "week_days";

    private ProgressDialog progressDialog;
    private VerticalStepperFormLayout verticalStepperForm;

    private AlarmNameStep nameStep;
    private AlarmDescriptionStep descriptionStep;
    private AlarmTimeStep timeStep;
    private AlarmDaysStep daysStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_stepper_form);

        // TODO Replace string array with normal strings
        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        String[] stepTitles = getResources().getStringArray(R.array.steps_titles);

        nameStep = new AlarmNameStep(stepTitles[0]);
        descriptionStep = new AlarmDescriptionStep(stepTitles[1]);
        timeStep = new AlarmTimeStep(stepTitles[2]);
        daysStep = new AlarmDaysStep(stepTitles[3]);

        verticalStepperForm = findViewById(R.id.vertical_stepper_form);
        verticalStepperForm.setup(this, nameStep, descriptionStep, timeStep, daysStep)
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .lastStepButtonText(getString(R.string.add_alarm))
                .init();
    }

    @Override
    public void onCompletedForm() {
        final Thread dataSavingThread = saveData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.form_sending_data_message));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                try {
                    dataSavingThread.interrupt();
                } catch (RuntimeException e) {
                    // Do nothing
                } finally {
                    verticalStepperForm.cancelFormCompletionAttempt();
                }
            }
        });
    }

    private Thread saveData() {

        // Fake data saving effect
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(STATE_NEW_ALARM_ADDED, true);
                    intent.putExtra(STATE_TITLE, nameStep.getStepData());
                    intent.putExtra(STATE_DESCRIPTION, descriptionStep.getStepData());
                    intent.putExtra(STATE_TIME_HOUR, timeStep.getStepData().hour);
                    intent.putExtra(STATE_TIME_MINUTES, timeStep.getStepData().minutes);
                    intent.putExtra(STATE_WEEK_DAYS, daysStep.getStepData());

                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return thread;
    }

    private void finishIfPossible() {
        if(verticalStepperForm.isAnyStepCompleted()) {
            final CloseConfirmationFragment closeConfirmation = new CloseConfirmationFragment();
            closeConfirmation.setOnConfirmClose(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            closeConfirmation.show(getSupportFragmentManager(), null);
        } else {
            finish();
        }
    }

    private void dismissDialogIfNecessary() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finishIfPossible();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed(){
        finishIfPossible();
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissDialogIfNecessary();
    }

    @Override
    protected void onStop() {
        super.onStop();

        dismissDialogIfNecessary();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putString(STATE_TITLE, nameStep.getStepData());
        savedInstanceState.putString(STATE_DESCRIPTION, descriptionStep.getStepData());
        savedInstanceState.putInt(STATE_TIME_HOUR, timeStep.getStepData().hour);
        savedInstanceState.putInt(STATE_TIME_MINUTES, timeStep.getStepData().minutes);
        savedInstanceState.putBooleanArray(STATE_WEEK_DAYS, daysStep.getStepData());

        // IMPORTANT: The call to super method must be here at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if(savedInstanceState.containsKey(STATE_TITLE)) {
            String title = savedInstanceState.getString(STATE_TITLE);

            nameStep.restoreStepData(title);
        }

        if(savedInstanceState.containsKey(STATE_DESCRIPTION)) {
            String description = savedInstanceState.getString(STATE_DESCRIPTION);

            descriptionStep.restoreStepData(description);
        }

        if(savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTES)) {
            int hour = savedInstanceState.getInt(STATE_TIME_HOUR);
            int minutes = savedInstanceState.getInt(STATE_TIME_MINUTES);
            AlarmTimeStep.TimeHolder time = new AlarmTimeStep.TimeHolder(hour, minutes);

            timeStep.restoreStepData(time);
        }

        if(savedInstanceState.containsKey(STATE_WEEK_DAYS)) {
            boolean[] alarmDays = savedInstanceState.getBooleanArray(STATE_WEEK_DAYS);

            daysStep.restoreStepData(alarmDays);
        }

        // IMPORTANT: The call to super method must be here at the end
        super.onRestoreInstanceState(savedInstanceState);
    }

    public static class CloseConfirmationFragment extends DialogFragment {

        private DialogInterface.OnClickListener onConfirmClose;

        void setOnConfirmClose(DialogInterface.OnClickListener onConfirmClose) {
            this.onConfirmClose = onConfirmClose;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.form_discard_question)
                    .setMessage(R.string.form_info_will_be_lost)
                    .setPositiveButton(R.string.form_discard, onConfirmClose)
                    .setNegativeButton(R.string.form_discard_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing if the user clicks "Cancel"
                        }
                    });

            return builder.create();
        }
    }
}
