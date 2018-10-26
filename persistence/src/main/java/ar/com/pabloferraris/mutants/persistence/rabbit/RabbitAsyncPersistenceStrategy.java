package ar.com.pabloferraris.mutants.persistence.rabbit;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.domain.DetectionResult;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;

public class RabbitAsyncPersistenceStrategy implements PersistenceStrategy {

	private static final String exchangeName = "DetectionResults";
	private static final Charset utf8 = Charset.forName("UTF-8");

	private Gson gson;
	private Connection conn;

	public RabbitAsyncPersistenceStrategy(String connectionString) throws Exception {
		gson = new Gson();
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(connectionString);
		factory.setAutomaticRecoveryEnabled(true);
		conn = factory.newConnection();
		initialize();
	}

	private void initialize() {
		try (Channel channel = conn.createChannel()) {
			channel.exchangeDeclare(exchangeName, "fanout", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		conn.close();
	}

	@Override
	public void add(DetectionResult result) throws IOException {
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
}
