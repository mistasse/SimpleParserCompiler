package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;



public class RegexNode extends ArrayNode implements OmniNode {
	public String regex;

	public RegexNode(Node[] n) {
		super(n);
		regex = child(0).value();
	}
	
	public RegexNode(Node regex, Node flags) {
		super(regex, flags);
		this.regex = regex.value();
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}
}
