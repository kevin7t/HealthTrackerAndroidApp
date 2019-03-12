package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.User;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kevin.androidhealthtracker.adapters.FriendsListAdapter;
import kevin.androidhealthtracker.adapters.FriendsListSpinnerAdapter;


/**
 * A login screen that offers login via email/password.
 */
public class CreateScheduleActivity extends AppCompatActivity {

    private WebClient client;
    private SharedPreferences prefs;
    private int userId;
    private String userName;

    private Spinner friendsListSpinner;
    private EditText dateTimeEditText;
    private Spinner activityTypeSpinner;

    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        client = MainActivity.client;
        prefs = MainActivity.prefs;
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        friendsListSpinner = findViewById(R.id.friendsListSpinner);
        dateTimeEditText = findViewById(R.id.dateTimeEditText);
        activityTypeSpinner = findViewById(R.id.activitySpinner);

        ExecutorService executor = Executors.newWorkStealingPool();
        Callable<List<User>> task = () -> {
            List<User> friendList = null;
            try {
                friendList = Arrays.asList(client.getAllFriends(userId));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return friendList;
        };
        Future<List<User>> userFuture = executor.submit(task);

        List<User> friendList = null;
        try {
            friendList = userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();e.printStackTrace();
        }

        FriendsListSpinnerAdapter friendListAdapter = new FriendsListSpinnerAdapter(getApplicationContext(), friendList);
        friendsListSpinner.setAdapter(friendListAdapter);
        friendsListSpinner.setOnItemSelectedListener(friendListSpinnerListener);

    }


    AdapterView.OnItemSelectedListener friendListSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            selectedUser = (User) friendsListSpinner.getSelectedItem();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    //TODO Get friends from client, populate first spinner, get activities enum and populate second spinner like creating status, enable done button top right 

}

