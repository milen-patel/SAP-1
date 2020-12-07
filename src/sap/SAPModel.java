package sap;

import java.util.List;
import java.util.ArrayList;

public class SAPModel implements ClockObserver {
	public static final int HLT = 0;
	public static final int MI = 1;
	public static final int RI = 2;
	public static final int RO = 3;
	public static final int IO = 4;
	public static final int II = 5;
	public static final int AI = 6;
	public static final int AO = 7;
	public static final int SO = 8;
	public static final int SU = 9;
	public static final int BI = 10;
	public static final int OI = 11;
	public static final int CE = 12;
	public static final int CO = 13;
	public static final int J = 14;
	public static final int FI = 15;

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
	private boolean[] controlLines;
	private RegisterFlags regFlags;

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
		this.controlLines = new boolean[16];
		this.observers = new ArrayList<SAPObserver>();
		this.bus = new Register8Bit();
		this.regFlags = new RegisterFlags();

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
		this.regFlags.clear();
		this.resetAllControlLines();

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
			o.controlLineChange();
			o.flagChange();
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

	public boolean[] getControlLines() {
		return this.controlLines;
	}

	public byte getStepCount() {
		return this.stepCount;
	}

	public Register getBus() {
		return this.bus;
	}

	public RegisterFlags getFlags() {
		return this.regFlags;
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

	private void notifyFlagChange() {
		for (SAPObserver o : observers) {
			o.controlLineChange();
		}
	}

	private void notifyStepCounterChange() {
		for (SAPObserver o : observers) {
			o.stepCycleChange(this.stepCount);
		}
	}
	
	private void notifyBusChange() {
		for (SAPObserver o : observers) {
			o.busChange(this.bus.getVal());
		}
	}
	
	private void notifyMARChange() {
		for (SAPObserver o : observers) {
			o.marChange(this.regMAR.getVal());
		}
	}
	
	private void notifyPCChange() {
		for (SAPObserver o : observers) {
			o.pcChange(this.programCounter.getVal());
		}
	}
	
	private void resetAllControlLines() {
		for (int i = 0; i < 15; i++) {
			this.controlLines[i] = false;
		}
	}

	@Override
	public void clockChange() {
		// If clock just fell, increment step count
		if (!Clock.getClock().getStatus()) {
			if (this.stepCount == 4) {
				this.stepCount = 0;
			} else {
				this.stepCount++;
			}
			this.notifyStepCounterChange();
			EventLog.getEventLog().addEntry("Step counter updated to " + this.stepCount);

			// If we are on cycle 1, set lines manually
			if (this.stepCount == 1) {
				this.resetAllControlLines();
				this.controlLines[CO] = true;
				this.controlLines[MI] = true;
				notifyFlagChange();
				EventLog.getEventLog().addEntry("Falling edge of cycle 1. The following control lines were set: CO, MI");
				return;
			}

			// If we are on cycle 2, set lines manually
			if (this.stepCount == 2) {
				this.resetAllControlLines();
				this.controlLines[CE] = true;
				this.controlLines[RO] = true;
				this.controlLines[II] = true;
				notifyFlagChange();
				EventLog.getEventLog().addEntry("Falling edge of cycle 2. The following control lines were set: CE, RO, II");
				return;
			}
			
			//temp
			this.resetAllControlLines();
			notifyFlagChange();
			return;
		
		} else {
			// Iterate over flags
			if (this.controlLines[CO]) {
				this.bus.loadVal(this.programCounter.getVal());
				this.notifyBusChange();
			}
			if (this.controlLines[MI]) {
				this.regMAR.loadVal(this.bus.getVal());
				this.notifyMARChange();
			}
			if (this.controlLines[CE]) {
				this.programCounter.counterEnable();
				this.notifyPCChange();
			}
			if (this.controlLines[HLT]) {}
			if (this.controlLines[RI]) {}
			if (this.controlLines[RO]) {}
			if (this.controlLines[IO]) {}
			if (this.controlLines[II]) {}
			if (this.controlLines[AI]) {}
			if (this.controlLines[AO]) {}
			if (this.controlLines[SO]) {}
			if (this.controlLines[SU]) {}
			if (this.controlLines[BI]) {}
			if (this.controlLines[OI]) {}
			if (this.controlLines[J]) {}
			if (this.controlLines[FI]) {}
		}

	}

}
