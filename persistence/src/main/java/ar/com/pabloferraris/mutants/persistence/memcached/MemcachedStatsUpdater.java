package ar.com.pabloferraris.mutants.persistence.memcached;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class MemcachedStatsUpdater implements PersistenceStrategy {

	private String connectionString;

	public MemcachedStatsUpdater(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public void add(DetectionResult result) throws IOException {
		MemcachedClient client = new MemcachedClient(AddrUtil.getAddresses(connectionString));
		client.delete("mutants");
		client.delete("humans");
		client.shutdown();
	}

	@Override
	public void close() throws Exception {
		
	}
	
}
