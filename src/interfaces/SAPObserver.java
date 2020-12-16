package interfaces;

public interface SAPObserver {
	// Indicates that the A register's value has changed to newVal
	public void regAChange(byte newVal);

	// Indicates that the B register's value has changed to newVal
	public void regBChange(byte newVal);

	// Indicates that the program counter's value has changed to newVal
	public void pcChange(byte newVal);

	// Indicates that the memory address register's value has changed to newVal
	public void marChange(byte newVal);

	// Indicates that the out register's value has changed to newVal
	public void outChange(byte newVal);

	// Indicates that the instruction register's value has changed to newVal
	public void irChange(byte newVal);

	// Indicates that the step cycle counter's value has changed to newVal
	public void stepCycleChange(byte newVal);

	// Indicates that either the zero flag or the carry flag's value has changed
	public void flagChange();

	// Indicates that newVal has been pushed onto the bus
	public void busChange(byte newVal);

	// Indicates that one or more control lines have changed
	public void controlLineChange();
}
