package RecordManager;

public class Table {
//	static int TableSize = 1000;
	
	public String TableName;
	public TableRow Row;
	public int RecordNum;
	
	public Table(String TableName, TableRow Row) {
		this.TableName = TableName; 
		this.Row = Row; 
		this.RecordNum = 0;
	}
}
