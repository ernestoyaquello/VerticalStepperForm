package verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.FormStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.R;

public class AlarmDaysStep extends FormStep<boolean[]> {

    private boolean[] alarmDays;
    private View daysStepContent;
    private boolean dataRestored;

    public AlarmDaysStep(String stepTitle) {
        super(stepTitle);
    }

    @NonNull
    @Override
    protected View getStepContentLayout(Context context, VerticalStepperFormLayout form, int stepPosition) {

        // We create this step view by inflating an XML layout
        LayoutInflater inflater = LayoutInflater.from(context);
        daysStepContent = inflater.inflate(R.layout.step_days_of_week_layout, null, false);
        setupAlarmDays(form, stepPosition);

        return daysStepContent;
    }

    @Override
    protected void onStepOpened(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        updateSubtitle(stepPosition, "", animated);
        if (dataRestored) {
            dataRestored = false;
            setupAlarmDays(form, stepPosition);
        }
    }

    @Override
    protected void onStepClosed(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        Context context = form.getContext();
        String selectedDaysAsString = getSelectedWeekDaysAsString(context);
        updateSubtitle(stepPosition, selectedDaysAsString, animated);
    }

    @Override
    public boolean[] getStepData() {
        return alarmDays;
    }

    @Override
    public void restoreStepData(boolean[] data) {
        alarmDays = data;
        dataRestored = true;
    }

    @Override
    protected IsDataValid isStepDataValid(boolean[] stepData) {
        boolean thereIsAtLeastOneDaySelected = false;
        for(int i = 0; i < stepData.length && !thereIsAtLeastOneDaySelected; i++) {
            if(stepData[i]) {
                thereIsAtLeastOneDaySelected = true;
            }
        }

        return new IsDataValid(thereIsAtLeastOneDaySelected);
    }

    private void setupAlarmDays(final VerticalStepperFormLayout form, final int stepPosition) {
        boolean firstSetup = alarmDays == null;
        alarmDays = firstSetup ? new boolean[7] : alarmDays;

        final String[] weekDays = form.getContext().getResources().getStringArray(R.array.week_days);
        for(int i = 0; i < weekDays.length; i++) {
            final int index = i;
            final View dayLayout = getDayLayout(form.getContext(), index);

            if (firstSetup) {
                // By default, we only mark the working days as activated
                if (index < 5) {
                    markAlarmDay(form, stepPosition, index, dayLayout, false);
                } else {
                    unmarkAlarmDay(form, stepPosition, index, dayLayout, false);
                }
            } else {
                updateDayLayout(form, stepPosition, index, dayLayout, false);
            }

            if (dayLayout != null) {
                dayLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDays[index] = !alarmDays[index];
                        updateDayLayout(form, stepPosition, index, dayLayout, true);
                    }
                });

                final TextView dayText = dayLayout.findViewById(R.id.day);
                dayText.setText(weekDays[index]);
            }
        }
    }

    private View getDayLayout(Context context, int i) {
        int id = daysStepContent.getResources().getIdentifier(
                "day_" + i, "id", context.getPackageName());
        return daysStepContent.findViewById(id);
    }

    private void updateDayLayout(VerticalStepperFormLayout form, int stepPostion, int dayIndex, View dayLayout, boolean updateState) {
        if (alarmDays[dayIndex]) {
            markAlarmDay(form, stepPostion, dayIndex, dayLayout, updateState);
        } else {
            unmarkAlarmDay(form, stepPostion, dayIndex, dayLayout, updateState);
        }
    }

    private void markDaysStepAsCompletedOrUncompleted(VerticalStepperFormLayout form, int stepPosition, boolean useAnimations) {
        if (isStepDataValid().isValid()) {
            form.markStepAsCompleted(stepPosition, useAnimations);
        } else {
            form.markStepAsUncompleted(stepPosition, null, useAnimations);
        }
    }

    private void markAlarmDay(VerticalStepperFormLayout form, int stepPosition, int dayIndex, View dayLayout, boolean markStepAsCompletedOrUncompleted) {
        alarmDays[dayIndex] = true;

        if (dayLayout != null) {
            Drawable bg = ContextCompat.getDrawable(form.getContext(), ernestoyaquello.com.verticalstepperform.R.drawable.circle_step_done);
            int colorPrimary = ContextCompat.getColor(form.getContext(), R.color.colorPrimary);
            bg.setColorFilter(new PorterDuffColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN));
            dayLayout.setBackground(bg);

            TextView dayText = dayLayout.findViewById(R.id.day);
            dayText.setTextColor(Color.rgb(255, 255, 255));
        }

        if(markStepAsCompletedOrUncompleted) {
            markDaysStepAsCompletedOrUncompleted(form, stepPosition, true);
        }
    }

    private void unmarkAlarmDay(VerticalStepperFormLayout form, int stepPosition, int dayIndex, View dayLayout, boolean markStepAsCompletedOrUncompleted) {
        alarmDays[dayIndex] = false;

        dayLayout.setBackgroundResource(0);

        TextView dayText = dayLayout.findViewById(R.id.day);
        int colour = ContextCompat.getColor(form.getContext(), R.color.colorPrimary);
        dayText.setTextColor(colour);

        if(markStepAsCompletedOrUncompleted) {
            markDaysStepAsCompletedOrUncompleted(form, stepPosition, true);
        }
    }

    private String getSelectedWeekDaysAsString(Context context) {
        String[] weekDayStrings = context.getResources().getStringArray(R.array.week_days_extended);
        List<String> selectedWeekDayStrings = new ArrayList<>();
        for (int i = 0; i < weekDayStrings.length; i++) {
            if (alarmDays[i]) {
                selectedWeekDayStrings.add(weekDayStrings[i]);
            }
        }

        return TextUtils.join(", ", selectedWeekDayStrings);
    }
}
