package it.filedriver.event;


public interface EventEmitter {

	String emit(String eventName) throws Exception;

}
