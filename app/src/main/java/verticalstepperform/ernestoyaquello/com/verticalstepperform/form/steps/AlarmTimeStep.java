package verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.FormStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.R;

public class AlarmTimeStep extends FormStep<AlarmTimeStep.TimeHolder> {

    private TextView alarmTimeTextView;

    private TimePickerDialog alarmTimePicker;
    private int alarmTimeHour;
    private int alarmTimeMinutes;

    public AlarmTimeStep(String stepTitle) {
        super(stepTitle);

        alarmTimeHour = 8;
        alarmTimeMinutes = 30;
    }

    @NonNull
    @Override
    protected View getStepContentLayout(Context context, VerticalStepperFormLayout form, int stepPosition) {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View timeStepContent = inflater.inflate(R.layout.step_time_layout, null, false);
        alarmTimeTextView = timeStepContent.findViewById(R.id.time);
        setupAlarmTime(context);

        return timeStepContent;
    }

    private void setupAlarmTime(Context context) {
        if (alarmTimePicker == null) {
            alarmTimePicker = new TimePickerDialog(context,
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

    @Override
    protected void onStepOpened(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        updateSubtitle(stepPosition, "", animated);
    }

    @Override
    protected void onStepClosed(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        updateSubtitle(stepPosition, getAlarmTimeAsString(), animated);
    }

    @Override
    public TimeHolder getStepData() {
        return new TimeHolder(alarmTimeHour, alarmTimeMinutes);
    }

    @Override
    public void restoreStepData(TimeHolder data) {
        alarmTimeHour = data.hour;
        alarmTimeMinutes = data.minutes;

        alarmTimePicker.updateTime(alarmTimeHour, alarmTimeMinutes);
        updatedAlarmTimeText();
    }

    @Override
    protected IsDataValid isStepDataValid(TimeHolder stepData) {
        return new IsDataValid(true);
    }

    private void updatedAlarmTimeText() {
        alarmTimeTextView.setText(getAlarmTimeAsString());
    }

    private String getAlarmTimeAsString() {
        String hourString = ((alarmTimeHour > 9) ?
                String.valueOf(alarmTimeHour) : ("0" + alarmTimeHour));
        String minutesString = ((alarmTimeMinutes > 9) ?
                String.valueOf(alarmTimeMinutes) : ("0" + alarmTimeMinutes));
        return hourString + ":" + minutesString;
    }

    public static class TimeHolder {

        public int hour;
        public int minutes;

        public TimeHolder(int hour, int minutes) {
            this.hour = hour;
            this.minutes = minutes;
        }
    }
}
