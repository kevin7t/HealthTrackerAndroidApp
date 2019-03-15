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
import com.kevin.healthtracker.datamodels.User;

import org.w3c.dom.Text;

import java.util.List;

import kevin.androidhealthtracker.R;


public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private Context context;
    private List<Schedule> scheduleList;

    public ScheduleListAdapter(@NonNull Context context, List<Schedule> schedules) {
        super(context, R.layout.all_schedule_listview_item, schedules);
        this.scheduleList = schedules;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.all_schedule_listview_item, null, true);
        TextView scheduleUser1 = convertView.findViewById(R.id.scheduleUserName1);
        TextView scheduleUser2 = convertView.findViewById(R.id.scheduleUserName2);
        TextView scheduleDate = convertView.findViewById(R.id.scheduleDateValue);
        TextView scheduleContent = convertView.findViewById(R.id.scheduleContentValue);
        TextView scheduleStatus = convertView.findViewById(R.id.scheduleStatusTextviewValue);
        scheduleUser1.setText(scheduleList.get(position).getUser1().getUserName());
        scheduleUser2.setText(scheduleList.get(position).getUser2().getUserName());
        scheduleDate.setText(scheduleList.get(position).getDateTime().toString());
        scheduleContent.setText(scheduleList.get(position).getContent());
        scheduleStatus.setText(scheduleList.get(position).getScheduleStatus().toString());
        return convertView;
    }
}
