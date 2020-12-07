package sap;

import java.util.List;
import java.util.ArrayList;

public class SAPModel implements ClockObserver {
	public enum RegisterType {
		A, B, ALU, IR, OUT, PC, MAR, BUS
	}

	private Register regA;
	private Register regB;
	private Register regOut;
	private Register regIR;
	private Register regMAR;
	private Register bus;
	private PC programCounter;
	private byte stepCount;
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
		this.bus = new Register8Bit();

		// For testing purposes only
		this.regA.loadVal((byte) 16);
		this.regB.loadVal((byte) 17);
		this.regIR.loadVal((byte) 32);
		this.programCounter.loadVal((byte) 15);
		this.regOut.loadVal((byte) 64);
		this.regMAR.loadVal((byte) 1);

		// Make the model a clock observer
		Clock.getClock().addObserver(this);
	}

	public void reset() {
		this.log.addEntry("User has requested to reset SAP...");
		this.regA.clear();
		this.regB.clear();
		this.regOut.clear();
		this.programCounter.clear();
		this.regIR.clear();
		this.regMAR.clear();
		this.bus.clear();
		this.stepCount = 0;

		// Reset clock iff high
		if (Clock.getClock().getStatus()) {
			Clock.getClock().toggleClock();
		}

		for (SAPObserver o : observers) {
			o.regAChange(this.regA.getVal());
			o.regBChange(this.regB.getVal());
			o.outChange(this.regOut.getVal());
			o.pcChange(this.programCounter.getVal());
			o.irChange(this.regIR.getVal());
			o.stepCycleChange(this.stepCount);
			o.marChange(this.regMAR.getVal());
			o.busChange(this.bus.getVal());
		}
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

	public boolean[] getFlags() {
		return this.flags;
	}

	public byte getStepCount() {
		return this.stepCount;
	}
	public Register getBus() {
		return this.bus;
	}

	// Observable Pattern
	public void addObserver(SAPObserver o) {
		if (o == null) {
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

	@Override
	public void clockChange() {
		// If clock just fell, increment step count
		if (!Clock.getClock().getStatus()) {
			if (this.stepCount == 5) {
				this.stepCount = 0;
			} else {
				this.stepCount++;
			}
			for (SAPObserver o : observers) {
				o.stepCycleChange(this.stepCount);
			}
		} else {
			// Rising edge detected
			if (this.stepCount == 1) {
				this.regMAR.loadVal(this.getPC().getVal());
			}
		}

	}

}
