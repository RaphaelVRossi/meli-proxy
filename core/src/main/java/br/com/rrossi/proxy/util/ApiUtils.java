package br.com.rrossi.proxy.util;

import br.com.rrossi.proxy.exception.ProxyException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
public class ApiUtils {

    private static final String BASE_URL = "https://api.mercadolibre.com/";

    public static final String API_URI = "apiUri";

    public static URI getUri(UriInfo uriInfo) {

        if (uriInfo.getPathParameters().isEmpty() || !uriInfo.getPathParameters().containsKey(ApiUtils.API_URI))
            throw new ProxyException();

        String apiUri = uriInfo.getPathParameters().get(ApiUtils.API_URI).get(0);

        if (apiUri == null || apiUri.isEmpty())
            throw new ProxyException();

        if (uriInfo.getQueryParameters() != null && !uriInfo.getQueryParameters().isEmpty()) {
            StringBuilder queryParametersBuilder = new StringBuilder("?");

            uriInfo.getQueryParameters().forEach((key, value) -> value.forEach(query ->
                    queryParametersBuilder.append(key).append("=").append(query).append("&")));

            apiUri += queryParametersBuilder.toString();
        }

        return URI.create(BASE_URL + apiUri);
    }

    public static String[] createRequestHeaders(HttpHeaders requestHeaders) {
        List<String> headers = new LinkedList<>();

        requestHeaders.getRequestHeaders().entrySet().stream()
                .filter(stringListEntry -> !"host".equalsIgnoreCase(stringListEntry.getKey()))
                .filter(stringListEntry -> !"content-length".equalsIgnoreCase(stringListEntry.getKey()))
                .forEach((entry) -> entry.getValue().forEach(value -> {
                    headers.add(entry.getKey());
                    headers.add(value);
                }));

        String hostHeader = requestHeaders.getRequestHeaders().keySet().stream().filter("host"::equalsIgnoreCase)
                .findFirst().orElse(null);

        if (hostHeader != null) {
            headers.add("X-REDIRECT-HOST");
            headers.add(requestHeaders.getHeaderString(hostHeader));
        }

        return headers.toArray(new String[]{});
    }

    public static HttpRequest.Builder createHttpRequestBuilder(UriInfo uriInfo, HttpHeaders headers) {
        return HttpRequest.newBuilder()
                .headers(ApiUtils.createRequestHeaders(headers))
                .uri(ApiUtils.getUri(uriInfo));
    }
}
