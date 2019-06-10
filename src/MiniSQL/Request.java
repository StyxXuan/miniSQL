package MiniSQL;

import java.util.Vector;

import RecordManager.Condition;
import RecordManager.Attribute;
/*
 * types:
 * 		create table: 1
 * 		create index: 2
 * 		select all: 3
 * 		select condition: 4
 * 		drop table: 5
 * 		drop index: 6
 * 		insert: 7
 * 		delete all: 8
 * 		delete condition: 9 
 * 		exit: 10
 * 		execfile: 11 
 */
public class Request 
{	
	public int type = 0;
	String file2excute = null;
	String tablename = null;
	String primarykey = null;
	String indexname = null;
	String attributename = null;
	Vector<String> attriSelect = null;
	Vector<String> insertValue = null;
//	Vector<Condition> conditionVec = null;
	Condition condition = null;
	Vector<Attribute> attriVec = null;
	int primarykeylocation = -1;
	boolean exit = false;
	
	public Request()
	{
		
	}
	public Request(int tp, Vector<String> parses, Condition con, Vector<Attribute> attri, int location)
	{
		type = tp;
		switch(type)
		{
		case 1:
			this.tablename = parses.elementAt(0);
			this.primarykey = parses.elementAt(1);
			this.attriVec = attri;
			this.primarykeylocation = location;
			break;
		case 2:
			this.indexname = parses.elementAt(0);
			this.tablename = parses.elementAt(1);
			this.attributename = parses.elementAt(2);
			break;
		case 3:
			this.tablename = parses.elementAt(0);
			break;
		case 4:
			this.tablename = parses.elementAt(0);
			this.condition = con;
			break;
		case 5:
			this.tablename = parses.elementAt(0);
			break;
		case 6:
			this.indexname = parses.elementAt(0);
			this.tablename = parses.elementAt(1);
			this.attributename = parses.elementAt(2);
			break;
		case 7:
			this.tablename = parses.elementAt(0);
			for(int i = 1; i <= location; i++)
			{
				this.insertValue.addElement(parses.elementAt(i));
			}
			break;
		case 8:
			this.tablename = parses.elementAt(0);
			break;
		case 9:
			this.tablename = parses.elementAt(0);
			this.condition = con;
			break;
		case 10:
			this.exit = true;
			break;
		case 11:
			this.file2excute = parses.elementAt(0);
			break;
		}
	}
	
	
}
