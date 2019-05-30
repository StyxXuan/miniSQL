package MiniSQL;

import java.io.IOException;
import java.io.RandomAccessFile;


public class Block {
	static int Size = 4096;
	protected String file;
	protected int fileOffset;
	protected int spaceUsed;
	public boolean isDirty;
	public boolean isPined;
	protected byte[] data;
	public BlockType type;
	
	public Block() {
		this.file = null;
		this.fileOffset = 0;
		this.spaceUsed = 0;
		this.isDirty = false;
		this.isPined = false;
		this.data = null;
		this.type = BlockType.Table;
	}
	
	public Block(Block B){
		this.file = B.file;
		this.fileOffset = B.fileOffset;
		this.spaceUsed = B.spaceUsed;
		this.isDirty = B.isDirty;
		this.data = B.data;
		this.isPined = B.isPined;
		this.type = B.type;
	}
	
	public void SetBlock(String file, int fileOffset, BlockType type) {
		this.file = file;
		this.fileOffset = fileOffset;
		this.type = type;
	}
	
	public Boolean IsEmpty() {
		return (data != null);
	}
	
	public void WriteData(byte []data2write, int length) {
		this.isDirty = true;
		byte[] NewData = new byte[this.spaceUsed + length];
		for(int i=0; i<spaceUsed; i++)
			NewData[i] = this.data[i];
		
		for(int i=0; i<length; i++)
			NewData[i + this.spaceUsed] = data2write[i];
		
		this.spaceUsed += length;
		this.data = NewData;
	}
	
	@SuppressWarnings("resource")
	public void WriteBack() throws IOException {
		RandomAccessFile File = new RandomAccessFile(file, "wb");
		File.seek(fileOffset);
		File.write(data, fileOffset, spaceUsed);
	}
}
