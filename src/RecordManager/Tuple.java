package RecordManager;

import java.util.Vector;

public class Tuple {
	int valid;
	Vector<String>Data;
	public Tuple() {
		valid = 0;
		Data = null;
	}
	
	public Tuple(int valid, Vector<String> data) {
		this.valid = valid;
		this.Data = data;
	}
	
	public int size() {
		int Length = 0;
		for(int i=0; i<Data.size(); i++) {
			Length += Data.get(i).length();
		}
		return Length + 4; //add the valid 4 byte of Integer
	}
	
	public String GetData(int index) {
		return Data.get(index);
	}
	
	public byte[] GetBytes() {
		String data = "";
		for(int i=0; i<Data.size(); i++)
			data += Data.get(i);
		
		return data.getBytes();
	}
	
//	public void GetTup
}
