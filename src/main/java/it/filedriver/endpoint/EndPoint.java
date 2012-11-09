package it.filedriver.endpoint;

import it.filedriver.connection.Connection;
import it.filedriver.connection.ConnectionProvider;

public interface EndPoint {

	public Connection waitForNewConnection() throws Exception;

	public void close() throws Exception;

	public ConnectionProvider getConnectionProvider();
}