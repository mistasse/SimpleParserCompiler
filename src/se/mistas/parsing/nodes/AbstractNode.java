package se.mistas.parsing.nodes;


public abstract class AbstractNode implements Node {
	
	@Override
	public Node[] children() {
		return new Node[0];
	}
	
	@Override
	public String value() {
		return null;
	}
	
	@Override
	public boolean specified() {
		return true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
	public void dump(String s, String total, StringBuilder b) {
		b.append(total).append(toString()).append('\n');
		for(Node c : children())
			c.dump(s, total+s, b);
	}
	
	public int size() {
		return children().length;
	}
	
	public Node child(int i) {
		return children()[i];
	}
	
	public String dump() {
		StringBuilder b = new StringBuilder();
		dump("  ", "", b);
		return b.toString();
	}
}
