package se.mistas.parsing.nodes;

import java.util.Arrays;

import se.mistas.parsing.pipeline.NodeVisitor;


public class CodeBlockNode extends ArrayNode implements OmniNode {
	public OmniNode[] statements;

	public CodeBlockNode(Node[] children) {
		super(children);
		statements = Arrays.asList(children).toArray(new OmniNode[children.length]);
	}
	
	public CodeBlockNode(Node stblock) {
		super(stblock.children());
		this.statements = Arrays.asList(stblock.children()).toArray(new OmniNode[stblock.size()]);
	}
	
	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}

}
