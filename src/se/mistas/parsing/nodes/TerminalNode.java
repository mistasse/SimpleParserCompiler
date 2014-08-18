package se.mistas.parsing.nodes;

public class TerminalNode extends AbstractNode {
	private String value;
	
	public TerminalNode(String value) {
		this.value = value;
	}
	
	public String value() {
		return value;
	}
	
	@Override
	public String toString() {
		return super.toString()+" "+value;
	}
	
}
