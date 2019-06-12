package RecordManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class Tuple {
	int valid;
	Vector<String>Data;
	public Tuple() {
		valid = 0;
		Data = new Vector<String>();
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
	
	
	
	public byte[] GetBytes(Table table) {
		byte[] data = new byte[table.Row.size()];
		int index = 0;
		for(int i=0; i<table.Row.attrinum; i++)
		{
			
			if(table.Row.attlist.get(i).Type == FieldType.INT) {
				int num = Integer.valueOf(Data.get(i));
				ByteArrayOutputStream BOP = new ByteArrayOutputStream();
				DataOutputStream DOP = new DataOutputStream(BOP);
				try {
					DOP.writeInt(num);
				} catch (IOException e) {
					System.out.println("error happens in writing int");
					e.printStackTrace();
				}
				
				byte[] temp = BOP.toByteArray();
				data[index++] = temp[0];
				data[index++] = temp[1];
				data[index++] = temp[2];
				data[index++] = temp[3];
			}else if(table.Row.attlist.get(i).Type == FieldType.FLOAT) {
				float num = Float.valueOf(Data.get(i));
				ByteArrayOutputStream BOP = new ByteArrayOutputStream();
				DataOutputStream DOP = new DataOutputStream(BOP);
				try {
					DOP.writeFloat(num);
				} catch (IOException e) {
					System.out.println("error happens in writing int");
					e.printStackTrace();
				}
				
				byte[] temp = BOP.toByteArray();
				data[index++] = temp[0];
				data[index++] = temp[1];
				data[index++] = temp[2];
				data[index++] = temp[3];
			}else if(table.Row.attlist.get(i).Type == FieldType.STRING) {
				byte []mid =  new byte[table.Row.attlist.get(i).length];
				mid = Data.get(i).getBytes();
				for(int j = 0; j<table.Row.attlist.get(i).length; j++)
					data[index++] = mid[j];
			}
		}
		
		return data;
	}
	

}
