package br.com.rrossi.proxy.model;

import lombok.Data;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@Data
public class ApiStatisticModel {
    private String basePath;
    private long contentLength;
    private long responseTime;
    private Integer responseCode;
    private String appId;
    private String userId;
}
