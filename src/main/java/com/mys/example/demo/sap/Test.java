package com.mys.example.demo.sap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.rt.DefaultParameterList;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) throws JCoException {
        SapConfig sapConfig= new SapConfig();
        sapConfig.initSap();
        String functionName = "ZGET_MO_DETAIL";
        String tableName = "IT_MO";
        JSONObject param = new JSONObject();
        param.put("I_MO", "1120083975");

        JCoTable table = SapUtil.calFunction(functionName, tableName, param);
        System.out.println("=======" + JSON.toJSONString(table));

    }
}
