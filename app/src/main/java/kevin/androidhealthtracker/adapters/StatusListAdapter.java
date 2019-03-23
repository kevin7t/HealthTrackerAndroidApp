package kevin.androidhealthtracker.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.dto.LikeDTO;
import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.ResourceAccessException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.ReplyActivity;
import kevin.androidhealthtracker.WebClient;

public class StatusListAdapter extends ArrayAdapter<StatusDTO> {

    private Activity context;
    private List<StatusDTO> statusDTOList;
    protected WebClient client;
    protected SharedPreferences prefs;
    private TextView likeTextView;
    private TextView likeCountTextView;

    public StatusListAdapter(@NonNull Activity context, List<StatusDTO> statusDTO) {
        super(context, R.layout.status_listview_item, statusDTO);
        this.statusDTOList = statusDTO;
        this.context = context;
        client = MainActivity.client;
        prefs = MainActivity.prefs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        convertView = layoutInflater.inflate(R.layout.status_listview_item, null, true);

        ImageView profilePicture = convertView.findViewById(R.id.userProfilePicture);
        TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusContentTextView);
        TextView dateTimeTextView = convertView.findViewById(R.id.statusDateTimeView);
        likeTextView = convertView.findViewById(R.id.likeText);
        likeCountTextView = convertView.findViewById(R.id.likeCountView);
        TextView replyTextView = convertView.findViewById(R.id.replyTextView);
        TextView replyCountTextView = convertView.findViewById(R.id.replyCountView);

        StatusDTO statusDTO = statusDTOList.get(position);
        switch (statusDTOList.get(position).getType()) {
            case WALK:
                profilePicture.setImageResource(R.drawable.ic_walkrunsprint);
                break;
            case RUN:
                profilePicture.setImageResource(R.drawable.ic_walkrunsprint);
                break;
            case SPRINT:
                profilePicture.setImageResource(R.drawable.ic_walkrunsprint);
                break;
            case SWIM:
                profilePicture.setImageResource(R.drawable.ic_swim);
                break;
            case BIKE:
                profilePicture.setImageResource(R.drawable.ic_bike);
                break;
            case ROW:
                profilePicture.setImageResource(R.drawable.ic_rowing);
                break;
            case WEIGHT:
                profilePicture.setImageResource(R.drawable.ic_weights);
                break;
        }
        userNameTextView.setText(statusDTO.getUserName());
        Date date = statusDTO.getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateTimeTextView.setText(dateFormat.format(date));
        statusTextView.setText(statusDTO.getContent());
        likeCountTextView.setText(String.valueOf(statusDTO.getLikeCount()));
        replyCountTextView.setText(String.valueOf(statusDTO.getReplyCount()));

        likeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeTask likeTask = new LikeTask(statusDTO.getId());
                likeTask.execute();
            }
        });

        replyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReplyActivity.class);
                intent.putExtra("statusId", statusDTO.getId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }


    private class LikeTask extends AsyncTask<Void, Void, Boolean> {
        private int statusId;

        LikeTask(int statusId) {
            this.statusId = statusId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                LikeDTO likeDTO = new LikeDTO();
                likeDTO.setStatusId(statusId);
                likeDTO.setUserId(prefs.getInt("userId", 0));
                Arrays.asList(client.addLike(likeDTO));
            } catch (ResourceAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            likeTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            likeCountTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }


}
