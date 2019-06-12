package RecordManager;


import java.util.Vector;


public class Condition
{
	public enum Operation{
		EQUAL, NOT_EQUAL, LESS, MORE, LESS_EQUAL, MORE_EQUAL, EMPTY;
	}

	public Vector<String> Attributes = new Vector<String>();
	public Vector<String> Numbers = new Vector<String>();
	
	public Vector<Operation> Ops = new Vector<Operation>();
	public Vector<String>Conjunctions = new Vector<String>();
	
	public Condition(String AttName, String ToCampare, Operation Op) {
		Attributes.addElement(AttName);
		Numbers.addElement(ToCampare);
		Ops.addElement(Op);
		Conjunctions = null;
	}
	
	public Condition() {};
	public Condition(Vector<String> Attributes, Vector<String> Numbers, Vector<Operation> Ops, Vector<String>Conjunctions) {
		this.Attributes = Attributes;
		this.Numbers = Numbers;
		this.Ops = Ops;
		this.Conjunctions = Conjunctions;
	}

	public boolean Satisfy(Tuple tup, TableRow Row) {
		int index = 0;
		if(Attributes.size() == 0)
			return false;
		
		boolean res = Satisfy(tup, Row, Attributes.get(index), Numbers.get(index), Ops.get(index));
		index++;
		System.out.println("here to judge");
		while(Conjunctions != null && Conjunctions.size() > index) {
			String Con = Conjunctions.get(index-1);
			if(Con.equals("and")) {
				res &= Satisfy(tup, Row, Attributes.get(index), Numbers.get(index), Ops.get(index));
			}else if(Con.equals("or")) {
				res |= Satisfy(tup, Row, Attributes.get(index), Numbers.get(index), Ops.get(index));
			}
			index++;
		}
		return res;
	}
	
	public boolean Satisfy(Tuple tup, TableRow Row, String Attribute, String Number, Operation Op){
		FieldType Type = Row.GetType(Attribute);
		System.out.println(Type);
		System.out.println(Number+ " " + Attribute);
		boolean res = false;
		if(Op == Operation.EQUAL) {
			System.out.println("The operation equal");
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (num == att);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (num == att);
			}else if(Type == FieldType.STRING) {
				System.out.println("String equal");
				int index = Row.GetIndex(Attribute);
				System.out.println("index = " + index);
				String att = tup.GetData(index);
				System.out.println("att = " + att);
				res = (Number.equals(att));
			}
		}else if(Op == Operation.NOT_EQUAL) {
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (num != att);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (num != att);
			}else if(Type == FieldType.STRING) {
				String att = tup.GetData(Row.GetIndex(Attribute));
				res = (!Number.equals(att));
			}
		}else if(Op == Operation.LESS) {
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (att < num);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (att < num);
			}else if(Type == FieldType.STRING) {
				String att = tup.GetData(Row.GetIndex(Attribute));
				res = (att.compareTo(Number) < 0);
			}
		}else if(Op == Operation.MORE) {
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (att > num);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (att > num);
			}else if(Type == FieldType.STRING) {
				String att = tup.GetData(Row.GetIndex(Attribute));
				res = (att.compareTo(Number) > 0);
			}
		}else if(Op == Operation.LESS_EQUAL) {
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (att <= num);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (att <= num);
			}else if(Type == FieldType.STRING) {
				String att = tup.GetData(Row.GetIndex(Attribute));
				res = !(att.compareTo(Number) > 0);
			}
		}else if(Op == Operation.MORE_EQUAL) {
			if(Type == FieldType.FLOAT) {
				float num = Float.parseFloat(Number);
				float att = Float.parseFloat(tup.GetData(Row.GetIndex(Attribute)));
				res = (att >= num);
			}else if(Type == FieldType.INT) {
				int num = Integer.getInteger(Number);
				int att = Integer.getInteger(tup.GetData(Row.GetIndex(Attribute)));
				res = (att >= num);
			}else if(Type == FieldType.STRING) {
				String att = tup.GetData(Row.GetIndex(Attribute));
				res = !(att.compareTo(Number) < 0);
			}
		}
		
		return res;
	}
	
}
