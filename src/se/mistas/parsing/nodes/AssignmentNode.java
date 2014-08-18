package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class AssignmentNode extends ArrayNode implements OmniNode {
	public String left;
	public RawNode value;
	public String type;
	
	public AssignmentNode(Node[] children) {
		super(children);
		left = child(0).value();
		value = (RawNode)child(1);
		if(child(2).specified())
			type = child(2).value();
	}
	
	public AssignmentNode(Node left, Node value, Node type) {
		super(left, value, type);
		this.left = left.value();
		this.value = (RawNode) value;
		if(type.specified())
			this.type = type.value();
	}
	
	public boolean isNotLeaf() {
		return type != null;
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
