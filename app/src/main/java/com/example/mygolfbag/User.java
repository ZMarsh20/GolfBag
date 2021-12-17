package com.example.mygolfbag;

import java.io.Serializable;

public class User implements Serializable {
    int id;
    String username, password, name;

    public User(String username, String password, String name, int id) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.id = id;
    }
}
