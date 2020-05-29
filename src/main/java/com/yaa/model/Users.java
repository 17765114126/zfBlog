package com.yaa.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class Users implements Serializable{
    private Integer uid;

    private String username;

    private String password;

    private String email;

    private String homeUrl;

    private String screenName;

    private Integer created;

    private Integer activated;

    private Integer logged;

    private String groupName;
}