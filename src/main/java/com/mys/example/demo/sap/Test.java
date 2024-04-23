package com.mys.example.demo.sap;

import com.alibaba.fastjson.JSON;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws JCoException {
        SapConfig sapConfig= new SapConfig();
        sapConfig.initSap();
        SapUtil sapUtil= new SapUtil();
        String functionName = "ZGET_MO_DETAIL";
        String tableName = "IT_MO";
        Map<String, Object> param = new HashMap<>();
        param.put("I_MO", "1120083975");
        JCoTable table = sapUtil.calFunction(functionName, tableName, param);
        System.out.println("=======" + JSON.toJSONString(table));

    }
}
