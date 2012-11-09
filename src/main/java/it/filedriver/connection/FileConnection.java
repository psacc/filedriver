package it.filedriver.connection;

import it.filedriver.MultifileInputStream;
import it.filedriver.MultifileOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileConnection implements Connection {

	private String fileEndPointPath;
	private String connectionId;
	private MultifileInputStream multifileInputStream;
	private MultifileOutputStream multifileOutputStream;
	private boolean reverse;

	public FileConnection(String fileEndPointPath, String connectionId,
			boolean reverse) throws IOException {
		this.fileEndPointPath = fileEndPointPath;
		this.connectionId = connectionId;
		this.setReverse(reverse);
	}

	@Override
	public OutputStream getOutputStream() throws Exception {
		if (multifileOutputStream == null) {
			this.multifileOutputStream = createOutputStream();
		}
		return multifileOutputStream;
	}

	private MultifileOutputStream createOutputStream() {
		File file = createConnectionDirectory(false);
		MultifileOutputStream multifileOutputStream = new MultifileOutputStream(
				file);
		return multifileOutputStream;
	}

	private File createConnectionDirectory(boolean forward) {
		File file = new File(fileEndPointPath + connectionId + "_"
				+ (forward == reverse ? "fw" : "bw") + "_io");
		file.mkdir();
		return file;
	}

	@Override
	public InputStream getInputStream() throws Exception {
		if (multifileInputStream == null) {
			this.multifileInputStream = createInputStream();
		}
		return multifileInputStream;
	}

	private MultifileInputStream createInputStream() {
		File file = createConnectionDirectory(true);
		MultifileInputStream multifileInputStream = new MultifileInputStream(
				file);
		return multifileInputStream;
	}

	@Override
	public void shutdownInput() throws Exception {
		if (multifileInputStream != null) {
			multifileInputStream.close();
		}
	}

	@Override
	public void shutdownOutput() throws Exception {
		if (multifileOutputStream != null) {
			multifileOutputStream.close();
		}
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;
	}

	public boolean isReverse() {
		return reverse;
	}

}
