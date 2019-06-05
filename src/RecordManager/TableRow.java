package RecordManager;

import java.util.List;

public class TableRow {
	public List<Attribute> attlist;
	public int attrinum;
	public TableRow(List<Attribute> attlist, int attrinum){
		this.attlist = attlist;
		this.attrinum = attrinum;
	}
	
	public int RowSize() {
		int size = 0;
		for(int i=0; i<attrinum; i++) {
			size += attlist.get(i).length;
		}
		return size;
	}
	
}
