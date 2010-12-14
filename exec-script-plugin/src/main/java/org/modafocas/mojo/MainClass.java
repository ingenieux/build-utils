package org.modafocas.mojo;

import java.io.Serializable;

public class MainClass implements Serializable, Comparable<MainClass> {
    private static final long serialVersionUID = 9101724678399402240L;

    String className;
    
    String simpleName;

    public String getClassName() {
	return className;
    }
    
    public String getSimpleName() {
	return simpleName;
    }

    public void setClassName(String className) {
	this.className = className;
	
	simpleName = className.toLowerCase().replaceFirst("^(.*\\.)", "");
    }

    public int compareTo(MainClass o) {
	return this.simpleName.compareTo(o.simpleName);
    }
}
