package RecordManager;

import java.util.List;

public class TableRow {
	public List<Attribute> attlist;
	public int attrinum;
	public TableRow( List<Attribute> attlist, int attrinum){
		this.attlist = attlist;
		this.attrinum = attrinum;
	}
	
	public FieldType GetType(String AttName) {
		for(int i=0; i<attlist.size(); i++) {
			if(attlist.get(i).attriName == AttName)
				return attlist.get(i).Type;
		}
		return FieldType.Empty;
	}
	
	public int GetIndex(String AttName) {
		for(int i=0; i<attlist.size(); i++) {
			if(attlist.get(i).attriName == AttName)
				return i;
		}
		return -1;
	}
	
	public int size() {
		int Size = 0;
		for(int i=0; i<attlist.size(); i++) {
			if(attlist.get(i).Type == FieldType.INT)
				Size += 4;
			else if(attlist.get(i).Type == FieldType.FLOAT)
				Size += 4;
			else if(attlist.get(i).Type == FieldType.STRING)
				Size += attlist.get(i).length;
		}
		
		return Size;
	}
}
