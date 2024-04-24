package com.mys.example.demo.sap;

import com.alibaba.fastjson.JSONObject;
import com.sap.conn.jco.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Controller
public class BasicController {

    // ZGET_MO_DETAIL&tableName=IT_MO&I_MO=1120083975
    // http://localhost:8080/executeFunction?ZGET_MO_DETAIL&tableName=IT_MO&I_MO=1120083975
    @RequestMapping("/executeFunction")
    @ResponseBody
    public Object executeFunction(@RequestParam(name = "functionName", defaultValue = "defaultValue") String functionName,
                                  @RequestParam(name = "tableName", defaultValue = "defaultValue") String tableName,
                                  @RequestParam(name = "I_MO", defaultValue = "defaultValue") String I_MO) throws JCoException {

        JSONObject param = new JSONObject();
        param.put("I_MO", I_MO);
        // functionName=ZGET_MO_DETAIL&tableName=IT_MO&I_MO=1120083975
        Object result = JCoClient.callFunction(functionName,tableName, param);

        return result;
    }
 
}