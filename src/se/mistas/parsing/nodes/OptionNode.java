package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class OptionNode extends ArrayNode implements OmniNode {
	public String key, value;

	public OptionNode(Node[] children) {
		super(children);
		key = child(0).value();
		value = child(1).value();
	}
	
	public OptionNode(Node key, Node value) {
		super(key, value);
		this.key = key.value();
		this.value = value.value();
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}
	
}
