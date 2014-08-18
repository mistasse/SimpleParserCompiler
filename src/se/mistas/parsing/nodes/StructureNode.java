package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class StructureNode extends ArrayNode implements OmniNode {
	public String name;
	public PhraseNode phrase;

	public StructureNode(Node[] children) {
		super(children);
		name = child(0).value();
		phrase = (PhraseNode)child(1);
	}
	
	public StructureNode(Node name, Node phrase) {
		super(name, phrase);
		this.name = name.value();
		this.phrase = (PhraseNode) phrase;
	}

	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}
	
}
