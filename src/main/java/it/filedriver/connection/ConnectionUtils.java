package it.filedriver.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionUtils {
	private static final int BUFFER_SIZE = 1024;

	public static void blockigStreamCopy(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		blockigStreamCopy(inputStream, outputStream, null);
	}

	public static void blockigStreamCopy(InputStream inputStream,
			OutputStream outputStream, Integer maximumNumberOfBytesToRead)
			throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		boolean thereIsMoreData = true;
		int totalNumberOfBytesWrote = 0;
		do {
			int numberOfBytesToRead = maximumNumberOfBytesToRead == null ? buffer.length
					: Math.min(buffer.length, maximumNumberOfBytesToRead
							- totalNumberOfBytesWrote);
			int numberOfBytesToWrite = inputStream.read(buffer, 0,
					numberOfBytesToRead);
			thereIsMoreData = !(numberOfBytesToWrite == -1 || (maximumNumberOfBytesToRead != null && (numberOfBytesToWrite
					+ totalNumberOfBytesWrote >= maximumNumberOfBytesToRead)));
			if (numberOfBytesToWrite > 0) {
				outputStream.write(buffer, 0, numberOfBytesToWrite);
				totalNumberOfBytesWrote = totalNumberOfBytesWrote
						+ numberOfBytesToWrite;
			}
		} while (thereIsMoreData);
	}

	public static void streamConnectionDataFromTo(final Connection source,
			final Connection destination) throws Exception {
		blockigStreamCopy(source.getInputStream(),
				destination.getOutputStream());
		source.shutdownInput();
		destination.shutdownOutput();
	}

}
