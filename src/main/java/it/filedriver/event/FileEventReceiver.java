package it.filedriver.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileEventReceiver implements EventReceiver {
	private static final int POLL_FREQUENCY = 100;

	private String fileEndPointPath;

	public FileEventReceiver(String fileEndPointPath) {
		this.fileEndPointPath = fileEndPointPath;
	}

	public void cleanWaitingEvents() {
		for (File file : FileEventUtils.listEventNameFiles(fileEndPointPath)) {
			file.delete();
		}
	}

	@Override
	public List<String> waitFor(String eventName) throws InterruptedException {
		return waitFor(new String[] { eventName });
	}

	@Override
	public List<String> waitFor(String[] events) throws InterruptedException {
		List<String> eventIdList = new ArrayList<String>();
		while (eventIdList.isEmpty()) {
			eventIdList = searchForEvents(events);
			if (eventIdList == null) {
				Thread.sleep(POLL_FREQUENCY);
			}
		}
		return eventIdList;
	}

	private List<String> searchForEvents(String[] events) {
		File[] listFiles = FileEventUtils.listEventNameFiles(fileEndPointPath,
				events);

		SortedSet<String> eventIdList = new TreeSet<String>();
		if (listFiles != null) {
			for (File file : listFiles) {
				eventIdList.add(file.getName());
				file.delete();
			}
		}
		return new ArrayList<String>(eventIdList);
	}
}
