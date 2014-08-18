package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;

public class AltNode extends ArrayNode implements OmniNode {
	public PhraseNode phrases[];
	
	public AltNode(Node[] children) {
		super(children);
		phrases = new PhraseNode[child(1).size()+1];
		phrases[0] = (PhraseNode)child(0);
		System.arraycopy(child(1).children(), 0, phrases, 1, child(1).size());
	}
	
	public AltNode(Node phrase0, Node phrases) {
		super(phrase0, phrases);
		this.phrases = new PhraseNode[phrases.size()+1];
		this.phrases[0] = (PhraseNode)phrase0;
		System.arraycopy(phrases.children(), 0, this.phrases, 1, phrases.size());
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
