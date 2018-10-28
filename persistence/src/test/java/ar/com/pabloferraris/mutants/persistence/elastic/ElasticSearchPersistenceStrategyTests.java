package ar.com.pabloferraris.mutants.persistence.elastic;

import static org.junit.Assert.assertEquals;

import org.elasticsearch.action.index.IndexRequest;
import org.junit.Test;

import ar.com.pabloferraris.mutants.persistence.DnaHashHelper;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public class ElasticSearchPersistenceStrategyTests {

	@Test
	public void checkRequest() throws Exception {
		DetectionResult expected = new DetectionResult();
		expected.setDna(new String [] { "ATA", "CAC", "TTA" });
		expected.setMutant(false);
		
		ElasticSearchPersistenceStrategy persistence = new ElasticSearchPersistenceStrategy("http://localhost");
		IndexRequest request = persistence.createRequest(expected);
		
		String index = String.valueOf(expected.isMutant());
		String id = DnaHashHelper.getHash(expected.getDna());
		
		assertEquals(index, request.index());
		assertEquals(id, request.id());

		persistence.close();
	}
}
