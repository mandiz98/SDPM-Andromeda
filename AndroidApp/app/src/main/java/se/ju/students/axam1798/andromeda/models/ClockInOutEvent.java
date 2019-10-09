package se.ju.students.axam1798.andromeda.models;

import com.google.gson.annotations.SerializedName;

public class ClockInOutEvent {

    @SerializedName("id")
    private int m_id;

    @SerializedName("eventId")
    private int m_eventId;

    @SerializedName("clockedIn")
    private boolean m_clockedIn;

    public boolean wasClockedIn() {
        return m_clockedIn;
    }
}
