package com.br.salesbuddy.model.body;

public class User {
    private int id;
    private String name;
    private String token;

    public User(int id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getToken() { return token; }
}