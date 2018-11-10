package verticalstepperform.ernestoyaquello.com.verticalstepperform;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.listener.VerticalStepperFormListener;

public class NewAlarmFormActivity extends AppCompatActivity implements VerticalStepperFormListener {

    private static final int ALARM_TITLE_STEP_POSITION = 0;
    private static final int ALARM_DESCRIPTION_STEP_POSITION = 1;
    private static final int ALARM_TIME_STEP_POSITION = 2;
    private static final int ALARM_DAYS_STEP_POSITION = 3;
    
    private static final int MIN_CHARACTERS_TITLE = 3;
    
    public static final String STATE_NEW_ALARM_ADDED = "new_alarm_added";
    public static final String STATE_TITLE = "title";
    public static final String STATE_DESCRIPTION = "description";
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";
    public static final String STATE_WEEK_DAYS = "week_days";

    private EditText alarmTitleEditText;
    
    private EditText alarmDescriptionEditText;

    private TextView alarmTimeTextView;
    private TimePickerDialog alarmTimePicker;
    private int alarmTimeHour;
    private int alarmTimeMinutes;
    private boolean isTimeSet;

    private boolean[] alarmDays;
    private View daysStepContent;

    private ProgressDialog progressDialog;
    private VerticalStepperFormLayout verticalStepperForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_stepper_form);

        int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
        int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
        String[] stepTitles = getResources().getStringArray(R.array.steps_titles);
        //String[] stepSubtitles = getResources().getStringArray(R.array.steps_subtitles);

        verticalStepperForm = findViewById(R.id.vertical_stepper_form);
        verticalStepperForm.setup(stepTitles, this)
                //.stepSubtitles(stepSubtitles)
                //.showVerticalLineWhenStepsAreCollapsed(true) // false by default
                .primaryColor(colorPrimary)
                .primaryDarkColor(colorPrimaryDark)
                .displayBottomNavigation(true)
                .init();
    }

    @Override
    @NonNull
    public View getStepLayout(int stepPosition) {
        switch (stepPosition) {
            case ALARM_TITLE_STEP_POSITION:
                return createAlarmTitleStep();
            case ALARM_DESCRIPTION_STEP_POSITION:
                return createAlarmDescriptionStep();
            case ALARM_TIME_STEP_POSITION:
                return createAlarmTimeStep();
            case ALARM_DAYS_STEP_POSITION:
                return createAlarmDaysStep();
            default:
                throw new IndexOutOfBoundsException("No layout can be created for the unexpected step with number " + stepPosition);
        }
    }

    @Override
    public void onStepOpened(int stepPosition) {
        switch (stepPosition) {
            case ALARM_TITLE_STEP_POSITION:
                String alarmTitle = alarmTitleEditText.getText().toString();
                updateAlarmTitleStepState(alarmTitle);
                break;

            case ALARM_DESCRIPTION_STEP_POSITION:
            case ALARM_TIME_STEP_POSITION:
                // As soon as they open, we mark these two steps as completed because no user input
                // or checking is required on them: they already have default values and can never
                // end up having invalid ones
                verticalStepperForm.setStepAsCompleted(stepPosition);
                break;

            case ALARM_DAYS_STEP_POSITION:
                updateAlarmDaysStepState();
                break;
        }
    }

    @Override
    public void onCompletedForm() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.form_sending_data_message));

        saveData();
    }

    private View createAlarmTitleStep() {
        // We create this step view programmatically
        alarmTitleEditText = new EditText(this);
        alarmTitleEditText.setHint(R.string.form_hint_title);
        alarmTitleEditText.setSingleLine(true);
        alarmTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateAlarmTitleStepState(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        alarmTitleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(isAlarmTitleCorrect(v.getText().toString())) {
                    verticalStepperForm.goToNextStep();
                }
                return false;
            }
        });

        return alarmTitleEditText;
    }

    private View createAlarmDescriptionStep() {
        // We create this step view programmatically
        alarmDescriptionEditText = new EditText(this);
        alarmDescriptionEditText.setHint(R.string.form_hint_description);
        alarmDescriptionEditText.setSingleLine(true);
        alarmDescriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                verticalStepperForm.goToNextStep();
                return false;
            }
        });

        return alarmDescriptionEditText;
    }

    private View createAlarmTimeStep() {
        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        View timeStepContent = inflater.inflate(R.layout.step_time_layout, null, false);
        alarmTimeTextView = timeStepContent.findViewById(R.id.time);

        setupAlarmTime();

        return timeStepContent;
    }

    private void setupAlarmTime() {

        if (!isTimeSet) {
            alarmTimeHour = 8;
            alarmTimeMinutes = 30;
            isTimeSet = true;
        }

        if (alarmTimePicker == null) {
            alarmTimePicker = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            alarmTimeHour = hourOfDay;
                            alarmTimeMinutes = minute;

                            updatedAlarmTimeText();
                        }
                    }, alarmTimeHour, alarmTimeMinutes, true);
        } else {
            alarmTimePicker.updateTime(alarmTimeHour, alarmTimeMinutes);
        }

        if (alarmTimeTextView != null) {
            alarmTimeTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alarmTimePicker.show();
                }
            });
        }
    }

    private View createAlarmDaysStep() {
        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        daysStepContent = inflater.inflate(R.layout.step_days_of_week_layout, null, false);

        setupAlarmDays();

        return daysStepContent;
    }

    private void setupAlarmDays() {
        boolean firstSetup = alarmDays == null;
        alarmDays = firstSetup ? new boolean[7] : alarmDays;

        final String[] weekDays = getResources().getStringArray(R.array.week_days);
        for(int i = 0; i < weekDays.length; i++) {
            final int index = i;
            final View dayLayout = getDayLayout(index);

            if (firstSetup) {
                // By default, we only mark the working days as activated
                if (index < 5) {
                    markAlarmDay(index, dayLayout, false);
                } else {
                    unmarkAlarmDay(index, dayLayout, false);
                }
            } else {
                updateDayLayout(index, dayLayout);
            }

            if (dayLayout != null) {
                dayLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDays[index] = !alarmDays[index];
                        updateDayLayout(index, dayLayout);
                    }
                });

                final TextView dayText = dayLayout.findViewById(R.id.day);
                dayText.setText(weekDays[index]);
            }
        }
    }

    private void updateDayLayout(int dayIndex, View dayLayout) {
        if (alarmDays[dayIndex]) {
            markAlarmDay(dayIndex, dayLayout, true);
        } else {
            unmarkAlarmDay(dayIndex, dayLayout, true);
        }
    }

    private void updateAlarmTitleStepState(String alarmTitle) {
        if (isAlarmTitleCorrect(alarmTitle)) {
            verticalStepperForm.setActiveStepAsCompleted();
        } else {
            String titleErrorString = getResources().getString(R.string.error_title_min_characters);
            String titleError = String.format(titleErrorString, MIN_CHARACTERS_TITLE);

            verticalStepperForm.setActiveStepAsUncompleted(titleError);
        }
    }

    private boolean isAlarmTitleCorrect(String alarmTitle) {
        return alarmTitle.length() >= MIN_CHARACTERS_TITLE;
    }

    private void updateAlarmDaysStepState() {
        if (isThereAtLeastOneDaySelected()) {
            verticalStepperForm.setStepAsCompleted(ALARM_DAYS_STEP_POSITION);
        } else {
            verticalStepperForm.setStepAsUncompleted(ALARM_DAYS_STEP_POSITION, null);
        }
    }

    private boolean isThereAtLeastOneDaySelected() {
        boolean thereIsAtLeastOneDaySelected = false;
        for(int i = 0; i < alarmDays.length && !thereIsAtLeastOneDaySelected; i++) {
            if(alarmDays[i]) {
                thereIsAtLeastOneDaySelected = true;
            }
        }

        return thereIsAtLeastOneDaySelected;
    }

    private View getDayLayout(int i) {
        int id = daysStepContent.getResources().getIdentifier(
                "day_" + i, "id", getPackageName());
        return daysStepContent.findViewById(id);
    }

    private void markAlarmDay(int dayIndex, View dayLayout, boolean updateStepState) {
        alarmDays[dayIndex] = true;

        if (dayLayout != null) {
            Drawable bg = ContextCompat.getDrawable(getBaseContext(),
                    ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
            int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            dayLayout.setBackground(bg);

            TextView dayText = dayLayout.findViewById(R.id.day);
            dayText.setTextColor(Color.rgb(255, 255, 255));
        }

        if(updateStepState) {
            updateAlarmDaysStepState();
        }
    }

    private void unmarkAlarmDay(int dayIndex, View dayLayout, boolean updateStepState) {
        alarmDays[dayIndex] = false;

        dayLayout.setBackgroundResource(0);

        TextView dayText = dayLayout.findViewById(R.id.day);
        int colour = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        dayText.setTextColor(colour);

        if(updateStepState) {
            updateAlarmDaysStepState();
        }
    }

    private void updatedAlarmTimeText() {
        String hourString = ((alarmTimeHour > 9) ?
                String.valueOf(alarmTimeHour) : ("0" + alarmTimeHour));
        String minutesString = ((alarmTimeMinutes > 9) ?
                String.valueOf(alarmTimeMinutes) : ("0" + alarmTimeMinutes));
        String time = hourString + ":" + minutesString;

        alarmTimeTextView.setText(time);
    }

    private void saveData() {

        // Fake data saving effect
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(STATE_NEW_ALARM_ADDED, true);
                    intent.putExtra(STATE_TITLE, alarmTitleEditText.getText().toString());
                    intent.putExtra(STATE_DESCRIPTION, alarmDescriptionEditText.getText().toString());
                    intent.putExtra(STATE_TIME_HOUR, alarmTimeHour);
                    intent.putExtra(STATE_TIME_MINUTES, alarmTimeMinutes);
                    intent.putExtra(STATE_WEEK_DAYS, alarmDays);

                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

        if(alarmTitleEditText != null) {
            savedInstanceState.putString(STATE_TITLE, alarmTitleEditText.getText().toString());
        }

        if(alarmDescriptionEditText != null) {
            savedInstanceState.putString(STATE_DESCRIPTION, alarmDescriptionEditText.getText().toString());
        }

        if (isTimeSet) {
            savedInstanceState.putInt(STATE_TIME_HOUR, alarmTimeHour);
            savedInstanceState.putInt(STATE_TIME_MINUTES, alarmTimeMinutes);
        }

        if(alarmDays != null) {
            savedInstanceState.putBooleanArray(STATE_WEEK_DAYS, alarmDays);
        }

        // IMPORTANT: The call to super method must be here at the end
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        if(savedInstanceState.containsKey(STATE_TITLE)) {
            String title = savedInstanceState.getString(STATE_TITLE);
            alarmTitleEditText.setText(title);
        }

        if(savedInstanceState.containsKey(STATE_DESCRIPTION)) {
            String description = savedInstanceState.getString(STATE_DESCRIPTION);
            alarmDescriptionEditText.setText(description);
        }

        if(savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTES)) {
            alarmTimeHour = savedInstanceState.getInt(STATE_TIME_HOUR);
            alarmTimeMinutes = savedInstanceState.getInt(STATE_TIME_MINUTES);
            isTimeSet = true;

            setupAlarmTime();
            updatedAlarmTimeText();
        }

        if(savedInstanceState.containsKey(STATE_WEEK_DAYS)) {
            alarmDays = savedInstanceState.getBooleanArray(STATE_WEEK_DAYS);

            setupAlarmDays();
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
                            // Do nothing here
                        }
                    });

            return builder.create();
        }
    }
}
