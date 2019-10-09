package se.ju.students.axam1798.andromeda.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import se.ju.students.axam1798.andromeda.models.Event;
import se.ju.students.axam1798.andromeda.models.User;

public interface APIService {

    @GET("users")
    Call<List<User>> getUsers();

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int id);

    @GET("users")
    Call<User> getUserByRfid(@Query("rfid") String rfid);

    @POST("users")
    Call<User> createUser(@Body User user);

    @POST("events")
    Call<Event> createEvent(@Body Event event);

    @GET("events")
    Call<List<Event>> getEventsByKey(@Query("eventKey") int eventKey, @Query("userId") int userId);
}
