package org.modafocas.mojo;

import io.github.pixee.security.BoundedLineReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Executa e espera enter para dar shutdown no servidor
 * 
 * @goal run
 */
public class RunMojo extends StartMojo {
    public void execute() throws MojoExecutionException, MojoFailureException {
	super.execute();
	
	System.out.println("Digite <enter> para sair");

	BufferedReader reader = new BufferedReader(new InputStreamReader(
		System.in));

	try {
	    BoundedLineReader.readLine(reader, 1000000);
	} catch (IOException e) {

	} finally {
	    closeQuietly();
	}
    }

}
