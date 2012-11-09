package it.filedriver.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SocketConnection implements Connection {
	private Socket socket;
	
	public SocketConnection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public void shutdownInput() throws IOException {
		socket.shutdownInput();
	}

	@Override
	public void shutdownOutput() throws IOException {
		socket.shutdownOutput();
	}

}
