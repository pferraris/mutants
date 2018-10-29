package ar.com.pabloferraris.mutants.persistence.elastic;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import ar.com.pabloferraris.mutants.persistence.DnaHashHelper;
import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class ElasticSearchPersistenceStrategy implements PersistenceStrategy {

	private static final Logger logger = LogManager.getLogger(ElasticSearchPersistenceStrategy.class);
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
		logger.info("DetectionResult appended on elasticsearch");
	}
	
	IndexRequest createRequest(DetectionResult result) {
		String index = String.valueOf(result.isMutant());
		String id = DnaHashHelper.getHash(result.getDna());
		logger.info("Appending DetectionResult on elasticsearch with id " + id + " on index " + index);
		return new IndexRequest(index, "doc", id)
				.source("dna", result.getDna(),
						"isMutant",	result.isMutant());
	}
}
