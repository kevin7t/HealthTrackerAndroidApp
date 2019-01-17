package kevin.androidhealthtracker.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import kevin.androidhealthtracker.datamodels.Weight;

@Dao
public interface WeightDAO {
    @Query("SELECT * FROM weight_table")
    List<Weight> getAll();

    //TODO Must use type converter to convert date to a string and back
    @Query("SELECT * FROM weight_table WHERE date = :date")
    Weight getByDate(String date);

    @Query("SELECT * FROM weight_table ORDER BY date DESC LIMIT 1")
    Weight getLatest();

    @Insert
    void insert(Weight weight);

    @Update
    void update(Weight weight);
}
