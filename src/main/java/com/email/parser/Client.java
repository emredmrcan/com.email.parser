package com.email.parser;

import lombok.Data;

@Data
public class Client {
    String name;
    String email;
    String tel;
    String city;
    String applicationSource;

    private Client(String name, String email, String tel, String city, String applicationSource) {
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.city = city;
        this.applicationSource = applicationSource;
    }

    public static Client from(String plainContent) {
        String[] split = plainContent.split(":");
        return new Client(getCleanText(split[1]),
                getCleanText(split[2]),
                getCleanText(split[3]),
                getCleanText(split[4]),
                getCleanText(split[5]));
    }

    private static String getCleanText(String s) {
        return s.trim().split(System.lineSeparator())[0];
    }
}
