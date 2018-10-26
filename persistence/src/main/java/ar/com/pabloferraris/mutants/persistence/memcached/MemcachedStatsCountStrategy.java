package ar.com.pabloferraris.mutants.persistence.memcached;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class MemcachedStatsCountStrategy implements StatsCountStrategy {

	private StatsCountStrategy source;
	private String connectionString;

	public MemcachedStatsCountStrategy(String connectionString, StatsCountStrategy source) {
		this.connectionString = connectionString;
		this.source = source;
	}

	@Override
	public void close() throws Exception {
		source.close();
	}

	@Override
	public Stats fetch() throws IOException {
		MemcachedClient client = null;
		Stats stats = null;
		try {
			client = new MemcachedClient(AddrUtil.getAddresses(connectionString));
			Integer mutants = (Integer) client.get("mutants");
			Integer humans = (Integer) client.get("humans");
			if (mutants != null && humans != null) {
				stats = new Stats(mutants, humans);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (stats == null) {
			stats = source.fetch();
			if (client != null) {
				client.add("mutants", 60 * 60, stats.getMutants());
				client.add("humans", 60 * 60, stats.getHumans());
			}
		}
		if (client != null) {
			client.shutdown();
		}
		return stats;
	}
}
