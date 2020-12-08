package sap;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Memory {
	private byte[] data;
	private Register MAR;
	private List<RAMObserver> observers;
	public Memory(Register MAR) {
		this.data = new byte[16];
		this.MAR = MAR;
		this.observers = new ArrayList<RAMObserver>();
		
		// Load garbage values into memory
		for (int i = 0; i < 16; i++) {
			//this.data[i] = (byte) ThreadLocalRandom.current().nextInt(0, 254);
			this.data[i] = 0;
		}
		
		//TODO delete this
		this.data[0] = 0b01010001;
		this.data[1] = 0b00101110;
		this.data[2] = (byte) 0b11100000;
		this.data[3] = 0b01001010;
		this.data[4] = 0b01100001;
		this.data[14] = 0b00000001;
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
		return this.data[(int) this.MAR.getVal()];
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
