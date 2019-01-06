package kevin.androidhealthtracker.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

import kevin.androidhealthtracker.datamodels.Weight;

@Dao
public interface WeightDAO {
    @Query("SELECT * FROM weight_table")
    List<Weight> getAll();

    @Query("SELECT * FROM weight_table WHERE date = :date")
    Weight getByDate(Date date);

    @Insert
    void insert(Weight weight);

    @Update
    void update(Weight weight);
}
