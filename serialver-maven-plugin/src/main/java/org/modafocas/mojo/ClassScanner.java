package org.modafocas.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.DirectoryWalker;
import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.EmptyVisitor;

public class ClassScanner extends DirectoryWalker<SerializedClass> {
    private Log log;

    public ClassScanner(Log log) {
	this.log = log;
    }

    public Collection<SerializedClass> scan(File startDirectory)
	    throws IOException {
	Set<SerializedClass> results = new TreeSet<SerializedClass>();

	walk(startDirectory, results);

	return results;
    }

    @Override
    protected boolean handleDirectory(File directory, int depth,
	    Collection<SerializedClass> results) throws IOException {
	if (".svn".equals(directory.getName()))
	    return false;

	return true;
    }

    @Override
    protected void handleFile(File file, int depth,
	    Collection<SerializedClass> results) throws IOException {
	if (!file.getName().endsWith(".class"))
	    return;

	SerializedClass mainClass = getMetadata(file);

	if (null == mainClass)
	    return;

	results.add(mainClass);
    }

    private SerializedClass getMetadata(File file) throws IOException {
	ClassReader reader = new ClassReader(new FileInputStream(file));

	SerializedClassAdapter mca = new SerializedClassAdapter(
		new EmptyVisitor());

	reader.accept(mca, 0);

	if (mca.matches()) {
	    SerializedClass serializedClass = new SerializedClass();

	    serializedClass.setClassName(mca.getName());
	    serializedClass.setFound(mca.getFound());
	    serializedClass.setExpected(mca.getExpected());

	    return serializedClass;
	}

	return null;
    }
}
