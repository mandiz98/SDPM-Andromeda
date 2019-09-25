package se.ju.students.axam1798.andromeda.API;

public class APIError {

    private String name;
    private int status;
    private String message;

    public APIError(String name, int status, String message) {
        this.name = name;
        this.status = status;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
