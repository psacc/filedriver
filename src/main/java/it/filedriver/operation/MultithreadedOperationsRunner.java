package it.filedriver.operation;

import it.filedriver.util.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MultithreadedOperationsRunner implements OperationsRunner {
	private final Map<Integer, Thread> registeredThreads = Collections
			.synchronizedMap(new HashMap<Integer, Thread>());
	private Integer maxConcurrentOperations = null;
	private BlockingQueue<Runnable> queuedOperations = new LinkedBlockingQueue<Runnable>();
	private int newThreadId = 0;
	private Object allOperationsEndedSignal = null;

	private class Operation implements Runnable {

		private Runnable runnable;
		private Integer threadId;

		@Override
		public void run() {
			// FIXME
			Logger logger = new Logger();
			Runnable op = logger.proxy(getRunnable());
			do {
				Logger.log("ThreadId-" + newThreadId + ": starting operation "
						+ op);
				op.run();
				Logger.log("ThreadId-" + newThreadId + ": operation ended "
						+ op);
				op = logger.proxy(queuedOperations.poll());
			} while (op != null);
			synchronized (registeredThreads) {
				registeredThreads.remove(getThreadId());
				if (registeredThreads.isEmpty()) {
					synchronized (allOperationsEndedSignal) {
						allOperationsEndedSignal.notify();
					}
				}
			}
		}

		public void setThreadId(Integer threadId) {
			this.threadId = threadId;
		}

		public Integer getThreadId() {
			return threadId;
		}

		public void setRunnable(Runnable runnable) {
			this.runnable = runnable;
		}

		public Runnable getRunnable() {
			return runnable;
		}
	}

	/* (non-Javadoc)
	 * @see it.filedriver.IOperationsRunner#start(java.lang.Runnable)
	 */
	@Override
	public void start(Runnable runnable) {
		if (maxConcurrentOperations == null
				|| getRegisteredThreads().size() < maxConcurrentOperations) {
			createAllOperationsEndendSignalUnlessExisting();
			Integer newThreadId = getNewThreadId();
			Operation operation = new Operation();
			operation.setRunnable(runnable);
			operation.setThreadId(newThreadId);
			Thread thread = new Thread(operation);
			registeredThreads.put(newThreadId, thread);
			thread.start();
		} else {
			Logger.log("Operation queued");
			queuedOperations.add(runnable);
		}
	}

	private void createAllOperationsEndendSignalUnlessExisting() {
		if (allOperationsEndedSignal == null) {
			allOperationsEndedSignal = new Object();
		}
	}

	private Integer getNewThreadId() {
		return newThreadId++;
	}

	private class Executable {
		public void execute(Thread t) {
		}
	}

	public void stopAll() {
		forEachThread(new Executable() {
			public void execute(Thread t) {
				t.interrupt();
			}
		});
		registeredThreads.clear();
	}

	private void forEachThread(Executable op) {
		synchronized (registeredThreads) {
			for (Thread t : registeredThreads.values()) {
				op.execute(t);
			}
		}
	}

	public void setMaxConcurrentOperations(Integer maxConcurrentOperations) {
		this.maxConcurrentOperations = maxConcurrentOperations;
	}

	public Integer getMaxConcurrentOperations() {
		return maxConcurrentOperations;
	}

	public void waitAllAsyncOperations() throws InterruptedException {
		if (allOperationsEndedSignal != null) {
			synchronized (allOperationsEndedSignal) {
				allOperationsEndedSignal.wait();
				allOperationsEndedSignal = null;
			}
		}
	}

	public Map<Integer, Thread> getRegisteredThreads() {
		return Collections.unmodifiableMap(registeredThreads);
	}
}
