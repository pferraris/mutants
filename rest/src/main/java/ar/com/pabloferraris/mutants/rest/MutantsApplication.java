package ar.com.pabloferraris.mutants.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class MutantsApplication extends ResourceConfig {

	public MutantsApplication() {
		register(new DetectorBinder());
		packages(true, "ar.com.pabloferraris.mutants.rest");
	}
	
}
