package ar.com.pabloferraris.mutants.persistence.memcached;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.BeforeClass;
import org.junit.Test;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;
import net.spy.memcached.MemcachedClientIF;

public class MemcachedStatsUpdaterTests {

	private static MemcachedClientIF client;
	
	@BeforeClass
	public static void initialize() {
		client = mock(MemcachedClientIF.class);
	}
	
	private MemcachedStatsUpdater createStatsUpdater() {
		MemcachedStatsUpdater statsUpdater = new MemcachedStatsUpdater("localhost");
		statsUpdater.setClient(client);
		return statsUpdater;
	}

	@Test
	public void expireCacheWhenAddingNewResults() throws Exception {
		MemcachedStatsUpdater statsUpdater = createStatsUpdater();
		
		DetectionResult result = new DetectionResult();
		result.setMutant(true);

		String index = result.isMutant() ?
				MemcachedStatsCountStrategy.MUTANTS :
				MemcachedStatsCountStrategy.HUMANS;
		when(client.delete(index)).thenReturn(CompletableFuture.supplyAsync(() -> true));
		
		statsUpdater.add(result);
		statsUpdater.close();
	}

	@Test
	public void cannotExpireCache() throws Exception {
		MemcachedStatsUpdater statsUpdater = createStatsUpdater();
		
		DetectionResult result = new DetectionResult();
		result.setMutant(false);

		String index = result.isMutant() ?
				MemcachedStatsCountStrategy.MUTANTS :
				MemcachedStatsCountStrategy.HUMANS;
		when(client.delete(index)).thenReturn(CompletableFuture.supplyAsync(() -> false));
		
		statsUpdater.add(result);
		statsUpdater.close();
	}
}
