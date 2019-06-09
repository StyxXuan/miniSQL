package BufferManager;

import java.io.IOException;
import java.util.Map;

import RecordManager.Table;

public class BufferManager {
	
	public static int Max_Block = 1024;
	
	
	static public Block []Buffer = new Block[Max_Block];
	static private int []Age = new int[Max_Block];
	
	static public Map<String, Table> tables;
	
	static public void InitBuffer() {
		for(int i=0; i<Max_Block; i++) {
			Age[i] = 0;
			Buffer[i] = new Block();
		}
	}
	
	static public void FlushAll() {
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
	
	static public void RemoveBlockFromBuffer(Table table) {
		for(int i=0; i<Max_Block; i++) {
			if(Buffer[i].file == tableFileNameGet(table.TableName)) {
				Buffer[i].isValid = false;
			}
		}
	}
	
	static public Block GetNextBlock(Block b) {
		return FindBlock(b.file, b.fileOffset + Max_Block);
	}
	
	static public int LRU() {
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
	
	static public boolean BufferReplace(int index, String file, int offset) {
		if(Buffer[index].isDirty) {
			try {
				Buffer[index].WriteBack();
				Buffer[index].SetBlock(file, offset - offset % Max_Block);
				Buffer[index].LoadBlock();
				Age[index] = (int)System.currentTimeMillis();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}
	
	static public Block GetBlock(int index) {
		return Buffer[index];
	}
	
	static public String FindAtt(Table table, String field, String file, int offset) {
		Block b = BufferManager.FindBlock(file, offset);
		int length = -1, BlockOff = 0;
		for(int i=0; i<table.Row.attrinum; i++){
			if(table.Row.attlist.get(i).attriName == field) {
				length = table.Row.attlist.get(i).length;
				break;
			}
			BlockOff += table.Row.attlist.get(i).length;
		}
		if(length == -1) 
			return "NotFound";
		
		return b.GetString(BlockOff, length);
	}
	
	static public Block FindBlock(String file, int offset) {
		for(int i=0; i<Max_Block; i++){
			if(Buffer[i].file == file && Buffer[i].fileOffset <= offset && Buffer[i].fileOffset < offset + Max_Block)
				return Buffer[i];
		}
		
		int index = LRU();
		if(!BufferReplace(index, file, offset)) {
			System.out.println("Buffer replace error");
			return null;
		}
		
		return GetBlock(index);
	}
	
	public static String tableFileNameGet(String filename)
	{
		return "TABLE_FILE_" + filename + ".SQLTable";
	}
	
	public static String indexFileNameGet(String filename)
	{
		return "INDEX_FILE_" + filename + ".SQLIndex";
	}
}
