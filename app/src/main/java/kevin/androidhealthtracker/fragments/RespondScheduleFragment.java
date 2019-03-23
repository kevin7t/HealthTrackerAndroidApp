package kevin.androidhealthtracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import org.springframework.web.client.RestClientException;

import kevin.androidhealthtracker.MainActivity;
import kevin.androidhealthtracker.R;
import kevin.androidhealthtracker.WebClient;

public class RespondScheduleFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Accept?");
        builder.setMessage("Accept schedule request");

        Bundle mArgs = getArguments();
        int scheduleId = mArgs.getInt("scheduleId");

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AcceptScheduleTask acceptScheduleTask = new AcceptScheduleTask(scheduleId);
                acceptScheduleTask.execute();
            }
        });
        builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeclineScheduleTask declineScheduleTask = new DeclineScheduleTask(scheduleId);
                declineScheduleTask.execute();
            }
        });
        return builder.create();

    }

    public class AcceptScheduleTask extends AsyncTask<Void, Void, Boolean> {
        private int scheduleId;
        WebClient client = MainActivity.client;

        AcceptScheduleTask(int scheduleId) {
            this.scheduleId = scheduleId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                client.acceptSchedule(scheduleId);
            } catch (RestClientException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            }
        }
    }

    public class DeclineScheduleTask extends AsyncTask<Void, Void, Boolean> {
        private int scheduleId;
        WebClient client = MainActivity.client;


        DeclineScheduleTask(int scheduleId) {
            this.scheduleId = scheduleId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                client.declineSchedule(scheduleId);
            } catch (RestClientException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
            }
        }
    }
}
