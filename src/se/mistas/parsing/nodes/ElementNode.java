package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class ElementNode extends ArrayNode implements OmniNode {
	public boolean goesOut;
	public OmniNode value;
	
	public ElementNode(Node[] children) {
		super(children);
		value = (OmniNode) child(1);
		goesOut = child(0).specified();
	}
	
	public ElementNode(Node goesOut, Node value) {
		super(goesOut, value);
		this.goesOut = goesOut.specified();
		this.value = (OmniNode)value;
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
