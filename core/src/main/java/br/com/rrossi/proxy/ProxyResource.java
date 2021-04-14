package br.com.rrossi.proxy;

import br.com.rrossi.proxy.api.MercadoLibreApi;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/{apiUri:.*}")
public class ProxyResource {

    private final MercadoLibreApi mercadoLibreApiClient;

    @Inject
    public ProxyResource(MercadoLibreApi mercadoLibreApiClient) {
        this.mercadoLibreApiClient = mercadoLibreApiClient;
    }

    @GET
    @Produces
    public Response get(@Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiGet(info, headers);
    }

    @POST
    @Consumes
    @Produces
    public Response post(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPost(body, info, headers);
    }

    @PUT
    @Consumes
    @Produces
    public Response put(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPut(body, info, headers);
    }

    @PATCH
    @Consumes
    @Produces
    public Response patch(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiPatch(body, info, headers);
    }

    @DELETE
    @Consumes
    @Produces
    public Response delete(String body, @Context UriInfo info, @Context HttpHeaders headers) {
        return mercadoLibreApiClient.callApiDelete(body, info, headers);
    }
}