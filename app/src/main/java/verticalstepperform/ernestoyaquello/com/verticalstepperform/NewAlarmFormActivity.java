package verticalstepperform.ernestoyaquello.com.verticalstepperform;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormBaseActivity;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;

public class NewAlarmFormActivity extends VerticalStepperFormBaseActivity {

    public static final String NEW_ALARM_ADDED = "new_alarm_added";

    // Information about the steps/fields of the form
    private static final int TITLE_STEP_NUM = 0;
    private static final int DESCRIPTION_STEP_NUM = 1;
    private static final int TIME_STEP_NUM = 2;
    private static final int DAYS_STEP_NUM = 3;
    private static final String[] stepsStrings = {
            "Title",
            "Description",
            "Time",
            "Week schedule"
    };

    // Title step
    private EditText titleEditText;
    private static final int MIN_CHARACTERS_TITLE = 3;
    public static final String STATE_TITLE = "title";

    // Description step
    private EditText descriptionEditText;
    public static final String STATE_DESCRIPTION = "description";

    // Time step
    private TextView timeTextView;
    private TimePickerDialog timePicker;
    private Pair<Integer, Integer> time;
    public static final String STATE_TIME_HOUR = "time_hour";
    public static final String STATE_TIME_MINUTES = "time_minutes";

    // Week days step
    private boolean[] weekDays;
    private LinearLayout[] weekDaysLayouts;
    private TextView[] weekDaysTextviews;
    public static final String STATE_WEEK_DAYS = "week_days";

    // METHODS THAT HAVE TO BE IMPLEMENTED TO MAKE THE LIBRARY WORK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_stepper_form);
        // We need to use these lines of code to initialize the stepper form
        verticalStepperForm = (VerticalStepperFormLayout) findViewById(R.id.vertical_stepper_form);
        if(verticalStepperForm != null) {
            verticalStepperForm.setSteps(stepsStrings);
            int colorPrimary = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary);
            int colorPrimaryDark = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            verticalStepperForm.setStepNumberColor(colorPrimary);
            verticalStepperForm.setButtonColor(colorPrimary);
            verticalStepperForm.setButtonPressedColor(colorPrimaryDark);
            initStepperForm();
        }
    }

    @Override
    protected View createCustomStep(int numStep, RelativeLayout stepContent) {
        // Here we generate the content view of the correspondent step and we return it so it gets
        // automatically added to the step layout (AKA stepContent)
        View view = null;
        switch (numStep) {
            case TITLE_STEP_NUM:
                view = createAlarmTitleStep();
                break;
            case DESCRIPTION_STEP_NUM:
                view = createAlarmDescriptionStep();
                break;
            case TIME_STEP_NUM:
                view = createAlarmTimeStep();
                break;
            case DAYS_STEP_NUM:
                view = createAlarmDaysStep();
                break;
        }
        return view;
    }

    @Override
    protected void customStepsCheckingOnStepOpening() {
        switch (activeStep) {
            case TITLE_STEP_NUM:
                // When this step is open, we check that the title is correct
                checkTitleStepCompletion(titleEditText.getText().toString());
                break;
            case DESCRIPTION_STEP_NUM:
            case TIME_STEP_NUM:
                // As soon as they are open, these two steps are marked as completed because they
                // have default values
                setStepAsCompleted(activeStep);
                break;
            case DAYS_STEP_NUM:
                // When this step is open, we check the days to verify that at least one is selected
                checkDays();
                break;
        }
    }

    @Override
    protected void sendData() {

        // TODO Use here the data of the form as you wish

        // Fake data sending effect
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    intent.putExtra(NEW_ALARM_ADDED, true);
                    intent.putExtra(STATE_TITLE, titleEditText.getText().toString());
                    intent.putExtra(STATE_DESCRIPTION, descriptionEditText.getText().toString());
                    intent.putExtra(STATE_TIME_HOUR, time.first);
                    intent.putExtra(STATE_TIME_MINUTES, time.second);
                    intent.putExtra(STATE_WEEK_DAYS, weekDays);
                    // You must set confirmBack to false before calling finish() to avoid the confirmation dialog
                    confirmBack = false;
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start(); // You should delete this code and add yours

    }

    // OTHER METHODS USED TO MAKE THIS EXAMPLE WORK

    // Creation of the title step
    private View createAlarmTitleStep() {
        // This step view is generated programmatically
        titleEditText = new EditText(this);
        titleEditText.setSingleLine(true);
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkTitleStepCompletion(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        titleEditText.setHint("Alarm title (mandatory)");
        titleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(v.getText().toString().length() >= MIN_CHARACTERS_TITLE) {
                    goToNextStep();
                }
                return false;
            }
        });
        return titleEditText;
    }

    // Creation of the description step
    private View createAlarmDescriptionStep() {
        // This step view is generated programmatically
        descriptionEditText = new EditText(this);
        descriptionEditText.setSingleLine(false);
        descriptionEditText.setHint("Alarm description (optional)");
        return descriptionEditText;
    }

    // Creation of the time step
    private View createAlarmTimeStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout timeStepContent =
                (LinearLayout) inflater.inflate(R.layout.step_time_layout, null, false);
        timeTextView = (TextView) timeStepContent.findViewById(R.id.time);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show();
            }
        });
        return timeStepContent;
    }

    // Creation of the week days step
    private View createAlarmDaysStep() {
        // This step view is generated by inflating a layout XML file
        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
        LinearLayout addDaysStepContent = (LinearLayout) inflater.inflate(
                R.layout.step_days_of_week_layout, null, false);
        String[] weekDays = getResources().getStringArray(R.array.week_days);
        for(int i = 0; i < weekDays.length; i++) {
            int id = addDaysStepContent.getResources().getIdentifier(
                    "day_" + i, "id", getPackageName());
            LinearLayout dayLayout = (LinearLayout) addDaysStepContent.findViewById(id);
            final TextView dayText = (TextView) dayLayout.findViewById(R.id.day);
            weekDaysLayouts[i] = dayLayout;
            weekDaysTextviews[i] = dayText;
            if(i < 5) {
                activateDay(i, false);
            } else {
                deactivateDay(i, false);
            }
            dayText.setText(weekDays[i]);
            final int iFinal = i;
            dayLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((Boolean)v.getTag())) {
                        deactivateDay(iFinal, true);
                    } else {
                        activateDay(iFinal, true);
                    }
                }
            });
        }
        return addDaysStepContent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Saving title field
        if(titleEditText != null) {
            savedInstanceState.putString(STATE_TITLE, titleEditText.getText().toString());
        }

        // Saving description field
        if(descriptionEditText != null) {
            savedInstanceState.putString(STATE_DESCRIPTION, descriptionEditText.getText().toString());
        }

        // Saving time field
        if(time != null) {
            savedInstanceState.putInt(STATE_TIME_HOUR, time.first);
            savedInstanceState.putInt(STATE_TIME_MINUTES, time.second);
        }

        // Saving week days field
        if(weekDays != null) {
            savedInstanceState.putBooleanArray(STATE_WEEK_DAYS, weekDays);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restoration of title field
        if(savedInstanceState.containsKey(STATE_TITLE)) {
            String title = savedInstanceState.getString(STATE_TITLE);
            titleEditText.setText(title);
        }

        // Restoration of description field
        if(savedInstanceState.containsKey(STATE_DESCRIPTION)) {
            String description = savedInstanceState.getString(STATE_DESCRIPTION);
            descriptionEditText.setText(description);
        }

        // Restoration of time field
        if(savedInstanceState.containsKey(STATE_TIME_HOUR)
                && savedInstanceState.containsKey(STATE_TIME_MINUTES)) {
            int hour = savedInstanceState.getInt(STATE_TIME_HOUR);
            int minutes = savedInstanceState.getInt(STATE_TIME_MINUTES);
            time = new Pair(hour, minutes);
            setTime(hour, minutes);
            if(timePicker == null) {
                setTimePicker(hour, minutes);
            } else {
                timePicker.updateTime(hour, minutes);
            }
        }

        // Restoration of week days field
        if(savedInstanceState.containsKey(STATE_WEEK_DAYS)) {
            weekDays = savedInstanceState.getBooleanArray(STATE_WEEK_DAYS);
            if (weekDays != null) {
                for (int i = 0; i < weekDays.length; i++) {
                    if (weekDays[i]) {
                        activateDay(i, false);
                    } else {
                        deactivateDay(i, false);
                    }
                }
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void setInitialVariables() {
        super.setInitialVariables();

        // Time step variables
        time = new Pair(8, 30);
        setTimePicker(8, 30);

        // Week days step variables
        weekDays = new boolean[7];
        weekDaysLayouts = new LinearLayout[7];
        weekDaysTextviews = new TextView[7];
        for(int i = 0; i < weekDays.length; i++) {
            if(i < 5) {
                weekDays[i] = true;
            } else {
                weekDays[i] = false;
            }
        }

    }

    private void setTimePicker(int hour, int minutes) {
        timePicker = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTime(hourOfDay, minute);
                    }
                }, hour, minutes, true);
    }

    private void checkTitleStepCompletion(String title) {
        if(title.length() >= MIN_CHARACTERS_TITLE) {
            setActiveStepAsCompleted();
        } else {
            setActiveStepAsUncompleted();
        }
    }

    private void setTime(int hour, int minutes) {
        time = new Pair(hour, minutes);
        String hourString = ((time.first > 9) ?
                String.valueOf(time.first) : ("0" + time.first));
        String minutesString = ((time.second > 9) ?
                String.valueOf(time.second) : ("0" + time.second));
        timeTextView.setText(hourString + ":" + minutesString);
    }

    private void activateDay(int i, boolean check) {
        LinearLayout dayLayout = weekDaysLayouts[i];
        TextView dayText = weekDaysTextviews[i];
        dayLayout.setTag(true);
        Drawable bg = ContextCompat.getDrawable(getBaseContext(),
                ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
        bg.setColorFilter(new PorterDuffColorFilter(
                verticalStepperForm.getStepNumberColor(), PorterDuff.Mode.SRC_IN));
        dayLayout.setBackground(bg);
        int color = Color.rgb(255, 255, 255);
        dayText.setTextColor(color);
        weekDays[i] = true;
        if(check) {
            checkDays();
        }
    }

    private void deactivateDay(int i, boolean check) {
        LinearLayout dayLayout = weekDaysLayouts[i];
        TextView dayText = weekDaysTextviews[i];
        dayLayout.setTag(false);
        dayLayout.setBackgroundResource(0);
        int colour = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        dayText.setTextColor(colour);
        weekDays[i] = false;
        if(check) {
            checkDays();
        }
    }

    private void checkDays() {
        boolean thereIsAtLeastOneDaySelected = false;
        for(int i = 0; i < weekDays.length && !thereIsAtLeastOneDaySelected; i++) {
            if(weekDays[i]) {
                setStepAsCompleted(DAYS_STEP_NUM);
                thereIsAtLeastOneDaySelected = true;
            }
        }
        if(!thereIsAtLeastOneDaySelected) {
            setStepAsUncompleted(DAYS_STEP_NUM);
        }
    }

}
