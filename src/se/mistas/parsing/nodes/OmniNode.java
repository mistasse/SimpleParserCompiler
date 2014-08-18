package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;

public interface OmniNode {
	public void accept(NodeVisitor nv);
}
