package it.filedriver.event;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.List;

public class FileEventUtils {
	private static final String EVENT_SUFFIX = "_event";

	public static String createSequentialIdWithName(int count, String name) {
		return new Date().getTime() + "-" + count + "." + name + EVENT_SUFFIX;
	}

	public static File[] listEventNameFiles(String fileEndPointPath) {
		return listEventNameFiles(fileEndPointPath, null);
	}

	public static File[] listEventNameFiles(String fileEndPointPath,
			final String[] events) {
		final String[] filterEvents = events == null ? new String[] { "" }
				: events;

		File[] listFiles = new File(fileEndPointPath)
				.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String eventId) {
						boolean accept = false;
						for (String eventName : filterEvents) {
							if (hasEventIdName(eventId, eventName)) {
								accept = true;
								break;
							}
						}
						return accept;
					}
				});
		return listFiles;
	}

	private static boolean hasEventIdName(String eventId, String eventName) {
		return eventId.endsWith(eventName + EVENT_SUFFIX);
	}

	public static boolean received(List<String> list, String eventName) {
		boolean received = false;
		for (String eventId : list) {
			if (hasEventIdName(eventId, eventName)) {
				received = true;
				break;
			}
		}

		return received;
	}
}
