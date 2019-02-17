package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.StatusType;
import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.RestClientException;

public class NewStatusActivity extends AppCompatActivity {

    public static SharedPreferences prefs;
    private WebClient client;
    private PostStatusTask postStatusTask = null;
    private int userId;
    private int score = 50;
    private String userName;
    private TextView statusTextview, usernameTextview;
    private Spinner statusTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create New Status");
        setSupportActionBar(toolbar);
        client = MainActivity.client;
        prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        usernameTextview = findViewById(R.id.userNameTextView);
        statusTextview = findViewById(R.id.statusContentTextView);
        statusTypeSpinner = findViewById(R.id.statusTypeSpinner);

        usernameTextview.setText("Username: " + userName);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        statusTypeSpinner.setOnItemSelectedListener(spinnerListener);
        statusTypeSpinner.setAdapter(adapter);
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

    private void postStatus() {
        StatusDTO statusDTO = new StatusDTO();
        statusDTO.setUserId(userId);
        statusDTO.setContent(statusTextview.getText().toString());
        statusDTO.setType(getTypeFromStatus());
        postStatusTask = new PostStatusTask(statusDTO);
        postStatusTask.execute();
    }

    private StatusType getTypeFromStatus() {
        String type = statusTypeSpinner.getSelectedItem().toString();
        StatusType statusType;
        switch (type) {
            case "Walk":
                statusType = StatusType.WALK;
                score = 10;
                break;
            case "Run":
                statusType = StatusType.RUN;
                score = 20;
                break;
            case "Sprint":
                statusType = StatusType.SPRINT;
                score = 30;
                break;
            case "Swim":
                statusType = StatusType.SWIM;
                score = 30;
                break;
            case "Bike":
                statusType = StatusType.BIKE;
                score = 30;
                break;
            case "Row":
                statusType = StatusType.ROW;
                score = 30;
                break;
            case "Weights":
                statusType = StatusType.WEIGHT;
                score = 30;
                break;


            default:
                return StatusType.WEIGHT;
        }
        return statusType;
    }

    /**
     * Inflate top menu to allow buttons to be shown
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_status, menu);
        return true;
    }

    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            try {
                postStatus();
                Toast.makeText(this, "Status posted", Toast.LENGTH_LONG).show();
                finish();
            } catch (RestClientException e) {
                e.printStackTrace();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class PostStatusTask extends AsyncTask<Void, Void, Boolean> {
        private StatusDTO status;

        PostStatusTask(StatusDTO status) {
            this.status = status;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                client.createStatus(status);
                client.increaseScore(userId, score);
            } catch (RestClientException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                finish();
            }
        }
    }

}
