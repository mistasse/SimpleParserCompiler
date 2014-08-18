package se.mistas.parsing.compilation;

import se.mistas.parsing.pipeline.CompilerVisitor;

public interface Placeholder {
	void acceptCompilation(CompilerVisitor cv);
}
