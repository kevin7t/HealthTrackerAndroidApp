package kevin.androidhealthtracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.kevin.healthtracker.datamodels.dto.ReplyDTO;
import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import org.springframework.web.client.ResourceAccessException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.ReplyActivity;
import kevin.androidhealthtracker.WebClient;

public class ReplyListAdapter extends ArrayAdapter<ReplyDTO> {

    private Context context;
    private List<ReplyDTO> replyDTOList;
    protected WebClient client;
    protected SharedPreferences prefs;
    public ReplyListAdapter(@NonNull Context context, List<ReplyDTO> replyDTO) {
        super(context, R.layout.reply_listview_item, replyDTO);
        this.replyDTOList = replyDTO;
        this.context = context;
        client = MainActivity.client;
        prefs = MainActivity.prefs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.reply_listview_item, null, true);

        TextView userNameTextView = convertView.findViewById(R.id.replyUserNameTextView);
        TextView replyTextView = convertView.findViewById(R.id.replyContextTextView);
        TextView dateTimeTextView = convertView.findViewById(R.id.replyDateTimeView);

        userNameTextView.setText(replyDTOList.get(position).getUsername());
        replyTextView.setText(replyDTOList.get(position).getContent());
        Date date = replyDTOList.get(position).getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateTimeTextView.setText(dateFormat.format(date));
        return convertView;
    }}

