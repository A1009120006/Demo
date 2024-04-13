package com.mys.example.demo.dao;

import com.mys.example.demo.pojo.SysRole;
import org.springframework.stereotype.Repository;

@Repository
public interface SysRoleDao {
    int insert(SysRole record);

    int insertSelective(SysRole record);
}