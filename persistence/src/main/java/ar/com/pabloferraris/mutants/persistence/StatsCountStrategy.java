package ar.com.pabloferraris.mutants.persistence;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.domain.Stats;

public interface StatsCountStrategy extends AutoCloseable {
	Stats fetch() throws IOException;
}
