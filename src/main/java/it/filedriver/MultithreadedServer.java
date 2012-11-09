package it.filedriver;

import it.filedriver.connection.Connection;
import it.filedriver.connection.ConnectionHandler;
import it.filedriver.endpoint.EndPoint;
import it.filedriver.operation.OperationsRunner;

public class MultithreadedServer implements Server {
	private EndPoint endPoint;
	private ConnectionHandler connectionHandler;
	private int connectionsCount;
	private boolean acceptingConnections = false;
	private final Object waitForAcceptedConnectionLock = new Object();
	private OperationsRunner operationsRunner;

	public void start() {
		acceptingConnections = true;

		getOperationsRunner().start(new Runnable() {

			@Override
			public void run() {
				executeAcceptConnectionsLoop();
			}
		});
	}

	private void executeAcceptConnectionsLoop() {
		try {
			while (acceptingConnections) {
				final Connection connection = endPoint.waitForNewConnection();
				setConnectionsCount(getConnectionsCount() + 1);
				synchronized (waitForAcceptedConnectionLock) {
					waitForAcceptedConnectionLock.notifyAll();
				}
				getOperationsRunner().start(new Runnable() {

					@Override
					public void run() {
						try {
							handleConnection(connection);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			acceptingConnections = false;
		}
	}

	private void handleConnection(Connection connection) throws Exception {
		if (connectionHandler != null) {
			connectionHandler.acceptNewConnection(connection);
		}
	}

	public void setConnectionHandler(ConnectionHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	public ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public void stop() throws Exception {
		acceptingConnections = false;
		getOperationsRunner().stopAll();
		endPoint.close();
	}

	public void setEndPoint(EndPoint endPoint) {
		this.endPoint = endPoint;
	}

	public EndPoint getEndPoint() {
		return endPoint;
	}

	public void waitForAcceptedConnection() throws InterruptedException {
		if (connectionsCount == 0) {
			synchronized (waitForAcceptedConnectionLock) {
				waitForAcceptedConnectionLock.wait();
			}
		}
	}

	public synchronized void setConnectionsCount(int connectionsCount) {
		this.connectionsCount = connectionsCount;
	}

	public synchronized int getConnectionsCount() {
		return connectionsCount;
	}

	public OperationsRunner getOperationsRunner() {
		return operationsRunner;
	}

	public void setOperationsRunner(OperationsRunner operationsRunner) {
		this.operationsRunner = operationsRunner;
	}
}