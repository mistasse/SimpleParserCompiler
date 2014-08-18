package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import se.mistas.parsing.compilation.ParsingException;
import se.mistas.parsing.nodes.OmniNode;
import se.mistas.parsing.nodes.State;
import se.mistas.parsing.pipeline.Pipeline;

public class Compiler {
	public static void main(String[] args) {
		StringBuilder b = new StringBuilder();
		InputStreamReader in = new InputStreamReader(Compiler.class.getResourceAsStream("/Grammar.txt"));

		try {
			char buf[] = new char[4096];
			int l;
			while((l = in.read(buf)) != -1)
				b.append(buf, 0, l);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		State s = null;
		se.CustomParser parser = new se.CustomParser(b.toString());
//		se.mistas.parsing.generated.CustomParser parser = new se.mistas.parsing.generated.CustomParser(b.toString());
		long t1 = System.currentTimeMillis();
		for(int i = 0; i < 1; i++)
			try {
				s = parser.CodeBlock(0);
			} catch (ParsingException e) {
				System.err.println("Error in block "+e.structure+" at line "+b.toString().substring(0, e.offset).split("\n").length);
			}
		t1 = System.currentTimeMillis() - t1;
		if(s.length() != b.length())
			throw new RuntimeException("Parsing failed!");
		Pipeline p1 = new Pipeline((OmniNode)s.node());
		
		FileOutputStream fs;
		try {
			fs = new FileOutputStream("datas/"+p1.hv.config.get("Class")+".class");
			fs.write(p1.compile());
			fs.close();
			System.out.println(t1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
