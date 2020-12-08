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

	public enum InstructionTypes {
		NOP, LDA, ADD, SUB, STA, LDI, JMP, JC, JZ, OUT, HLT, INVALID
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

	private List<SAPObserver> observers;

	public SAPModel() {
		this.regA = new Register8Bit();
		this.regB = new Register8Bit();
		this.regOut = new Register8Bit();
		this.regIR = new Register8Bit();
		this.regMAR = new Register4Bit();
		this.programCounter = new PC();
		this.stepCount = 0;
		this.RAM = new Memory(this.regMAR);
		this.log = EventLog.getEventLog();
		this.adder = new ALU(this.regA, this.regB);
		this.controlLines = new boolean[16];
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
		this.adder.regFlags.clear();
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
		return this.adder.regFlags;
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

	private void notifyControlLineChange() {
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

	private void notifyIRChange() {
		for (SAPObserver o : observers) {
			o.irChange(this.regIR.getVal());
		}
	}

	private void notifyAChange() {
		for (SAPObserver o : observers) {
			o.regAChange(this.regA.getVal());
		}
	}

	private void notifyBChange() {
		for (SAPObserver o : observers) {
			o.regBChange(this.regB.getVal());
		}
	}

	private void notifyOutChange() {
		for (SAPObserver o : observers) {
			o.outChange(this.regOut.getVal());
		}
	}

	private void notifyFlagRegisterChange() {
		for (SAPObserver o : observers) {
			o.flagChange();
		}
	}

	private void resetAllControlLines() {
		for (int i = 0; i < 16; i++) {
			this.controlLines[i] = false;
		}
		this.bus.loadVal((byte) 0);
		this.notifyBusChange();
		Clock.getClock().setIsHalted(false);
	}

	private InstructionTypes decodeIR() {
		// Get the value stored in the instruction register
		byte instructionVal = this.regIR.getVal();
		// Discard the four least significant bits
		instructionVal = (byte) (instructionVal & 0b11110000);

		// Analyze the value
		switch (instructionVal) {
		case 0b00000000:
			return InstructionTypes.NOP;
		case 0b00010000:
			return InstructionTypes.LDA;
		case 0b00100000:
			return InstructionTypes.ADD;
		case 0b00110000:
			return InstructionTypes.SUB;
		case 0b01000000:
			return InstructionTypes.STA;
		case 0b01010000:
			return InstructionTypes.LDI;
		case 0b01100000:
			return InstructionTypes.JMP;
		case 0b01110000:
			return InstructionTypes.JC;
		case (byte) 0b10000000:
			return InstructionTypes.JZ;
		case (byte) 0b11100000:
			return InstructionTypes.OUT;
		case (byte) 0b11110000:
			return InstructionTypes.HLT;
		default:
			return InstructionTypes.INVALID;
		}
	}

	@Override
	public void clockChange() {
		// If clock just fell, increment step count
		if (!Clock.getClock().getStatus()) {
			if (this.stepCount == 5) {
				this.stepCount = 1;
			} else {
				this.stepCount++;
			}
			this.notifyStepCounterChange();
			EventLog.getEventLog().addEntry("Step counter updated to " + this.stepCount);

			if (this.stepCount == 1) {
				// If we are on cycle 1, set lines manually
				this.resetAllControlLines();
				this.controlLines[CO] = true;
				this.controlLines[MI] = true;
				notifyControlLineChange();
				EventLog.getEventLog()
						.addEntry("Falling edge of cycle 1. The following control lines were set: CO, MI");
			} else if (this.stepCount == 2) {
				// If we are on cycle 2, set lines manually
				this.resetAllControlLines();
				this.controlLines[CE] = true;
				this.controlLines[RO] = true;
				this.controlLines[II] = true;
				notifyControlLineChange();
				EventLog.getEventLog()
						.addEntry("Falling edge of cycle 2. The following control lines were set: CE, RO, II");
			} else {

				// Figure out what instruction we are executing
				InstructionTypes currIns = this.decodeIR();
				System.out.println(currIns);

				if (currIns == InstructionTypes.NOP) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Falling edge of cycle 3. NOP => No control lines");

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Falling edge of cycle 4. NOP => No control lines");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Falling edge of cycle 5. NOP => No control lines");

					}
				}

				if (currIns == InstructionTypes.LDA) {
					if (this.stepCount == 3) {
						System.out.println("made it ");
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}

				if (currIns == InstructionTypes.ADD) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[BI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						this.controlLines[SO] = true;
						this.controlLines[FI] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();

					}
				}

				if (currIns == InstructionTypes.SUB) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[BI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						this.controlLines[SU] = true;
						this.controlLines[SO] = true;
						this.controlLines[FI] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();

					}
				}

				if (currIns == InstructionTypes.STA) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[AO] = true;
						this.controlLines[RI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}

				if (currIns == InstructionTypes.LDI) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}

				if (currIns == InstructionTypes.JMP) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[J] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
				if (currIns == InstructionTypes.JC) { // TODO
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
				if (currIns == InstructionTypes.JZ) { // TODO
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
				if (currIns == InstructionTypes.OUT) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[AO] = true;
						this.controlLines[OI] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
				if (currIns == InstructionTypes.HLT) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[HLT] = true;
						notifyControlLineChange();

					}
					if (this.stepCount == 4) { // TODO delete these and clean other instructions
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
				if (currIns == InstructionTypes.INVALID) { // TODO
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();

					}
				}
			}
			// todo, all out flags should go
			if (this.controlLines[CO]) {
				this.bus.loadVal(this.programCounter.getVal());
				this.notifyBusChange();
			}
			if (this.controlLines[RO]) {
				this.bus.loadVal((byte) this.RAM.memoryOut());
				this.notifyBusChange();
			}
			if (this.controlLines[IO]) {
				// Instruction register puts 4 least significant bits onto bus
				this.bus.loadVal((byte) (0b00001111 & this.regIR.getVal()));
				this.notifyBusChange();
			}
			if (this.controlLines[AO]) {
				this.bus.loadVal(this.regA.getVal());
				this.notifyBusChange();
			}
			if (this.controlLines[SO]) {
				this.bus.loadVal(this.adder.ALUOut(this.controlLines[SU]));
				this.notifyBusChange(); // TODO make sure this wont be problematic
			}
			// todo bus is empty when an out flag goes off
	

			return;

		} else {
			// Iterate over flags
			if (this.controlLines[FI]) {
				this.adder.flagsIn(this.controlLines[SU]);
				this.notifyFlagRegisterChange();
			}
			if (this.controlLines[MI]) {
				this.regMAR.loadVal(this.bus.getVal());
				this.notifyMARChange();
			}
			if (this.controlLines[CE]) {
				this.programCounter.counterEnable();
				this.notifyPCChange();
			}
			if (this.controlLines[HLT]) {
				Clock.getClock().setIsHalted(true);
			}
			if (this.controlLines[RI]) {
				this.RAM.memoryIn(this.bus.getVal());
			}

			if (this.controlLines[II]) {
				this.regIR.loadVal(this.bus.getVal());
				this.notifyIRChange();
			}
			if (this.controlLines[AI]) {
				this.regA.loadVal(this.bus.getVal());
				this.notifyAChange();
			}

			if (this.controlLines[SU]) {
				// TODO
			}
			if (this.controlLines[BI]) {
				this.regB.loadVal(this.bus.getVal());
				this.notifyBChange();
			}
			if (this.controlLines[OI]) {
				this.regOut.loadVal(this.bus.getVal());
				this.notifyOutChange();
			}
			if (this.controlLines[J]) {
				this.programCounter.loadVal((byte) (this.bus.getVal() & 0b1111));
				this.notifyPCChange();
			}
			
		}

	}

}
