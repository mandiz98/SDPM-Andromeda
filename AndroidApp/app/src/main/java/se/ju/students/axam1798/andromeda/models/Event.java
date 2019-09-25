package se.ju.students.axam1798.andromeda.models;

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

    public Event(int id, int userId, int eventKey, Date dateCreated) {
        this.m_id = id;
        this.m_userId = userId;
        this.m_eventKey = eventKey;
        this.m_dateCreated = dateCreated;
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
}
