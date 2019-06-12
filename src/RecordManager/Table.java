package RecordManager;

public class Table {
//	static int TableSize = 1000;
	
	public String TableName;
	public TableRow Row ;
	public int RecordNum;
	
	public Table() {}
	
	public Table(String TableName, TableRow Row) {
		this.TableName = TableName; 
		this.Row = Row; 
		this.RecordNum = 0;
	}
	
	public Table(String TableName, TableRow Row, int RecNum) {
		this.TableName = TableName; 
		this.Row = Row; 
		this.RecordNum = RecNum;
	}
	
	public Attribute GetAttribute(String AttriName) {
		return Row.GetAtt(AttriName);
	}
}
