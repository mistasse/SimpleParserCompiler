package se.mistas.parsing.nodes;

public class State {
	private Node n;
	private int length;
	
	public State(Node n, int length) {
		this.n = n;
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
}
