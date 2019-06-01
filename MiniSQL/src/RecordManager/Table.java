package RecordManager;

public class Table {
	String TableName;
	TableRow Row;
	int RecordNum;
	
	public Table(String TableName, TableRow Row) {
		this.TableName = TableName; 
		this.Row = Row; 
		this.RecordNum = 0;
	}
}
