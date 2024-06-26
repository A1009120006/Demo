package com.mys.example.demo.activiti.task;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LogServiceTask implements JavaDelegate {

  public void execute(DelegateExecution execution) {
    Map<String, Object> vars = execution.getVariables();;
    log.info(execution.getCurrentFlowElement().getName()+ "_" + execution.getCurrentActivityId()+ "_" + execution.getEventName() + "vars = " + JSON.toJSONString(vars));
  }

}