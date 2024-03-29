package br.com.rrossi.proxy.client;

import br.com.rrossi.proxy.exception.ProxyException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
@Slf4j
public abstract class ApiClient {
    abstract HttpClient getHttpClient();

    public <T> void sendStatisticAsync(HttpRequest request, String body, HttpResponse.BodyHandler<T> responseBodyHandler) {
        logRequestClient(request, body);

        getHttpClient().sendAsync(request, responseBodyHandler)
                .whenComplete((response, throwable) -> logResponseClient(request, response)).toCompletableFuture()
                .orTimeout(5000, TimeUnit.MILLISECONDS);
    }

    public <T> HttpResponse<T> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return sendAsync(request,null,responseBodyHandler);
    }

    public <T> HttpResponse<T> sendAsync(HttpRequest request, String body,
                                         HttpResponse.BodyHandler<T> responseBodyHandler) {
        logRequestClient(request, body);

        CompletableFuture<HttpResponse<T>> future = new CompletableFuture<>();

        getHttpClient().sendAsync(request, responseBodyHandler)
                .whenComplete((response, throwable) -> logResponseClient(request, response)).toCompletableFuture()
                .orTimeout(5000, TimeUnit.MILLISECONDS)
                .thenApply(future::complete);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new ProxyException();
        }
    }

    public <T> HttpResponse<T>
    send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return send(request, null, responseBodyHandler);
    }

    public <T> HttpResponse<T> send(HttpRequest request, String body, HttpResponse.BodyHandler<T> responseBodyHandler) {
        logRequestClient(request, body);

        HttpResponse<T> response;
        try {
            response = getHttpClient().send(request, responseBodyHandler);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new ProxyException();
        }

        logResponseClient(request, response);

        return response;
    }

    private void logRequestClient(HttpRequest request, String body) {
        log.info("Call {} {} {}", request.method(), request.uri(), request.headers());
        if (body != null)
            log.info("Body {}", body);
    }

    private void logResponseClient(HttpRequest request, HttpResponse<?> response) {
        log.info("Return {} {} {} {}", request.method(), request.uri(), request.headers(), response.statusCode());
        if (response.body() != null)
            log.info("Body {}", response.body());
    }
}
