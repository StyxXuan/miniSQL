package RecordManager;

public class Table {
	public String TableName;
	public TableRow Row;
	public int RecordNum;
	
	public Table(String TableName, TableRow Row) {
		this.TableName = TableName; 
		this.Row = Row; 
		this.RecordNum = 0;
	}
}
