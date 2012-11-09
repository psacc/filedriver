package it.filedriver.event;

import java.util.List;

public interface EventReceiver {

	public List<String> waitFor(String eventName) throws InterruptedException;

	public List<String> waitFor(String[] events) throws InterruptedException;

	public void cleanWaitingEvents();

}
