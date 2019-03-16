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

import java.text.SimpleDateFormat;
import java.util.Date;
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
        //TODO Change profile picture depending on the status type
        TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusContentTextView);
        TextView dateTimeTextView = convertView.findViewById(R.id.statusDateTimeView);

        StatusDTO statusDTO = statusDTOList.get(position);
        switch (statusDTOList.get(position).getType()){
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
                break;
        }
        userNameTextView.setText("User: " + statusDTO.getUserName());
        Date date = statusDTO.getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateTimeTextView.setText(statusDTO.getType() + ": " + dateFormat.format(date));
        statusTextView.setText( statusDTO.getContent());
        return convertView;
    }

}
