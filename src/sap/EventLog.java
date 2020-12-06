package sap;

import java.util.ArrayList;
import java.util.List;

/* The event log is responsible for encapsulating information about significant changes made in the model class
 * The event log is displayed to the user since the View class is-a LogObserver
 * The event log has been created as a singleton, meaning that only one instance of the class can ever exist
 */
public class EventLog {
	/*
	 * The implementation of the class relies on maintaining a list of strings that
	 * represent all of the events that have occurred in the log
	 */
	private List<String> events;
	/*
	 * Using the observer/observable design pattern, we must maintain a list of
	 * LogObserver's
	 */
	private List<LogObserver> observers;
	/* Encapsulate an instance of 'this' to implement the factory design pattern */
	private static EventLog eventLog;

	/* Private constructor */
	private EventLog() {
		events = new ArrayList<String>();
		observers = new ArrayList<LogObserver>();
	}

	/*
	 * Static method that is used to access the singleton log instance in
	 * replacement of a constructor If there isn't already a log, we create one and
	 * return it If a log already exists, then return the log that should be
	 * encapsulated in the eventLog variable
	 */
	public static EventLog getEventLog() {
		if (eventLog == null) {
			eventLog = new EventLog();
			return eventLog;
		} else {
			return eventLog;
		}
	}

	/* Adds an entry to the log and notifies appropriate observers */
	public void addEntry(String entry) {
		/* Check input before adding to the log */
		if (entry == null) {
			throw new RuntimeException("Null string passed to log addEntry()");
		}
		events.add(entry);
		/*
		 * Now that we have changed the log, notify observers so the UI can repaint
		 * itself
		 */
		notifyObservers(entry);
	}

	/* Observable Methods */
	public void addObserver(LogObserver o) {
		observers.add(o);
	}

	public void removeObserver(LogObserver o) {
		observers.remove(o);
	}

	public void notifyObservers(String s) {
		for (LogObserver o : observers) {
			o.newLogEntry(s);
		}
	}

}