package it.filedriver.connection;

import it.filedriver.event.EventEmitter;
import it.filedriver.event.FileEventEmitter;

public class FileConnectionProvider implements ConnectionProvider {

	private String fileEndPointPath;
	private EventEmitter eventEmitter;

	public FileConnectionProvider(String fileEndPointPath) {
		this.fileEndPointPath = fileEndPointPath;
		this.eventEmitter = new FileEventEmitter(fileEndPointPath);
	}

	@Override
	public Connection createNew() throws Exception {
		String connectionId = eventEmitter.emit("connection");
		return new FileConnection(fileEndPointPath, connectionId, true);
	}
}
