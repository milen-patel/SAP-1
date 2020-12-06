package sap;

public class SAPModel {
	Register regA;
	Register regB;
	Register regOut;
	Register regIR;
	PC programCounter;
	int stepCount;
	Memory RAM;
	EventLog log;

	public SAPModel() {
		this.regA = new Register8Bit();
		this.regB = new Register8Bit();
		this.regOut = new Register8Bit();
		this.regIR = new Register8Bit();
		this.programCounter = new PC();
		this.stepCount = 0;
		this.RAM = new Memory();
		this.log = EventLog.getEventLog();
	}
	
	public void reset() {
		this.log.addEntry("User has requested to reset SAP...");
		this.regA.clear();
		this.regB.clear();
		this.regOut.clear();
		this.programCounter.clear();
		this.regIR.clear();
	}
	

	
}
