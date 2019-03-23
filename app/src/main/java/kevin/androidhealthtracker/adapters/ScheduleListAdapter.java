package kevin.androidhealthtracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.Schedule;

import java.text.SimpleDateFormat;
import java.util.List;

import kevin.androidhealthtracker.R;


public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private Context context;
    private List<Schedule> scheduleList;
    private int userId;

    public ScheduleListAdapter(@NonNull Context context, List<Schedule> schedules, int userId) {
        super(context, R.layout.all_schedule_listview_item, schedules);
        this.scheduleList = schedules;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.all_schedule_listview_item, null, true);
        TextView scheduleUser = convertView.findViewById(R.id.scheduleUserTextView);
        TextView scheduleDate = convertView.findViewById(R.id.scheduleDateValue);
        TextView scheduleContent = convertView.findViewById(R.id.scheduleContentValue);
        TextView scheduleStatus = convertView.findViewById(R.id.scheduleStatusTextviewValue);

        Schedule schedule = scheduleList.get(position);
        if (schedule.getUser1().getId() == userId && schedule.getUserActionId() == userId) {
            scheduleUser.setText("You have invited " + schedule.getUser2().getUserName());
        }
        if (schedule.getUser2().getId() == userId && schedule.getUserActionId() == userId) {
            scheduleUser.setText("You have invited " + schedule.getUser1().getUserName());
        }

        if (schedule.getUser1().getId() == userId && schedule.getUserActionId() != userId) {
            scheduleUser.setText(schedule.getUser2().getUserName() + " has invited you to workout");
        }

        if (schedule.getUser2().getId() == userId && schedule.getUserActionId() != userId) {
            scheduleUser.setText(schedule.getUser1().getUserName() + " has invited you to workout");
        }

        scheduleDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(scheduleList.get(position).getDateTime()));
        scheduleContent.setText(scheduleList.get(position).getContent());
        scheduleStatus.setText(scheduleList.get(position).getScheduleStatus().toString());
        return convertView;
    }
}
