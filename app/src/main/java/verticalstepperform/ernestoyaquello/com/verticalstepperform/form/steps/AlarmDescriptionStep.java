package verticalstepperform.ernestoyaquello.com.verticalstepperform.form.steps;

import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;
import ernestoyaquello.com.verticalstepperform.FormStep;
import verticalstepperform.ernestoyaquello.com.verticalstepperform.R;

public class AlarmDescriptionStep extends FormStep<String> {

    private TextInputEditText alarmDescriptionEditText;

    public AlarmDescriptionStep(String stepTitle) {
        super(stepTitle);
    }

    @NonNull
    @Override
    protected View getStepContentLayout(Context context, final VerticalStepperFormLayout form, int stepPosition) {

        // We create this step view programmatically
        alarmDescriptionEditText = new TextInputEditText(context);
        alarmDescriptionEditText.setHint(R.string.form_hint_description);
        alarmDescriptionEditText.setSingleLine(true);
        alarmDescriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                form.goToNextStep(true);
                return false;
            }
        });

        return alarmDescriptionEditText;
    }

    @Override
    protected void onStepOpened(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        updateSubtitle(stepPosition, "", animated);
    }

    @Override
    protected void onStepClosed(VerticalStepperFormLayout form, int stepPosition, boolean animated) {
        String description = getStepData();
        description = description == null || description.isEmpty()
                ? form.getContext().getString(R.string.form_empty_field)
                : description;
        updateSubtitle(stepPosition, description, animated);
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