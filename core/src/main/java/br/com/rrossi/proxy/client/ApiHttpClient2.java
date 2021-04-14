package br.com.rrossi.proxy.client;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
@Slf4j
@Named("ApiHttpClient2")
@Alternative
@Priority(1)
@ApplicationScoped
public class ApiHttpClient2 extends ApiClient {

    private final HttpClient httpClient;

    public ApiHttpClient2() {
        ExecutorService executorService = Executors.newFixedThreadPool(ConfigProvider.getConfig()
                .getValue("proxy.http.thread", Integer.class));

        this.httpClient = HttpClient.newBuilder()
                .executor(executorService)
                .version(HttpClient.Version.HTTP_2)
                .build();
    }

    @Override
    public HttpClient getHttpClient() {
        return httpClient;
    }
}
