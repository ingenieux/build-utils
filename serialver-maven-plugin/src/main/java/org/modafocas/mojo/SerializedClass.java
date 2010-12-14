package org.modafocas.mojo;

import java.io.Serializable;

public class SerializedClass implements Serializable,
	Comparable<SerializedClass> {
    public static final SerializedClass NULL_CLASS = new SerializedClass() {
	private static final long serialVersionUID = 1L;
    };
    
    private static final long serialVersionUID = 9101724678399402240L;

    String className;

    Long expected;

    Long found;

    public Long getExpected() {
	return expected;
    }

    public void setExpected(Long expected) {
	this.expected = expected;
    }

    public Long getFound() {
	return found;
    }

    public void setFound(Long found) {
	this.found = found;
    }

    public String getClassName() {
	return className;
    }

    public void setClassName(String className) {
	this.className = className;
    }

    public int compareTo(SerializedClass o) {
	return this.className.compareTo(o.className);
    }
}
