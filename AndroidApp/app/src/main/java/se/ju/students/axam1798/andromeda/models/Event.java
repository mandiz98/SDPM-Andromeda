package se.ju.students.axam1798.andromeda.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Event {

    @SerializedName("id")
    private int m_id;

    @SerializedName("userId")
    private int m_userId;

    @SerializedName("eventKey")
    private int m_eventKey;

    @SerializedName("dateCreated")
    private Date m_dateCreated;

    @SerializedName("data")
    private String m_data;

    @SerializedName("clockInOutEvent")
    private ClockInOutEvent m_clockInOutEvent;

    public Event(int id, int userId, int eventKey, Date dateCreated, String data) {
        this.m_id = id;
        this.m_userId = userId;
        this.m_eventKey = eventKey;
        this.m_dateCreated = dateCreated;
        this.m_data = data;
    }

    public int getId() {
        return m_id;
    }

    public int getUserId() {
        return m_userId;
    }

    public int getEventKey() {
        return m_eventKey;
    }

    public Date getDateCreated() {
        return m_dateCreated;
    }

    public String getData() {
        return m_data;
    }

    public boolean wasClockedIn() {
        if(m_clockInOutEvent != null)
            return m_clockInOutEvent.wasClockedIn();
        return false;
    }
}
