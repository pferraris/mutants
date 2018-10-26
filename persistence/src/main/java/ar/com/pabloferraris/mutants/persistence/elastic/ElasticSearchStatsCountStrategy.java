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
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import com.google.gson.Gson;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.Pair;
import ar.com.pabloferraris.mutants.persistence.domain.Stats;

public class ElasticSearchStatsCountStrategy implements StatsCountStrategy {

	private RestClient client;
	private Gson gson;

	public ElasticSearchStatsCountStrategy(String connectionString) {
		gson = new Gson();
		client = RestClient.builder(HttpHost.create(connectionString)).build();
	}

	@Override
	public void close() throws Exception {
		client.close();
	}
	
	@Override
	public Stats fetch() throws IOException {
		try {
			/* We need to execute two queries to fetch mutants and humans counts.
			 * We will execute the queries in parallel and then we will merge the results.
			 */
			List<Callable<Pair<Boolean, Integer>>> callables = Arrays.asList(
					() -> getIndexCount(true),
					() -> getIndexCount(false));
			ExecutorService executor = Executors.newWorkStealingPool(2);
			Map<Boolean, Integer> counts = executor.invokeAll(callables)
			    .stream()
			    .map(future -> {
			        try {
			            return future.get();
			        }
			        catch (Exception e) {
			            throw new IllegalStateException(e);
			        }
			    })
			    .collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
			return new Stats(counts.get(true), counts.get(false));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	private Pair<Boolean, Integer> getIndexCount(boolean index) throws IOException {
		Request request = new Request("GET", "/" + String.valueOf(index) + "/_count");
		try {
			Response response = client.performRequest(request);
			String content = EntityUtils.toString(response.getEntity());
			ElasticEntity entity = gson.fromJson(content, ElasticEntity.class);
			return new Pair<Boolean, Integer>(index, entity.getCount());
		} catch (ResponseException e) {
			int statusCode = e.getResponse().getStatusLine().getStatusCode();
			if (statusCode == 404) {
				return new Pair<Boolean, Integer>(index, 0);
			}
			throw new IOException(e);
		}
	}
	
	private class ElasticEntity {
		private int count;
		
		public int getCount() {
			return count;
		}
	}
}
