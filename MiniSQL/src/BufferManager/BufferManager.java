package BufferManager;

import java.io.IOException;
import java.util.Map;

import RecordManager.Table;

public class BufferManager {
	
	private static int Max_Block = 1024;
	
	
	static public Block []Buffer = new Block[Max_Block];
	static private int []Age = new int[Max_Block];
	
	static public Map<String, Table> tables;
	
	public void InitBuffer() {
		for(int i=0; i<Max_Block; i++) {
			Age[i] = 0;
			Buffer[i] = new Block();
		}
	}
	
	public void FlushAll() {
		for(int i=0; i<Max_Block; i++) {
			if(Buffer[i].isValid && Buffer[i].isDirty) {
				try {
					Buffer[i].WriteBack();
				} catch (IOException e) {
					System.out.println("Flush error");
				}
			}
		}
	}
	
	public int LRU() {
		int index = -1;
		int MIN = 10000;
		for(int i=0; i<Max_Block; i++){
			if((Age[i] < MIN) && (!Buffer[i].isPined)) {
				index = i;
				MIN = Age[i];
			}
		}
		
		return index;
	}
	
	public boolean BufferReplace(int index, String file, int offset) {
		if(Buffer[index].isDirty) {
			try {
				Buffer[index].WriteBack();
				Buffer[index].SetBlock(file, offset);
				Buffer[index].LoadBlock();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	public Block GetBlock(int index) {
		return Buffer[index];
	}
	
	public Block FindBlock(String file, int offset) {
		for(int i=0; i<Max_Block; i++){
			if(Buffer[i].file == file && Buffer[i].fileOffset == offset)
				return Buffer[i];
		}
		
		return null;
	}
}
