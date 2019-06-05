package RecordManager;

public class Tuple {
	int valid;
	byte []data;
	public Tuple() {
		valid = 0;
		data = null;
	}
	
	public Tuple(int valid, byte data[]) {
		this.valid = valid;
		this.data = data;
	}
	
	public int size() {
		return data.length + 4; //add the valid 4 byte of Integer
	}
}
