package org.modafocas.mojo;

import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class MainClassAdapter {
    boolean valid = false;

    private String name;

    public MainClassAdapter(ClassNode classNode) {
	this.valid = parse(classNode);
    }

    public boolean parse(ClassNode classNode) {
	this.name = classNode.name;

	if (classNode.name.contains("$"))
	    return false;

	int flags = Opcodes.ACC_PUBLIC;

	int nonFlags = Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE;

	if (flags != (classNode.access & flags))
	    return false;

	if (0 != (classNode.access & nonFlags))
	    return false;

	MethodNode foundMethod = null;

	for (Object o : classNode.methods) {
	    MethodNode method = (MethodNode) o;

	    if (!"main".equals(method.name))
		{
		    continue;
		}

	    flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;

	    if (flags != (method.access & flags))
		{
		    continue;
		}

	    if (0 != (method.access & Opcodes.ACC_ABSTRACT))
		{
		    continue;
		}

	    Type returnType = Type.getReturnType(method.desc);

	    if (!(returnType == Type.INT_TYPE || returnType == Type.VOID_TYPE))
		{
		    continue;
		}

	    foundMethod = method;
	}

	if (null == foundMethod)
	    return false;

	name = classNode.name.replace('/', '.');

	return true;
    }

    public boolean isValid() {
	return valid;
    }

    public String getName() {
	return name;
    }

    public static void main(String[] args) throws Exception {
	ClassNode classNode = new ClassNode();

	InputStream inputStream = MainClassAdapter.class
		.getResourceAsStream("MainClassAdapter.class");

	ClassReader classReader = new ClassReader(inputStream);

	classReader.accept(classNode, 0);

	MainClassAdapter mca = new MainClassAdapter(classNode);

	if (mca.isValid()) {
	    String name = mca.getName();

	    System.out.println(name);
	}
    }

}
