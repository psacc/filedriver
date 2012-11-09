package it.filedriver.connection;


import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketConnectionProvider implements ConnectionProvider {
	private String host = "localhost";
	private int localPort;

	public SocketConnectionProvider(String port) {
		this(Integer.valueOf(port));
	}
	
	public SocketConnectionProvider(int localPort) {
		this.localPort = localPort;
	}

	@Override
	public Connection createNew() throws UnknownHostException, IOException {
		return new SocketConnection(new Socket(getHost(), getLocalPort()));
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return host;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getLocalPort() {
		return localPort;
	}

}
