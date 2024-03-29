package br.com.rrossi.proxy.filter;

import br.com.rrossi.proxy.client.ApiClient;
import br.com.rrossi.proxy.model.ApiStatisticModel;
import br.com.rrossi.proxy.util.ApiUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.opentracing.Traced;
import org.jboss.resteasy.core.interception.jaxrs.ContainerResponseContextImpl;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */

@Slf4j
@Provider
@Priority(Priorities.USER + 1)
@SessionScoped
public class LoggingHttpFilter implements ContainerRequestFilter, ContainerResponseFilter, Serializable {
    private static final String INITIAL_TIME = "initialTime";
    private static final String FINISH_TIME = "finishTime";
    private static final String METHOD = "x-method";
    private static final String PATH = "x-path";

    private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;
    private static final int maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Context
    HttpServerResponse response;

    @Inject
    ApiClient apiClient;

    @ConfigProperty(name = "proxy.http.statistic.enable", defaultValue = "true")
    boolean statisticEnable;

    @ConfigProperty(name = "proxy.http.statistic.url", defaultValue = "http://statistic-analyzer:8080/api-info")
    String statisticUrl;

    @Override
    @Traced
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        MDC.put(INITIAL_TIME, String.valueOf(System.currentTimeMillis()));
        MDC.put(METHOD, containerRequestContext.getMethod());
        MDC.put(PATH, ApiUtils.getBasePath(info));

        String requestBody = null;

        if (containerRequestContext.hasEntity()) {
            requestBody = IOUtils.toString(containerRequestContext.getEntityStream(), StandardCharsets.UTF_8);

            InputStream in = IOUtils.toInputStream(requestBody, StandardCharsets.UTF_8);
            containerRequestContext.setEntityStream(in);

            final StringBuilder body = new StringBuilder();

            containerRequestContext.setEntityStream(
                    this.logInboundEntity(body, containerRequestContext.getEntityStream()));

            log.info("{}", body);
        }

        log.info("{} {} | {} | {} | {} | {} | Request received", request.method(), ApiUtils.getBasePath(info), requestBody, request.params().names(),
                request.query(), request.headers().entries());
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        if (MDC.get(INITIAL_TIME) == null)
            MDC.put(INITIAL_TIME, String.valueOf(System.currentTimeMillis()));

        BuiltResponse jaxrsResponse = ((ContainerResponseContextImpl) containerResponseContext).getJaxrsResponse();

        MDC.put(FINISH_TIME, String.valueOf(System.currentTimeMillis() - Long.parseLong(MDC.get(INITIAL_TIME))));

        log.info("{} | {} | {} | Response sended", jaxrsResponse.getEntity(), response.headers().names(), Long.parseLong(MDC.get(FINISH_TIME)));

        sendApiStatistics(jaxrsResponse.getStatus(), jaxrsResponse.getEntity().toString());
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), StandardCharsets.UTF_8));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }

    private void sendApiStatistics(int statusCode, String responseBody) {
        if (!statisticEnable)
            return;

        ApiStatisticModel model = new ApiStatisticModel();

        model.setBasePath(ApiUtils.getBasePath(info));

        String authToken = request.getHeader("Authorization");

        String appId = ApiUtils.recoverInfoFromAuthToken(authToken, ApiUtils.AuthType.APP_ID);
        String userId = ApiUtils.recoverInfoFromAuthToken(authToken, ApiUtils.AuthType.USER_ID);

        model.setAppId(appId);
        model.setUserId(userId);
        model.setResponseCode(statusCode);
        model.setResponseTime(Long.parseLong(MDC.get(FINISH_TIME)));
        model.setContentLength(responseBody.length());

        String modelJson = JsonbBuilder.create().toJson(model);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(statisticUrl))
                .POST(HttpRequest.BodyPublishers.ofString(modelJson))
                .build();

        apiClient.sendStatisticAsync(httpRequest, modelJson, HttpResponse.BodyHandlers.ofString());
    }
}
