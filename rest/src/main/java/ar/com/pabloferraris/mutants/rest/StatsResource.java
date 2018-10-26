package ar.com.pabloferraris.mutants.rest;

import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;

/**
 * Resource for get counts stats of performed detections
 */
@Path("stats")
public class StatsResource {

	@Inject
	private StatsCountStrategy statsCount;

	/**
	 * @return Response with counts stats of performed detections
	 */
	@GET
	@Produces("application/vnd.stats-v1+json")
	public Response get() {
		try {
			Stats stats = statsCount.fetch();
			Map<String, Object> entity = new Hashtable<String, Object>();
			entity.put("count_mutant_dna", stats.getMutants());
			entity.put("count_human_dna", stats.getHumans());
			entity.put("ratio", stats.getRatio());
			return Response.ok().entity(entity).build();
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
	}
}
