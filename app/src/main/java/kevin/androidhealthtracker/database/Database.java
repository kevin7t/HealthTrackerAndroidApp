package kevin.androidhealthtracker.database;

import android.arch.persistence.room.RoomDatabase;

import kevin.androidhealthtracker.DAO.DailyCaloriesDAO;
import kevin.androidhealthtracker.DAO.WeightDAO;
import kevin.androidhealthtracker.datamodels.DailyCalories;
import kevin.androidhealthtracker.datamodels.Weight;

@android.arch.persistence.room.Database(entities = {DailyCalories.class, Weight.class}, version = 1)
public abstract class Database extends RoomDatabase {
    public abstract DailyCaloriesDAO dailyCaloriesDAO();

    public abstract WeightDAO weightDAO();

}
