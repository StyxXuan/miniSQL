package MiniSQL;

public interface BufferManager {
	
	byte[] read(Address addr);
	boolean write(Address addr, byte[] data);
	Address findFreeSpace(String table);
	boolean free(Address addr);
	
	
	
	Block readFromFile(String file, int offset);
	boolean writeToFile(Block b, String file, int offset);
}
