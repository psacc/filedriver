package it.filedriver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.filedriver.event.FileEventReceiver;
import it.filedriver.event.FileEventUtils;
import it.filedriver.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MultifileOutputStreamTest {
	@Before
	public void setup() {
		FileUtils.empty(FileDriverTest.TEST_END_POINT_PATH);
	}

	@Test
	public void testWriteInt() throws IOException, InterruptedException {
		MultifileOutputStream outputStream = new MultifileOutputStream(
				FileDriverTest.TEST_END_POINT_PATH);
		outputStream.write('<');
		Assert.assertTrue(new File(FileDriverTest.TEST_END_POINT_PATH
				+ File.separator + "write0.dat").exists());
		assertReceivedEventIn(FileDriverTest.TEST_END_POINT_PATH, "write");
	}

	@Test
	public void testClose() throws IOException, InterruptedException {
		MultifileOutputStream outputStream = new MultifileOutputStream(
				FileDriverTest.TEST_END_POINT_PATH);
		doSomeWrites(outputStream);
		outputStream.close();
		assertReceivedEventIn(FileDriverTest.TEST_END_POINT_PATH, "close");
		assertDirectoryEmpty(FileDriverTest.TEST_END_POINT_PATH);
	}

	private void assertReceivedEventIn(String path, String eventName)
			throws InterruptedException {
		List<String> list = new FileEventReceiver(path).waitFor(eventName);
		Assert.assertTrue("Missing write event",
				FileEventUtils.received(list, eventName));
	}

	private void doSomeWrites(MultifileOutputStream outputStream)
			throws IOException {
		outputStream.write(new byte[] { 12, '<', 'c', '>' });
	}

	private static final void assertDirectoryEmpty(String testEndPointPath) {
		File endPointPath = new File(testEndPointPath);
		assertTrue("Directory " + testEndPointPath + " not empty.",
				endPointPath.list() != null && endPointPath.list().length == 0);
	}

	@Test
	public void testWriteByteArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteByteArrayIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testFlush() {
		fail("Not yet implemented");
	}

}
