package ar.com.pabloferraris.mutants.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

@Path("ping")
public class PingResource {

	/**
	 * Check if service is alive
	 * @return Response with status code 200 and detector strategy in use
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response ping() {
		return Response.ok().entity("pong").build();
	}

}
