package ar.com.pabloferraris.mutants.persistence.memcached;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.BeforeClass;
import org.junit.Test;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;
import net.spy.memcached.MemcachedClientIF;

public class MemcachedStatsCountStrategyTests {

	private static MemcachedClientIF client;
	private static StatsCountStrategy source;
	
	@BeforeClass
	public static void initialize() {
		client = mock(MemcachedClientIF.class);
		source = mock(StatsCountStrategy.class);
	}
	
	private MemcachedStatsCountStrategy createStatsCount() {
		MemcachedStatsCountStrategy statsCount = new MemcachedStatsCountStrategy("localhost", source);
		statsCount.setClient(client);
		return statsCount;
	}

	@Test
	public void getStatsFromCache() throws Exception {
		MemcachedStatsCountStrategy statsCount = createStatsCount();
		
		Stats expected = new Stats(40, 100);
		
		when(client.get("mutants")).thenReturn(expected.getMutants());
		when(client.get("humans")).thenReturn(expected.getHumans());

		Stats current = statsCount.fetch();
		assertEquals(expected.getHumans(), current.getHumans());
		assertEquals(expected.getMutants(), current.getMutants());
		
		statsCount.close();
	}

	@Test
	public void getStatsFromSource() throws Exception {
		MemcachedStatsCountStrategy statsCount = createStatsCount();
		
		Stats expected = new Stats(40, 100);
		
		when(client.get("mutants")).thenReturn(null);
		when(client.get("humans")).thenReturn(null);
		when(source.fetch()).thenReturn(expected);

		Stats current = statsCount.fetch();
		assertEquals(expected.getHumans(), current.getHumans());
		assertEquals(expected.getMutants(), current.getMutants());
		assertEquals(expected.getRatio(), current.getRatio(), 0);
		
		statsCount.close();
	}
	
	
}
