package kevin.androidhealthtracker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.kevin.healthtracker.datamodels.Reply;
import com.kevin.healthtracker.datamodels.dto.LikeDTO;
import com.kevin.healthtracker.datamodels.dto.ReplyDTO;

import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kevin.androidhealthtracker.adapters.FriendsListAdapter;
import kevin.androidhealthtracker.adapters.ReplyListAdapter;

public class ReplyActivity extends AppCompatActivity {
    public static SharedPreferences prefs;
    public static WebClient client;
    private Toolbar toolbar;
    private int userId;
    private String userName;
    private ListView replyListview;
    private Button replyButton;
    private EditText replyEditText;
    private int statusId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        client = MainActivity.client;
        prefs = MainActivity.prefs;

        userId = prefs.getInt("userId", 0);
        userName = prefs.getString("userName", null);

        replyListview = findViewById(R.id.replyListView);
        replyButton = findViewById(R.id.replyButton);
        replyEditText = findViewById(R.id.replyEditText);
        statusId = getIntent().getExtras().getInt("statusId");

        refreshReplyList();
        setTitle("Replies");
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendReplyTask sendReplyTask = new SendReplyTask(statusId);
                sendReplyTask.execute();
            }
        });
    }

    private void refreshReplyList() {
        GetReplyTask getReplyTask = new GetReplyTask();
        getReplyTask.execute();
    }

    private class SendReplyTask extends AsyncTask<Void, Void, Boolean> {
        private int statusId;

        SendReplyTask(int statusId) {
            this.statusId = statusId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (replyEditText.length() > 0) {
                    ReplyDTO replyDTO = new ReplyDTO();
                    replyDTO.setStatusId(statusId);
                    replyDTO.setUserId(userId);
                    replyDTO.setUsername(userName);
                    replyDTO.setContent(replyEditText.getText().toString());
                    client.sendReply(replyDTO);
                }

            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            refreshReplyList();
            replyEditText.setText("");
        }
    }

    private class GetReplyTask extends AsyncTask<Void, Void, Boolean> {
        List<ReplyDTO> replyList = new ArrayList<>();

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                replyList = Arrays.asList(client.getReplies(statusId));

            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            ReplyListAdapter replyListAdapter = new ReplyListAdapter(getApplicationContext(), replyList);
            replyListview.setAdapter(replyListAdapter);
        }
    }
}
