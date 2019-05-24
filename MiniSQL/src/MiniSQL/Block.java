package MiniSQL;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Block {
	String file;
	int fileOffset;
	int spaceUsed;
	boolean isDirty;
	boolean isPined;
	byte[] data;
	
	public Block(String file, int fileOffset, int spaceUsed, boolean isDirty, boolean isPined, byte[] data) {
		this.file = file;
		this.fileOffset = fileOffset;
		this.spaceUsed = spaceUsed;
		this.isDirty = isDirty;
		this.data = data;
		this.isPined = isPined;
	}
	
	@SuppressWarnings("resource")
	public void WriteBack() throws IOException {
		RandomAccessFile File = new RandomAccessFile(file, "wb");
		File.seek(fileOffset);
		File.write((byte)(spaceUsed));
		File.write(data, fileOffset+4, spaceUsed);
	}
}
