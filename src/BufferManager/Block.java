package BufferManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;



public class Block {
	public static int Size = BufferManager.Max_Block;
	String file = "";
	public int fileOffset = 0;
	public boolean isDirty = false;
	public boolean isPined = false;
	public boolean isValid = true;
	public byte[] data = new byte[Size];
	
	public void SetBlock(String file, int fileOffset) {
		this.file = file;
		this.fileOffset = fileOffset;
	}
	
	public void LoadBlock() {
		RandomAccessFile File;
		try {
			File = new RandomAccessFile(file, "rw");
			File.seek(fileOffset);
			File.read(this.data, 0, Block.Size);
			File.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
	}
	
	public void WriteData(byte []data2write, int length, int off) {
		if(off + length > Block.Size){
			System.out.println("exceed the block length");
			return;
		}
		
		this.isDirty = true;
		for(int i=off; i<off+length; i++)
			this.data[i] = data2write[i - off];
	}
	
	public void WriteBack() throws IOException {
		RandomAccessFile File = new RandomAccessFile(file, "rw");
		File.seek(fileOffset);
		File.write(data, 0, Block.Size);
		this.isDirty = false;
		File.close();
	}
	
	public int GetInt(int offset) {
		byte[] ByteInt = new byte[4];
		ByteInt[0] = this.data[offset + 0];
		ByteInt[1] = this.data[offset + 1];
		ByteInt[2] = this.data[offset + 2];
		ByteInt[3] = this.data[offset + 3];
		
		ByteArrayInputStream BIP = new ByteArrayInputStream(ByteInt);
		DataInputStream DIP = new DataInputStream(BIP);
		int res = 0;
		try {
			res = DIP.readInt();
		} catch (IOException e) {
			System.out.println("error happens in reading int");
			e.printStackTrace();
		}
		return res;	
	}
	
	public float GetFloat(int offset) {
		byte[] ByteFloat = new byte[4];
		ByteFloat[0] = this.data[offset + 0];
		ByteFloat[1] = this.data[offset + 1];
		ByteFloat[2] = this.data[offset + 2];
		ByteFloat[3] = this.data[offset + 3];
		
		ByteArrayInputStream BIP = new ByteArrayInputStream(ByteFloat);
		DataInputStream DIP = new DataInputStream(BIP);
		float res = 0;
		try {
			res = DIP.readFloat();
		} catch (IOException e) {
			System.out.println("error happens in reading float");
			e.printStackTrace();
		}
		return res;	
	}
	
	public String GetString(int offset, int length) {
		byte[] ByteString = new byte[length];
		for(int i=0; i<length; i++)
			ByteString[i] = data[i+offset]; 
		
		String Mid = null;
		try {
			Mid = new String(ByteString, "UTF-8");
			return Mid;
		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException");
		}
		return Mid;
	}
	
	public void WriteInt(int num, int offset) {
		ByteArrayOutputStream BOP = new ByteArrayOutputStream();
		DataOutputStream DOP = new DataOutputStream(BOP);
		
		try {
			DOP.writeInt(num);
		} catch (IOException e) {
			System.out.println("error happens in writing int");
			e.printStackTrace();
		}
		
		byte[] temp = BOP.toByteArray();
		data[offset] = temp[0];
		data[offset + 1] = temp[1];
		data[offset + 2] = temp[2];
		data[offset + 3] = temp[3];
		this.isDirty = true;
	}
	
	public void WriteFloat(float num, int offset) {
		ByteArrayOutputStream BOP = new ByteArrayOutputStream();
		DataOutputStream DOP = new DataOutputStream(BOP);
		
		try {
			DOP.writeFloat(num);;
		} catch (IOException e) {
			System.out.println("error happens in writing float");
			e.printStackTrace();
		}
		
		byte[] temp = BOP.toByteArray();
		data[offset] = temp[0];
		data[offset + 1] = temp[1];
		data[offset + 2] = temp[2];
		data[offset + 3] = temp[3];
		this.isDirty = true;
	}
	
	public void WriteString(String S, int offset) {
		byte []temp = S.getBytes();
		int length = temp.length;
		
		for(int i=0; i<length; i++)
			data[i + offset] = temp[i];
		
	}
	
}
