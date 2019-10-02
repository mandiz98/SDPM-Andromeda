package se.ju.students.axam1798.andromeda.API;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.ju.students.axam1798.andromeda.models.Event;
import se.ju.students.axam1798.andromeda.models.User;

public class APIClient {

    private static String API_URL = "https://andromeda-rest-api.herokuapp.com";

    private APIService apiService;
    private static APIClient instance;

    private APIClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(APIService.class);
    }

    public static APIClient getInstance() {
        if(instance == null)
            instance = new APIClient();
        return instance;
    }

    public void getUsers(Callback<List<User>> callback) {
        this.apiService.getUsers().enqueue(callback);
    }

    public void getUserById(int id, Callback<User> callback) {
        this.apiService.getUserById(id).enqueue(callback);
    }

    public void getUserByRfid(String rfid, Callback<User> callback) {
        this.apiService.getUserByRfid(rfid).enqueue(callback);
    }

    public void createUser(User user, Callback<User> callback) {
        this.apiService.createUser(user).enqueue(callback);
    }

    public void createEvent(Event event, Callback<Event> callback) {
        this.apiService.createEvent(event).enqueue(callback);
    }

    public static APIError decodeError(ResponseBody responseBody) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseBody.string(), APIError.class);
        } catch (IOException e) {
            return new APIError("Unknown", -1, "Error could not be parsed");
        }
    }
}
