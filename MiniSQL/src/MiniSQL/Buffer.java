package MiniSQL;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;


public class Buffer{
	
	List<Block> buffer;
	
	private static int Max_Block = 1024;
	
	private int Cur_Size;
	
	public Buffer() {this.Cur_Size = 0;}
	
	boolean addBlock(Block b) throws IOException{
		if(Cur_Size < Max_Block-1) {
			buffer.add(b);
			Cur_Size++;
			return true;
		}else {
			return LRUreplace(b);
		}
	}
	
	boolean LRUreplace(Block b) throws IOException{
		for(int i=0; i<Cur_Size; i++)
		{
			Block ToDelete = buffer.get(i);
			if(!ToDelete.isPined) {
				if(ToDelete.isDirty){
					ToDelete.WriteBack();
				}
				buffer.remove(i);
				Cur_Size--;
				break;
			}
		}
		
		if(Cur_Size < Max_Block-1) {
			buffer.add(b);
			Cur_Size++;
			return true;
		}
		return false;
	}
	
	boolean isDirty(Block b){return b.isDirty;}
	
	boolean pin(Block b){return b.isPined;}
	
	public Block readFromFile(String file, int offset) throws IOException {
		byte []b = null;
		@SuppressWarnings("resource")
		RandomAccessFile File = new RandomAccessFile(file, "rb");
		File.write(b, offset, Block.Size);
		Block B = new Block();
		B.SetBlock(file, offset, BlockType.Table);
		B.WriteData(b, Block.Size);
		return B;
	}
	
	public void writeToFile(Block b, String file, int offset) throws IOException{
		if(isDirty(b)) {
			@SuppressWarnings("resource")
			RandomAccessFile File = new RandomAccessFile(file, "wb");
			File.seek(offset);
			File.write(b.data, offset, b.spaceUsed);
		}
	}
}
