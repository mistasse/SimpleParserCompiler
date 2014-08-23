package se.mistas.parsing.pipeline;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import se.mistas.parsing.compilation.Placeholder;
import se.mistas.parsing.compilation.RegexPlaceholder;
import se.mistas.parsing.compilation.StringPlaceholder;
import se.mistas.parsing.compilation.UntilPlaceholder;
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

public class CompilerVisitor implements NodeVisitor, Opcodes {
	private Pipeline p;
	private ClassWriter cw;
	private MethodVisitor init, mv;
	private int locals, offset = 1;
	private String method;

	public CompilerVisitor(Pipeline p) {
		this.p = p;
	}
	
	@Override
	public void visit(StructureNode sn) {
		mv = cw.visitMethod(ACC_PUBLIC, sn.name, "(I)L"+state()+";", null, new String[]{exception()});
		method = sn.name;
		// On place la string en local$0
		locals = 2;
		
		// On case le state sur la pile!
		sn.phrase.accept(this);
		
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	@Override
	public void visit(PhraseNode pn) {
		boolean error = false;
		Label err = new Label();
		Label end = new Label();
//		Si plus qu'un, on cree un array. Sinon, si c'est just un, on retourne le state du dessus, et sinon,
//		On garde en memoire la longueur associee au combinateur
		if(pn.size() == 1) {
			pn.elements[0].accept(this);
			if(pn.type != null) {
				mv.visitInsn(DUP);
				mv.visitJumpInsn(IFNULL, end);
				
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "node", "()L"+nodeInterface()+";", false);
				
				mv.visitTypeInsn(NEW, filter(pn.type));
				mv.visitInsn(DUP_X1);
				mv.visitInsn(SWAP);
				
				mv.visitMethodInsn(INVOKESPECIAL, filter(pn.type), "<init>", "(L"+nodeInterface()+";)V", false);
				
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "setNode", "(L"+nodeInterface()+";)L"+state()+";", false);
			}
			mv.visitLabel(end);
			mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
		} else if(pn.asOne()) {
			int oldOffset = locals++;
			mv.visitVarInsn(ILOAD, offset);
			mv.visitVarInsn(ISTORE, oldOffset);
			int state = locals++;
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, state);
			
			for(ElementNode en : pn.elements) {
				en.accept(this);
				mv.visitInsn(DUP);
				
				if(error)
					mv.visitJumpInsn(IFNULL, err);
				else
					mv.visitJumpInsn(IFNULL, end);
				
				if(en.error)
					error = true;
				
				if(en.goesOut) {
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, state);
				}
				
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "length", "()I", false);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitInsn(IADD);
				mv.visitVarInsn(ISTORE, offset);
			}
			
			mv.visitVarInsn(ALOAD, state);
			mv.visitVarInsn(ILOAD, offset);
			mv.visitVarInsn(ILOAD, oldOffset);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKEVIRTUAL, state(), "setLength", "(I)L"+state()+";", false);
			
			if(pn.type != null) {
				mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "node", "()L"+nodeInterface()+";", false);
				
				mv.visitTypeInsn(NEW, filter(pn.type));
				mv.visitInsn(DUP_X1);
				mv.visitInsn(SWAP);
				
				mv.visitMethodInsn(INVOKESPECIAL, filter(pn.type), "<init>", "(L"+nodeInterface()+";)V", false);
				
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "setNode", "(L"+nodeInterface()+";)L"+state()+";", false);
			}
			
			if(error) {
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(err);
				mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
				
				mv.visitTypeInsn(NEW, exception());
				mv.visitInsn(DUP);
				mv.visitLdcInsn(method);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, exception(), "<init>", "(Ljava/lang/String;I)V", false);
				mv.visitInsn(ATHROW);
			}
			
			mv.visitLabel(end);
			mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
			
			mv.visitVarInsn(ILOAD, oldOffset);
			mv.visitVarInsn(ISTORE, offset);
			locals -= 2;
		} else {
			int inArray = 0;
			int oldOffset = locals++;
			mv.visitVarInsn(ILOAD, offset);
			mv.visitVarInsn(ISTORE, oldOffset);
			
			int array, params;
			if(pn.type == null) {
				array = locals++;
				params = -1;
				mv.visitIntInsn(SIPUSH, pn.returns);
				mv.visitTypeInsn(ANEWARRAY, nodeInterface());
				mv.visitVarInsn(ASTORE, array);
			} else {
				params = locals;
				array = -1;
				locals += pn.returns;
			}
			
			for(ElementNode en : pn.elements) {
				en.accept(this);
//				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(DUP);
				if(error)
					mv.visitJumpInsn(IFNULL, err);
				else
					mv.visitJumpInsn(IFNULL, end);
				
				if(en.error)
					error = true;
				
				if(en.goesOut)
					mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKEVIRTUAL, state(), "length", "()I", false);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitInsn(IADD);
				mv.visitVarInsn(ISTORE, offset);
				
				if(en.goesOut) {
					mv.visitMethodInsn(INVOKEVIRTUAL, state(), "node", "()L"+nodeInterface()+";", false);
					if(pn.type == null) {
						mv.visitVarInsn(ALOAD, array);
						mv.visitInsn(SWAP);
						mv.visitIntInsn(SIPUSH, inArray++);
						mv.visitInsn(SWAP);
						mv.visitInsn(AASTORE);
					} else {
						mv.visitVarInsn(ASTORE, params+inArray++);
					}
				}
			}
			
			mv.visitTypeInsn(NEW, state());
			mv.visitInsn(DUP);
			
			if(pn.type == null) {
				mv.visitTypeInsn(NEW, array());
				mv.visitInsn(DUP);
				mv.visitVarInsn(ALOAD, array);
				mv.visitMethodInsn(INVOKESPECIAL, array(), "<init>", "([L"+nodeInterface()+";)V", false);
			} else {
				mv.visitTypeInsn(NEW, filter(pn.type));
				mv.visitInsn(DUP);
				StringBuilder b = new StringBuilder();
				for(int i = params; i < params+pn.returns; i++) {
					mv.visitVarInsn(ALOAD, i);
					b.append("L").append(nodeInterface()).append(";");
				}
				mv.visitMethodInsn(INVOKESPECIAL, filter(pn.type), "<init>", "("+b.toString()+")V", false);
			}
			mv.visitVarInsn(ILOAD, offset);
			mv.visitVarInsn(ILOAD, oldOffset);
			mv.visitInsn(ISUB);
			mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);
			if(error) {
				mv.visitJumpInsn(GOTO, end);
				mv.visitLabel(err);
				mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
				
				mv.visitTypeInsn(NEW, exception());
				mv.visitInsn(DUP);
				mv.visitLdcInsn(method);
				mv.visitVarInsn(ILOAD, offset);
				mv.visitMethodInsn(INVOKESPECIAL, exception(), "<init>", "(Ljava/lang/String;I)V", false);
				mv.visitInsn(ATHROW);
			}
			
			mv.visitLabel(end);
			mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
			
			mv.visitVarInsn(ILOAD, oldOffset);
			mv.visitVarInsn(ISTORE, offset);
			
			locals--;
			if(pn.type == null)
				locals--;
			else
				locals -= pn.returns;
		}
	}

	@Override
	public void visit(AltNode an) {
		Label okLabel = new Label();
		
		for(PhraseNode pn : an.phrases) {
			pn.accept(this);
			mv.visitInsn(DUP);
			mv.visitJumpInsn(IFNONNULL, okLabel);
			
			mv.visitInsn(POP);
		}
		mv.visitInsn(ACONST_NULL);
		
		mv.visitLabel(okLabel);
		mv.visitFrame(F_SAME, 0, new Object[]{}, 0, new Object[]{});

	}

	@Override
	public void visit(MultNode mn) {
		Label beg = new Label();
		Label finish = new Label();
		
		int list = locals++;
		mv.visitTypeInsn(NEW, "java/util/LinkedList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedList", "<init>", "()V", false);
		mv.visitVarInsn(ASTORE, list);
		
		int oldOffset = locals++;
		mv.visitVarInsn(ILOAD, offset);
		mv.visitVarInsn(ISTORE, oldOffset);
		
		mv.visitLabel(beg);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		
		mn.phrase.accept(this);
		
		mv.visitInsn(DUP); // node node ... state state
		mv.visitJumpInsn(IFNULL, finish);
		mv.visitInsn(DUP);

		mv.visitMethodInsn(INVOKEVIRTUAL, state(), "length", "()I", false);
		mv.visitVarInsn(ILOAD, offset);
		mv.visitInsn(IADD);
		mv.visitVarInsn(ISTORE, offset);
		
		mv.visitMethodInsn(INVOKEVIRTUAL, state(), "node", "()L"+nodeInterface()+";", false);
		
		mv.visitVarInsn(ALOAD, list);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "add", "(Ljava/lang/Object;)Z", false);
		mv.visitInsn(POP);
		// node node ...
		
		mv.visitJumpInsn(GOTO, beg);
		
		mv.visitLabel(finish);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
//		// node node node ... null 
		mv.visitInsn(POP); // On supprime le null
		
		// On cree l'array
		mv.visitVarInsn(ALOAD, list);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "size", "()I", false);
		mv.visitTypeInsn(ANEWARRAY, nodeInterface());
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedList", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", false);
		mv.visitTypeInsn(CHECKCAST, "[L"+nodeInterface()+";");
		// array
		
		mv.visitTypeInsn(NEW, array());
		mv.visitInsn(DUP_X1);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKESPECIAL, array(), "<init>", "([L"+nodeInterface()+";)V", false);
		
		mv.visitTypeInsn(NEW, state());
		mv.visitInsn(DUP_X1); // state node state
		mv.visitInsn(SWAP);
		
		mv.visitVarInsn(ILOAD, offset); // state state node length
		mv.visitVarInsn(ILOAD, oldOffset);
		mv.visitInsn(ISUB);
		
		mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);
		
		mv.visitVarInsn(ILOAD, oldOffset);
		mv.visitVarInsn(ISTORE, offset);
		
		locals -= 2;
	}

	@Override
	public void visit(OptNode on) {
		Label okLabel = new Label();
		
		on.phrase.accept(this);
		mv.visitInsn(DUP);
		mv.visitJumpInsn(IFNONNULL, okLabel);
		mv.visitInsn(POP); // NULL, don't care!!!!
		
		mv.visitTypeInsn(NEW, state());
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, optional());
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, optional(), "<init>", "()V", false);
		
		mv.visitInsn(ICONST_0);
		mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);
		
		mv.visitLabel(okLabel);
		mv.visitFrame(F_SAME, 0, new Object[]{}, 0, new Object[]{});
	}

	@Override
	public void visit(ElementNode en) {
		en.value.accept(this);
	}

	@Override
	public void visit(StructureIdentifierNode sin) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, offset);
		mv.visitMethodInsn(INVOKEVIRTUAL, name(), sin.value(), "(I)L"+state()+";", false);
	}

	@Override
	public void visit(IdentifierNode in) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, offset);
		if(p.hv.leaftypes.get(in.value()) != null)
			mv.visitMethodInsn(INVOKEVIRTUAL, name(), in.value(), "(I)L"+state()+";", false);
		else
			mv.visitMethodInsn(INVOKEVIRTUAL, name(), methodName(p.hv.identifiers.get(in.value())), "(I)L"+state()+";", false);
	}

	@Override
	public void visit(RawNode rn) {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, offset);
		mv.visitMethodInsn(INVOKEVIRTUAL, name(), methodName(rn.placeholder), "(I)L"+state()+";", false);
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
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V1_6 , ACC_PUBLIC | ACC_SUPER, name(), null, "java/lang/Object", null);
		
		cw.visitField(ACC_PRIVATE | ACC_FINAL, "string", "Ljava/lang/String;", null, null).visitEnd();
		
		init = cw.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null, null);
		init.visitVarInsn(ALOAD, 0);
		init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		
		init.visitVarInsn(ALOAD, 0);
		init.visitVarInsn(ALOAD, 1);
		init.visitFieldInsn(PUTFIELD, name(), "string", "Ljava/lang/String;");
		
		for(Placeholder ph : p.hv.constants) {
			mv = cw.visitMethod(ACC_PRIVATE, methodName(ph), "(I)L"+state()+";", null, null);
			
			ph.acceptCompilation(this);
			
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		
		init.visitInsn(RETURN);
		init.visitMaxs(0, 0);
		init.visitEnd();
		
		for(OmniNode n : cbn.statements)
			n.accept(this);
		
		cw.visitEnd();
	}

	@Override
	public void visit(AssignmentNode an) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, an.left, "(I)L"+state()+";", null, null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, name(), methodName(an.value.placeholder), "(I)L"+state()+";", false);
		
		if(an.type != null) {
			Label failed = new Label();
			mv.visitInsn(DUP);
			mv.visitJumpInsn(IFNULL, failed);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, name(), "string", "Ljava/lang/String;");
			mv.visitInsn(SWAP);
			//  state string state
			mv.visitMethodInsn(INVOKEVIRTUAL, state(), "length", "()I", false); // state string length
			mv.visitVarInsn(ILOAD, offset);
			mv.visitInsn(IADD);
			mv.visitVarInsn(ILOAD, offset);
			mv.visitInsn(SWAP);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(II)Ljava/lang/String;", false);
			// state substr
			
			mv.visitTypeInsn(NEW, filter(an.type));
			mv.visitInsn(DUP_X1);
			mv.visitInsn(SWAP);
			mv.visitMethodInsn(INVOKESPECIAL, filter(an.type), "<init>", "(Ljava/lang/String;)V", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, state(), "setNode", "(L"+nodeInterface()+";)L"+state()+";", false);
			
			mv.visitLabel(failed);
			mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		}
		
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	@Override
	public void visit(OptionNode on) {
	}
	
	public void compile(RegexPlaceholder rp) {
		{
			cw.visitField(ACC_PRIVATE | ACC_FINAL, fieldName(rp), "Ljava/util/regex/Matcher;", null, null).visitEnd();
			MethodVisitor mv = init;
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(rp.regex);
			mv.visitMethodInsn(INVOKESTATIC, "java/util/regex/Pattern", "compile", "(Ljava/lang/String;)Ljava/util/regex/Pattern;", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Pattern", "matcher", "(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;", false);
			mv.visitFieldInsn(PUTFIELD, name(), fieldName(rp), "Ljava/util/regex/Matcher;");
		}
		Label ok = new Label();
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, name(), fieldName(rp), "Ljava/util/regex/Matcher;");
		mv.visitVarInsn(ILOAD, 1);
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, name(), "string", "Ljava/lang/String;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
		
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Matcher", "region", "(II)Ljava/util/regex/Matcher;", false);
		mv.visitInsn(DUP);
		
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Matcher", "lookingAt", "()Z", false);
		mv.visitJumpInsn(IFNE, ok);
		mv.visitInsn(POP);
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		
		mv.visitLabel(ok);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/regex/Matcher", "end", "()I", false);
		mv.visitVarInsn(ISTORE, 2);
		
		mv.visitTypeInsn(NEW, state());
		mv.visitInsn(DUP);
		mv.visitTypeInsn(NEW, leaf());
		mv.visitInsn(DUP);
		
		mv.visitMethodInsn(INVOKESPECIAL, leaf(), "<init>", "()V", false);
		//state state node
		mv.visitVarInsn(ILOAD, 2);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitInsn(ISUB);
		mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);
		mv.visitInsn(ARETURN);
	}
	
	public void compile(StringPlaceholder sp) {
		Label ok = new Label();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, name(), "string", "Ljava/lang/String;");
		mv.visitLdcInsn(sp.str);
		mv.visitVarInsn(ILOAD, offset);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "startsWith", "(Ljava/lang/String;I)Z", false);
		mv.visitJumpInsn(IFNE, ok); //startswith = 1 != 0
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);

		mv.visitLabel(ok);
		mv.visitFrame(0, 0, new Object[0], 0, new Object[0]);
		mv.visitTypeInsn(NEW, state());
		mv.visitInsn(DUP);

		mv.visitTypeInsn(NEW, leaf());
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, leaf(), "<init>", "()V", false);

		mv.visitIntInsn(SIPUSH, sp.str.length());

		mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);

		mv.visitInsn(ARETURN);
	}
	
	public void compile(UntilPlaceholder up) {
		Label beginning = new Label();
		Label end = new Label();
		Label escaped = new Label();
		Label drop = new Label();
		// string offset length escaped beginning
		int string = 0, offset = 1, oldOffset = 4, length = 3, esc = 2;
		mv.visitVarInsn(ILOAD, offset);
		mv.visitVarInsn(ISTORE, oldOffset);
		
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, name(), "string", "Ljava/lang/String;");
		mv.visitInsn(DUP);
		mv.visitVarInsn(ASTORE, string);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
		mv.visitVarInsn(ISTORE, length);
		
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, esc); // echappe
		
		mv.visitLabel(beginning);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		mv.visitVarInsn(ILOAD, offset); // load offset
		mv.visitVarInsn(ILOAD, length); // load length
		mv.visitJumpInsn(IF_ICMPGE, end);
		
		// Si echappe, on continue
		mv.visitVarInsn(ILOAD, esc);
		mv.visitJumpInsn(IFNE, escaped);

		mv.visitVarInsn(ALOAD, string);
		mv.visitVarInsn(ILOAD, offset);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
		mv.visitInsn(DUP);
		// char char
		// pas echappe. si equal, on finit
		mv.visitLdcInsn(up.c);
		mv.visitJumpInsn(IF_ICMPEQ, drop);
		
		mv.visitIincInsn(offset, 1);
		mv.visitLdcInsn('\\');
		mv.visitJumpInsn(IF_ICMPNE, beginning); // pas \ => on continue
		
		mv.visitInsn(ICONST_1); // sinon, on echappe vite fait
		mv.visitVarInsn(ISTORE, esc);
		mv.visitJumpInsn(GOTO, beginning);
		
		// ECHAPPE
		mv.visitLabel(escaped);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, esc);
		mv.visitIincInsn(offset, 1);
		mv.visitJumpInsn(GOTO, beginning);
//		
		mv.visitLabel(drop);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});
		mv.visitInsn(POP);
		mv.visitLabel(end);
		mv.visitFrame(0, 0, new Object[]{}, 0, new Object[]{});

		mv.visitTypeInsn(NEW, state());
		mv.visitInsn(DUP);
		
		mv.visitTypeInsn(NEW, leaf());
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, leaf(), "<init>", "()V", false);
		
		mv.visitVarInsn(ILOAD, offset);
		mv.visitVarInsn(ILOAD, oldOffset);
		mv.visitInsn(ISUB);
		mv.visitMethodInsn(INVOKESPECIAL, state(), "<init>", "(L"+nodeInterface()+";I)V", false);

		mv.visitVarInsn(ILOAD, oldOffset);
		mv.visitVarInsn(ISTORE, offset);
		
		mv.visitInsn(ARETURN);
	}
	
	public String filter(String id) {
		return p.hv.config.get("Package")+"/"+id;
	}
	
	public String name() {
		return p.hv.config.get("Class");
	}
	
	public String state() {
		return p.hv.config.get("State");
	}
	
	public String nodeInterface() {
		return p.hv.config.get("Node");
	}
	
	public String leaf() {
		return p.hv.config.get("Leaf");
	}
	
	public String array() {
		return p.hv.config.get("Array");
	}
	
	public String optional() {
		return p.hv.config.get("Optional");
	}
	
	public String methodName(Placeholder ph) {
		return methodName(p.hv.constants.indexOf(ph));
	}
	
	public String methodName(int id) {
		return "constant$"+id;
	}
	
	public String fieldName(Placeholder ph) {
		return "field$"+p.hv.constants.indexOf(ph);
	}
	
	public String exception() {
		return p.hv.config.get("Exception");
	}
	
	public byte[] bytecode() {
		return cw.toByteArray();
	}
	
}
