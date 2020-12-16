package sap;

import java.util.ArrayList;
import java.util.List;

import interfaces.ClockObserver;

/* Singleton clock */
public class Clock {
	private boolean status;
	private boolean isHalted;
	private List<ClockObserver> observers;
	private static Clock clock;

	public Clock() {
		this.status = false;
		this.isHalted = false;
		observers = new ArrayList<ClockObserver>();
	}

	public boolean getStatus() {
		getClock();
		return this.status;
	}
	
	public void setIsHalted(boolean newVal) {
		this.isHalted = newVal;
	}

	public void toggleClock() {
		// Validate that a clock exists
		getClock();
		
		// If halted, do nothing
		if (this.isHalted) {
			return;
		}
		
		this.status = !this.status;
		notifyObservers();
		
		// Add to event log
		if (status) {
			EventLog.getEventLog().addEntry("Rising Edge of Clock");
		} else {
			EventLog.getEventLog().addEntry("Falling Edge of Clock");
		}
		return;
	}

	public static Clock getClock() {
		if (clock == null) {
			clock = new Clock();
			return clock;
		} else {
			return clock;
		}
	}

	// Implement observable pattern
	public void addObserver(ClockObserver o) {
		if (o == null) {
			return;
		}
		this.observers.add(o);
	}

	public void removeObserver(ClockObserver o) {
		if (o == null) {
			return;
		}
		this.observers.remove(o);
	}

	public void notifyObservers() {
		for (ClockObserver o : observers) {
			o.clockChange();
		}
	}
}
