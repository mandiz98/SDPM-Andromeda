package se.ju.students.axam1798.andromeda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.logging.Logger;

import se.ju.students.axam1798.andromeda.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Dao<User, String> userDao = DaoManager.createDao(DatabaseHelper.getConnection(), User.class);
            User user = userDao.queryForId("1");
            System.out.println("asdsadsadsadsadsadsad: " + user.getRFID());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
