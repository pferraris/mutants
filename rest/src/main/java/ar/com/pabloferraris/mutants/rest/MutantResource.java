package ar.com.pabloferraris.mutants.rest;

import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.DnaException;
import ar.com.pabloferraris.mutants.rest.domain.Specimen;

/**
 * Resource for checking if specimen is a mutant
 */
@Path("mutant")
public class MutantResource {

	@Inject
	private Detector detector;

	/**
	 * Check if service is alive
	 * @return Response with status code 200 and detector strategy in use
	 */
	@GET
	@Produces("application/vnd.mutants-v1+json")
	public Response isAlive() {
		Map<String, Object> entity = new Hashtable<String, Object>();
		entity.put("strategy", detector.getStrategy().getClass().getName());
		return Response.ok().entity(entity).build();
	}

	/**
	 * Check if the DNA of a certain specimen corresponds to a mutant or not
	 * @return Response with status code 200 for a mutant or 404 for a human
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/vnd.mutants-v1+json")
	public Response checkV1(Specimen specimen) {
		try {
			if (specimen == null) {
				Map<String, Object> entity = new Hashtable<String, Object>();
				entity.put("causeCode", 2000);
				entity.put("message", "Request body cannot be null");
				return Response.status(Status.BAD_REQUEST).entity(entity).build();
			}
			if (detector.isMutant(specimen.getDna())) {
				return Response.ok().build();
			} else {
				return Response.status(Status.FORBIDDEN).entity("").build();
			}
		} catch (DnaException ex) {
			Map<String, Object> entity = new Hashtable<String, Object>();
			entity.put("causeCode", ex.getCauseCode());
			entity.put("message", ex.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(entity).build();
		} catch (Exception ex) {
			return Response.serverError().entity(ex).build();
		}
	}
}
