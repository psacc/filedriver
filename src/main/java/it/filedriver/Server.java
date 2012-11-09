package it.filedriver;

import it.filedriver.connection.ConnectionHandler;
import it.filedriver.endpoint.EndPoint;


public interface Server {

	void setEndPoint(EndPoint proxy);

	void setConnectionHandler(ConnectionHandler proxy);

	void start();

	EndPoint getEndPoint();

	void waitForAcceptedConnection() throws InterruptedException;

	int getConnectionsCount();

	void stop() throws Exception;
}
