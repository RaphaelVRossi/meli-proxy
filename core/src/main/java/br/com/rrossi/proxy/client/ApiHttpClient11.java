package br.com.rrossi.proxy.client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
@Slf4j
@Named("ApiHttpClient11")
@ApplicationScoped
public class ApiHttpClient11 extends ApiClient {

    private final HttpClient httpClient;

    public ApiHttpClient11() {
        ExecutorService executorService = Executors.newFixedThreadPool(ConfigProvider.getConfig()
                .getValue("proxy.http.thread", Integer.class));

        this.httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
