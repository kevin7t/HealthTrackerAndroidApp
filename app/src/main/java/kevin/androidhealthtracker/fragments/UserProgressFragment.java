package kevin.androidhealthtracker.fragments;

import android.app.Fragment;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kevin.androidhealthtracker.InputUserHealthDataActivity;
import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.database.HealthTrackerDatabase;
import kevin.androidhealthtracker.datamodels.DailyCalories;
import kevin.androidhealthtracker.datamodels.UserCalorieProfile;
import kevin.androidhealthtracker.datamodels.Weight;

import static android.app.Activity.RESULT_OK;

public class UserProgressFragment extends Fragment {
    private String USER_SETUP_STATUS = "user_setup_status";
    private static final String DATABASE_NAME = "healthtracker_db";

    private HealthTrackerDatabase healthTrackerDatabase;

    private Boolean USER_SETUP_STATUS_BOOLEAN;
    private static final int USER_DATA_REQUEST_CODE = 7;

    private View view;
    private ProgressBar calorieProgressBar;
    private FloatingActionButton floatingActionButton;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Button increaseConsumedCalories, decreaseConsumedCalories, increaseBurntCalories, decreaseBurntCalories;
    private EditText consumedCaloriesEditText, burntCaloriesEditText, weightEditText;
    private TextView goalCaloriesTextView, consumedCaloriesTextView, burntCaloriesTextView, netCaloriesTextView;
    private ExecutorService executor;

    private DailyCalories dailyCalories;
    private Weight weight;

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
        weightEditText = view.findViewById(R.id.WeightEditText);

        increaseConsumedCalories = view.findViewById(R.id.increaseConsumedCaloriesButton);
        decreaseConsumedCalories = view.findViewById(R.id.decreaseConsumedCaloriesButton);
        increaseBurntCalories = view.findViewById(R.id.increaseBurntCaloriesButton);
        decreaseBurntCalories = view.findViewById(R.id.decreaseBurntCaloriesButton);

        increaseConsumedCalories.setOnClickListener(increaseCaloriesConsumedListener);
        decreaseConsumedCalories.setOnClickListener(decreaseCaloriesConsumedListener);
        increaseBurntCalories.setOnClickListener(increaseBurntCaloriesListener);
        decreaseBurntCalories.setOnClickListener(decreaseBurntCaloriesListener);
        weightEditText.setOnEditorActionListener(weightListener);

        executor = Executors.newFixedThreadPool(4);
        prefs = MainActivity.prefs;
        editor = this.getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(floatingActionButtonListener);
        healthTrackerDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                HealthTrackerDatabase.class, DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        try {
            USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (USER_SETUP_STATUS_BOOLEAN == false) {
            //Show user profile setup fragment
            Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
            startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
        }
        /**
         * Get calories from DB
         */
        try {
            dailyCalories = getTodaysCalories();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * Get weight from DB
         */
        try {
            weight = getTodaysWeight();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        refreshProgress();
        //TODO: save weight to database and retrieve them on activity creation



        return view;
    }

    private void refreshProgress() {
        Integer goalCalories = dailyCalories.getGoalCalories();
        Integer consumedCalories = dailyCalories.getConsumedCalories();
        Integer burntCalories = dailyCalories.getBurntCalories();
        Integer netCalories = consumedCalories - burntCalories;
        double percentage = (double) netCalories / (double) goalCalories * 100;
        Integer progress = (int) Math.round(percentage);

        calorieProgressBar.setProgress(progress);
        goalCaloriesTextView.setText(goalCalories.toString());
        consumedCaloriesTextView.setText(consumedCalories.toString());
        consumedCaloriesEditText.setText(consumedCalories.toString());
        burntCaloriesTextView.setText(burntCalories.toString());
        burntCaloriesEditText.setText(burntCalories.toString());
        netCaloriesTextView.setText(netCalories.toString());
        weightEditText.setText(weight.getWeight().toString()+"kg");
    }

    private TextView.OnEditorActionListener weightListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                Float value = Float.valueOf(weightEditText.getText().toString());
                weight.setWeight(value);
                refreshProgress();
                saveWeightToDB();
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);
                return true;
            }
            return false;
        }
    };
    private View.OnClickListener floatingActionButtonListener = view -> {
        Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
        startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
    };

    private View.OnClickListener increaseCaloriesConsumedListener = view -> {
        dailyCalories.incrementConsumedCalories(100);
        consumedCaloriesEditText.setText(Integer.toString(dailyCalories.getConsumedCalories()));
        refreshProgress();
        saveCaloriesToDB();
    };

    private View.OnClickListener decreaseCaloriesConsumedListener = view -> {
        dailyCalories.incrementBurntCalories(100);
        consumedCaloriesEditText.setText(Integer.toString(dailyCalories.getConsumedCalories()));
        refreshProgress();
        saveCaloriesToDB();
    };

    private View.OnClickListener increaseBurntCaloriesListener = view -> {
        dailyCalories.incrementBurntCalories(100);
        burntCaloriesEditText.setText(Integer.toString(dailyCalories.getBurntCalories()));
        refreshProgress();
        saveCaloriesToDB();
    };

    private View.OnClickListener decreaseBurntCaloriesListener = view -> {
        dailyCalories.decrementBurntCalories(100);
        burntCaloriesEditText.setText(Integer.toString(dailyCalories.getBurntCalories()));
        refreshProgress();
        saveCaloriesToDB();
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If user has come from the setup activity then refresh the items
        if (requestCode == USER_DATA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS, false);
                    try {
                        UserCalorieProfile profile = getUserCalorieProfileFromDB();
                        dailyCalories = new DailyCalories(getTodaysDate(),profile.getLowCalories());
                        weight = new Weight(getTodaysDate());
                        weight.setWeight(profile.getWeight());

                        saveCaloriesToDB();
                        saveWeightToDB();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    refreshProgress();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getTodaysDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy");
        return formatter.parse(formatter.format(new Date())).toString();
    }

    private void saveWeightToDB() {
        new Thread(() -> {
            try {
                healthTrackerDatabase.weightDAO().insert(weight);
            } catch (SQLiteConstraintException e) {
                healthTrackerDatabase.weightDAO().update(weight);
            }
        }).start();
    }

    private Weight getWeightFromDB() {
        return healthTrackerDatabase.weightDAO().getLatest();
    }

    private List<Weight> getAllWeightFromDB() {
        return healthTrackerDatabase.weightDAO().getAll();
    }

    private void saveCaloriesToDB() {
        new Thread(() -> {
            try {
                healthTrackerDatabase.dailyCaloriesDAO().insert(dailyCalories);
            } catch (SQLiteConstraintException e) {
                healthTrackerDatabase.dailyCaloriesDAO().update(dailyCalories);
            }
        }).start();
    }

    private DailyCalories getCaloriesFromDB(String date) {
        return healthTrackerDatabase.dailyCaloriesDAO().getByDate(date);
    }

    private List<DailyCalories> getAllCaloriesFromDB() {
        return healthTrackerDatabase.dailyCaloriesDAO().getAll();
    }

    private UserCalorieProfile getUserCalorieProfileFromDB(){
        return healthTrackerDatabase.userCalorieProfileDAO().getLatest();
    }

    private Weight getTodaysWeight() throws ParseException {
        weight = getWeightFromDB();
        if (weight == null) {
            weight = new Weight(getTodaysDate());
            weight.setWeight(getUserWeightFromSetup());
            return weight;
        } else {
            return weight;
        }
    }

    private DailyCalories getTodaysCalories() throws ParseException {
        DailyCalories calories = getCaloriesFromDB(getTodaysDate());
        if (calories == null) {
            dailyCalories = new DailyCalories(getTodaysDate(), getUserCalorieLevelFromSetup());
            return dailyCalories;
        } else {
            return calories;
        }
    }

    private int getUserCalorieLevelFromSetup() {
        UserCalorieProfile profile = healthTrackerDatabase.userCalorieProfileDAO().getLatest();
        return profile.getLowCalories();
    }
    private float getUserWeightFromSetup() {
        UserCalorieProfile profile = healthTrackerDatabase.userCalorieProfileDAO().getLatest();
        return profile.getWeight();
    }
}

