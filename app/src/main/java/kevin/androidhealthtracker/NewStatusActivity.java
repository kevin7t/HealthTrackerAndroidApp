package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kevin.healthtracker.datamodels.StatusType;
import com.kevin.healthtracker.datamodels.User;
import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.RestClientException;

import java.lang.reflect.Array;

public class NewStatusActivity extends AppCompatActivity {

    public static SharedPreferences prefs;
    private WebClient client;
    private PostStatusTask postStatusTask = null;
    private int userId;
    private String userName;
    private TextView statusTextview;
    private TextView usernameTextview;
    private Spinner statusTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = MainActivity.client;
        prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        usernameTextview = findViewById(R.id.userNameTextView);
        statusTextview = findViewById(R.id.statusContentTextView);
        statusTypeSpinner = findViewById(R.id.statusTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.status_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        statusTypeSpinner.setAdapter(adapter);
        //TODO Set username but it comes up as null
    }

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
                break;
            case "Run":
                statusType = StatusType.RUNN;
                break;
            case "Sprint":
                statusType =  StatusType.SPRINT;
            break;
            case "Swim":
                statusType = StatusType.SWIM;
            break;
            case "Bike":
                statusType = StatusType.BIKE;
            break;
            case "Row":
                statusType = StatusType.ROW;
            break;
            case "Weights":
                statusType = StatusType.WEIGHT;
            break;


            default:
                return StatusType.WEIGHT;
        }
        return statusType;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_status, menu);
        return true;
    }

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
            } catch (RestClientException e) {
                Toast error = new Toast(NewStatusActivity.this);
                error.makeText(NewStatusActivity.this, e.toString(), Toast.LENGTH_LONG).show();
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
