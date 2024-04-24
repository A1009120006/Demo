package com.mys.example.demo.sap;

import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class SapConfiguration {
    @Autowired
    SapConnPropConfig sapConnPropConfig;

    @PostConstruct
    public void initJCoClient(){

        Properties mysConnectProperties = new Properties();
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConnPropConfig.getAshost());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sapConnPropConfig.getSysnr());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapConnPropConfig.getClient());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_USER, sapConnPropConfig.getUser());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConnPropConfig.getPassword());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_LANG, sapConnPropConfig.getLanguage());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, sapConnPropConfig.getPeakLimit());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, sapConnPropConfig.getPoolCapacity());

        try {
            MysDestinationDataProvider mysProvider = MysDestinationDataProvider.getInstance();
            // Register the MyDestinationDataProvider 环境注册
            Environment.registerDestinationDataProvider(mysProvider);
            // register client properties.
            mysProvider.addDestination(mysConnectProperties);
            //ping test
            JCoDestinationManager
                    .getDestination(MysDestinationDataProvider.DEFAULT_DESTINATION_NAME)
                    .ping();
        } catch (JCoException e) {
            throw new RuntimeException("Unable to create default destination",e);
        }
    }

}
