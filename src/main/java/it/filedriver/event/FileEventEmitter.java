package it.filedriver.event;

import java.io.File;
import java.io.IOException;

public class FileEventEmitter implements EventEmitter {
	private int count = 0;
	private String fileEndPointPath;

	public FileEventEmitter(String fileEndPointPath) {
		this.fileEndPointPath = fileEndPointPath;
	}

	@Override
	public String emit(String string) throws IOException {
		count++;
		String sequentialId = FileEventUtils.createSequentialIdWithName(count, string);
		new File(fileEndPointPath + sequentialId).createNewFile();
		return sequentialId;
	}
}
