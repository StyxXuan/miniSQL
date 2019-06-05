package RecordManager;

public class Condition <T extends Comparable<T>>
{
	final static int EQUAL = 0; // =
	final static int NOT_EQUAL = 1; // <>
	final static int LESS = 2; // <
	final static int MORE = 3; // >
	final static int LESS_EQUAL = 4; // <=
	final static int MORE_EQUAL = 5; // >=
	
	int operator;
	String attributeName;
	T com_value;
	
	public Condition(String attri, T val, int op)
	{
		this.attributeName = attri;
		this.com_value = val;
		this.operator = op;
	}
	boolean Compare(T content)
	{
		T myContent = this.com_value;
		switch(operator)
		{
		case EQUAL:
			return (myContent.compareTo(content) == 0);
		case NOT_EQUAL:
			return (myContent.compareTo(content) != 0);
		case LESS:
			return (content.compareTo(myContent) < 0);
		case MORE:
			return (content.compareTo(myContent) > 0);
		case LESS_EQUAL:
			return (content.compareTo(myContent) <= 0);
		case MORE_EQUAL:
			return (content.compareTo(myContent) >= 0);
		default:
			return true;
			
		}
	}
	
}
