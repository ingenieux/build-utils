package org.modafocas.mojo;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.DriverManager;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractDerbyMojo extends AbstractMojo {
    private static final String NETWORK_SERVER_CONTROL = "networkServerControl";

    /**
     * @parameter expression="${derby.databaseName}"
     * @required
     */
    String databaseName;

    /**
     * @parameter expression="${derby.createFrom}"
     */
    String createFrom;

    /**
     * Database Directory
     * 
     * @parameter expression="${project.build.directory}/db"
     * @required
     */
    File derbySystemDirectory;

    /**
     * Hostname to Listen / Bind To
     * 
     * @parameter
     */
    String host = "127.0.0.1";

    /**
     * Port to Listen
     * 
     * @parameter expression="${derby.port}"
     */
    int port = 50000;

    /**
     * User
     * 
     * @parameter
     */
    String user = "db2admin";

    /**
     * Password
     * 
     * @parameter
     */
    String pass = "db2admin";

    /**
     * Verbose?
     * 
     * @parameter
     */
    boolean verbose = false;
    
    @SuppressWarnings("unchecked")
    NetworkServerControl getNetworkServerControl() throws UnknownHostException,
	    Exception {
	if (getPluginContext().containsKey(NETWORK_SERVER_CONTROL))
	    return (NetworkServerControl) getPluginContext().get(
		    NETWORK_SERVER_CONTROL);

	NetworkServerControl networkServerControl = new NetworkServerControl(
		InetAddress.getByName(host), port, user, pass);

	getPluginContext().put(NETWORK_SERVER_CONTROL, networkServerControl);

	return networkServerControl;
    }

    PrintWriter getPrintWriter() {
	PrintWriter printWriter = new PrintWriter(System.out, true);

	if (!verbose)
	    printWriter = new PrintWriter(new NullOutputStream());

	return printWriter;
    }

    protected void closeQuietly() {
	try {
	    getNetworkServerControl().shutdown();
	} catch (Exception e) {
	    getLog().error(e);
	}
    }

    protected void startServer() throws Exception, UnknownHostException {
	System.setProperty("derby.system.home",
		derbySystemDirectory.getAbsolutePath());

	boolean bCreateDatabase = !(new File(this.derbySystemDirectory,
		databaseName).exists());

	if (!derbySystemDirectory.exists())
	    derbySystemDirectory.mkdirs();

	NetworkServerControl networkServer = getNetworkServerControl();

	networkServer.start(getPrintWriter());

	while (true) {
	    try {
		networkServer.ping();

		break;
	    } catch (Exception exc) {
		getLog().info(exc);
	    }
	}

	if (bCreateDatabase) {
	    System.out.println("Criando base: " + databaseName);

	    String createConnectionUrl = String.format("jdbc:derby:%s",
		    databaseName);

	    if (notBlank(createFrom)) {
		createConnectionUrl += String.format(";createFrom=%s",
			createFrom);
	    } else {
		createConnectionUrl += ";create=true";
	    }

	    DriverManager.getConnection(createConnectionUrl, user, pass);
	}
    }

    private boolean notBlank(String s) {
	if (s == null)
	    return false;

	if ("".equals(s.trim()))
	    return false;

	return true;
    }
}
