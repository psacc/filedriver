package it.filedriver.event;

import static org.junit.Assert.*;

import java.io.IOException;

import it.filedriver.FileDriverTest;
import it.filedriver.util.FileUtils;

import org.junit.Before;
import org.junit.Test;

public class FileEventEmitterTest {
	@Before
	public void setup() {
		FileUtils.empty(FileDriverTest.TEST_END_POINT_PATH);
	}

	@Test
	public void testEmit() throws IOException, InterruptedException {
		String eventName = "test-event";
		String eventId = new FileEventEmitter(FileDriverTest.TEST_END_POINT_PATH)
				.emit(eventName);
		assertTrue(new FileEventReceiver(FileDriverTest.TEST_END_POINT_PATH)
				.waitFor(eventName).contains(eventId));
	}

}
