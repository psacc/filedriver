package it.filedriver;

import it.filedriver.connection.ConnectionHandler;
import it.filedriver.connection.ConnectionProvider;
import it.filedriver.connection.ProxyConnectionHandler;
import it.filedriver.endpoint.EndPoint;
import it.filedriver.endpoint.FileEndPoint;
import it.filedriver.operation.MultithreadedOperationsRunner;
import it.filedriver.operation.OperationsRunner;
import it.filedriver.util.Logger;

public class ServerFactory {

	private Logger logger;

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Server createAndStartServer(EndPoint endPoint,
			ConnectionHandler connectionHandler) {
		return createAndStartServer(endPoint, connectionHandler,
				logger.proxy(new MultithreadedOperationsRunner()));
	}

	public Server createAndStartServer(EndPoint endPoint,
			ConnectionHandler connectionHandler,
			OperationsRunner operationsRunner) {
		MultithreadedServer server = logger.proxy(new MultithreadedServer());
		server.setOperationsRunner(operationsRunner);
		server.setEndPoint(logger.proxy(endPoint));
		server.setConnectionHandler(logger.proxy(connectionHandler));
		server.start();
		return server;
	}

	public Server createFileEndPointProxy(
			ConnectionProvider connectionsProvider, String path) {
		ProxyConnectionHandler proxyConnectionHandler = new ProxyConnectionHandler();
		proxyConnectionHandler.setConnectionsProvider(logger
				.proxy(connectionsProvider));
		MultithreadedOperationsRunner multithreadedOperationsRunner = logger
				.proxy(new MultithreadedOperationsRunner());
		proxyConnectionHandler
				.setOperationsRunner(multithreadedOperationsRunner);
		return createAndStartServer(new FileEndPoint(path),
				proxyConnectionHandler, multithreadedOperationsRunner);
	}

}
