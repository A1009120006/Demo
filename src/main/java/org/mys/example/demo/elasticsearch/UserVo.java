package org.mys.example.demo.elasticsearch;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
 
/**
 * @author hulei
 * @date 2023/8/26 18:37
 */
 
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVo extends ESDocument {
 
    private String userName;
 
    private int age;
 
    private String email;
 
    private int version;
 
    private Double height;
 
    private Date createTime;
 
    private Date updateTime;
 
}