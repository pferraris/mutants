package ar.com.pabloferraris.mutants.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class MutantsApplication extends ResourceConfig {

	public MutantsApplication() {
		register(new DetectorBinder());
		register(new PersistenceBinder());
		register(new StatsCountBinder());
		packages(true, "ar.com.pabloferraris.mutants.rest");
	}
	
}
