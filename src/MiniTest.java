
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
		String sql = "";
		boolean Loop = true;
		BufferedReader bufferedReader = null;
		while(Loop) {
			try {
				System.out.println("input ");
				String State = "";
				State = bufferedReader.readLine();
				sql += State;
				if(sql.contains(";")) {
					boolean check = false;
//					boolean check = Interpreter.checkSyntax(sql);
					if(!check)
						throw new Exception("SyntaxException");
					
					Request req = Interpreter.parse(sql);
//					check = Interpreter.checkLexeme(req);
					if(!check)
						throw new Exception("LexemeException");
					
					Response resp = Interpreter.excute(req);
					resp.PrintInfor();
					sql = "";
				}
			}
			catch(Exception Exp) {
				System.out.println(Exp.getMessage());
			}
		}
	}
}
