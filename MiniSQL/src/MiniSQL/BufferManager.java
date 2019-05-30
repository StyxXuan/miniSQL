package MiniSQL;

import java.io.IOException;
import java.util.List;

public class BufferManager {
	
	static private Buffer buffer;
	
	static private List<Table> tables;
	
	static public byte[] read(Address addr) {
		return null;
	}
	static public boolean write(Address addr, byte[] data) {
		return false;
	}
	static public Address findFreeSpace(String table) {
		return null;
	}
	static public boolean free(Address addr) {
		return false;
	}
	
	
	
	static public Block readFromFile(String file, int offset) throws IOException {
		return null;
	}
	static public void writeToFile(Block b, String file, int offset) throws IOException {
	}
}
