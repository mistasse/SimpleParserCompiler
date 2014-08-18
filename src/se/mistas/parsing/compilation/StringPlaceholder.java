package se.mistas.parsing.compilation;

import se.mistas.parsing.nodes.StringNode;
import se.mistas.parsing.pipeline.CompilerVisitor;

public class StringPlaceholder implements Placeholder {
	public String str;
	
	public StringPlaceholder(StringNode n) {
		str = n.value();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof StringPlaceholder)
			return str.equals(((StringPlaceholder) obj).str);
		return false;
	}
	
	@Override
	public String toString() {
		return "\""+str+"\"";
	}

	@Override
	public void acceptCompilation(CompilerVisitor cv) {
		cv.compile(this);
	}
	
}
