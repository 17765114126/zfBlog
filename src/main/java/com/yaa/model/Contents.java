package com.yaa.model;

import lombok.Data;

@Data
public class Contents {
    private Integer cid;

    private String title;

    private String slug;

    private String thumbImg;

    private Integer created;

    private Integer modified;

    private Integer authorId;

    private String type;

    private String status;

    private String fmtType;

    private String tags;

    private String categories;

    private Integer hits;

    private Integer commentsNum;

    private Integer allowComment;

    private Integer allowPing;

    private Integer allowFeed;

    private String content;

    public boolean getComment(){
        return allowComment.equals("0") ? false : true;
    }

}