package com.mys.example.demo.sap;

import com.sap.conn.jco.*;

import java.util.Map;

import static com.mys.example.demo.sap.SapConfig.MYS_DEFAULT_DESTINATION;

public class SapUtil {
    public static JCoTable calFunction(String functionName, String tableName, Map<String, Object> param){
            JCoFunction function = null;
            JCoDestination destination = null;
            int result = 0;//调用接口返回状态
            String message = "";//调用接口返回信息
            try {
                destination = JCoDestinationManager.getDestination(MYS_DEFAULT_DESTINATION);
                function = destination.getRepository().getFunctionTemplate(functionName).getFunction();
                JCoParameterList input = function.getImportParameterList();
                //工单号
                param.forEach(input::setValue);
                function.execute(destination);
//                result= function.getExportParameterList().getInt("RESULT");//调用接口返回结果
//                message= function.getExportParameterList().getString("MSG");//调用接口返回信息
                JCoParameterList tableParameterList = function.getTableParameterList();
                JCoTable table = tableParameterList.getTable(tableName);
                return table;
//                for (int i = 0; i < table.getNumRows(); i++) {
//                    table.setRow(i);
////               System.out.println(table.getString("GERNR01").replaceFirst("^0*", ""));
////               System.out.println("物料号:"+table.getString("MATNR"));
//                    new string[] { "AUFNR", "KUNNR", "KHJC", "KDMAT", "BSTKD", "VERID", "MATNR", "MAKTX", "PSMNG", "MXMNG", "MBMNG", "CZFWX", "CZFWS", "CPGG", "ZXMNG" }
//                    System.out.println("AUFNR:" + table.getString("AUFNR"));
//                    System.out.println("KUNNR:" + table.getString("KUNNR"));
//                    System.out.println("KHJC:" + table.getString("KHJC"));
//                    System.out.println("KDMAT:" + table.getString("KDMAT"));
//                    System.out.println("BSTKD:" + table.getString("BSTKD"));
//                    System.out.println("VERID:" + table.getString("VERID"));
//                    System.out.println("MATNR:" + table.getString("MATNR"));
//                    System.out.println("MAKTX:" + table.getString("MAKTX"));
//                    System.out.println("PSMNG:" + table.getString("PSMNG"));
//                    System.out.println("MXMNG:" + table.getString("MXMNG"));
//                    System.out.println("MBMNG:" + table.getString("MBMNG"));
//                    System.out.println("CZFWX:" + table.getString("CZFWX"));
//                    System.out.println("CZFWS:" + table.getString("CZFWS"));
//                    System.out.println("CPGG:" + table.getString("CPGG"));
//                    System.out.println("ZXMNG:" + table.getString("ZXMNG"));
////               System.out.println(table.getString("IDAT2").equals("0000-00-00"));
//                }
//                System.out.println("调用返回结果--->" + result + ";调用返回状态--->" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }
}
