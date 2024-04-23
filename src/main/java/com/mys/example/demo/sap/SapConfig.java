package com.mys.example.demo.sap;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import java.util.Properties;

@Configuration
public class SapConfig {

    public static final String MYS_DEFAULT_DESTINATION = "MYS_DESTINATION";

    @PostConstruct
    public void initSap() throws JCoException {
        // 获取单例
        MysDestinationDataProvider myProvider = MysDestinationDataProvider
                .getInstance();

        // Register the MyDestinationDataProvider 环境注册
        Environment.registerDestinationDataProvider(myProvider);

        // 连接池
        Properties mysConnectProperties = getProperties();
        // Add a destination
        myProvider.addDestination(MYS_DEFAULT_DESTINATION, mysConnectProperties);
        // Get a destination with name
        JCoDestination jCoDestination = JCoDestinationManager.getDestination(MYS_DEFAULT_DESTINATION);

        // Test the destination with the name
        jCoDestination.ping();

        System.out.println("======连接成功");

    }

    private static Properties getProperties() {
        Properties mysConnectProperties = new Properties();
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.0.0.25");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_USER, "JOBUSER");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "myssap2018");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");
        mysConnectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        return mysConnectProperties;
    }

    /*
     * 直连测试
     */
//    public static void main(String[] args) throws Exception {
//        // 获取单例
//        MysDestinationDataProvider myProvider = MysDestinationDataProvider
//                .getInstance();
//
//        // Register the MyDestinationDataProvider 环境注册
//        Environment.registerDestinationDataProvider(myProvider);
//
//        // TEST 01：直接测试
//        // ABAP_AS is the test destination name ：ABAP_AS为目标连接属性名（只是逻辑上的命名）
//        System.out.println("Test destination - " + ABAP_AS);
//        Properties connectProperties = new Properties();
//
//        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "10.0.0.25");
//        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
//        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "100");
//        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "JOBUSER");
//        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "myssap2018");
//        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
//
//        // Add a destination
//        myProvider.addDestination(ABAP_AS, connectProperties);
//
//        // Get a destination with the name of "ABAP_AS"
//        JCoDestination DES_ABAP_AS = JCoDestinationManager.getDestination(ABAP_AS);
//
//        // Test the destination with the name of "ABAP_AS"
//        try {
//            DES_ABAP_AS.ping();
//            System.out.println("Destination - " + ABAP_AS + " is ok");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            System.out.println("Destination - " + ABAP_AS + " is invalid");
//        }
//    }

}
