package ar.com.pabloferraris.mutants.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;

public class StatsResourceTests extends JerseyTest {
	private StatsCountStrategy statsCount;
	
	@Override
	protected Application configure() {
		AbstractBinder statsCountBinder = new AbstractBinder() {
			@Override
			protected void configure() {
				statsCount = Mockito.mock(StatsCountStrategy.class);
				bind(statsCount).to(StatsCountStrategy.class);
			}
		};

		return new ResourceConfig()
				.register(statsCountBinder)
				.packages(true, "ar.com.pabloferraris.mutants.rest");
	}

	@Test
	public void getStats() throws IOException {
		Stats expected = new Stats(40, 100);
		when(statsCount.fetch()).thenReturn(expected);
		Response response = target("stats").request().get();
        assertTrue(response.getStatus() == Status.OK.getStatusCode());
        assertTrue(response.hasEntity());
		@SuppressWarnings("unchecked")
		Map<String, Object> current = response.readEntity(Map.class);
        assertNotNull(current);
        assertTrue(current.containsKey("ratio"));
        assertEquals(expected.getRatio(), (double)current.get("ratio"), 0);
	}

}
