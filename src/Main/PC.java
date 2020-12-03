package Main;

public class PC implements Register{
	private byte content;
	
	public PC() {
		this.content = 0;
	}

	@Override
	public void loadVal(byte newVal) {
		this.content = newVal;
	}

	@Override
	public byte getVal() {
		return this.content;
	}
	
	public void counterEnable() {
		if (this.content == 15) {
			this.content = 0;
		} else {
			this.content++;
		}
	}

	@Override
	public void clear() {
		this.content = 0;
	}
}
