package org.modafocas.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal stop
 */
public class StopMojo extends AbstractDerbyMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
	closeQuietly();
    }
}
