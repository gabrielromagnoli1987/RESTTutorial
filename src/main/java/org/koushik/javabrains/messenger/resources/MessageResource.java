package org.koushik.javabrains.messenger.resources;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.koushik.javabrains.messenger.model.Message;
import org.koushik.javabrains.messenger.resources.beans.MessageFilterBean;
import org.koushik.javabrains.messenger.service.MessageService;

@Path("/messages") // mapeo de la URL a la clase 
public class MessageResource {
	
	MessageService messageService = new MessageService(); // video 16
	
	@GET // mapeo del metodo http al metodo java
	@Produces(MediaType.APPLICATION_XML) // tambien hay que especificar en que formato se debe devolver el response
									// (esto esta relacionado con el content type de los headers)
									// el formato del response puede ser tambien xml, json, etc	
	public List<Message> getMessages() {
		return messageService.getAllMessages();
	}
	
	
	/* Se pueden definir beans para organizar mejor el codigo y no tener todas las @QueryParam desperramadas en la 
	 * signatura del metodo */
	@GET
	public List<Message> getMessages(@BeanParam MessageFilterBean filterBean) {
		if (filterBean.getYear() > 0) {
			return messageService.getAllMessagesForYear(filterBean.getYear());
		}
		if (filterBean.getStart() >= 0 && filterBean.getSize() > 0) {
			return messageService.getAllMessagesPaginated(filterBean.getStart(), filterBean.getSize());
		}
		return messageService.getAllMessages();
	}
	
	@GET
	@Path("/test") // aca estamos concatenando con el path de la clase, quedaria "messages/test"
	@Produces(MediaType.APPLICATION_JSON)
	public String test(){
		return "test";
	}
	
	@GET
	@Path("/{messageId}") // con {} se hace uso de las variables
	@Produces(MediaType.APPLICATION_JSON)
	public Message test(@PathParam("messageId") long id) { // se realiza un mapeo de messageId de Path 
																  // a messageId de PathParam y luego se setea el valor 
																  // de la variable al parametro id del metodo test
		// jersey recibe un string por parametro a traves de la url, el haber seteado el tipo long de id hace que jersey
		// realice una conversion automatica de string a long
		return messageService.getMessage(id);
	}
	
	// A diferencia de APPLICATION_XML, JAX-RS NO viene con un manejador de MediaType.APPLICATION_JSON por defecto
	// que sepa responder en json
	// El siguiente error es el que puede verse en este caso: MessageBodyWriter not found for media type=application/json
	// Para solucionar esto hay que agregar el .jar necesario encargado de realizar esta conversión (agregar en el pom.xml)
	// jersey-media-moxy
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMessage(Message message, @Context UriInfo uriInfo) {
		
		Message newMessage = messageService.addMessage(message);
		String newId = String.valueOf(newMessage.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri)
				.entity(newMessage)
				.build();
	}
	
	@PUT
	@Path("/{messageId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Message updateMessage(@PathParam("messageId") long id, Message message) {
		message.setId(id);
		return messageService.updateMessage(message);
	}
	
	@DELETE
	@Path("/{messageId}")	
	@Produces(MediaType.APPLICATION_JSON) // el tipo tambien explica que estas anotattions (consumes y produces) 
										  //pueden ser declaradas sobre la clase si todos los metodos van a operar de la misma forma
	// es una forma de reducir mas el codigo y legibilidad, yo lo dejo asi ahora porque ya agregue comentarios por todos lados
	public void deleteMessage(@PathParam("messageId") long id){
		messageService.removeMessage(id);
	}
	
	
	/* video 25: subresources. Todos los requests dirigidos a los comentarios ahora los va a atender
	 * el commentResource donde tiene otros metodos que mapean la url */
	
	@Path("/{messageId}/comments")
	public CommentResource getCommentResource() {
		return new CommentResource();
	}
	
	
	
	@GET
	@Path("/{messageId}")
	public Message getMessage(@PathParam("messageId") long id, @Context UriInfo uriInfo) { // @Context sobre la clase UriInfo devuelve el toda la info relacinada de la iru y la setea en el parametro uriInfo 
		Message message = messageService.getMessage(id);
		message.addLink(getUriForSelf(uriInfo, message), "self");
		message.addLink(getUriForProfile(uriInfo, message), "profile");
		message.addLink(getUriForComments(uriInfo, message), "comments");
		
		return message;
		
	}

	private String getUriForComments(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder() 						// http://localhost:7070/messenger/webapi
				.path(MessageResource.class)						// concatena /messages   (lo extrae de la annotation @Path("/messages") de esta clase MessageResource) 
	       		.path(MessageResource.class, "getCommentResource")  // concatena el path que aparece arriba del metodo getCommentResource
	       		.path(CommentResource.class)						// agrega esta parte al path por si cambiar el valor del @Path("/") de la clase CommentResource
	       		.resolveTemplate("messageId", message.getId())      // cambia la porcion literal string de la url "messageId" por el id del mensaje
	            .build();
	    return uri.toString();
	}

	private String getUriForProfile(UriInfo uriInfo, Message message) {
		URI uri = uriInfo.getBaseUriBuilder()
       		 .path(ProfileResource.class)
       		 .path(message.getAuthor())
             .build();
        return uri.toString();
	}

	private String getUriForSelf(UriInfo uriInfo, Message message) {
		String uri = uriInfo.getBaseUriBuilder()
		 .path(MessageResource.class)
		 .path(Long.toString(message.getId()))
		 .build()
		 .toString();
		return uri;
	}

}
