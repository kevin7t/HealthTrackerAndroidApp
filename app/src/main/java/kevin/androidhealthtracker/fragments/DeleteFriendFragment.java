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

public class DeleteFriendFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Delete friend?");
        builder.setMessage("Delete friend ");

        Bundle mArgs = getArguments();
        int user1 = mArgs.getInt("user1");
        int user2 = mArgs.getInt("user2");

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeleteFriendTask deleteFriendTask = new DeleteFriendTask(user1, user2);
                deleteFriendTask.execute();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();

    }

    public class DeleteFriendTask extends AsyncTask<Void, Void, Boolean> {
        private int user1, user2;
        WebClient client = MainActivity.client;

        DeleteFriendTask(int user1, int user2) {
            this.user1 = user1;
            this.user2 = user2;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                client.deleteFriend(user1, user2);
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
