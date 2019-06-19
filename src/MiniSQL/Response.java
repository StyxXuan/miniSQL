package MiniSQL;

import java.util.Vector;

import RecordManager.Tuple;
import RecordManager.FieldType;
import RecordManager.Table;
public class Response {
	public boolean Affected;
	public double time;
	public Table table = new Table();
	public Vector<Tuple>Tups = new Vector<Tuple>();
	public boolean isSelect;
	public Response(boolean Aff, double Time) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = null;
		this.isSelect = false;
	}
	
	public Response(boolean Aff, double Time, Vector<Tuple>Tups, Table table) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = Tups;
		this.table = table;
		this.isSelect = true;
	}
	
	public void PrintInfo(Table table)
	{
		int col = 0, row = 0, i , j, k;
		row = this.Tups.size() + 1;
		col = table.Row.attrinum;
		int []len = new int[col];
		for(i = 0; i < table.Row.attrinum; i++)
		{
			if(table.Row.attlist.get(i).Type == FieldType.INT || table.Row.attlist.get(i).Type == FieldType.FLOAT)
				len[i] = 10;
			else if(table.Row.attlist.get(i).Type == FieldType.STRING)
			{
				len[i] = table.Row.attlist.get(i).attriName.length();
				if(table.Row.attlist.get(i).length > len[i])
					len[i] = table.Row.attlist.get(i).length;
			}
		}
		for(j = 0; j < row; j++)
		{
			System.out.print("+");
			for(i = 0; i < col; i++)
			{
				for(k = 1; k <= len[i]; k++)
					System.out.print("-");
				System.out.print("+");
			}
			System.out.println();
			System.out.print("|");
			for(i = 0; i < col; i++)
			{
				if(j == 0)
				{
					String attri = String.format("%-" + len[i] + "s", this.table.Row.attlist.get(i).attriName);
					System.out.print(attri + "|");
				}
				else
				{
					String tup = String.format("%-" + len[i] + "s", Tups.elementAt(j - 1).GetData(i));
					System.out.print(tup + "|");
				}
			}
			System.out.println();

			if(j == row - 1)
			{
				System.out.print("+");
				for(i = 0; i < col; i++)
				{
					for(k = 1; k <= len[i]; k++)
						System.out.print("-");
					System.out.print("+");
				}
				System.out.println();
			}
		}
		System.out.println("the request affected " + this.Affected);
		System.out.println("using time " + this.time);
		System.out.println();
	}
	
	public void PrintInfor() {
		System.out.println("the request affected " + this.Affected);
		System.out.println("using time " + this.time);
		System.out.println();
	}
}
