package ar.com.pabloferraris.mutants.persistence.elastic;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Request;
import org.junit.Test;

import com.google.gson.Gson;

import ar.com.pabloferraris.mutants.persistence.domain.Stats;

public class ElasticSearchStatsCountStrategyTests {

	private static final Gson gson = new Gson();
	
	@Test
	public void getStats() throws Exception {
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put(String.valueOf(true), 40);
		expected.put(String.valueOf(false), 100);
		ElasticSearchStatsCountStrategy statsCount = createStatsCount(expected);
		
		Stats stats = statsCount.fetch();
		assertEquals(expected.get(String.valueOf(true)).intValue(), stats.getMutants());
		assertEquals(expected.get(String.valueOf(false)).intValue(), stats.getHumans());
		
		statsCount.close();
	}

	private ElasticSearchStatsCountStrategy createStatsCount(Map<String, Integer> expected) {
		return new TestElasticSearchStatsCountStrategy(expected);
	}

	private class TestElasticSearchStatsCountStrategy extends ElasticSearchStatsCountStrategy {
		private Map<String, Integer> expected;
		
		public TestElasticSearchStatsCountStrategy(Map<String, Integer> expected) {
			super("http://localhost");
			this.expected = expected; 
		}
		
		@Override
		protected String requestContent(Request request) {
			String index = request.getEndpoint().replace("_count", "").replace("/", "");
			ElasticEntity response;
			if (expected.containsKey(index)) {
				response = new ElasticEntity(expected.get(index));
			} else {
				response = new ElasticEntity(0);
			}
			return gson.toJson(response);
		}
	}
}
