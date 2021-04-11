package br.com.rrossi.proxy.exception;

import br.com.rrossi.proxy.model.ProxyExceptionModel;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by Raphael Rossi <raphael.vieira.rossi@gmail.com> 11/04/2021.
 */
@Provider
public class QuotaExceptionMapper implements ExceptionMapper<QuotaException> {

    @Override
    public Response toResponse(QuotaException exception) {

        ProxyExceptionModel model = new ProxyExceptionModel();
        model.setMessage("Too many requests");
        model.setError("too_many_requests");
        model.setStatus(429);
        model.setCause(new String[]{});

        return Response.status(Response.Status.TOO_MANY_REQUESTS)
                .entity(model)
                .build();
    }
}
