package se.mistas.parsing.compilation;

import se.mistas.parsing.nodes.RegexNode;
import se.mistas.parsing.pipeline.CompilerVisitor;

public class RegexPlaceholder implements Placeholder {
	public String regex;
	
	public RegexPlaceholder(RegexNode rn) {
		regex = rn.regex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RegexPlaceholder)
			return regex.equals(((RegexPlaceholder) obj).regex);
		return false;
	}
	
	@Override
	public String toString() {
		return "/"+regex+"/";
	}

	@Override
	public void acceptCompilation(CompilerVisitor cv) {
		cv.compile(this);
	}
}
