package MiniSQL;

import java.io.IOException;

public interface BufferManager {
	
//	byte[] read(Address addr);
//	boolean write(Address addr, byte[] data);
//	Address findFreeSpace(String table);
//	boolean free(Address addr);
//	
//	
	
	Block readFromFile(String file, int offset) throws IOException;
	void writeToFile(Block b, String file, int offset) throws IOException;
}
