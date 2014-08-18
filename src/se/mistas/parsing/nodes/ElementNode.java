package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class ElementNode extends ArrayNode implements OmniNode {
	public boolean goesOut;
	public OmniNode value;
	public boolean error;
	
	public ElementNode(Node[] children) {
		super(children);
		value = (OmniNode) child(1);
		goesOut = child(0).specified();
//		this.error = child(2).specified();
	}
	
	public ElementNode(Node goesOut, Node value, Node error) {
		super(goesOut, value, error);
		this.goesOut = goesOut.specified();
		this.value = (OmniNode)value;
		this.error = error.specified();
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
