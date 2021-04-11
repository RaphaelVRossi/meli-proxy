package br.com.rrossi.proxy.api;

import br.com.rrossi.proxy.client.ApiClient;
import br.com.rrossi.proxy.util.ApiUtils;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
@Slf4j
@Named("MercadoLibreApiAsyncClient")
@ApplicationScoped
public class MercadoLibreApiAsyncClient implements MercadoLibreApi {

    final ApiClient apiClient;

    @Inject
    public MercadoLibreApiAsyncClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Response callApiGet(UriInfo uriInfo, HttpHeaders headers) {
        HttpRequest request = ApiUtils.createHttpRequestBuilder(uriInfo, headers).GET().build();

        HttpResponse<String> httpResponse = this.apiClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return Response.status(httpResponse.statusCode()).entity(httpResponse.body()).build();
    }

    @Override
    public Response callApiPost(String body, UriInfo uriInfo, HttpHeaders headers) {
        HttpRequest request = ApiUtils.createHttpRequestBuilder(uriInfo, headers)
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> httpResponse = this.apiClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return Response.status(httpResponse.statusCode()).entity(httpResponse.body()).build();
    }

    @Override
    public Response callApiPut(String body, UriInfo uriInfo, HttpHeaders headers) {
        HttpRequest request = ApiUtils.createHttpRequestBuilder(uriInfo, headers)
                .PUT(HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> httpResponse = this.apiClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return Response.status(httpResponse.statusCode()).entity(httpResponse.body()).build();
    }

    @Override
    public Response callApiPatch(String body, UriInfo uriInfo, HttpHeaders headers) {
        HttpRequest request = ApiUtils.createHttpRequestBuilder(uriInfo, headers)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> httpResponse = this.apiClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return Response.status(httpResponse.statusCode()).entity(httpResponse.body()).build();
    }

    @Override
    public Response callApiDelete(String body, UriInfo uriInfo, HttpHeaders headers) {
        HttpRequest request = ApiUtils.createHttpRequestBuilder(uriInfo, headers)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(body)).build();

        HttpResponse<String> httpResponse = this.apiClient
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());

        return Response.status(httpResponse.statusCode()).entity(httpResponse.body()).build();
    }
}
