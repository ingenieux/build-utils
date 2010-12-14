package org.modafocas.mojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class ModifySVUID {
    Pattern PATTERN_SVUID = Pattern
	    .compile("private\\s+static\\s+final\\s+long\\s+serialVersionUID\\s+=\\s+(\\d+)L;");

    private SerializedClass sc;
    private File sourceDirectory;
    private String encoding;

    private Log log;

    public ModifySVUID(SerializedClass sc, File sourceDirectory, String encoding) {
	this.sc = sc;
	this.sourceDirectory = sourceDirectory;
	this.encoding = encoding;
    }

    public Log getLog() {
	return log;
    }

    public void setLog(Log log) {
	this.log = log;
    }

    public void execute() throws IOException, MojoExecutionException {
	InputStreamReader reader = null;

	File targetFile = getTargetFile();

	reader = new InputStreamReader(new FileInputStream(targetFile),
		encoding);

	String sourceCode = IOUtils.toString(reader);

	StringBuffer newSourceCode = new StringBuffer(sourceCode);

	Matcher matcher = PATTERN_SVUID.matcher(newSourceCode);

	while (matcher.find()) {
	    Long value = Long.valueOf(matcher.group(1));

	    if (value.equals(sc.getFound())) {
		String newStatement = String.format(
			"private static final long serialVersionUID = %dL;",
			sc.getExpected());

		int start = matcher.start();

		int end = matcher.end();

		newSourceCode.delete(start, end);

		newSourceCode.insert(start, newStatement);

		break;
	    }
	}

	if (sourceCode.equals(newSourceCode.toString()))
	    throw new MojoExecutionException("Source nao foi mudado");
	
	Writer writer = null;
	writer = new PrintWriter(targetFile, encoding);
	IOUtils.copy(new StringReader(newSourceCode.toString()), writer);
	writer.close();
    }

    private File getTargetFile() {
	// TODO: Olhar esta linha com muita calma
	String filename = sc.className
		+ ".java".replaceAll("($[^\\.]+)+.java$", ".java");

	File targetFile = new File(sourceDirectory, filename);

	if (null != log)
	    log.info("Buscando por " + filename + " em " + sourceDirectory);

	return targetFile;
    }
}
