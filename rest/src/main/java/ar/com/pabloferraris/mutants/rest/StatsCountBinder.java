package ar.com.pabloferraris.mutants.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import ar.com.pabloferraris.mutants.persistence.StatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.elastic.ElasticSearchStatsCountStrategy;
import ar.com.pabloferraris.mutants.persistence.memcached.MemcachedStatsCountStrategy;

public class StatsCountBinder extends AbstractBinder {
	@Override
	protected void configure() {
		String elasticConnectionString = System.getenv("ELASTIC_CONNECTIONSTRING");
		String memcachedConnectionString = System.getenv("MEMCACHED_CONNECTIONSTRING");
		StatsCountStrategy source = new ElasticSearchStatsCountStrategy(elasticConnectionString);
		StatsCountStrategy cache = new MemcachedStatsCountStrategy(memcachedConnectionString, source);
		bind(cache).to(StatsCountStrategy.class);
	}
}
