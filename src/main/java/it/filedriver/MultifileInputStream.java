package it.filedriver;

import it.filedriver.event.EventEmitter;
import it.filedriver.event.EventReceiver;
import it.filedriver.event.FileEventEmitter;
import it.filedriver.event.FileEventReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MultifileInputStream extends InputStream {
	private int readCount = 0;
	private String endPointPath;
	private EventReceiver eventReceiver;
	private EventEmitter eventEmitter;
	private boolean closed = false;
	private Queue<String> eventQueue = new LinkedList<String>();

	public MultifileInputStream(String endPointPath) {
		this.endPointPath = endPointPath;
		this.eventReceiver = new FileEventReceiver(endPointPath);
		this.eventEmitter = new FileEventEmitter(endPointPath);
	}

	public MultifileInputStream(File file) {
		this(file.getAbsolutePath() + File.separator);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return readJustOneByte(b, off, len);
	}

	private int readJustOneByte(byte[] b, int off, int len) throws IOException {
		int result = -1;
		if (len <= 0) {
			result = 0;
		} else {
			int data = read();
			if (data != -1) {
				byte read = (byte) data;
				b[off] = read;
				result = 1;
			}
		}

		return result;
	}

	@Override
	public int read() throws IOException {
		int readData = -1;
		String event = getOneEvent();
		if (event.contains("write") && !closed) {
			File file = new File(endPointPath + "write" + readCount + ".dat");
			if (file.canRead()) {
				FileInputStream fileInputStream = new FileInputStream(file);
				readData = fileInputStream.read();
				fileInputStream.close();
				file.delete();
				readCount++;
			} else {
				throw new IOException("Unable to read " + file);
			}
		} else if (event.contains("close")) {
			closed = true;
		}
		return readData;
	}

	private String getOneEvent() throws IOException {
		if (eventQueue.isEmpty()) {
			eventQueue.addAll(waitForData());
		}
		return eventQueue.poll();
	}

	private List<String> waitForData() throws IOException {
		try {
			return eventReceiver.waitFor(new String[] { "write", "close" });
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void close() throws IOException {
		closed = true;
		try {
			eventEmitter.emit("close");
		} catch (Exception e) {
			throw new IOException(e);
		}
		super.close();
	}
}