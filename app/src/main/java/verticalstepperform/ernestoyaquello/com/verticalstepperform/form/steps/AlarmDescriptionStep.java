package verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import ernestoyaquello.com.verticalstepperform.Step;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.R;

public class AlarmDescriptionStep extends Step<String> {

    private TextInputEditText alarmDescriptionEditText;

    public AlarmDescriptionStep(String title) {
        this(title, "");
    }

    public AlarmDescriptionStep(String title, String subtitle) {
        super(title, subtitle);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view programmatically
        alarmDescriptionEditText = new TextInputEditText(getContext());
        alarmDescriptionEditText.setHint(R.string.form_hint_description);
        alarmDescriptionEditText.setSingleLine(true);
        alarmDescriptionEditText.setOnEditorActionListener((v, actionId, event) -> {
            getFormView().goToNextStep(true);
            return false;
        });

        return alarmDescriptionEditText;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepClosed(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // No need to do anything here
    }

    @Override
    public String getStepData() {
        Editable text = alarmDescriptionEditText.getText();
        if (text != null) {
            return text.toString();
        }

        return "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        String description = getStepData();
        return description == null || description.isEmpty()
                ? getContext().getString(R.string.form_empty_field)
                : description;
    }

    @Override
    protected void restoreStepData(String data) {
        if (alarmDescriptionEditText != null) {
            alarmDescriptionEditText.setText(data);
        }
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }
}