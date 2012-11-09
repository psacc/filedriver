package it.filedriver.endpoint;

import it.filedriver.connection.Connection;
import it.filedriver.connection.ConnectionProvider;
import it.filedriver.connection.FileConnection;
import it.filedriver.connection.FileConnectionProvider;
import it.filedriver.event.EventReceiver;
import it.filedriver.event.FileEventReceiver;

public class FileEndPoint implements EndPoint {

	private String fileEndPointPath;
	private EventReceiver eventReceiver;
	private boolean closed = false;

	public FileEndPoint(String fileEndPointPath) {
		this.fileEndPointPath = fileEndPointPath;
		eventReceiver = new FileEventReceiver(fileEndPointPath);
	}

	@Override
	public Connection waitForNewConnection() throws Exception {
		if (closed) {
			throw new Exception("Closed endpoint");
		}

		// TODO handle more than one connection in queue
		String connectionId = eventReceiver.waitFor("connection").get(0);
		return new FileConnection(fileEndPointPath, connectionId, false);
	}

	@Override
	public void close() throws Exception {
		closed = true;
		eventReceiver.cleanWaitingEvents();
	}

	@Override
	public ConnectionProvider getConnectionProvider() {
		return new FileConnectionProvider(fileEndPointPath);
	}

}
