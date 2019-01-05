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
import android.widget.Button;
import android.widget.EditText;
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
    private Button increaseConsumedCalories, decreaseConsumedCalories, increaseBurntCalories, decreaseBurntCalories;
    private EditText consumedCaloriesEditText, burntCaloriesEditText;
    private Integer calories, consumedCalories, burntCalories, netCalories, progress;

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

        consumedCaloriesEditText = view.findViewById(R.id.calorieIntakeEditText);
        burntCaloriesEditText = view.findViewById(R.id.burntCaloriesEditText);

        increaseConsumedCalories = view.findViewById(R.id.increaseConsumedCaloriesButton);
        decreaseConsumedCalories = view.findViewById(R.id.decreaseConsumedCaloriesButton);
        increaseBurntCalories = view.findViewById(R.id.increaseBurntCaloriesButton);
        decreaseBurntCalories = view.findViewById(R.id.decreaseBurntCaloriesButton);

        increaseConsumedCalories.setOnClickListener(increaseCaloriesConsumedListener);
        decreaseConsumedCalories.setOnClickListener(decreaseCaloriesConsumedListener);
        increaseBurntCalories.setOnClickListener(increaseBurntCaloriesListener);
        decreaseBurntCalories.setOnClickListener(decreaseBurntCaloriesListener);


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
        //TODO: Reset calorie tracking per day, use prefs to store string of date and match to current date on activity launch
        //First store user details, then store the weight with a date for the graph

        if (USER_SETUP_STATUS_BOOLEAN == false) {
            //Show user profile setup fragment
            Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
            startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
        } else {
            try {
                refreshProgress();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

        }


        return view;
    }

    private void saveConsumedCalories(Integer consumedCalories) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        editor.putInt("consumedcalories", consumedCalories);
        editor.apply();
    }

    private void saveBurntCalories(Integer burntCalories) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        editor.putInt("burntcalories", burntCalories);
        editor.apply();
    }

    private void refreshProgress() {
        calories = prefs.getInt("lowcalories", 0);
        consumedCalories = prefs.getInt("consumedcalories", 0);
        burntCalories = prefs.getInt("burntcalories", 0);
        netCalories = consumedCalories - burntCalories;
        double percentage = (double) netCalories / (double) calories * 100;
        progress = (int) Math.round(percentage);

        calorieProgressBar.setProgress(progress);
        goalCaloriesTextView.setText(calories.toString());
        consumedCaloriesTextView.setText(consumedCalories.toString());
        consumedCaloriesEditText.setText(consumedCalories.toString());
        burntCaloriesTextView.setText(burntCalories.toString());
        burntCaloriesEditText.setText(burntCalories.toString());
        netCaloriesTextView.setText(netCalories.toString());
    }

    private View.OnClickListener floatingActionButtonListener = view -> {
        Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
        startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
    };

    private View.OnClickListener increaseCaloriesConsumedListener = view -> {
        consumedCalories += 100;
        consumedCaloriesEditText.setText(consumedCalories.toString());
        saveConsumedCalories(consumedCalories);
        refreshProgress();
    };

    private View.OnClickListener decreaseCaloriesConsumedListener = view -> {
        if (consumedCalories - 100 >= 0) {
            consumedCalories -= 100;
        }
        consumedCaloriesEditText.setText(consumedCalories.toString());
        saveConsumedCalories(consumedCalories);
        refreshProgress();
    };

    private View.OnClickListener increaseBurntCaloriesListener = view -> {
        burntCalories += 100;
        burntCaloriesEditText.setText(burntCalories.toString());
        saveBurntCalories(burntCalories);
        refreshProgress();
    };

    private View.OnClickListener decreaseBurntCaloriesListener = view -> {
        if (burntCalories - 100 >= 0) {
            burntCalories -= 100;
        }
        burntCaloriesEditText.setText(burntCalories.toString());
        saveBurntCalories(burntCalories);
        refreshProgress();
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_DATA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS, false);
                    refreshProgress();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

