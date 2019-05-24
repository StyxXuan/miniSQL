package MiniSQL;

public class Attribute {
	FieldType Type;
	String attriName;
	int length, offset;
	Boolean isPrimary, isUnique, hasIndex;
	public Attribute(FieldType Type, String attriName, int length, int offset) {
		this.Type = Type;
		this.attriName = attriName;
		this.length = length;
		this.offset = offset;
	}
	
	public void SetPrimary() {
		this.isPrimary = true;
	}
	public void SetUnique() {
		this.isUnique = true;
	}
	
	public void SetIndex() {
		this.hasIndex = true;
	}
	
}
