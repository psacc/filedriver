package it.filedriver;

import it.filedriver.event.EventEmitter;
import it.filedriver.event.FileEventEmitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MultifileOutputStream extends OutputStream {
	private final String endPointPath;
	private long writeCounter = 0;
	private EventEmitter eventEmitter;

	public MultifileOutputStream(String endPointPath) {
		this.endPointPath = endPointPath;
		this.eventEmitter = new FileEventEmitter(endPointPath);
	}

	public MultifileOutputStream(File file) {
		this(file.getAbsolutePath() + File.separator);
	}

	@Override
	public void write(int b) throws IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(new File(
				endPointPath + "write" + writeCounter + ".dat"));
		fileOutputStream.write(b);
		fileOutputStream.close();
		try {
			eventEmitter.emit("write");
		} catch (Exception e) {
			throw new IOException(e);
		}
		writeCounter++;
	}

	@Override
	public void close() throws IOException {
		try {
			eventEmitter.emit("close");
		} catch (Exception e) {
			throw new IOException(e);
		}
		super.close();
	}
}