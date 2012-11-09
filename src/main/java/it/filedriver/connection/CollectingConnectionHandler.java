package it.filedriver.connection;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class CollectingConnectionHandler implements ConnectionHandler {
	private BlockingQueue<Connection> connectionQueue = new ArrayBlockingQueue<Connection>(1);
	
	@Override
	public void acceptNewConnection(Connection connection) throws Exception {
		connectionQueue.offer(connection);
	}

	public Connection waitForConnection()
			throws InterruptedException {
		return connectionQueue.take();
	}

}
