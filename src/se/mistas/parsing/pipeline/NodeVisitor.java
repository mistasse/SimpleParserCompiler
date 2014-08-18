package se.mistas.parsing.pipeline;

import se.mistas.parsing.nodes.AltNode;
import se.mistas.parsing.nodes.AssignmentNode;
import se.mistas.parsing.nodes.CodeBlockNode;
import se.mistas.parsing.nodes.ElementNode;
import se.mistas.parsing.nodes.IdentifierNode;
import se.mistas.parsing.nodes.MultNode;
import se.mistas.parsing.nodes.OptNode;
import se.mistas.parsing.nodes.OptionNode;
import se.mistas.parsing.nodes.PhraseNode;
import se.mistas.parsing.nodes.RawNode;
import se.mistas.parsing.nodes.RegexNode;
import se.mistas.parsing.nodes.StringNode;
import se.mistas.parsing.nodes.StructureIdentifierNode;
import se.mistas.parsing.nodes.StructureNode;
import se.mistas.parsing.nodes.UntilNode;

public interface NodeVisitor {
	
	void visit(StructureNode sn);
	
	void visit(PhraseNode pn);
	void visit(AltNode an);
	void visit(MultNode mn);
	void visit(OptNode on);
	void visit(ElementNode en);
	
	void visit(StructureIdentifierNode sin);
	void visit(IdentifierNode in);
	void visit(RawNode rn);
	
	void visit(RegexNode rn);
	void visit(UntilNode un);
	void visit(StringNode sn);
	
	void visit(CodeBlockNode cbn);
	void visit(AssignmentNode an);
	void visit(OptionNode on);
	
}
