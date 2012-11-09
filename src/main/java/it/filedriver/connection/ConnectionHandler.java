package it.filedriver.connection;


public interface ConnectionHandler {
	public void acceptNewConnection(Connection connection) throws Exception;
}