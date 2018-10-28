package ar.com.pabloferraris.mutants.persistence.elastic;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import ar.com.pabloferraris.mutants.persistence.DnaHashHelper;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class ElasticSearchPersistenceStrategy implements PersistenceStrategy {

	private RestHighLevelClient client;

	public ElasticSearchPersistenceStrategy(String connectionString) {
		client = new RestHighLevelClient(RestClient.builder(HttpHost.create(connectionString)));
	}

	@Override
	public void close() throws Exception {
		client.close();
	}

	@Override
	public void add(DetectionResult result) throws IOException {
		client.index(createRequest(result), RequestOptions.DEFAULT);
	}
	
	IndexRequest createRequest(DetectionResult result) {
		String index = String.valueOf(result.isMutant());
		String id = DnaHashHelper.getHash(result.getDna());
		return new IndexRequest(index, "doc", id)
				.source("dna", result.getDna(),
						"isMutant",	result.isMutant());
	}
}
