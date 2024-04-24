package com.mys.example.demo.sap;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class MysDestinationDataProvider implements DestinationDataProvider {

    public static final String DEFAULT_DESTINATION_NAME = "MYS_SAP";
    private DestinationDataEventListener destinationDataEventListener;
    private HashMap<String, Properties> destinations;

    private static MysDestinationDataProvider mysDestinationDataProvider = new MysDestinationDataProvider();

    private MysDestinationDataProvider() {// 单例模式
        if (mysDestinationDataProvider == null) {
            destinations = new HashMap<>();
        }
    }

    public static MysDestinationDataProvider getInstance() {
        return mysDestinationDataProvider;
    }



    // 实现接口：获取连接配置属性
    public Properties getDestinationProperties(String destinationName) {

        if (destinations.containsKey(destinationName)) {
            return destinations.get(destinationName);
        } else {
            throw new RuntimeException("Destination " + destinationName + " is not available");
        }
    }

    public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
        this.destinationDataEventListener = eventListener;
    }

    public boolean supportsEvents() {
        return true;
    }

    /**
     * Add new destination 添加连接配置属性
     *
     * @param properties
     *            holds all the required data for a destination
     **/
    public void addDestination(String destinationName, Properties properties) {
        synchronized (destinations) {
            destinations.put(destinationName, properties);
        }
    }
    public void addDestination(Properties properties) {
        addDestination(DEFAULT_DESTINATION_NAME, properties);
    }

}
