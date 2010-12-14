package org.modafocas.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal scan
 * @requiresDependencyResolution runtime
 */
public class ScanMojo extends AbstractMojo {
    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}/classes"
     * @required
     */
    File outputDirectory;

    /**
     * @parameter expression="${project.build.sourceDirectory}"
     */
    File sourceDirectory;

    /**
     * @parameter expression="${project.build.sourceEncoding}"
     * @required
     */
    String encoding;

    public void execute() throws MojoExecutionException {
	File f = outputDirectory;

	getLog().info("Scan em " + f);

	if (!f.exists()) {
	    getLog().info("Diretorio inexiste. Saindo.");
	    return;
	}

	try {
	    Collection<SerializedClass> serializedClasses = new ClassScanner(getLog())
		    .scan(outputDirectory);

	    for (SerializedClass serializedClass : serializedClasses) {
		String message = String.format(
			"  * " + serializedClass.getClassName()
				+ " (expected: %d found: %d)",
			serializedClass.getExpected(),
			serializedClass.getFound());

		getLog().warn(message);

		ModifySVUID msvuid = new ModifySVUID(serializedClass,
			sourceDirectory, encoding);
		
		msvuid.setLog(getLog());

		msvuid.execute();
	    }
	} catch (IOException e) {
	    throw new MojoExecutionException("Failure", e);
	}
    }
}
