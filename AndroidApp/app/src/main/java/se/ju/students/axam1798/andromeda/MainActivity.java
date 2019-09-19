package se.ju.students.axam1798.andromeda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import se.ju.students.axam1798.andromeda.API.APIClient;
import se.ju.students.axam1798.andromeda.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Remove
         * Example code to make an API call
        User user = new User(0, UUID.randomUUID().toString(), false, false);
        APIClient.getInstance().createUser(user, new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null)
                    Toast.makeText(getApplicationContext(), response.body().getRFID(), Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getApplicationContext(), APIClient.decodeError(response.errorBody()).getMessage() + "<- error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
         */
    }
}
