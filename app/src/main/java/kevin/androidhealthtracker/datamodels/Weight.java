package kevin.androidhealthtracker.datamodels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "weight_table")
public class Weight {

    @Ignore
    public Weight(String date){
        this.date = date;
        weight = 0.0f;
    }

    @PrimaryKey
    @ColumnInfo(name = "date")
    @NonNull
    private String date;

    @ColumnInfo(name = "weight")
    private Float weight;

 }
