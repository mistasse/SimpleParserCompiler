package se.mistas.parsing.nodes;


public interface Node {
	Node[] children();
	Node child(int i);
	String value();
	boolean specified();
	public int size();
	
	public String dump();
	public void dump(String a, String b, StringBuilder c);
}
