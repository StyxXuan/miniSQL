
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import BufferManager.Block;
import BufferManager.BufferManager;
import RecordManager.RecordManager;
import RecordManager.Table;
import RecordManager.Tuple;
import RecordManager.Attribute;
import RecordManager.FieldType;
import MiniSQL.Interpreter;
import MiniSQL.Request;
import MiniSQL.Response;


public class MiniTest {
	@SuppressWarnings("null")
	public static void main(String[] args) throws IOException{
//		String sql = "";
//		boolean Loop = true;
//		BufferedReader bufferedReader = null;
//		while(Loop) {
//			try {
//				System.out.println("input ");
//				String State = "";
//				State = bufferedReader.readLine();
//				sql += State;
//				if(sql.contains(";")) {
//					boolean check = Interpreter.checkSyntax(sql);
//					if(!check)
//						throw new Exception("SyntaxException");
//					
//					Request req = Interpreter.parse(sql);
//					check = Interpreter.checkLexeme(req);
//					if(!check)
//						throw new Exception("LexemeException");
//					
//					Response resp = Interpreter.excute(req);
//					resp.PrintInfor();
//					sql = "";
//				}
//			}
//			catch(Exception Exp) {
//				System.out.println(Exp.getMessage());
//			}
//		}
		
		BufferManager.InitBuffer();
//		Attribute Att = new Attribute(FieldType.INT, "NewInt");
//		
//		List<Attribute>Atts =  new ArrayList<Attribute>();;
//		
//		Atts.add(Att);
//		
//		RecordManager.createTable("NewOne", Atts);
//		
//		System.out.println("Create right");
//		
//		Table table = BufferManager.tables.get("NewOne");
//		
//		Vector<String>ATTs = new Vector<String>();
//		ATTs.add("12345");
//		
//		Tuple tup = new Tuple(1, ATTs);
//		
//		RecordManager.insert(table, tup);
//		
//		System.out.println("insert right");
		
		System.out.println(BufferManager.tableFileNameGet("NewOne"));
		
		Block b = BufferManager.FindBlock(BufferManager.tableFileNameGet("NewOne"), 0);
		
		System.out.println("b.read " + b.GetInt(0));
		
		System.out.println("b.byte " + b.data[0]);
		
		System.out.println("b.read " + b.GetInt(4));
		
		System.out.println("b.byte " + b.data[1]);
		
		BufferManager.FlushAll();
		
		System.out.println("FlushAll right");
	}
}
