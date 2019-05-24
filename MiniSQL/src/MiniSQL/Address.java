package MiniSQL;

public class Address {
	String file;
	int fileOffset;
	int blockOffset;
	
	public Address(String file, int fileOffset, int blockOffset) {
		this.file = file;
		this.fileOffset = fileOffset;
		this.blockOffset = blockOffset;
	}
}
