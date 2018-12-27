package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import kevin.androidhealthtracker.InputUserHealthDataActivity;
import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;

import static android.app.Activity.RESULT_OK;

public class UserProgressFragment extends Fragment {
    private String USER_SETUP_STATUS = "user_setup_status";
    private Boolean USER_SETUP_STATUS_BOOLEAN;
    private static final int USER_DATA_REQUEST_CODE = 7;

    private View view;
    private ProgressBar calorieProgressBar;
    private FloatingActionButton floatingActionButton;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private TextView goalCaloriesTextView, consumedCaloriesTextView, burntCaloriesTextView, netCaloriesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_progressfragment, container, false);
        calorieProgressBar = view.findViewById(R.id.CalorieProgressBar);
        goalCaloriesTextView = view.findViewById(R.id.CalorieGoalValue);
        consumedCaloriesTextView = view.findViewById(R.id.ConsumedCalorieValue);
        burntCaloriesTextView = view.findViewById(R.id.BurntCaloriesValue);
        netCaloriesTextView = view.findViewById(R.id.NetCalorieValue);
        prefs = MainActivity.prefs;
        editor = this.getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(floatingActionButtonListener);


        try {
            USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //TODO: Room integration, store weight as a table, and the user profile as its own thing
        //First store user details, then store the weight with a date for the graph
        //TODO Usersetup does not work correctly, the progress bar does not update

        if (USER_SETUP_STATUS_BOOLEAN == false) {
            //Show user profile setup fragment
            Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
            startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
        } else {
            try {
                calorieProgressBar.setProgress(0);
                goalCaloriesTextView.setText(prefs.getInt("lowcalories", 0));
                consumedCaloriesTextView.setText(0);
                burntCaloriesTextView.setText(0);
                netCaloriesTextView.setText(0);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

        }


        return view;
    }

    private View.OnClickListener floatingActionButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
            startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_DATA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS,false);
                    calorieProgressBar.setProgress(0);
                    goalCaloriesTextView.setText(prefs.getInt("lowcalories", 0));
                    consumedCaloriesTextView.setText(0);
                    burntCaloriesTextView.setText(0);
                    netCaloriesTextView.setText(0);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

