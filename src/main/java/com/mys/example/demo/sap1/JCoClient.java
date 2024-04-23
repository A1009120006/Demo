package com.mys.example.demo.sap1;

import com.mys.example.demo.sap.MysDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

import java.util.Properties;

/**
 * FileName: DefaultJCoClient
 * @Author:  Miao
 * Date:    2024/3/13:15:03
 * Description: Deafault implement of {@link JCoClient}
 */
public class JCoClient{

    private final String defaultKey = "MYS_SAP";

    public JCoClient(String destinationName, Properties settings) {
        initJCoConnection(destinationName, settings);
    }

    public JCoClient(Properties settings) {
        initJCoConnection(defaultKey, settings);
    }
 
    /**
     * 创建client
     * @param settings {@link JCoSettings}
     */
    public static void initJCoConnection(String destinationName, Properties settings)  {
        try {
            // register client properties.
            MysDestinationDataProvider.getInstance().addDestination(destinationName, settings);
            //ping test
            JCoDestinationManager
                    .getDestination(destinationName)
                    .ping();
        } catch (JCoException e) {
            throw new RuntimeException("Unable to create：["+destinationName+"]",e);
        }
 
    }
    /**
     * get Jco destination
     * @return The destination {@link JCoDestination}
     */
 
    public JCoDestination getDestination()  {
        try {
            return JCoDestinationManager
                    .getDestination(defaultKey);
        }catch (JCoException ex) {
            throw new RuntimeException(ex);
        }
    }
 
}