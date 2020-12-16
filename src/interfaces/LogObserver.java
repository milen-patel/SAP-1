package interfaces;

public interface LogObserver {
	/*
	 * A log observer must know when something has been added to the log In this
	 * game, the View serves as a LogObserver The view wants to know what is being
	 * added to the log so that it can update its UI appropriately 'entry"
	 * represents the new string that has been added to the log
	 */
	public void newLogEntry(String entry);
}