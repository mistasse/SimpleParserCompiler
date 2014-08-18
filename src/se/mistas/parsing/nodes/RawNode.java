package se.mistas.parsing.nodes;

import se.mistas.parsing.compilation.Placeholder;
import se.mistas.parsing.compilation.RegexPlaceholder;
import se.mistas.parsing.compilation.StringPlaceholder;
import se.mistas.parsing.compilation.UntilPlaceholder;
import se.mistas.parsing.pipeline.NodeVisitor;

public class RawNode extends ArrayNode implements OmniNode {
	public Placeholder placeholder;

	public RawNode(Node[] children) {
		super(children);
		if(child(0) instanceof UntilNode)
			placeholder = new UntilPlaceholder((UntilNode)child(0));
		else if(child(0) instanceof StringNode)
			placeholder = new StringPlaceholder((StringNode)child(0));
		else if(child(0) instanceof RegexNode)
			placeholder = new RegexPlaceholder((RegexNode)child(0));
	}
	
	public RawNode(Node value) {
		super(value);
		if(value instanceof UntilNode)
			placeholder = new UntilPlaceholder((UntilNode)value);
		else if(value instanceof StringNode)
			placeholder = new StringPlaceholder((StringNode)value);
		else if(value instanceof RegexNode)
			placeholder = new RegexPlaceholder((RegexNode)value);
	}

	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
