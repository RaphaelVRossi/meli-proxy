package br.com.rrossi.proxy;

import br.com.rrossi.proxy.api.MercadoLibreApi;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/{apiUri:.*}")
@Consumes
@Produces
public class ProxyResource {

    private final MercadoLibreApi mercadoLibreApiClient;

    @Inject
    public ProxyResource(MercadoLibreApi mercadoLibreApiClient) {
        this.mercadoLibreApiClient = mercadoLibreApiClient;
    }

    @GET
    public Response get(@Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiGet(info, headers);
    }

    @POST
    public Response post(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPost(body, info, headers);
    }

    @PUT
    public Response put(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPut(body, info, headers);
    }

    @PATCH
    public Response patch(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPatch(body, info, headers);
    }

    @DELETE
    public Response delete(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiDelete(body, info, headers);
    }
}