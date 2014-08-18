package se.mistas.parsing.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.mistas.parsing.compilation.Placeholder;
import se.mistas.parsing.nodes.AltNode;
import se.mistas.parsing.nodes.AssignmentNode;
import se.mistas.parsing.nodes.CodeBlockNode;
import se.mistas.parsing.nodes.ElementNode;
import se.mistas.parsing.nodes.IdentifierNode;
import se.mistas.parsing.nodes.MultNode;
import se.mistas.parsing.nodes.OmniNode;
import se.mistas.parsing.nodes.OptNode;
import se.mistas.parsing.nodes.OptionNode;
import se.mistas.parsing.nodes.PhraseNode;
import se.mistas.parsing.nodes.RawNode;
import se.mistas.parsing.nodes.RegexNode;
import se.mistas.parsing.nodes.StringNode;
import se.mistas.parsing.nodes.StructureIdentifierNode;
import se.mistas.parsing.nodes.StructureNode;
import se.mistas.parsing.nodes.UntilNode;

/**
 * Verifies every variable is defined, collections unique constants,
 * determines leaf types if ones.
 * 
 * Also tags "one element out" phrases!
 */
public class HeaderVisitor implements NodeVisitor {
	public List<String> missing = new ArrayList<>();
	public List<String> defined = new ArrayList<>();
	
	public List<Placeholder> constants = new ArrayList<Placeholder>();
	
	public HashMap<String, Integer> identifiers = new HashMap<String, Integer>();
	public HashMap<String, String> leaftypes = new HashMap<String, String>();
	public HashMap<String, String> config = new HashMap<String, String>();
	
	public HeaderVisitor(Pipeline p) {
	}
	
	@Override
	public void visit(StructureNode sn) {
		if(defined.contains(sn.name))
			throw new RuntimeException(sn.name+" already exists");
		defined.add(sn.name);
		while(missing.remove(sn.name));
		
		sn.phrase.accept(this);
	}

	@Override
	public void visit(PhraseNode pn) {
		if(pn.elements.length == 1)
			pn.elements[0].goesOut = true;
		for(ElementNode en : pn.elements) {
			if(en.goesOut)
				pn.returns++;
			en.accept(this);
		}
	}

	@Override
	public void visit(AltNode an) {
		for(PhraseNode pn : an.phrases)
			pn.accept(this);
	}

	@Override
	public void visit(MultNode mn) {
		mn.phrase.accept(this);
	}

	@Override
	public void visit(OptNode on) {
		on.phrase.accept(this);
	}

	@Override
	public void visit(ElementNode en) {
		en.value.accept(this);
	}

	@Override
	public void visit(StructureIdentifierNode sin) {
		if(!defined.contains(sin.value()))
			missing.add(sin.value());
	}

	@Override
	public void visit(IdentifierNode in) {
		if(!defined.contains(in.value()))
			missing.add(in.value());
	}
	
	@Override
	public void visit(RawNode rn) {
		if(!constants.contains(rn.placeholder))
			constants.add(rn.placeholder);
	}

	@Override
	public void visit(RegexNode rn) {
	}

	@Override
	public void visit(UntilNode un) {
	}

	@Override
	public void visit(StringNode sn) {
	}

	@Override
	public void visit(CodeBlockNode cbn) {
		for(OmniNode n : cbn.statements)
			n.accept(this);
		for(String s : missing)
			throw new RuntimeException(s+" definition is missing");
	}

	@Override
	public void visit(AssignmentNode an) {
		if(defined.contains(an.left))
			throw new RuntimeException(an.left+" already exists");
		defined.add(an.left);
		while(missing.remove(an.left));
		
		if(an.type != null)
			leaftypes.put(an.left, an.type);
		
		an.value.accept(this);
		identifiers.put(an.left, identifierCstId(an.value.placeholder));
	}

	@Override
	public void visit(OptionNode on) {
		if(config.get(on.key) == null)
			config.put(on.key, on.value);
		else
			throw new RuntimeException("Option already exists");
	}

	public int identifierCstId(Placeholder cst) {
		return constants.indexOf(cst);
	}

}
