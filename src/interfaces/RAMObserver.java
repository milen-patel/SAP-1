package interfaces;

public interface RAMObserver {
	/*
	 * Method called to all observers indicating that a particular memory address
	 * has changed. Address refers to the address [0, 15] of the memory position
	 * that has been changed. It is up to the observer to determine what the new
	 * value is.
	 */
	public void valChanged(int address);
}
