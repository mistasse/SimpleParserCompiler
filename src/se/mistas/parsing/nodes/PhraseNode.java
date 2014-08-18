package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class PhraseNode extends ArrayNode implements OmniNode {
	public ElementNode elements[];
	public int returns = 0;
	public String type;
	
	public PhraseNode(Node[] children) {
		super(children);
		
		elements = new ElementNode[child(1).size()+1];
		elements[0] = (ElementNode)child(0);
		System.arraycopy(child(1).children(), 0, elements, 1, child(1).size());
		
		if(child(2).specified())
			type = child(2).value();
	}
	
	public PhraseNode(Node first, Node seconds, Node type) {
		super(first, seconds);
		
		elements = new ElementNode[seconds.size()+1];
		elements[0] = (ElementNode)first;
		System.arraycopy(seconds.children(), 0, elements, 1, seconds.size());
		
		if(type.specified())
			this.type = type.value();
	}
	
	public boolean asOne() {
		return returns == 1;
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
