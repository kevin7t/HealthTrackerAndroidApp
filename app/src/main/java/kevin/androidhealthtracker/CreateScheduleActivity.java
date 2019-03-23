package kevin.androidhealthtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.RequestStatus;
import com.kevin.healthtracker.datamodels.Schedule;
import com.kevin.healthtracker.datamodels.User;
import com.kevin.healthtracker.datamodels.dto.ScheduleDTO;

import org.springframework.web.client.RestClientException;

import java.sql.Timestamp;
import java.text.ParseException;
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
    private String activityContent;

    private ExecutorService executor;

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

        executor = Executors.newWorkStealingPool();
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
        activityTypeSpinner.setOnItemSelectedListener(activitySpinnerListener);
        activityTypeSpinner.setAdapter(adapter);

        dateTimeEditText.setOnClickListener(timeListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            try {
                ScheduleDTO newSchedule = new ScheduleDTO();
                newSchedule.setUser1id(userId);
                newSchedule.setUser2id(selectedUser.getId());
                newSchedule.setDateTime(timestamp);
                newSchedule.setScheduleStatus(RequestStatus.PENDING);
                newSchedule.setContent(activityContent);
                newSchedule.setUserActionId(userId);
                if (newSchedule.getUser2id() != 0 &&
                        newSchedule.getDateTime() != null &&
                        newSchedule.getScheduleStatus() != null &&
                        newSchedule.getContent() != null) {
                    Callable<Schedule> task = () -> {
                        Schedule schedule = null;
                        try {
                            schedule = client.addSchedule(newSchedule);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return schedule;
                    };
                    executor.submit(task);
                }

            } catch (RestClientException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Schedule sent", Toast.LENGTH_LONG).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemSelectedListener activitySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
            activityContent = activityTypeSpinner.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    EditText.OnClickListener timeListener = new EditText.OnClickListener() {
        @Override
        public void onClick(View viewOnClick) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(viewOnClick.getContext(), R.style.MyPickerDialogTheme,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            timePicker(viewOnClick, year, monthOfYear, dayOfMonth);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();

        }
    };


    public void timePicker(View view, int year, int monthOfYear, int dayOfMonth) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), R.style.MyPickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @
                    Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:MM");
//                String dateTimeString = dayOfMonth + "-" + monthOfYear + "-" + year + " " + hourOfDay + ":" + minutes;
//
//                Date date = null;
//                try {
//                    date = simpleDateFormat.parse(dateTimeString);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

//                calendar.setTime(date);
                calendar.set(year,monthOfYear,dayOfMonth,hourOfDay,minutes);
                timestamp = new Timestamp(calendar.getTime().getTime());
                dateTimeEditText.setText(timestamp.toString());
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


}

