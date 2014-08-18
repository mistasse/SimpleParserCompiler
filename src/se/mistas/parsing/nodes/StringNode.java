package se.mistas.parsing.nodes;

import se.mistas.parsing.pipeline.NodeVisitor;


public class StringNode extends TerminalNode implements OmniNode {

	public StringNode(String value) {
		super(filter(value));
	}

	@Override
	public void accept(NodeVisitor nv) {
		nv.visit(this);
	}
	
	public static String filter(String s) {
		StringBuilder b = new StringBuilder(s.length());
		boolean escaped = false;
		for(int i = 0; i < s.length(); i++) {
			if(!escaped && s.charAt(i) == '\\')
				escaped = true;
			else if(escaped) {
				switch(s.charAt(i)) {
				case 't': b.append('\t'); break;
				case 'n': b.append('\n'); break;
				default: b.append(s.charAt(i)); break;
				}
				escaped = false;
			} else
				b.append(s.charAt(i));
		}
		return b.toString();
	}

}
