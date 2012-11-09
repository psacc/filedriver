package it.filedriver.connection;


public interface ConnectionProvider {

	public Connection createNew() throws Exception;

}
