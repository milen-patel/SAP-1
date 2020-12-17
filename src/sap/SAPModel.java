package sap;

import java.util.List;

import interfaces.ClockObserver;
import interfaces.Register;
import interfaces.SAPObserver;

import java.util.ArrayList;

public class SAPModel implements ClockObserver {
	// Assign constant integer values to each control line signal
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

	// Enumerates the valid register types in SAP-1
	public enum RegisterType {
		A, B, ALU, IR, OUT, PC, MAR, BUS
	}

	// Enumerates the valid instruction types supported in this simulator
	public enum InstructionTypes {
		NOP, LDA, ADD, SUB, STA, LDI, JMP, JC, JZ, OUT, HLT, INVALID
	}

	// Contents of the SAP-1
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

	// Since SAP is observable, it must maintain a list of its observers (which is
	// just the View in this implementation)
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

		// Register the model as a clock observer
		Clock.getClock().addObserver(this);
	}

	public void reset() {
		// Inform the log
		this.log.addEntry("User has requested to reset SAP...");

		// Clear all registers and other data values (except memory)
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

		// Reset clock if and only if clock is high
		if (Clock.getClock().getStatus()) {
			Clock.getClock().toggleClock();
		}

		// Notify observers so view can repaint itself
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

	private void resetAllControlLines() {
		// Set all control lines to false
		for (int i = 0; i < 16; i++) {
			this.controlLines[i] = false;
		}

		// Nothing is putting its value onto the bus, so clear it
		this.bus.loadVal((byte) 0);

		// Tell the view to repaint the bus
		this.notifyBusChange();

		// If the clock is halted, remove that constraint
		Clock.getClock().setIsHalted(false);
	}

	private InstructionTypes decodeIR() {
		// Get the value stored in the instruction register
		byte instructionVal = this.regIR.getVal();

		// Discard the four least significant bits
		instructionVal = (byte) (instructionVal & 0b11110000);

		// Analyze the value
		return decodeInstructionHelper(instructionVal);
	}

	// Helper method that parses a byte and finds its instruciton type
	private InstructionTypes decodeInstructionHelper(byte instructionVal) {
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

	public void analyzeInstruction(byte address) {
		// Start constructing entry for the log
		String logVal = "[" + address + "]\t";

		// First, add the instruction to logVal
		byte instructionVal = (byte) (this.getRAM().getRAM()[address] & 0b11110000);
		InstructionTypes t = decodeInstructionHelper(instructionVal);

		// Handle result of decoded instruction
		switch (t) {
		case NOP:
			logVal += "NOP";
			break;
		case LDA:
			logVal += "LDA";
			break;
		case ADD:
			logVal += "ADD";
			break;
		case SUB:
			logVal += "SUB";
			break;
		case STA:
			logVal += "STA";
			break;
		case LDI:
			logVal += "LDI";
			break;
		case JMP:
			logVal += "JMP";
			break;
		case JC:
			logVal += "JC";
			break;
		case JZ:
			logVal += "JZ";
			break;
		case OUT:
			logVal += "OUT";
			break;
		case HLT:
			logVal += "HLT";
			break;
		default:
			logVal += "N/A";
		}

		// Then, add the argument to logVal
		logVal += " ";
		if (t != InstructionTypes.NOP && t != InstructionTypes.INVALID && t != InstructionTypes.HLT && t != InstructionTypes.OUT) {
			logVal += this.getRAM().getRAM()[address] & 0b00001111;
		}

		// Finally, add decimal value
		logVal += "\t" + this.getRAM().getRAM()[address];

		// Add final parsed string to the event log
		EventLog.getEventLog().addEntry(logVal);
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
				EventLog.getEventLog().addEntry("The control lines were set: CO, MI");

			} else if (this.stepCount == 2) {

				// If we are on cycle 2, set lines manually
				this.resetAllControlLines();
				this.controlLines[CE] = true;
				this.controlLines[RO] = true;
				this.controlLines[II] = true;
				notifyControlLineChange();
				EventLog.getEventLog().addEntry("The control lines were set: CE, RO, II");

			} else {

				// Figure out what instruction we are executing
				InstructionTypes currIns = this.decodeIR();

				if (currIns == InstructionTypes.NOP) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("NOP => No control lines");

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("NOP => No control lines");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("NOP => No control lines");

					}
				}

				if (currIns == InstructionTypes.LDA) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDA => IO, MI set");

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDA => RO, AI set");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDA => Do nothing");
					}
				}

				if (currIns == InstructionTypes.ADD) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("ADD => IO, MI set");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[BI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("ADD => RO, BI set");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						this.controlLines[SO] = true;
						this.controlLines[FI] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("ADD => ∑O, FI, AI set");

					}
				}

				if (currIns == InstructionTypes.SUB) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("SUB => IO, MI set");

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[RO] = true;
						this.controlLines[BI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("SUB => RO, BI set");
					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						this.controlLines[SU] = true;
						this.controlLines[SO] = true;
						this.controlLines[FI] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("SUB => ∑O, SU, AI, FI set");

					}
				}

				if (currIns == InstructionTypes.STA) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[MI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("STA => IO, MI set");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						this.controlLines[AO] = true;
						this.controlLines[RI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("STA => AO, RI set");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("STA => Do nothing");
					}
				}

				if (currIns == InstructionTypes.LDI) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[AI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDI => IO, AI set");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDI => Do nothing");
					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("LDI => Do nothing");
					}
				}

				if (currIns == InstructionTypes.JMP) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[IO] = true;
						this.controlLines[J] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JMP => IO, J set");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JMP => Do nothing");
					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JMP => Do nothing");
					}
				}
				if (currIns == InstructionTypes.JC) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						if (this.getFlags().getCF()) {
							this.controlLines[IO] = true;
							this.controlLines[J] = true;
							EventLog.getEventLog().addEntry("JC => IO, J set since CF=1");
						} else {
							EventLog.getEventLog().addEntry("JC => IO, Do nothing since CF=0");
						}
						notifyControlLineChange();
					}

					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JC => Do nothing");
					}

					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JZ => Do nothing");
					}
				}
				if (currIns == InstructionTypes.JZ) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						if (this.getFlags().getZF()) {
							this.controlLines[IO] = true;
							this.controlLines[J] = true;
							EventLog.getEventLog().addEntry("JZ => IO, J set since ZF=1");
						} else {
							EventLog.getEventLog().addEntry("JZ => Do nothing since ZF=0");
						}
						notifyControlLineChange();

					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JZ => Do nothing");
					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("JZ => Do nothing");
					}
				}
				if (currIns == InstructionTypes.OUT) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[AO] = true;
						this.controlLines[OI] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("OUT => AO, OI set");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("OUT => Do nothing");
					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("OUT => Do nothing");
					}
				}
				if (currIns == InstructionTypes.HLT) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						this.controlLines[HLT] = true;
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("HLT => HLT set");

					}
					// No need to handle stepCount 4 and 5 since the clock can no longer advance
					// with HLT enabled
				}
				if (currIns == InstructionTypes.INVALID) {
					if (this.stepCount == 3) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Invalid Instruction => Do nothing");
					}
					if (this.stepCount == 4) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Invalid Instruction => Do nothing");

					}
					if (this.stepCount == 5) {
						this.resetAllControlLines();
						notifyControlLineChange();
						EventLog.getEventLog().addEntry("Invalid Instruction => Do nothing");
					}
				}
			}

			// Now we have set all of the falling edge signals, update the SAP accordingly
			// since OUT instructions do not rely on the clock
			if (this.controlLines[CO]) {
				this.bus.loadVal(this.programCounter.getVal());
				EventLog.getEventLog().addEntry("Program Counter value put onto bus (4 Bits)");
				this.notifyBusChange();
			}
			if (this.controlLines[RO]) {
				this.bus.loadVal((byte) this.RAM.memoryOut());
				EventLog.getEventLog().addEntry("RAM value put onto bus");
				this.notifyBusChange();
			}
			if (this.controlLines[IO]) {
				// Put 4 least significant bits of Instruction Register onto the bus
				this.bus.loadVal((byte) (0b00001111 & this.regIR.getVal()));
				this.notifyBusChange();
				EventLog.getEventLog().addEntry("Instruction Register value put onto bus (4 Bits)");
			}
			if (this.controlLines[AO]) {
				this.bus.loadVal(this.regA.getVal());
				this.notifyBusChange();
				EventLog.getEventLog().addEntry("A register value put onto bus");
			}
			if (this.controlLines[SU]) {
				this.notifyAChange();
			}
			if (this.controlLines[SO]) {
				this.bus.loadVal(this.adder.ALUOut(this.controlLines[SU]));
				this.notifyBusChange();
				EventLog.getEventLog().addEntry("ALU sum value put onto bus");
			}
			
			return;

		} else {
			// Meaning we are on the rising edge of the clock, handle all signals dependent
			// on a rising clock edge
			if (this.controlLines[FI]) {
				this.adder.flagsIn(this.controlLines[SU]);
				this.notifyFlagRegisterChange();
				EventLog.getEventLog().addEntry("Flags register updated");

			}
			if (this.controlLines[MI]) {
				this.regMAR.loadVal(this.bus.getVal());
				this.notifyMARChange();
				EventLog.getEventLog().addEntry("Memory adddress register read in from bus");

			}
			if (this.controlLines[CE]) {
				this.programCounter.counterEnable();
				this.notifyPCChange();
				EventLog.getEventLog().addEntry("Program counter incremented");

			}
			if (this.controlLines[HLT]) {
				Clock.getClock().setIsHalted(true); // TODO
			}
			if (this.controlLines[RI]) {
				this.RAM.memoryIn(this.bus.getVal());
				EventLog.getEventLog().addEntry("RAM read in from bus");
			}

			if (this.controlLines[II]) {
				this.regIR.loadVal(this.bus.getVal());
				this.notifyIRChange();
				EventLog.getEventLog().addEntry("Instruction register read in from bus");
			}
			if (this.controlLines[AI]) {
				this.regA.loadVal(this.bus.getVal());
				this.notifyAChange();
				EventLog.getEventLog().addEntry("A register read in from bus");
			}
			if (this.controlLines[BI]) {
				this.regB.loadVal(this.bus.getVal());
				this.notifyBChange();
				EventLog.getEventLog().addEntry("B register read in from bus");

			}
			if (this.controlLines[OI]) {
				this.regOut.loadVal(this.bus.getVal());
				this.notifyOutChange();
				EventLog.getEventLog().addEntry("Output register read in from bus");

			}
			if (this.controlLines[J]) {
				this.programCounter.loadVal((byte) (this.bus.getVal() & 0b1111));
				this.notifyPCChange();
				EventLog.getEventLog().addEntry("Program Counter changed from J flag");
			}

		}

	}

	// Getter Methods
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

}
