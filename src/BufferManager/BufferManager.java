package BufferManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RecordManager.Attribute;
import RecordManager.FieldType;
import RecordManager.Table;
import RecordManager.TableRow;

public class BufferManager {
	
	public static int Max_Block = 1024;
	
	
	static public Block []Buffer = new Block[Max_Block];
	static private int []Age = new int[Max_Block];
	
	static public Map<String, Table> tables;
	
	static public Map<String, String> indexs;// first is the IndexName, second is the "table_attribute"
	
	static public void Init() {
		InitBuffer();
		InitTables();
		InitIndex();
	}
	
	static public void InitIndex() {
		indexs = new HashMap<String, String>();
		RandomAccessFile File;
		try {
			File = new RandomAccessFile(indexcatlogFileNameGet(), "rw");
			int index = 0;
			int N = File.read();
			index += 4;
			for(int i=0; i<N; i++) {
				File.seek(index);
				int Indexlength = File.read();
				index +=4;
				byte []b = new byte[Indexlength]; 
				File.seek(index);
				File.read(b, 0, Indexlength);
				String IndexName = new String(b, "UTF-8");
				index += Indexlength;
				File.seek(index);
				int TableAttLength = File.read();
				index +=4;
				byte []a = new byte[TableAttLength]; 
				File.seek(index);
				File.read(a, 0, TableAttLength);
				String TableAtt = new String(a, "UTF-8");
				index += TableAttLength;
				indexs.put(IndexName, TableAtt);
			}
			File.close();
		} catch (FileNotFoundException e) {
			System.out.println("file not file exception");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("meet an exception");
			e.printStackTrace();
		}
		
	}
	
	
	static public void InitBuffer() {
		for(int i=0; i<Max_Block; i++) {
			Age[i] = 0;
			Buffer[i] = new Block();
			Buffer[i].isValid = true;
		}
	}
	
	static public void SaveTables() {
		RandomAccessFile File;
		try {
			int index = 0;
			File = new RandomAccessFile(catlogFileNameGet(), "rw");
			File.write(tables.size());
			index += 4;
			
			for (Map.Entry<String, Table> entry : tables.entrySet()) {	
				Table table = entry.getValue();
				byte []b = table.TableName.getBytes("UTF-8");
				File.seek(index);
				File.write(b.length);
				index +=4;
				File.seek(index);
				File.write(b, 0, b.length);
				index += b.length;
				File.seek(index);
				File.write(table.RecordNum);
				index += 4;
				File.seek(index);
				File.write(table.Row.attrinum);
				index += 4;
				
				for(int j=0; j<table.Row.attrinum; j++) {
					Attribute att = table.Row.attlist.get(j);
					File.seek(index);
					File.write(FieldType.toInt(att.Type));
					index+=4;
					byte []attname = att.attriName.getBytes("UTF-8");
					File.seek(index);
					File.write(attname.length);
					index += 4;
					File.seek(index);
					File.write(attname, 0, attname.length);
					index += attname.length;
					File.seek(index);
					File.write(att.length);
					index += 4;
					File.seek(index);
					File.write(att.offset);
					index += 4;
					File.seek(index);
					File.write((att.isPrimary == true)? 1:0);
					index += 4;
					File.seek(index);
					File.write((att.isUnique == true)? 1:0);
					index += 4;
					File.seek(index);
					File.write((att.hasIndex == true)? 1:0);
					index += 4;
				}
			}
			File.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
	}
	
	static public void SaveIndexs() {
		RandomAccessFile File;
		try {
			int index = 0;
			File = new RandomAccessFile(indexcatlogFileNameGet(), "rw");
			File.write(indexs.size());
			index += 4;
			
			for (Map.Entry<String, String> entry : indexs.entrySet()) {	
				String IndexName = entry.getKey(); 
				String TableAtt = entry.getValue();
				byte []b = IndexName.getBytes("UTF-8");
				File.seek(index);
				File.write(b.length);
				index +=4;
				File.seek(index);
				File.write(b, 0, b.length);
				index += b.length;
				byte []a = TableAtt.getBytes("UTF-8");
				File.seek(index);
				File.write(a.length);
				index += 4;
				File.seek(index);
				File.write(a, 0, a.length);
				index += a.length;
			}
			File.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
	}
	
	static public void InitTables() {
		tables = new HashMap<String, Table>();
		RandomAccessFile File;
		try {
			int index = 0;
			File = new RandomAccessFile(catlogFileNameGet(), "rw");
			int N = File.read();
			index += 4;
			for(int i = 0; i<N; i++) {
				File.seek(index);
				int tablenamelength = File.read();
				index +=4;
				byte []b = new byte[tablenamelength]; 
				File.seek(index);
				File.read(b, 0, tablenamelength);
				String TableName = new String(b, "UTF-8");
				index += tablenamelength;
				File.seek(index);
				int RecNum = File.read();
				index += 4;
				File.seek(index);
				int AttNum = File.read();
				index += 4;
				List<Attribute> attlist = new ArrayList<Attribute>();
			
				for(int j=0; j<AttNum; j++) {
					File.seek(index);
					int Type = File.read();
					index+=4;
					File.seek(index);
					int AttNameLen = File.read();
					byte []attname = new byte[AttNameLen];
					index += 4;
					File.seek(index);
					File.read(attname, 0, AttNameLen);
					String AttName = new String(attname, "UTF-8");
					index += AttNameLen;
					File.seek(index);
					int length = File.read();
					index += 4;
					File.seek(index);
					int offset = File.read();
					index += 4;
					File.seek(index);
					int isPrimary = File.read();
					index += 4;
					File.seek(index);
					int isUnique = File.read();
					index += 4;
					File.seek(index);
					int hasIndex = File.read();
					index += 4;
					Attribute Att = new Attribute(FieldType.GetType(Type), AttName, length, offset);
					Att.isPrimary = (isPrimary == 1);
					Att.isUnique = (isUnique == 1);
					Att.hasIndex = (hasIndex == 1);
					attlist.add(Att);
				}
				TableRow row = new TableRow(attlist, AttNum);
				Table table = new Table(TableName, row, RecNum);
				tables.put(TableName, table);
			}
			File.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
	}
	
	static public void quit() throws IOException {
		FlushAll();
		SaveTables();
		SaveIndexs();
	}
	
	static public void FlushAll() {
		for(int i=0; i<Max_Block; i++) {
			if(Buffer[i].isDirty) {
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
				Buffer[i].isValid = true;
			}
		}
	}
	
	static public void RemoveBlockFromBuffer(String FileName) {
		for(int i=0; i<Max_Block; i++) {
			if(Buffer[i].file == FileName) {
				Buffer[i].isValid = true;
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
			if(Buffer[i].isValid == true) {
				return i;
			}
			if((Age[i] < MIN) && (!Buffer[i].isPined)) {
				index = i;
				MIN = Age[i];
			}
		}
		
		return index;
	}
	
	static public boolean BufferReplace(int index, String file, int offset) {
		try {
			if(Buffer[index].isDirty && !Buffer[index].isValid) 
				Buffer[index].WriteBack();
				
			Buffer[index].SetBlock(file, offset - offset % Max_Block);
			Buffer[index].LoadBlock();
			Age[index] = (int)System.currentTimeMillis();
		} catch (IOException e) {
			return false;
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
	
	static public Block FindBlock(String FileName, int offset) {
		int BlockOff = offset / Max_Block;
		for(int i=0; i<Max_Block; i++){
			int BufferBlockOff = Buffer[i].fileOffset / Max_Block;
			if((Buffer[i].file.equals(FileName)) && (BufferBlockOff == BlockOff))
				return Buffer[i];
		}
		
		int index = LRU();
		if(!BufferReplace(index, FileName, offset)) {
			System.out.println("Buffer replace error");
			return null;
		}
		
		return GetBlock(index);
	}
	
	static public String tableFileNameGet(String filename)
	{
		return "DBFile/TABLE_FILE_" + filename + ".SQLTable";
	}
	
	static public String indexFileNameGet(String filename)
	{
		return "DBFile/INDEX_FILE_" + filename + ".SQLIndex";
	}
	
	static public String catlogFileNameGet() {
		return "DBFile/CATLOG_FILE.SQLCAT";
	}
	
	static public String indexcatlogFileNameGet() {
		return "DBFile/IndexCat_File.SQLCAT";
	}
}
