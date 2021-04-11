package br.com.rrossi.proxy.api;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 10/04/2021.
 */
public interface MercadoLibreApi {
    Response callApiGet(UriInfo uriInfo, HttpHeaders headers);
    Response callApiPost(String body, UriInfo uriInfo, HttpHeaders headers);
    Response callApiPut(String body, UriInfo uriInfo, HttpHeaders headers);
    Response callApiPatch(String body, UriInfo uriInfo, HttpHeaders headers);
    Response callApiDelete(String body, UriInfo uriInfo, HttpHeaders headers);
}
