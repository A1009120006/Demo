package com.mys.example.demo.sap;

import com.alibaba.fastjson.JSONObject;
import com.sap.conn.jco.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileName: DefaultJCoClient
 * @Author:  Miao
 * Date:    2024/3/13:15:03
 * Description: Deafault implement of {@link JCoClient}
 */
public class JCoClient{

    public static List<Map<String, Object>> callFunction(String functionName, String tableName, JSONObject param){
        JCoFunction function = null;
        JCoDestination destination = null;
        int result = 0;//调用接口返回状态
        String message = "";//调用接口返回信息
        try {
            destination = JCoDestinationManager.getDestination(MysDestinationDataProvider.DEFAULT_DESTINATION_NAME);
            function = destination.getRepository().getFunctionTemplate(functionName).getFunction();
            JCoParameterList input = function.getImportParameterList();
            //工单号
            param.forEach(input::setValue);
            function.execute(destination);
//                result= function.getExportParameterList().getInt("RESULT");//调用接口返回结果
//                message= function.getExportParameterList().getString("MSG");//调用接口返回信息
            JCoParameterList tableParameterList = function.getTableParameterList();
            JCoTable table = tableParameterList.getTable(tableName);
            // 获取列头
            JCoMetaData metaData = table.getMetaData();
            int columnCount = metaData.getFieldCount();
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (int rowIndex = 0; rowIndex < table.getNumRows(); rowIndex++) {
                table.setRow(rowIndex);
                Map<String, Object> rowMap = new HashMap<>();
                for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                    String colName = metaData.getName(colIndex);
                    int colType = metaData.getType(colIndex);
                    switch (colType) {
                        case JCoMetaData.TYPE_INT:
                        case JCoMetaData.TYPE_INT1:
                        case JCoMetaData.TYPE_INT2:
                            // int类型
                            rowMap.put(colName, table.getInt(colName));
                            break;
                        case JCoMetaData.TYPE_NUM:
                        case JCoMetaData.TYPE_FLOAT:
                        case JCoMetaData.TYPE_BCD: //Binary Coded Decimal 二进制编码小数
                            // double类型
                            rowMap.put(colName, table.getDouble(colName));
                            break;
                        case JCoMetaData.TYPE_DATE:
                        case JCoMetaData.TYPE_TIME:
                            //date类型
                            rowMap.put(colName, table.getDate(colName));
                            break;
                        default:
                            //其他类型都转为string，后续根据需要分开
                            rowMap.put(colName, table.getString(colName));
                    }
                    System.out.println(colName + ":" + table.getString(colName));
                }
                dataList.add(rowMap);
            }
            System.out.println("调用返回结果--->" + result + ";调用返回状态--->" + message);
            return dataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
 
}