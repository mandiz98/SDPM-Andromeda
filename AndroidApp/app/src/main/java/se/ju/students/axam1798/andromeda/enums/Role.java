package se.ju.students.axam1798.andromeda.enums;

public enum Role {
    UNKNOWN(0),
    TECHNICIAN(1),
    MANAGER(2);

    public int id;

    private Role(int id) {
        this.id = id;
    }

    public static Role fromId(int roleId) {
        for (Role role: values()) {
            if(role.id == roleId)
                return role;
        }
        return Role.UNKNOWN;
    }
}
