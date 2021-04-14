package br.com.rrossi.proxy.filter;

import br.com.rrossi.proxy.cache.QuotaService;
import br.com.rrossi.proxy.exception.QuotaException;
import br.com.rrossi.proxy.util.ApiUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.Serializable;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */

@Slf4j
@Provider
@Priority(Priorities.USER + 2)
@SessionScoped
public class QuotaHttpFilter implements ContainerRequestFilter, ContainerResponseFilter, Serializable {
    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Context
    HttpServerResponse response;

    @Inject
    QuotaService quotaService;

    @ConfigProperty(name = "proxy.http.quota.ip.enable", defaultValue = "true")
    boolean quotaIpEnable;

    @ConfigProperty(name = "proxy.http.quota.ip.max-calls", defaultValue = "10")
    int quotaIpMaxCalls;

    @ConfigProperty(name = "proxy.http.quota.path.enable", defaultValue = "true")
    boolean quotaPathEnable;

    @ConfigProperty(name = "proxy.http.quota.path.max-calls", defaultValue = "10")
    int quotaPathMaxCalls;

    @ConfigProperty(name = "proxy.http.quota.ip-path.enable", defaultValue = "true")
    boolean quotaIpPathEnable;

    @ConfigProperty(name = "proxy.http.quota.ip-path.max-calls", defaultValue = "10")
    int quotaIpPathMaxCalls;

    @ConfigProperty(name = "proxy.http.quota.path-appid.enable", defaultValue = "true")
    boolean quotaPathAppIdEnable;

    @ConfigProperty(name = "proxy.http.quota.path-appid.max-calls", defaultValue = "10")
    int quotaPathAppIdMaxCalls;

    @ConfigProperty(name = "proxy.http.quota.path-userid.enable", defaultValue = "true")
    boolean quotaPathUserIdEnable;

    @ConfigProperty(name = "proxy.http.quota.path-userid.max-calls", defaultValue = "10")
    int quotaPathUserIdMaxCalls;

    @Override
    @Traced
    public void filter(ContainerRequestContext containerRequestContext) {
        String ipAddress = request.remoteAddress().host();
        String basePath = ApiUtils.getBasePath(info);

        String authToken = request.getHeader("Authorization");

        String appId = ApiUtils.recoverInfoFromAuthToken(authToken, ApiUtils.AuthType.APP_ID);
        String userId = ApiUtils.recoverInfoFromAuthToken(authToken, ApiUtils.AuthType.USER_ID);

        validateIpQuota(ipAddress);
        validatePathQuota(basePath);
        validateIpPathQuota(ipAddress, basePath);
        validatePathAppIdQuota(basePath, appId);
        validatePathUserIdQuota(basePath, userId);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        //Pass
    }

    void validateIpQuota(String ipAddress) {
        if (!quotaIpEnable)
            return;

        Integer callQuantity = quotaService.increment(ipAddress);
        if (callQuantity > quotaIpMaxCalls)
            throw new QuotaException("ipAddress");
    }

    void validatePathQuota(String basePath) {
        if (!quotaPathEnable)
            return;

        Integer callQuantity = quotaService.increment(basePath);
        if (callQuantity > quotaPathMaxCalls)
            throw new QuotaException("basePath");
    }

    void validateIpPathQuota(String ipAddress, String basePath) {
        if (!quotaIpPathEnable)
             return;

        String key = String.format("%s.%s", ipAddress, basePath);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > quotaIpPathMaxCalls)
            throw new QuotaException("ipAddress.basePath");
    }

    void validatePathAppIdQuota(String basePath, String appId) {
        if (!quotaPathAppIdEnable || appId == null)
            return;

        String key = String.format("%s.%s", basePath, appId);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > quotaPathAppIdMaxCalls)
            throw new QuotaException("basePath.appId");
    }

    void validatePathUserIdQuota(String basePath, String userId) {
        if (!quotaPathUserIdEnable || userId == null)
            return;

        String key = String.format("%s.%s", basePath, userId);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > quotaPathUserIdMaxCalls)
            throw new QuotaException("basePath.userId");
    }
}
