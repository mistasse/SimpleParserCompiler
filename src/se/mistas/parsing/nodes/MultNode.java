package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class MultNode extends ArrayNode implements OmniNode {
	public PhraseNode phrase;

	public MultNode(Node[] children) {
		super(children);
		phrase = (PhraseNode) child(0);
	}
	
	public MultNode(Node phrase) {
		super(phrase);
		this.phrase = (PhraseNode) phrase;
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
