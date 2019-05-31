package BufferManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Vector;

import MiniSQL.BlockType;
import MiniSQL.Table;

public class BufferManager {
	
	private static int Max_Block = 1024;
	
	static private Block []Buffer = new Block[Max_Block];
	
//	static private List<Table> tables;
	
	public int LRU() {
		
	}
	
	static public Block GetNextBlock(Block b) {
		Block Next = new Block();
		Next.SetBlock(b.file, b.fileOffset + Block.Size, b.type);
		return Next;
	}
	
	static void AddBlock(Block b) {
		try {
			Buffer.addBlock(b);
		} catch (IOException e) {
			System.out.println("Error happened in the buffer");
			e.printStackTrace();
		}
	}
	
	public Block GetBlock(String file, int offset, BlockType Type) {
		Block b = null;
		for(int i=0; i<Buffer.Size(); i++)
		{
			b = Buffer.GetBlock(i);
			if((b.file == file) && (b.fileOffset == offset) && (b.type == Type)) 
				return b;
			
		}
		
		b = readFromFile(file, offset);
		AddBuffer(b);
		
		return b;
	}
	
	public Block readFromFile(String file, int offset) {
		Block b = new Block();
		b.SetBlock(file, offset, BlockType.Table);
		
		byte []Data = new byte[Block.Size];
		
		RandomAccessFile File;
		try {
			File = new RandomAccessFile(file, "rb");
			File.read(Data, offset, Block.Size);
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
		b.WriteData(Data, Block.Size);
		
		return b;
	}
	
	static public void writeToFile(Block b) {
		byte []Data = b.data;
		
		RandomAccessFile File;
		try {
			File = new RandomAccessFile(b.file, "wb");
			File.write(Data, b.fileOffset, Block.Size);
		} catch (FileNotFoundException e) {
			System.out.println("Error happened in openning the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error happened in transfering data");
			e.printStackTrace();
		}
	}
	
	static public void writeAllBackToFile() {
		while(buffer.Size() != 0) {
			Block b = buffer.removeBlcok(0);
			writeToFile(b);
			b = null;
		}
	}
	
	
}
