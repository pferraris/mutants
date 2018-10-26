package ar.com.pabloferraris.mutants.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.rest.domain.Specimen;

public class MutantResourceTests extends JerseyTest {

	private PersistenceStrategy persistence;
	
	@Override
	protected Application configure() {
		AbstractBinder persistenceBinder = new AbstractBinder() {
			@Override
			protected void configure() {
				persistence = Mockito.mock(PersistenceStrategy.class);
				bind(persistence).to(PersistenceStrategy.class);
			}
		};

		return new ResourceConfig()
				.register(new DetectorBinder())
				.register(persistenceBinder)
				.packages(true, "ar.com.pabloferraris.mutants.rest");
	}
	
	@Test
	public void isAlive() {
		Response response = target("mutant").request().get();
        assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void detectsMutant() {
		final Specimen specimen = new Specimen();
		specimen.setDna(new String [] { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" });
		Response response = target("mutant").request().post(Entity.entity(specimen, MediaType.APPLICATION_JSON));
        assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void detectsHuman() {
		final Specimen specimen = new Specimen();
		specimen.setDna(new String [] { "ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG" });
		Response response = target("mutant").request().post(Entity.entity(specimen, MediaType.APPLICATION_JSON));
        assertTrue(response.getStatus() == Status.FORBIDDEN.getStatusCode());
	}

	@Test
	public void nullSpecimen() {
		Specimen specimen = null;
		Response response = target("mutant").request().post(Entity.entity(specimen, MediaType.APPLICATION_JSON));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.hasEntity());
        @SuppressWarnings("unchecked")
		Map<String, Object> entity = response.readEntity(Map.class);
        assertNotNull(entity);
        assertTrue(entity.containsKey("causeCode"));
        assertEquals(2000, entity.get("causeCode"));
	}

	@Test
	public void nullDna() {
		Specimen specimen = new Specimen();
		Response response = target("mutant").request().post(Entity.entity(specimen, MediaType.APPLICATION_JSON));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.hasEntity());
        @SuppressWarnings("unchecked")
		Map<String, Object> entity = response.readEntity(Map.class);
        assertNotNull(entity);
        assertTrue(entity.containsKey("causeCode"));
        assertEquals(1000, entity.get("causeCode"));
	}

	@Test
	public void emptyDna() {
		Specimen specimen = new Specimen();
		specimen.setDna(new String[0]);
		Response response = target("mutant").request().post(Entity.entity(specimen, MediaType.APPLICATION_JSON));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.hasEntity());
        @SuppressWarnings("unchecked")
		Map<String, Object> entity = response.readEntity(Map.class);
        assertNotNull(entity);
        assertTrue(entity.containsKey("causeCode"));
        assertEquals(1001, entity.get("causeCode"));
	}
}
