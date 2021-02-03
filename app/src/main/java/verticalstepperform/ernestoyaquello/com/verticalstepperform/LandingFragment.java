package verticalstepperform.ernestoyaquello.com.verticalstepperform;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import verticalstepperform.ernestoyaquello.com.verticalstepperform.models.Alarm;

public class LandingFragment extends Fragment {

    private static final String DATA_RECEIVED = "data_received";
    private static final String INFORMATION = "information";
    private static final String DISCLAIMER = "disclaimer";

    private FloatingActionButton fab;
    private TextView information, disclaimer;
    private boolean dataReceived = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        final Fragment fragment = this;
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(fragment);
                navController.navigate(R.id.action_landingFragment_to_newAlarmFormFragment);
            }
        });

        information = view.findViewById(R.id.information);
        disclaimer = view.findViewById(R.id.disclaimer);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<String> liveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData(NewAlarmFormFragment.ALARM_DATA_SERIALIZED_KEY);
        liveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String alarmSerialized) {
                dataReceived = alarmSerialized != null && !alarmSerialized.isEmpty();
                if (dataReceived) {
                    Alarm alarm = Alarm.fromSerialized(alarmSerialized);
                    int hour = alarm.getTimeHour();
                    int minutes = alarm.getTimeMinutes();
                    String alertTime = ((hour > 9) ? hour : ("0" + hour)) + ":" + ((minutes > 9) ? minutes : ("0" + minutes));
                    String alertInformationText = getResources().getString(R.string.main_activity_alarm_added_info, alarm.getTitle(), alertTime);

                    information.setText(alertInformationText);
                    disclaimer.setVisibility(View.VISIBLE);

                    Snackbar.make(fab, getString(R.string.new_alarm_added), Snackbar.LENGTH_LONG).show();
                } else {
                    information.setText(R.string.main_activity_explanation);
                    disclaimer.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        dataReceived = savedInstanceState != null && savedInstanceState.getBoolean(DATA_RECEIVED, false);
        if(dataReceived) {
            information.setText(savedInstanceState.getString(INFORMATION));
            disclaimer.setText(savedInstanceState.getString(DISCLAIMER));
            disclaimer.setVisibility(View.VISIBLE);
        } else {
            information.setText(R.string.main_activity_explanation);
            disclaimer.setVisibility(View.GONE);
        }
    }
}
