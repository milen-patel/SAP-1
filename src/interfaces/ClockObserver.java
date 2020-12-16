package interfaces;

public interface ClockObserver {
	/*
	 * Method called to indicate that the system clock has changed from either LOW
	 * to HIGH or HIGH to LOW. The system clock is used to synchronize all
	 * components of the SAP-1.
	 */
	public void clockChange();

}
