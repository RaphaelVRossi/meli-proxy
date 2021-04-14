package br.com.rrossi.proxy.model;

import lombok.Data;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@Data
public class ApiStatisticModel {
    private String id = UUID.randomUUID().toString();
    public static String ID = "id";

    private String basePath;
    public static String BASE_PATH = "basePath";

    private Long contentLength;
    public static String CONTENT_LENGTH = "contentLength";

    private Float responseTime;
    public static String RESPONSE_TIME = "responseTime";

    private Integer responseCode;
    public static String RESPONSE_CODE = "responseCode";

    private String appId;
    public static String APP_ID = "appId";

    private String userId;
    public static String USER_ID = "userId";

    private Long count;
    public static String COUNT = "count";

    public ApiStatisticModel() {
    }

    public ApiStatisticModel(Document document) {
        if (document.containsKey(ID))
            this.setId(document.get(ID).toString());
        if (document.containsKey(BASE_PATH))
            this.setBasePath(document.get(BASE_PATH).toString());
        if (document.containsKey(CONTENT_LENGTH))
            this.setContentLength(Long.valueOf(document.get(CONTENT_LENGTH).toString()));
        if (document.containsKey(RESPONSE_TIME))
            this.setResponseTime(Float.valueOf(document.get(RESPONSE_TIME).toString()));
        if (document.containsKey(RESPONSE_CODE))
            this.setResponseCode(Integer.valueOf(document.get(RESPONSE_CODE).toString()));
        if (document.containsKey(APP_ID))
            this.setAppId(document.get(APP_ID).toString());
        if (document.containsKey(USER_ID))
            this.setUserId(document.get(USER_ID).toString());
        if (document.containsKey(COUNT))
            this.setCount(Long.valueOf(document.get(COUNT).toString()));
    }
}
