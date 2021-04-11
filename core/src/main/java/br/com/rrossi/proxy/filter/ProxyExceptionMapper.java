package br.com.rrossi.proxy.filter;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@Provider
public class ProxyExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        return Response.serverError()
                .entity("{\"message\":\"Proxy internal error\",\"error\":\"internal_server_error\",\"status\":500,\"cause\":[]}")
                .build();
    }
}
