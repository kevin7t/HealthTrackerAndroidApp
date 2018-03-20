package kevin.androidhealthtracker.datamodels;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"userName", "password"})
public class Account {
    String userName;
    String password;

    public Account(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
