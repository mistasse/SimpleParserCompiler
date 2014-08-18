package se.mistas.parsing.nodes;


public class ArrayNode extends AbstractNode {
	private Node[] children;
	
	public ArrayNode(Node... children) {
		this.children = children;
	}
	
	@Override
	public Node[] children() {
		return children;
	}
	
}
