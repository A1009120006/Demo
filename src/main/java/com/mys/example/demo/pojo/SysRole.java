package com.mys.example.demo.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * sys_role
 */
@Data
public class SysRole implements Serializable {
    private String roleid;

    private String roleno;

    private String rolenm;

    private String roledesc;

    private String addempno;

    private LocalDateTime addtime;

    private String lastmodiempno;

    private LocalDateTime lastmoditime;

    private Integer seqno;

    private String activeflag;

    private String companyid;

    private String isadmin;

    private static final long serialVersionUID = 1L;
}