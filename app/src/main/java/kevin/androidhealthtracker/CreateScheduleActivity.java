package kevin.androidhealthtracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * A login screen that offers login via email/password.
 */
public class CreateScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
    }

    //TODO Open with friends list view as main fragment in schedule_fragment_container, need fragment manager on create
    //TODO After on click from friends list, swap schedule_fragment_container to a new view with time selector and edit text 

}

