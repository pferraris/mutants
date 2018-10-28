package ar.com.pabloferraris.mutants.rest;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


public class MutantsApplicationTests extends JerseyTest {

	@Override
	protected Application configure() {
		return new MutantsApplication();
	}

	@Test
	public void isAlive() {
		Response response = target("mutant").request().get();
        assertTrue(response.getStatus() == Status.OK.getStatusCode());
	}

}
