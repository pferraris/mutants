package ar.com.pabloferraris.mutants.persistence.rabbit;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

public class RabbitAsyncPersistenceStrategy implements PersistenceStrategy {

	private static final String exchangeName = "DetectionResults";
	private static final Charset utf8 = Charset.forName("UTF-8");
	private static final Gson gson = new Gson();

	private ConnectionFactory factory;
	private Connection conn;

	public RabbitAsyncPersistenceStrategy(String connectionString)
			throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		factory = new ConnectionFactory();
		factory.setUri(connectionString);
		factory.setAutomaticRecoveryEnabled(true);
	}

	RabbitAsyncPersistenceStrategy(ConnectionFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void add(DetectionResult result) throws IOException, TimeoutException {
		ensureConnection();
		String content = gson.toJson(result);
		byte[] buffer = content.getBytes(utf8);
		Channel channel = conn.createChannel();
		channel.basicPublish(exchangeName, String.valueOf(result.isMutant()), null, buffer);
		try {
			channel.close();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	private void ensureConnection() throws IOException, TimeoutException {
		if (conn == null || !conn.isOpen()) {
			conn = factory.newConnection();
			try (Channel channel = conn.createChannel()) {
				channel.exchangeDeclare(exchangeName, "fanout", true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (conn != null && conn.isOpen()) {
			conn.close();
		}
	}
}
