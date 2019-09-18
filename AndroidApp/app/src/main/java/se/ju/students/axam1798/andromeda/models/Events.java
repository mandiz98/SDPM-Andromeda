package se.ju.students.axam1798.andromeda.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "events")
public class Events {

    @DatabaseField(id = true)
    private int id;

    // Relation to a user object. However, the userId is what is stored.
    @DatabaseField(columnName = "userId", foreign = true)
    private User user;

    // TODO: Should be a relation to a "EventKey" object (?)
    @DatabaseField()
    private String eventKey;

    // TODO: Description should be inside the "EventKey" object (?)
    @DatabaseField()
    private String description;

    @DatabaseField()
    private Date dateCreated;
}
