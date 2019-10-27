package verticalstepperform.ernestoyaquello.com.verticalstepperform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_ALARM_REQUEST_CODE = 1;

    private static final String DATA_RECEIVED = "data_received";
    private static final String INFORMATION = "information";
    private static final String DISCLAIMER = "disclaimer";

    private FloatingActionButton fab;
    private TextView information, disclaimer;
    private boolean dataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewAlarmFormActivity.class);
                startActivityForResult(intent, NEW_ALARM_REQUEST_CODE);
            }
        });

        information = findViewById(R.id.information);
        disclaimer = findViewById(R.id.disclaimer);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        dataReceived = savedInstanceState.getBoolean(DATA_RECEIVED, false);
        if(dataReceived) {
            information.setText(savedInstanceState.getString(INFORMATION));
            disclaimer.setText(savedInstanceState.getString(DISCLAIMER));
            disclaimer.setVisibility(View.VISIBLE);
        } else {
            information.setText(R.string.main_activity_explanation);
            disclaimer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(DATA_RECEIVED, dataReceived);
        if (dataReceived) {
            savedInstanceState.putString(INFORMATION, information.getText().toString());
            savedInstanceState.putString(DISCLAIMER, disclaimer.getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK
            && requestCode == NEW_ALARM_REQUEST_CODE
            && data != null
            && data.hasExtra(NewAlarmFormActivity.STATE_NEW_ALARM_ADDED)) {

            dataReceived = true;

            String alertTitle = data.getExtras().getString(NewAlarmFormActivity.STATE_TITLE);

            int hour = data.getExtras().getInt(NewAlarmFormActivity.STATE_TIME_HOUR);
            int minutes = data.getExtras().getInt(NewAlarmFormActivity.STATE_TIME_MINUTES);
            String alertTime = ((hour > 9) ? hour : ("0" + hour)) + ":" + ((minutes > 9) ? minutes : ("0" + minutes));

            String alertInformationText = getResources().getString(R.string.main_activity_alarm_added_info, alertTitle, alertTime);
            information.setText(alertInformationText);
            disclaimer.setVisibility(View.VISIBLE);

            Snackbar.make(fab, getString(R.string.new_alarm_added), Snackbar.LENGTH_LONG).show();
        } else {
            dataReceived = false;

            information.setText(R.string.main_activity_explanation);
            disclaimer.setVisibility(View.GONE);
        }
    }
}
