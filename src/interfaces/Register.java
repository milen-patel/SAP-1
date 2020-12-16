package interfaces;

public interface Register {
	/*
	 * Replaces the current value stored in the register with newVal
	 */
	public void loadVal(byte newVal);

	/*
	 * Returns the value currently stored in the register, as a byte
	 */
	public byte getVal();
	
	/*
	 * Clears the contents of the register
	 */
	public void clear();
}
