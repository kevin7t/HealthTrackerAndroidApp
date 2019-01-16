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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kevin.androidhealthtracker.InputUserHealthDataActivity;
import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.database.HealthTrackerDatabase;
import kevin.androidhealthtracker.datamodels.DailyCalories;
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
        weightEditText.setOnClickListener(weightListener);

        executor = Executors.newFixedThreadPool(4);
        prefs = MainActivity.prefs;
        editor = this.getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(floatingActionButtonListener);
        healthTrackerDatabase = Room.databaseBuilder(getActivity().getApplicationContext(),
                HealthTrackerDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        /**
         * Get calories from DB
         */
        try {
            dailyCalories = getTodaysCalories();
        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }

        /**
         * Get weight from DB
         */
        try {
            weight = getTodaysWeight();
        } catch (ExecutionException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }

        try {
            USER_SETUP_STATUS_BOOLEAN = prefs.getBoolean(USER_SETUP_STATUS, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //TODO: save weight to database and retrieve them on activity creation

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


//        testDB();
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
        weightEditText.setText(weight.toString());
        saveCaloriesToDB();
        saveWeightToDB();
    }

    private View.OnClickListener weightListener = view -> {
        Float value = Float.valueOf(weightEditText.getText().toString());
        weight.setWeight(value);
        refreshProgress();
    };
    private View.OnClickListener floatingActionButtonListener = view -> {
        Intent userSetupIntent = new Intent(getActivity(), InputUserHealthDataActivity.class);
        startActivityForResult(userSetupIntent, USER_DATA_REQUEST_CODE);
    };

    private View.OnClickListener increaseCaloriesConsumedListener = view -> {
        dailyCalories.incrementConsumedCalories(100);
        consumedCaloriesEditText.setText(Integer.toString(dailyCalories.getConsumedCalories()));
        refreshProgress();
    };

    private View.OnClickListener decreaseCaloriesConsumedListener = view -> {
        dailyCalories.incrementBurntCalories(100);
        consumedCaloriesEditText.setText(Integer.toString(dailyCalories.getConsumedCalories()));
        refreshProgress();
    };

    private View.OnClickListener increaseBurntCaloriesListener = view -> {
        dailyCalories.incrementBurntCalories(100);
        burntCaloriesEditText.setText(Integer.toString(dailyCalories.getBurntCalories()));
        refreshProgress();
    };

    private View.OnClickListener decreaseBurntCaloriesListener = view -> {
        dailyCalories.decrementBurntCalories(100);
        burntCaloriesEditText.setText(Integer.toString(dailyCalories.getBurntCalories()));
        refreshProgress();
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If user has come from the setup activity then refresh the items
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

    private void testDB() {

        try {
            System.out.println(getAllWeightFromDB().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        String date = null;
        try {
            date = getTodaysDate();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        weight.setDate(date);
        weight.setWeight(60.0f);
        saveWeightToDB();
        Future<Weight> weightFuture = getWeightFromDB(date);
        if (weightFuture.isDone()) {
            try {
                System.out.println(weightFuture.get().toString());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        try {
            System.out.println(getAllWeightFromDB().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
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

    private Future<Weight> getWeightFromDB(String date) {
        Callable<Weight> task = () -> {
            Weight weight = null;
            try {
                weight = healthTrackerDatabase.weightDAO().getByDate(date);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return weight;
        };

        Future<Weight> weightFuture = executor.submit(task);
        return weightFuture;
    }

    private Future<List<Weight>> getAllWeightFromDB() {
        Callable<List<Weight>> task = () -> {
            List<Weight> weightList = null;
            try {
                weightList = healthTrackerDatabase.weightDAO().getAll();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return weightList;
        };

        Future<List<Weight>> weightFuture = executor.submit(task);
        return weightFuture;
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

    private Future<DailyCalories> getCaloriesFromDB(String date) {
        Callable<DailyCalories> task = () -> {
            DailyCalories dailyCalories = null;
            try {
                dailyCalories = healthTrackerDatabase.dailyCaloriesDAO().getByDate(date);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return dailyCalories;
        };

        Future<DailyCalories> dailyCaloriesFuture = executor.submit(task);
        return dailyCaloriesFuture;
    }

    private Future<List<DailyCalories>> getAllCaloriesFromDB() {
        Callable<List<DailyCalories>> task = () -> {
            List<DailyCalories> dailyCaloriesList = null;
            try {
                dailyCaloriesList = healthTrackerDatabase.dailyCaloriesDAO().getAll();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return dailyCaloriesList;
        };

        Future<List<DailyCalories>> dailyCaloriesFuture = executor.submit(task);
        return dailyCaloriesFuture;
    }

    private Weight getTodaysWeight() throws ExecutionException, InterruptedException, ParseException {
        Future<Weight> result = getWeightFromDB(getTodaysDate());
        Weight weight = null;
//        while (result.get() == null) {
//            Thread.sleep(1000);
//        }
        if (result.isDone() && result.get() != null) {
            weight = result.get();
        }
        if (weight == null) {
            weight = new Weight(getTodaysDate());
            refreshProgress();
            return weight;
        } else {
            return weight;
        }
    }

    private DailyCalories getTodaysCalories() throws ExecutionException, InterruptedException, ParseException {
        Future<DailyCalories> result = getCaloriesFromDB(getTodaysDate());
        DailyCalories calories = null;
        //TODO Find a way to wait for future to complete because otherwise it returns nothing
//        while (result.get() == null) {
//            //This will break if there is no value
//            Thread.sleep(1000);
//        }
        Thread.sleep(5000);
        if (result.isDone() && result.get() != null) {
            calories = result.get();
        }
        if (calories == null) {
            dailyCalories = new DailyCalories(getTodaysDate(), getUserCalorieLevel());
            refreshProgress();
            return dailyCalories;
        } else {
            return calories;
        }
    }

    private int getUserCalorieLevel() {
        return prefs.getInt("lowcalories", 0);
    }

}

