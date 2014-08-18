package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;



public class UntilNode extends TerminalNode implements OmniNode {

	public UntilNode(String value) {
		super(value);
	}

	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}


}
