package ernestoyaquello.com.verticalstepperform.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * This fragment can be used to display a confirmation dialog when the user tries to go back
 */
public class BackConfirmationFragment extends DialogFragment {

    private DialogInterface.OnClickListener onConfirmBack;
    private DialogInterface.OnClickListener onNotConfirmBack;

    public void setOnConfirmBack(DialogInterface.OnClickListener onConfirmBack) {
        this.onConfirmBack = onConfirmBack;
    }

    public void setOnNotConfirmBack(DialogInterface.OnClickListener onNotConfirmBack) {
        this.onNotConfirmBack = onNotConfirmBack;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(ernestoyaquello.com.verticalstepperform.R.string.vertical_form_stepper_form_discard_question)
                .setMessage(ernestoyaquello.com.verticalstepperform.R.string.vertical_form_stepper_form_info_will_be_lost)
                .setNegativeButton(ernestoyaquello.com.verticalstepperform.R.string.vertical_form_stepper_form_discard_cancel,
                        onConfirmBack)
                .setPositiveButton(ernestoyaquello.com.verticalstepperform.R.string.vertical_form_stepper_form_discard,
                        onNotConfirmBack);
        return builder.create();
    }
}