package it.filedriver.endpoint;

import it.filedriver.connection.Connection;
import it.filedriver.connection.ConnectionProvider;
import it.filedriver.connection.SocketConnection;
import it.filedriver.connection.SocketConnectionProvider;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketEndPoint implements EndPoint {

	private ServerSocket serverSocket;

	public ServerSocketEndPoint(String port) throws NumberFormatException,
			IOException {
		this(Integer.parseInt(port));
	}

	public ServerSocketEndPoint(int port) throws IOException {
		serverSocket = new ServerSocket(port, 0, null);
	}

	@Override
	public Connection waitForNewConnection() throws Exception {
		return new SocketConnection(serverSocket.accept());
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
	}

	@Override
	public ConnectionProvider getConnectionProvider() {
		return new SocketConnectionProvider(serverSocket.getLocalPort());
	}
}
