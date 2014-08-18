package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class OptNode extends ArrayNode implements OmniNode {
	public PhraseNode phrase;

	public OptNode(Node[] children) {
		super(children);
		phrase = (PhraseNode) child(0);
	}
	
	public OptNode(Node phrase) {
		super(phrase);
		this.phrase = (PhraseNode) phrase;
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
