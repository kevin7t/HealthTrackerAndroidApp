package kevin.androidhealthtracker.datamodels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(tableName = "weight_table")
public class WeightDated {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    private Date date;

    @NonNull
    @ColumnInfo(name = "weight")
    private Float weight;

    //TODO Add this to a table when updating weight
}
