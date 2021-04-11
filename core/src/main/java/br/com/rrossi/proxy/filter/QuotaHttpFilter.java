package br.com.rrossi.proxy.filter;

import br.com.rrossi.proxy.cache.QuotaService;
import br.com.rrossi.proxy.exception.QuotaException;
import br.com.rrossi.proxy.util.ApiUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.mutiny.redis.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.opentracing.Traced;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.*;
import java.nio.charset.StandardCharsets;

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
        Integer callQuantity = quotaService.increment(ipAddress);
        if (callQuantity > 10)
            throw new QuotaException("ipAddress");
    }

    void validatePathQuota(String basePath) {
        Integer callQuantity = quotaService.increment(basePath);
        if (callQuantity > 10)
            throw new QuotaException("basePath");
    }

    void validateIpPathQuota(String ipAddress, String basePath) {
        String key = String.format("%s.%s", ipAddress, basePath);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > 10)
            throw new QuotaException("ipAddress.basePath");
    }

    void validatePathAppIdQuota(String basePath, String appId) {
        if (appId == null)
            return;

        String key = String.format("%s.%s", basePath, appId);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > 10)
            throw new QuotaException("basePath.appId");
    }

    void validatePathUserIdQuota(String basePath, String userId) {
        if (userId == null)
            return;

        String key = String.format("%s.%s", basePath, userId);
        Integer callQuantity = quotaService.increment(key);
        if (callQuantity > 10)
            throw new QuotaException("basePath.userId");
    }
}
