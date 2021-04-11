package br.com.rrossi.proxy.exception;

import br.com.rrossi.proxy.model.ProxyExceptionModel;

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

        ProxyExceptionModel model = new ProxyExceptionModel();
        model.setMessage("Proxy internal error");
        model.setError("internal_server_error");
        model.setStatus(500);
        model.setCause(new String[]{});

        return Response.serverError()
                .entity(model)
                .build();
    }
}
