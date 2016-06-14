package ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

public class VerticalStepperFormLayout extends RelativeLayout {

    private LayoutInflater mInflater;
    protected String[] steps;
    private int stepNumberColor;
    private int buttonColor;
    private int buttonPressedColor;

    public VerticalStepperFormLayout(Context context) {
        super(context);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.vertical_stepper_form_layout, this, true);
    }

    public void setSteps(String[] steps) {
        this.steps = steps;
    }

    public String[] getSteps() {
        return steps;
    }

    public int getStepNumberColor() {
        return stepNumberColor;
    }

    public void setStepNumberColor(int stepNumberColor) {
        this.stepNumberColor = stepNumberColor;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public int getButtonPressedColor() {
        return buttonPressedColor;
    }

    public void setButtonPressedColor(int buttonPressedColor) {
        this.buttonPressedColor = buttonPressedColor;
    }
}