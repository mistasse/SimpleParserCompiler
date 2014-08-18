package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class IdentifierNode extends TerminalNode implements OmniNode {

	public IdentifierNode(String value) {
		super(value);
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
