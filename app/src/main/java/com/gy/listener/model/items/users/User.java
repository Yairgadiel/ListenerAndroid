package com.gy.listener.model.items.users;

public class User {

    // region Members

    private String _id;
    private String _name;
    private String _email;

    // endregion

    // region C'tor

    /**
     * Empty default C'tor necessary for firebase object building
     */
    public User() {}

    public User(String id, String name, String email) {
        _id = id;
        _name = name;
        _email = email;
    }


    // endregion

    // region Properties

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getEmail() {
        return _email;
    }

    public void setEmail(String email) {
        _email = email;
    }

    // endregion
}
