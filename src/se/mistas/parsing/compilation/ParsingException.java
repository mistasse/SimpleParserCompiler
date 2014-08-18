package se.mistas.parsing.compilation;

public class ParsingException extends Exception {
	public String structure;
	public int offset;
	
	private static final long serialVersionUID = 1L;
	public ParsingException(String structure, int offset) {
		super("Expected to find a "+structure+" but failed at offset "+offset);
		this.structure = structure;
		this.offset = offset;
	}
	
	
}
