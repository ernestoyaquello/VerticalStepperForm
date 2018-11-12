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

    public AlarmDescriptionStep(String stepTitle) {
        super(stepTitle);
    }

    @NonNull
    @Override
    protected View createStepContentLayout() {

        // We create this step view programmatically
        alarmDescriptionEditText = new TextInputEditText(getContext());
        alarmDescriptionEditText.setHint(R.string.form_hint_description);
        alarmDescriptionEditText.setSingleLine(true);
        alarmDescriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getFormLayout().goToNextStep(true);
                return false;
            }
        });

        return alarmDescriptionEditText;
    }

    @Override
    protected void onStepOpened(boolean animated) {
        updateSubtitle("", animated);
    }

    @Override
    protected void onStepClosed(boolean animated) {
        String description = getStepData();
        description = description == null || description.isEmpty()
                ? getContext().getString(R.string.form_empty_field)
                : description;
        updateSubtitle(description, animated);
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
    public void restoreStepData(String data) {
        if (alarmDescriptionEditText != null) {
            alarmDescriptionEditText.setText(data);
        }
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        return new IsDataValid(true);
    }
}