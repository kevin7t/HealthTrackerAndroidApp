package kevin.androidhealthtracker.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.provider.ContactsContract;

import kevin.androidhealthtracker.DAO.DailyCaloriesDAO;
import kevin.androidhealthtracker.DAO.UserCalorieProfileDAO;
import kevin.androidhealthtracker.DAO.WeightDAO;
import kevin.androidhealthtracker.datamodels.DailyCalories;
import kevin.androidhealthtracker.datamodels.UserCalorieProfile;
import kevin.androidhealthtracker.datamodels.Weight;

@android.arch.persistence.room.Database(entities = {DailyCalories.class, Weight.class, UserCalorieProfile.class}, version = 2, exportSchema = false)
public abstract class HealthTrackerDatabase extends RoomDatabase {
    public abstract DailyCaloriesDAO dailyCaloriesDAO();

    public abstract WeightDAO weightDAO();

    public abstract UserCalorieProfileDAO userCalorieProfileDAO();

    public static volatile HealthTrackerDatabase INSTANCE;

    static HealthTrackerDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ContactsContract.Data.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            HealthTrackerDatabase.class, "database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}


