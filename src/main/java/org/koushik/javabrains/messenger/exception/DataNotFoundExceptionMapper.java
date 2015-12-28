package org.koushik.javabrains.messenger.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.koushik.javabrains.messenger.model.ErrorMessage;

// la annotation @Provider sirve para hacerle saber a Jersey que la clase queda "registrada" para poder ser utilizada por Jersey

// ExceptionMapper sirve para mapear la exception arrojada cuando se produce un error al response que hay que devolver
// Jersey catchea la Exception para que no siga subiendo en el stack hasta el servlet y rompa todo. Cuando la catchea la devuelve
// al user a traves del Response

// Cada vez que se agrega una annotation del tipo @Provider hay que reiniciar el server para que jersey parsee la nueva clase registrada

@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {

	@Override
	public Response toResponse(DataNotFoundException ex) {
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), 404, "http://javabrains.koushik.org");
		return Response.status(Status.NOT_FOUND)
				.entity(errorMessage) // el entity seria como el body del response
				.build();
	}

}
