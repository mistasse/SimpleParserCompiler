package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import se.mistas.parsing.compilation.ParsingException;
import se.mistas.parsing.generated.ParserCompiler;
import se.mistas.parsing.nodes.CodeBlockNode;
import se.mistas.parsing.nodes.State;
import se.mistas.parsing.pipeline.Pipeline;

public class Main {
	public static void main(String[] args) {
		if(args.length != 1)
			throw new RuntimeException("Don't know what to do with such arguments.");
		
		int l;
		StringBuilder b = new StringBuilder();
		char[] buffer = new char[4096];
		
		try(InputStreamReader in = new InputStreamReader(new FileInputStream(args[0]))) {
			while((l = in.read(buffer)) != -1)
				b.append(buffer, 0, l);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ParserCompiler parser = new ParserCompiler(b.toString());
		try {
			State s = parser.CodeBlock(0);
			if(s.length() != b.length())
				throw new RuntimeException("Couldn't continue further than line "+b.toString().substring(0, s.length()).split("\n").length);
			Pipeline p = new Pipeline((CodeBlockNode)s.node());

			File f = new File(p.hv.config.get("Class")+".class");
			if(!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			try(OutputStream out = new FileOutputStream(f)) {
				out.write(p.compile());
			}
			System.out.println("Compilation succeeded:\n"+p.hv.config.get("Class"));
		} catch (ParsingException e) {
			throw new RuntimeException("Error in block "+e.structure+" at line "+b.toString().substring(0, e.offset).split("\n").length, e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
