package org.modafocas.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.DirectoryWalker;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class ClassScanner extends DirectoryWalker<MainClass> {
    public Collection<MainClass> scan(File startDirectory) throws IOException {
	Set<MainClass> results = new TreeSet<MainClass>();

	walk(startDirectory, results);

	return results;
    }

    @Override
    protected boolean handleDirectory(File directory, int depth,
	    Collection<MainClass> results) throws IOException {
	if (".svn".equals(directory.getName()))
	    return false;

	return true;
    }

    @Override
    protected void handleFile(File file, int depth,
	    Collection<MainClass> results) throws IOException {
	if (!file.getName().endsWith(".class"))
	    return;
	
	MainClass mainClass = getMetadata(file);

	if (null == mainClass)
	    return;

	results.add(mainClass);
    }

    private MainClass getMetadata(File file) throws IOException {
	ClassReader reader = new ClassReader(new FileInputStream(file));

	ClassNode classNode = new ClassNode();

	reader.accept(classNode, 0);

	MainClassAdapter mca = new MainClassAdapter(classNode);

	if (mca.isValid()) {
	    MainClass mainClass = new MainClass();

	    mainClass.setClassName(mca.getName());

	    return mainClass;
	}

	return null;
    }
}
