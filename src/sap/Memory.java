package sap;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import interfaces.RAMObserver;
import interfaces.Register;

public class Memory {
	// Maintain the RAM values as a byte array
	private byte[] data;

	// Memory maintains a reference to the memory address register
	private Register MAR;

	// Memory is observable, so it must maintain a list of observers
	private List<RAMObserver> observers;

	public Memory(Register MAR) {
		this.data = new byte[16];
		this.MAR = MAR;
		this.observers = new ArrayList<RAMObserver>();

		// Load garbage values into memory
		for (int i = 0; i < 16; i++) {
			this.data[i] = (byte) ThreadLocalRandom.current().nextInt(0, 254);
		}

	}

	// loads val into the memory address held in the Memory Address Register
	public void memoryIn(byte val) {
		this.data[this.MAR.getVal()] = val;
		this.notifyObservers(this.MAR.getVal());
	}

	// Manually changes a memory address, used in RAM Widget
	public void manualValueChange(int address, byte newVal) {
		this.data[address] = newVal;
		this.notifyObservers(address);
	}

	// Returns the data stored in the address held in the Memory Address Register
	public int memoryOut() {
		return this.data[(int) this.MAR.getVal()];
	}

	// Returns the memory contents
	public byte[] getRAM() {
		return this.data;
	}

	// Methods for implementing the observable design pattern
	public void addRAMObserver(RAMObserver o) {
		if (o == null) {
			return;
		}
		this.observers.add(o);
	}

	public void removeRAMObserver(RAMObserver o) {
		if (o == null) {
			return;
		}
		this.observers.remove(o);
	}

	private void notifyObservers(int address) {
		for (RAMObserver o : observers) {
			o.valChanged(address);
		}
	}

}
