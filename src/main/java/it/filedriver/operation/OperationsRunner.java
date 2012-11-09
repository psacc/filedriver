package it.filedriver.operation;

public interface OperationsRunner {

	public abstract void start(Runnable runnable);

	public abstract void stopAll();

}