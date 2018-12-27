package kevin.androidhealthtracker.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kevin.healthtracker.datamodels.dto.StatusDTO;

import java.util.List;

import kevin.androidhealthtracker.R;

public class StatusListAdapter extends ArrayAdapter<StatusDTO> {

    private Activity context;
    private List<StatusDTO> statusDTOList;

    public StatusListAdapter(@NonNull Activity context, List<StatusDTO> statusDTO) {
        super(context, R.layout.status_listview_item, statusDTO);
        this.statusDTOList = statusDTO;
        this.context = context;
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

        userNameTextView.setText("UserCalorieProfile: " + statusDTOList.get(position).getUserName());
        dateTimeTextView.setText(statusDTOList.get(position).getCreatedAt().toString());
        statusTextView.setText(statusDTOList.get(position).getContent());
        return convertView;
    }

}
