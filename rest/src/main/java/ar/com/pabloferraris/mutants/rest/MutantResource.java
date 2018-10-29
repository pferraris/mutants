package ar.com.pabloferraris.mutants.rest;

import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.com.pabloferraris.mutants.detection.Detector;
import ar.com.pabloferraris.mutants.detection.DnaException;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;
import ar.com.pabloferraris.mutants.rest.domain.Specimen;

/**
 * Resource for checking if specimen is a mutant
 */
@Path("mutant")
public class MutantResource {

	@Inject
	private Detector detector;

	@Inject
	private PersistenceStrategy persistence;

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
			DetectionResult result = new DetectionResult();
			result.setDna(specimen.getDna());
			result.setMutant(detector.isMutant(specimen.getDna()));
			persistence.add(result);
			if (result.isMutant()) {
				return Response.ok().build();
			} else {
				return Response.status(Status.FORBIDDEN).entity("").build();
			}
		} catch (DnaException e) {
			Map<String, Object> entity = new Hashtable<String, Object>();
			entity.put("causeCode", e.getCauseCode());
			entity.put("message", e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(entity).build();
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
	}
}
