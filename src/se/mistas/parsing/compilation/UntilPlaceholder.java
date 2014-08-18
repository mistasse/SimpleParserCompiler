package se.mistas.parsing.compilation;

import se.mistas.parsing.nodes.UntilNode;
import se.mistas.parsing.pipeline.CompilerVisitor;

public class UntilPlaceholder implements Placeholder {
	public char c;
	
	public UntilPlaceholder(UntilNode n) {
		c = n.value().charAt(0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UntilPlaceholder)
			return c == ((UntilPlaceholder)obj).c;
		return false;
	}
	
	@Override
	public String toString() {
		return "until "+c;
	}

	@Override
	public void acceptCompilation(CompilerVisitor cv) {
		cv.compile(this);
	}
	
}
