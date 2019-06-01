package RecordManager;

import java.util.List;

public class TableRow {
	List<Attribute> attlist;
	int attrinum;
	TableRow(List<Attribute> attlist, int attrinum){
		this.attlist = attlist;
		this.attrinum = attrinum;
	}
}
