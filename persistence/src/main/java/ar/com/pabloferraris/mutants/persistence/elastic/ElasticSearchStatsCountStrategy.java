package ar.com.pabloferraris.mutants.persistence.elastic;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import com.google.gson.Gson;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Pair;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;

public class ElasticSearchStatsCountStrategy implements StatsCountStrategy {

	private static final Gson gson = new Gson();

	private RestClient client;

	public ElasticSearchStatsCountStrategy(String connectionString) {
		client = RestClient.builder(HttpHost.create(connectionString)).build();
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	@Override
	public Stats fetch() throws IOException {
		try {
			/*
			 * We need to execute two queries to fetch mutants and humans counts. We will
			 * execute the queries in parallel and then we will merge the results.
			 */
			List<Callable<Pair<Boolean, Integer>>> callables = Arrays.asList(() -> getIndexCount(true),
					() -> getIndexCount(false));
			ExecutorService executor = Executors.newWorkStealingPool(2);
			Map<Boolean, Integer> counts = executor.invokeAll(callables)
					.stream()
					.map(future -> {
						try {
							return future.get();
						} catch (Exception e) {
							throw new IllegalStateException(e);
						}})
					.collect(Collectors.toMap(x -> x.getKey(),x -> x.getValue()));
			return new Stats(counts.get(true), counts.get(false));
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	private Pair<Boolean, Integer> getIndexCount(boolean index) throws IOException {
		Request request = new Request("GET", "/" + String.valueOf(index) + "/_count");
		try {
			String content = requestContent(request);
			ElasticEntity entity = gson.fromJson(content, ElasticEntity.class);
			return new Pair<Boolean, Integer>(index, entity.getCount());
		} catch (ResponseException e) {
			if (e.getResponse().getStatusLine().getStatusCode() == 404) {
				return new Pair<Boolean, Integer>(index, 0);
			}
			throw e;
		}
	}

	protected String requestContent(Request request) throws ParseException, IOException {
		return EntityUtils.toString(client.performRequest(request).getEntity());
	}

	protected class ElasticEntity {
		private int count;

		public ElasticEntity(int count) {
			this.count = count;
		}

		public int getCount() {
			return count;
		}
	}
}
