package cs.man.ac.uk.taverna.server.android.client;

import java.net.URI;
import java.net.URISyntaxException;

import cs.man.ac.uk.taverna.server.android.client.utils.MessageDialog;

import uk.org.taverna.server.client.Run;
import uk.org.taverna.server.client.RunStatus;
import uk.org.taverna.server.client.Server;
import uk.org.taverna.server.client.connection.HttpBasicCredentials;
import uk.org.taverna.server.client.connection.UserCredentials;
import android.app.Activity;
import android.content.Context;

public class IoWorkflowBase extends Activity {
	
	private static final String TavernaServerAddress = "https://eric.rcs.manchester.ac.uk:8443/taverna-server-2/rest/runs";
    //private static final String MyExpWorkflowUriBase = "http://www.myexperiment.org/workflow.xml?id=74";

    protected static URI serverURI;
    protected static Server server;
    
    protected static UserCredentials defaultUser;
    
    public IoWorkflowBase()
    {
    	try {
			serverURI = new URI(TavernaServerAddress);
			server = new Server(serverURI);
			defaultUser = new HttpBasicCredentials("taverna:taverna");
			
		} catch (URISyntaxException e) {
			showDialog(this.getApplicationContext(), e.getMessage());
		}  	
    	
    	// TODO: how to make sure that server exists ? i.e URI exists
    }
    
    protected void waitForWorkflowRun(Run run) {
		while (run.getStatus() == RunStatus.RUNNING) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				showDialog(this.getApplicationContext(), e.getMessage());
			}
		}
	}
    
    // mehtod to show dialog
    public void showDialog(Context context, String message){
    	new MessageDialog(context, message).createDialog().show();
    }
}