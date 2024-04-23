package com.mys.example.demo.sap1;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.mys.example.demo.sap1.SapConnPropConfig.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
@Component
@Data
public class SapConnPropConfig {

    public static final String PREFIX = "sap";

    private String ashost;
    private String sysnr;
    private String client;
    private String user;
    private String password;
    private String language;
    private String poolCapacity;
    private String peakLimit;
    private String mshost;
    private String group;
    private String r3name;
    private String msserv;

}
