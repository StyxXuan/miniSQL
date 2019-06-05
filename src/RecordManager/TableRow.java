package RecordManager;

import java.util.Map;

public class TableRow {
	public Map<String, Attribute> attlist;
	public Map<String, Integer> Offset;
	public int attrinum;
	public TableRow(Map<String, Attribute> attlist, int attrinum){
		this.attlist = attlist;
		this.attrinum = attrinum;
		for(int i=0; i<attlist.size(); i++) {
			
		}
	}
	
	public FieldType GetType(String AttName) {
		return attlist.get(AttName).Type;
	}
	
}
