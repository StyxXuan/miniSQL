package RecordManager;

public class Condition
{
	final static int EQUAL = 0; // =
	final static int NOT_EQUAL = 1; // <>
	final static int LESS = 2; // <
	final static int MORE = 3; // >
	final static int LESS_EQUAL = 4; // <=
	final static int MORE_EQUAL = 5; // >=
	
	String AttName;
	String ToCampare;
	int CompareSign;
	
	boolean Compare_Int()
	{
		return false;
	}
	boolean Compare_Float()
	{
		return false;
	}
	boolean Compare_String()
	{
		return false;
	}
	boolean Compare_Char()
	{
		return false;
	}
	
}
