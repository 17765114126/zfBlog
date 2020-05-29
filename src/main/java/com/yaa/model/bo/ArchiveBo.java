package com.yaa.model.bo;


import com.yaa.model.Contents;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class ArchiveBo implements Serializable {

    private String date;
    private String count;
    private List<Contents> articles;
}
