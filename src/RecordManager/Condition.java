package RecordManager;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javafx.util.Pair;

public class Condition
{
	enum Operation{
		EQUAL, NOT_EQUAL, LESS, MORE, LESS_EQUAL, MORE_EQUAL;
	}

	Vector<String> Attributes;
	Vector<String> Numbers;
	
	Vector<Operation> Ops;
	Vector<String>Conjunctions;
	
	public Condition(String AttName, String ToCampare, Operation Op) {
		Attributes.addElement(AttName);
		Numbers.addElement(ToCampare);
		Ops.addElement(Op);
		Conjunctions = null;
	}
	
	public Condition(Vector<String> Attributes, Vector<String> Numbers, Vector<Operation> Ops, Vector<String>Conjunctions) {
		this.Attributes = Attributes;
		this.Numbers = Numbers;
		this.Ops = Ops;
		this.Conjunctions = Conjunctions;
	}
	
	public boolean Satisfy(Tuple tup, TableRow Row) {
		boolean res = false;
		
		return res;
	}
	
	public boolean Satisfy(Tuple tup, TableRow Row, String Attribute, String Number, Operation Op){
		FieldType Type = Row.GetType(Attribute);
		boolean res = false;
		if(Op == Operation.EQUAL) {
			if(Type == FieldType.FLOAT) {
				float num = FLoat.parseFloat(Number);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
			}else if(Type == FieldType.STRING) {
				
			}
		}else if(Op == Operation.NOT_EQUAL) {
			if(Type == FieldType.FLOAT) {
				
			}else if(Type == FieldType.INT) {
				
			}else if(Type == FieldType.STRING) {
				
			}
		}else if(Op == Operation.LESS) {
			if(Type == FieldType.FLOAT) {
				
			}else if(Type == FieldType.INT) {
				
			}else if(Type == FieldType.STRING) {
				
			}
		}else if(Op == Operation.MORE) {
			if(Type == FieldType.FLOAT) {
				
			}else if(Type == FieldType.INT) {
				
			}else if(Type == FieldType.STRING) {
				
			}
		}else if(Op == Operation.LESS_EQUAL) {
			if(Type == FieldType.FLOAT) {
				
			}else if(Type == FieldType.INT) {
				
			}else if(Type == FieldType.STRING) {
				
			}
		}else if(Op == Operation.MORE_EQUAL) {
			if(Type == FieldType.FLOAT) {
				
			}else if(Type == FieldType.INT) {
				
			}else if(Type == FieldType.STRING) {
				
			}
		}
		
		return res;
	}
	
}
