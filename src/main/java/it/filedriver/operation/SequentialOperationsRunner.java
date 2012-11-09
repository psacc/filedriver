package it.filedriver.operation;

public class SequentialOperationsRunner implements OperationsRunner {

	@Override
	public void start(Runnable runnable) {
		runnable.run();
	}

	@Override
	public void stopAll() {
		// ok
	}

}
