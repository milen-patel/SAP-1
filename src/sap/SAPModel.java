package sap;
import java.util.List;
import java.util.ArrayList;

public class SAPModel {
	public enum RegisterType {
		A, B, ALU, IR, OUT, PC, MAR
	}
	private Register regA;
	private Register regB;
	private Register regOut;
	private Register regIR;
	private Register regMAR;
	private PC programCounter;
	private int stepCount;
	private Memory RAM;
	private EventLog log;
	private ALU adder;
	private boolean[] flags;
	private List<SAPObserver> observers;

	public SAPModel() {
		this.regA = new Register8Bit();
		this.regB = new Register8Bit();
		this.regOut = new Register8Bit();
		this.regIR = new Register8Bit();
		this.regMAR = new Register4Bit();
		this.programCounter = new PC();
		this.stepCount = 0;
		this.RAM = new Memory();
		this.log = EventLog.getEventLog();
		this.adder = new ALU(this.regA, this.regB);
		this.flags = new boolean[16];
		this.observers = new ArrayList<SAPObserver>();
		
		// For testing purposes only
		this.regA.loadVal((byte) 16);
		this.regB.loadVal((byte) 17);
	}
	
	public void reset() {
		this.log.addEntry("User has requested to reset SAP...");
		this.regA.clear();
		this.regB.clear();
		this.regOut.clear();
		this.programCounter.clear();
		this.regIR.clear();
	}
	
	public Memory getRAM() {
		return this.RAM;
	}
	
	public Register getA() {
		return this.regA;
	}
	public Register getB() {
		return this.regB;
	}
	public ALU getALU() {
		return this.adder;
	}
	public Register getIR() {
		return this.regIR;
	}
	public Register getOut() {
		return this.regOut;
	}
	public PC getPC() {
		return this.programCounter;
	}
	public Register getMAR() {
		return this.regMAR;
	}
	
	// Observable Pattern
	public void addObserver(SAPObserver o) {
		if (o == null ) {
			return;
		}
		this.observers.add(o);
	}
	
	public void removeObserver(SAPObserver o) {
		if (o == null) {
			return;
		}
		this.observers.remove(o);
	}
	

	
}
