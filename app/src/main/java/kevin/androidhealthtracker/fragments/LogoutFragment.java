package kevin.androidhealthtracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import kevin.androidhealthtracker.R;


public class LogoutFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle("Log out");
        builder.setMessage("Log out from app");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE).edit();
                editor.remove("userId");
                editor.remove("userName");
                editor.putBoolean("loggedIn", false);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return builder.create();
    }
}
