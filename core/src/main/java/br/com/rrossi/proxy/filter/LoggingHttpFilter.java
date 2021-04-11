package br.com.rrossi.proxy.filter;

import br.com.rrossi.proxy.exception.ProxyException;
import io.netty.handler.proxy.ProxyConnectException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.opentracing.Traced;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
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
@Priority(Priorities.USER + 1)
@SessionScoped
public class LoggingHttpFilter implements ContainerRequestFilter, ContainerResponseFilter, Serializable, WriterInterceptor {
    private static final String INITIAL_TIME = "initialTime";
    private static final String METHOD = "x-method";
    private static final String PATH = "x-path";

    private static final int DEFAULT_MAX_ENTITY_SIZE = 8 * 1024;
    private final int maxEntitySize = DEFAULT_MAX_ENTITY_SIZE;
    private static final String ENTITY_LOGGER_PROPERTY = LoggingHttpFilter.class.getName() + ".entityLogger";

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Context
    HttpServerResponse response;

    @Override
    @Traced
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        MDC.put(INITIAL_TIME, String.valueOf(System.currentTimeMillis()));
        MDC.put(METHOD, containerRequestContext.getMethod());
        MDC.put(PATH, info.getPath());

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

        log.info("{} {} | {} | {} | {} | {} | Request received", request.method(), info.getPath(), requestBody, request.params().names(),
                request.query(), request.headers().entries());
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        if (MDC.get(INITIAL_TIME) == null)
            MDC.put(INITIAL_TIME, String.valueOf(System.currentTimeMillis()));

        final StringBuilder b = new StringBuilder();

        if (containerResponseContext.hasEntity()) {
            final OutputStream stream = new LoggingHttpFilter.LoggingStream(b, containerResponseContext.getEntityStream());
            containerResponseContext.setEntityStream(stream);
            containerRequestContext.setProperty(LoggingHttpFilter.ENTITY_LOGGER_PROPERTY, stream);
        }
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }

        stream.mark(this.maxEntitySize + 1);
        final byte[] entity = new byte[this.maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, this.maxEntitySize), StandardCharsets.UTF_8));
        if (entitySize > this.maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        final LoggingHttpFilter.LoggingStream stream = (LoggingHttpFilter.LoggingStream) writerInterceptorContext.getProperty(LoggingHttpFilter.ENTITY_LOGGER_PROPERTY);
        writerInterceptorContext.proceed();
        String body = "-";
        if (stream != null)
            body = stream.getStringBuilder().toString();

        log.info("{} | {} | {} | Response sended", body, response.headers().names(), System.currentTimeMillis() - Long.parseLong(MDC.get(INITIAL_TIME)));
    }

    private class LoggingStream extends FilterOutputStream {

        private final StringBuilder b;

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        LoggingStream(final StringBuilder b, final OutputStream inner) {

            super(inner);

            this.b = b;
        }

        StringBuilder getStringBuilder() {
            final byte[] entity = this.baos.toByteArray();

            this.b.append(new String(entity, 0, Math.min(entity.length, LoggingHttpFilter.this.maxEntitySize), StandardCharsets.UTF_8));
            if (entity.length > LoggingHttpFilter.this.maxEntitySize) {
                this.b.append("...more...");
            }
            this.b.append('\n');

            return this.b;
        }

        @Override
        public void write(final int i) throws IOException {

            if (this.baos.size() <= LoggingHttpFilter.this.maxEntitySize) {
                this.baos.write(i);
            }
            this.out.write(i);
        }
    }
}
