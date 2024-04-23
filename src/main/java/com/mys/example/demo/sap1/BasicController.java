package com.mys.example.demo.sap1;

import com.sap.conn.jco.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
 
/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Controller
public class BasicController {
    @Autowired
    private JCoClient client;

    // ZGET_MO_DETAIL&tableName=IT_MO&I_MO=1120083975
    // http://localhost:8080/executeFunction?ZGET_MO_DETAIL&tableName=IT_MO&I_MO=1120083975
    @RequestMapping("/executeFunction")
    @ResponseBody
    public String executeFunction(@RequestParam(name = "functionName", defaultValue = "defaultValue") String functionName,
                                  @RequestParam(name = "tableName", defaultValue = "defaultValue") String tableName,
                                  @RequestParam(name = "I_MO", defaultValue = "defaultValue") String I_MO) throws JCoException {
        //1.获取函数RFC函数对象
        JCoFunction function = client.getDestination().getRepository().getFunctionTemplate(functionName).getFunction();
 
        //2.设置输入参数（如果有）
         JCoParameterList importParameterList = function.getImportParameterList();
         importParameterList.setValue("I_MO",I_MO);
 
        //3.调用并获取返回值
        JCoResponse response = new DefaultRequest(function).execute(client.getDestination());
 
        //4.封装返回值
        Map<String, Object> invokeResult  = new HashMap<>();
        for (JCoField jCoField : response) {
            String fieldName = jCoField.getName();
            JCoTable tableValue = function.getTableParameterList().getTable(fieldName);
 
            if(tableValue.isEmpty()){
                invokeResult.put(fieldName,null);
                break;
            }
            ArrayList<Map<String, Object>> resArrayList = new ArrayList<>();
            for (int i = 0; i < tableValue.getNumRows(); i++) {
                tableValue.setRow(i);
                Map<String, Object> tmpMap = new HashMap<>();
                tmpMap.put("AUFNR:", tableValue.getString("AUFNR"));
                tmpMap.put("KUNNR:", tableValue.getString("KUNNR"));
                tmpMap.put("KHJC:", tableValue.getString("KHJC"));
                tmpMap.put("KDMAT:", tableValue.getString("KDMAT"));
                tmpMap.put("BSTKD:", tableValue.getString("BSTKD"));
                tmpMap.put("VERID:", tableValue.getString("VERID"));
                tmpMap.put("MATNR:", tableValue.getString("MATNR"));
                tmpMap.put("PSMNG:", tableValue.getString("PSMNG"));
                tmpMap.put("MXMNG:", tableValue.getString("MXMNG"));
                tmpMap.put("MBMNG:", tableValue.getString("MBMNG"));
                tmpMap.put("CZFWS:", tableValue.getString("CZFWS"));
                tmpMap.put("CZFWX:", tableValue.getString("CZFWX"));
                tmpMap.put("PWERK:", tableValue.getString("PWERK"));
                tmpMap.put("CPGG:", tableValue.getString("CPGG"));
                tmpMap.put("ZXMNG:", tableValue.getString("ZXMNG"));
                resArrayList.add(tmpMap);
            }
            invokeResult.put(fieldName,resArrayList);
        }
        return invokeResult.toString();
    }
 
    static class DefaultRequest extends com.sap.conn.jco.rt.DefaultRequest {
        DefaultRequest(JCoFunction function) {
            super(function);
        }
    }
 
}