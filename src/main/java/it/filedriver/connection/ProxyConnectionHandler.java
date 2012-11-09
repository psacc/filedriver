package it.filedriver.connection;

import it.filedriver.operation.OperationsRunner;

public class ProxyConnectionHandler implements ConnectionHandler {
	private ConnectionProvider connectionsProvider;
	private OperationsRunner operationsRunner;

	@Override
	public void acceptNewConnection(final Connection connection)
			throws Exception {
		Connection unproxiedConnection = getConnectionsProvider().createNew();
		startThreadToStreamConnectionDataFromTo(connection, unproxiedConnection);
		startThreadToStreamConnectionDataFromTo(unproxiedConnection, connection);
	}

	private void startThreadToStreamConnectionDataFromTo(
			final Connection source, final Connection destination) {
		operationsRunner.start(new Runnable() {

			@Override
			public void run() {
				try {
					ConnectionUtils.streamConnectionDataFromTo(source,
							destination);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ConnectionProvider getConnectionsProvider() {
		return connectionsProvider;
	}

	public void setConnectionsProvider(ConnectionProvider connectionsProvider) {
		this.connectionsProvider = connectionsProvider;
	}

	public OperationsRunner getOperationsRunner() {
		return operationsRunner;
	}

	public void setOperationsRunner(OperationsRunner operationsRunner) {
		this.operationsRunner = operationsRunner;
	}
}