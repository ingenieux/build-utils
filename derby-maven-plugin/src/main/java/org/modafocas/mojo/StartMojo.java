package org.modafocas.mojo;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Inicia um Servidor de Rede Derby
 * 
 * @author aldrin
 * 
 * @goal start
 * 
 */
public class StartMojo extends AbstractDerbyMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
	try {
	    startServer();
	} catch (Exception exc) {
	    throw new MojoExecutionException("Failed", exc);
	}
    }

}
