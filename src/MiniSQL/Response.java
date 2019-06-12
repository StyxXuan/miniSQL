package MiniSQL;

import java.util.Vector;

import RecordManager.Tuple;
import RecordManager.Table;
public class Response {
	private boolean Affected;
	private double time;
	public Table table = new Table();
	public Vector<Tuple>Tups = new Vector<Tuple>();
	public Response(boolean Aff, double Time) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = null;
	}
	
	public Response(boolean Aff, double Time, Vector<Tuple>Tups) {
		this.Affected = Aff;
		this.time = Time;
		this.Tups = Tups;
	}
	public void PrintInfo(Table table)
	{
		int col = 0, row = 0, i , j, k, len = 0;
		row = this.Tups.size() + 1;
		col = this.table.Row.attrinum;
		for(i = 0; i < this.table.Row.attrinum; i++)
		{
			if(this.table.Row.attlist.get(i).attriName.length() > len)
				len = this.table.Row.attlist.get(i).attriName.length();
		}
		for(j = 0; j < row; j++)
		{
			for(i = 0; i < col; i++)
			{
				for(k = 1; k <= len; k++)
					System.out.print("-");
				System.out.print("+");
			}
			System.out.println();
			for(i = 0; i < col; i++)
			{
				if(j == 0)
				{
					String attri = String.format("%-" + len + "s", this.table.Row.attlist.get(i).attriName);
					System.out.print(attri + "|");
				}
				else
				{
					String tup = String.format("%-" + len + "s", Tups.elementAt(i).GetData(i));
					System.out.print(tup + "|");
				}
			}
			System.out.println();
			if(j == row - 1)
			{
				for(i = 0; i < col; i++)
				{
					for(k = 1; k <= len; k++)
						System.out.print("-");
					System.out.print("+");
				}
				System.out.println();
			}
		}
	}
	public void PrintInfor() {
		System.out.println("the request affected " + this.Affected);
		System.out.println("using time " + this.time);
	}
}
