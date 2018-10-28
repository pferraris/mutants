package ar.com.pabloferraris.mutants.persistence.memcached;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;

public class MemcachedStatsUpdater implements PersistenceStrategy {
	
	private String connectionString;
	private MemcachedClientIF client;

	public MemcachedStatsUpdater(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public void close() throws Exception {
		if (client != null) {
			client.shutdown();
		}
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
	public void add(DetectionResult result) throws IOException {
		ensureConnection();
		
		String index = result.isMutant() ?
				MemcachedStatsCountStrategy.MUTANTS :
				MemcachedStatsCountStrategy.HUMANS;
		boolean done;
		try {
			done = client.delete(index).get();
		} catch (InterruptedException | ExecutionException e) {
			done = false;
		}
		if (!done) {
			throw new IOException("Cannot expire cache");
		}
	}
}
