package kevin.androidhealthtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kevin.healthtracker.datamodels.User;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
    private Calendar calendar;
    private Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = MainActivity.client;
        prefs = MainActivity.prefs;
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        friendsListSpinner = findViewById(R.id.friendsListSpinner);
        dateTimeEditText = findViewById(R.id.dateTimeEditText);
        activityTypeSpinner = findViewById(R.id.activitySpinner);

        calendar = Calendar.getInstance();

        ExecutorService executor = Executors.newWorkStealingPool();
        Callable<List<User>> task = () -> {
            List<User> friendList = null;
            try {
                friendList = Arrays.asList(client.getAllFriends(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return friendList;
        };
        Future<List<User>> userFuture = executor.submit(task);

        List<User> friendList = null;
        try {
            friendList = userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

        FriendsListSpinnerAdapter friendListAdapter = new FriendsListSpinnerAdapter(getApplicationContext(), friendList);
        friendsListSpinner.setAdapter(friendListAdapter);
        friendsListSpinner.setOnItemSelectedListener(friendListSpinnerListener);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        activityTypeSpinner.setOnItemSelectedListener(spinnerListener);
        activityTypeSpinner.setAdapter(adapter);

        dateTimeEditText.setOnClickListener(timeListener);
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    EditText.OnClickListener timeListener = new EditText.OnClickListener() {
        @Override
        public void onClick(View viewOnClick) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(viewOnClick.getContext(),R.style.MyPickerDialogTheme,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            timePicker(viewOnClick);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        }
    };



    public void timePicker(View view) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), R.style.MyPickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @
                    Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                timestamp = new Timestamp(calendar.getTime().getTime());
                Date date = new Date();
                date.setTime(timestamp.getTime());
                dateTimeEditText.setText(new SimpleDateFormat("dd-MM-yyyy HH:MM").format(date));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();

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

