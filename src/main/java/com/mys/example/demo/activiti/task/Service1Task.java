package com.mys.example.demo.activiti.task;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class Service1Task {

  public void execute(String var1) {
    log.info("var1=" + var1);
  }

}