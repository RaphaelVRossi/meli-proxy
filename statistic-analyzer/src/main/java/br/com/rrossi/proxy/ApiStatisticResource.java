package br.com.rrossi.proxy;

import br.com.rrossi.proxy.model.ApiStatisticModel;
import br.com.rrossi.proxy.service.ApiStatisticService;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api-info")
public class ApiStatisticResource {

    private static final Jsonb toJson = JsonbBuilder.create(new JsonbConfig().withNullValues(false));

    @Inject
    ApiStatisticService apiStatisticService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello(@QueryParam("basePath") String basePath) {

        List<ApiStatisticModel> allApiCalls = apiStatisticService.findAllApiCalls(basePath);

        if (allApiCalls.isEmpty())
            return Response.noContent().build();

        return Response.ok(toJson.toJson(allApiCalls)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/consumes")
    public Response consumes() {

        List<ApiStatisticModel> allApiCalls = apiStatisticService.findApiCalls();

        if (allApiCalls.isEmpty())
            return Response.noContent().build();

        return Response.ok(toJson.toJson(allApiCalls)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/average")
    public Response average(@QueryParam("basePath") String basePath) {

        List<ApiStatisticModel> allApiCalls = apiStatisticService.findApiAverageResponseTime(basePath);

        if (allApiCalls.isEmpty())
            return Response.noContent().build();

        return Response.ok(toJson.toJson(allApiCalls)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status")
    public Response status(@QueryParam("basePath") String basePath) {

        List<ApiStatisticModel> allApiCalls = apiStatisticService.findApiStatusCode(basePath);

        if (allApiCalls.isEmpty())
            return Response.noContent().build();

        return Response.ok(toJson.toJson(allApiCalls)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveApiInfo(ApiStatisticModel model) {
        apiStatisticService.add(toJson.toJson(model));
        return Response.ok().build();
    }
}