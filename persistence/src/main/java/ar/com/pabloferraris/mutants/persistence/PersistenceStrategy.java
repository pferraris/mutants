package ar.com.pabloferraris.mutants.persistence;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

public interface PersistenceStrategy extends AutoCloseable {
	void add(DetectionResult result) throws IOException;
}
