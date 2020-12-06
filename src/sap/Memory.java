package sap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Memory {
	private byte[] data;
	private Register MAR;
	private List<RAMObserver> observers;
	public Memory() {
		this.data = new byte[16];
		this.MAR = new Register4Bit();
		this.observers = new ArrayList<RAMObserver>();
		
		// Load garbage values into memory
		for (int i = 0; i < 16; i++) {
			this.data[i] = (byte) ThreadLocalRandom.current().nextInt(0, 254);
		}
	}
	
	public void memoryIn(byte val) {
		this.data[this.MAR.getVal()] = val;
		this.notifyObservers(this.MAR.getVal());
	}
	
	public void manualValueChange(int address, byte newVal) {
		this.data[address] = newVal;
		this.notifyObservers(address);
	}
	
	public int memoryOut() {
		return this.data[this.MAR.getVal()];
	}
	
	public byte[] getRAM() {
		return this.data;
	}
	
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
		for (RAMObserver o: observers) {
			o.valChanged(address);
		}
	}
	
	

}
