package ar.com.pabloferraris.mutants.persistence.memcached;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;

public class MemcachedStatsCountStrategy implements StatsCountStrategy {
	static final String MUTANTS = "mutants";
	static final String HUMANS = "humans";

	private StatsCountStrategy source;
	private String connectionString;
	private MemcachedClientIF client;

	public MemcachedStatsCountStrategy(String connectionString, StatsCountStrategy source) {
		this.connectionString = connectionString;
		this.source = source;
	}

	@Override
	public void close() throws Exception {
		if (client != null) {
			client.shutdown();
		}
		source.close();
	}

	void setClient(MemcachedClientIF client) {
		this.client = client;
	}
	
	private void ensureConnection() throws IOException {
		if (client == null) {
			client = new MemcachedClient(AddrUtil.getAddresses(connectionString));
		}
	}
	
	@Override
	public Stats fetch() throws IOException {
		Stats stats = null;
		try {
			ensureConnection();
			Integer mutants = (Integer) client.get(MUTANTS);
			Integer humans = (Integer) client.get(HUMANS);
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
		return stats;
	}
}
