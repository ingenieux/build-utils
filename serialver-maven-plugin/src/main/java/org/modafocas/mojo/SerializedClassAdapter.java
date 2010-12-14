package org.modafocas.mojo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.commons.EmptyVisitor;
import org.objectweb.asm.commons.SerialVersionUIDAdder;

public class SerializedClassAdapter extends SerialVersionUIDAdder {
    EmptyVisitor emptyVisitor = new EmptyVisitor();

    private Long found = null;

    public SerializedClassAdapter(ClassVisitor cv) {
	super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
	    String superName, String[] interfaces) {
	this.name = name.replace('/', '.');

	super.visit(version, access, name, signature, superName, interfaces);
    }

    public String getName() {
	return this.name;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc,
	    String signature, Object value) {
	if ("serialVersionUID".equals(name)) {
	    this.found = (Long) value;

	    return emptyVisitor
		    .visitField(access, name, desc, signature, value);
	}

	return super.visitField(access, name, desc, signature, value);
    }

    public boolean matches() throws IOException {
	if (null == found)
	    return false;

	Long expected = getExpected();

	return !expected.equals(found);
    }

    public Long getFound() {
	return found;
    }

    public long getExpected() throws IOException {
	return super.computeSVUID();
    }

    public static void main(String[] args) throws Exception {
	Collection<SerializedClass> serializedClasses = new ClassScanner(null)
		.scan(new File("target/classes"));

	for (SerializedClass serializedClass : serializedClasses) {
	    System.out.println(String.format(
		    "  * " + serializedClass.getClassName()
			    + " (expected: %d found: %d)",
		    serializedClass.getExpected(), serializedClass.getFound()));

	    ModifySVUID msvuid = new ModifySVUID(serializedClass, new File(
		    "src/main/java"), "UTF-8");

	    msvuid.execute();
	}
    }
}
