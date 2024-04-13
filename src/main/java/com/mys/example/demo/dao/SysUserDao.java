package com.mys.example.demo.dao;

import com.mys.example.demo.pojo.SysUser;
import org.springframework.stereotype.Repository;

@Repository
public interface SysUserDao {
    int insert(SysUser record);

    int insertSelective(SysUser record);
}