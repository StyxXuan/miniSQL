package MiniSQL;
import java.io.IOException;
import java.util.List;


public class Buffer {
	
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
}
