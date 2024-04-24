package com.mys.example.demo.sap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JCoSettings implements Serializable {

    @Builder.Default private final String defaultKey = "MYS_SAP";

    @Builder.Default private String ashost = "";
    @Builder.Default private String sysnr = "";
    @Builder.Default private String client = "";
    @Builder.Default private String user = "";
    @Builder.Default private String password = "";
    @Builder.Default private String language = "";
    @Builder.Default private String poolCapacity = "";
    @Builder.Default private String peakLimit = "";
    @Builder.Default private String mshost = "";
    @Builder.Default private String r3name = "";
    @Builder.Default private String group = "";
    @Builder.Default private String msserv = "";


}
