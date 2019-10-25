package stubidp.stubidp.exceptions.mappers;

import stubidp.stubidp.exceptions.IdpUserNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class IdpUserNotFoundExceptionMapper implements ExceptionMapper<IdpUserNotFoundException> {
    @Override
    public Response toResponse(IdpUserNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
    }
}
