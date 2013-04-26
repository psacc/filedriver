package it.filedriver.operation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class MultithreadedOperationsRunnerTest {

	private static final int MANY_TIMES = 10;

	private class CompletableOperation implements Runnable {
		private boolean complete = false;

		@Override
		public void run() {
			setComplete(true);
		}

		public void setComplete(boolean complete) {
			this.complete = complete;
		}

		public boolean isComplete() {
			return complete;
		}
	}

	@Test
	public void testStartNone() {
		testStartAsync(0, null);
	}

	@Test
	public void testStartAsyncOne() {
		testStartAsync(1, null);
	}

	@Test
	public void testStartAsyncManyNoMaxConcurrency() {
		testStartAsync(MANY_TIMES, null);
	}

	@Test
	public void testStartAsyncManyOverMaxConcurrency() {
		testStartAsync(MANY_TIMES, MANY_TIMES + 1);
	}

	@Test
	public void testStartAsyncManyUnderMaxConcurrency() {
		testStartAsync(MANY_TIMES, MANY_TIMES - 1);
	}

	@Test
	public void testStartAsyncManyEqualsMaxConcurrency() {
		testStartAsync(MANY_TIMES, MANY_TIMES);
	}

	@Test
	public void testStartAsyncManyConcurrencySingle() {
		testStartAsync(MANY_TIMES, 1);
	}

	private void testStartAsync(int times, Integer maxConcurrency) {
		try {
			for (CompletableOperation operation : doStartAsyncAndWaitEnd(times,
					maxConcurrency)) {
				assertTrue("Operation not completed", operation.isComplete());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private List<CompletableOperation> doStartAsyncAndWaitEnd(int times,
			Integer maxConcurrency) throws InterruptedException {
		final List<CompletableOperation> list = new ArrayList<CompletableOperation>();
		final MultithreadedOperationsRunner runner = new MultithreadedOperationsRunner();
		runner.setMaxConcurrentOperations(maxConcurrency);

		repeat(times, new Runnable() {

			@Override
			public void run() {
				CompletableOperation completableOperation = new CompletableOperation();
				list.add(completableOperation);
				runner.start(completableOperation);
			}
		});

		runner.waitAllAsyncOperations();
		return list;
	}

	@Test
	public void testStopAll() {
		final MultithreadedOperationsRunner runner = new MultithreadedOperationsRunner();
		repeatManyTimes(new Runnable() {

			@Override
			public void run() {
				runner.start(new Runnable() {

					@Override
					public void run() {
						waitForever();
					}
				});
			}
		});
		runner.stopAll();
		assertNoThreadIsAlive(runner);
	}

	private void assertNoThreadIsAlive(MultithreadedOperationsRunner runner) {
		for (Thread t : runner.getRegisteredThreads().values()) {
			assertTrue("Thread still alive", !t.isAlive());
		}
	}

	private static void repeatManyTimes(Runnable runnable) {
		repeat(MANY_TIMES, runnable);
	}

	private static void repeat(int times, Runnable runnable) {
		for (int i = 0; i < times; i++) {
			runnable.run();
		}
	}

	private static void waitForever() {
		try {
			new Object().wait();
		} catch (RuntimeException e) {
			// do nothing
		} catch (Exception e) {
			// do nothing
		}
	}
}
