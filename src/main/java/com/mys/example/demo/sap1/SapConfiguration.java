package com.mys.example.demo.sap1;

import com.sap.conn.jco.ext.DestinationDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class SapConfiguration {
    @Bean
    public JCoClient jCoClient(SapConnPropConfig sapConnPropConfig){

        Properties mysConnectProperties = new Properties();
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sapConnPropConfig.getAshost());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sapConnPropConfig.getSysnr());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sapConnPropConfig.getClient());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_USER, sapConnPropConfig.getUser());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapConnPropConfig.getPassword());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_LANG, sapConnPropConfig.getLanguage());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, sapConnPropConfig.getPeakLimit());
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, sapConnPropConfig.getPoolCapacity());
        return new JCoClient(mysConnectProperties);
    }

}
