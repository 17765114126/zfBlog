package com.yaa.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class Comments implements Serializable {

    private Integer coid;

    private Integer cid;

    private Integer created;

    private String author;

    private Integer authorId;

    private Integer ownerId;

    private String mail;

    private String url;

    private String ip;

    private String agent;

    private String type;

    private String status;

    private Integer parent;

    private String content;

}