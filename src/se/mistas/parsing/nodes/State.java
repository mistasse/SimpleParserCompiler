package se.mistas.parsing.nodes;

public class State {
	private Node n;
	private int offset, length;
	
	public State(Node n, int offset, int length) {
		this.n = n;
		this.offset = offset;
		this.length = length;
	}
	
	public Node node() {
		return n;
	}
	
	public State setNode(Node n) {
		this.n = n;
		return this;
	}
	
	public State setLength(int length) {
		this.length = length;
		return this;
	}
	
	public int length() {
		return length;
	}
	
	public State setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public int offset() {
		return offset;
	}
}
