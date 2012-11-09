package it.filedriver.connection;

import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {

	public InputStream getInputStream() throws Exception;

	public OutputStream getOutputStream() throws Exception;

	public void shutdownInput() throws Exception;

	public void shutdownOutput() throws Exception;

}
