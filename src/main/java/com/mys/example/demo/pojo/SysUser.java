package com.mys.example.demo.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * sys_user
 */
@Data
public class SysUser implements Serializable {
    private String userid;

    private String logname;

    private String logpwd;

    private String empno;

    private String empnm;

    private String email;

    private String deptid;

    private String bfdeptid;

    private LocalDateTime regtime;

    private String lastmodiempno;

    private LocalDateTime lastmoditime;

    private LocalDateTime lastlogintime;

    private Integer logincnt;

    private String activeflag;

    private String lid;

    private String werks;

    private String firm;

    private static final long serialVersionUID = 1L;
}