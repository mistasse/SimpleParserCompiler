package se.mistas.parsing.pipeline;

import se.mistas.parsing.nodes.OmniNode;

public class Pipeline {
	public final OmniNode root;
	public HeaderVisitor hv;
	public CompilerVisitor cv;
	
	public Pipeline(OmniNode root) {
		this.root = root;
		hv = new HeaderVisitor(this);
		root.accept(hv);
	}
	
	public byte[] compile() {
		cv = new CompilerVisitor(this);
		root.accept(cv);
		return cv.bytecode();
	}
}
