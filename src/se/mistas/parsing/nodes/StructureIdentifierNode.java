package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class StructureIdentifierNode extends TerminalNode implements OmniNode {

	public StructureIdentifierNode(String value) {
		super(value);
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
