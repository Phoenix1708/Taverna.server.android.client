package cs.man.ac.uk.taverna.server.android.client;

import java.io.File;
import java.io.FileNotFoundException;

import cs.man.ac.uk.taverna.server.android.client.tests.TestHelper;
import uk.org.taverna.server.client.Run;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends IoWorkflowBase 
{
	/*************** for simple test only *****************/
	protected final static String TEST_WORKFLOW = "/Test data/test.t2flow";
	private final static String TEST_IN_FILE = "/Test data/in.txt";
	private final static TestHelper testHelper = new TestHelper();
	/*************** for simple test only *****************/
	
	private Context currentContext;

	public MainActivity() {
		super();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_panel);
		Button test = (Button) findViewById(R.id.testButton);
		currentContext = test.getContext();

		test.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				begin();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_panel, menu);
		return true;
	}

	/*// method to setup location to save downloaded workflow
	private String getFileSaveLocation() {

		// find application directory to store workflow downloaded
		String applicationDir = null;
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			applicationDir = packageInfo.applicationInfo.dataDir;
		} catch (NameNotFoundException e) {
			Log.e("yourtag", "Error Package name not found ", e);
		}

		if (applicationDir == null)
		{
			showDialog(currentContext, 
					"Invalid Application Directory. Please contact developer for support.");
		}
		// store in SD card
		File root = android.os.Environment.getExternalStorageDirectory();               

		File dir = new File (root.getAbsolutePath() + "/xmls");
		if(dir.exists() == false) 
		{
			dir.mkdirs();
		}
		return applicationDir;
	}

	// extract t2flow file name from URI
	private String extractT2flowFileName(String workFlowUri)
	{
		String t2flowFileName = null;
		// check whether the document name extension is t2flow 
		String[] strSeg = workFlowUri.split("/");
		if (strSeg.length == 0 ||  
				strSeg[strSeg.length - 1]
						.substring(strSeg[strSeg.length - 1].lastIndexOf("."))
						.equals("t2flow"))
		{
			showDialog(currentContext, 
					"The format of Content-uri is not expected. "+
					"Please contact developer for support.");

			Log.i("Content-uri format error", 
					"The format of Content-uri is not expected. "+
					"Please contact developer for support.");
		}
		else
		{
			t2flowFileName = strSeg[strSeg.length - 1];
		}

		return t2flowFileName;
	}

	// GET workflow description XML from MyExperiment
	private byte[] retrieveWorkflowFile(String workflowURI)
	{
		byte[] downloadedWorkflow = null; 	

		try
		{
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet workflowGet = new HttpGet();  
			HttpResponse getResponse = null;

			getResponse = httpClient.execute(workflowGet);
			HttpEntity entity = getResponse.getEntity();
			if (entity != null) 
			{
				String responceString = EntityUtils.toString(entity);
				// Log.i("Response String", responceString); // for test

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);

				XmlPullParser parser = factory.newPullParser();
				parser.setInput(new StringReader(responceString));

				// find content URI
				String t2flowFileURI = null;
				String t2flowFileName = null;
				int eventType;

				while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) 
				{
					while ((eventType = parser.next()) != XmlPullParser.END_TAG) 
					{
						if (eventType == XmlPullParser.START_TAG) 
						{
							String tagName = parser.getName();
							if (tagName.equals("content-uri"))
							{
								t2flowFileURI = parser.nextText();
								t2flowFileName = extractT2flowFileName(t2flowFileURI);
							}
						}
					}
				}// end of while

				if (t2flowFileName != null && t2flowFileURI != null){
					downloadedWorkflow = IoWorkflowHelper.DownloadWorkflow(
							t2flowFileURI, t2flowFileName, getFileSaveLocation());
				}
			}   		

		}
		catch(Exception e)
		{
			showDialog(currentContext, e.getMessage());

			Log.i("HTTP request error", e.getMessage());
		}    	

		return downloadedWorkflow;
	}*/

	// Execute workflow on server
	private String runWorkflowOnServer(byte[] downloadedWorkflow)
	{
		String result = "null";

		if (downloadedWorkflow == null){
			throw new IllegalArgumentException("downloadedWorkflow");
		}
		else
		{			
			// HttpPost httppost = new HttpPost(TavernaServerUri);
			// HttpResponse postResponse = null;  
			// String t2flowFileName = extractT2flowFileName(workFlowUri);

			try {			

				Run run = Run.create(server, downloadedWorkflow, defaultUser);

				File inputFile = testHelper.getResourceFile(TEST_IN_FILE);
				try {
					run.getInputPort("IN").setFile(inputFile);
				} catch (FileNotFoundException e) {
					showDialog(currentContext, 
							"Could not find input file: " + TEST_IN_FILE);
				}

				run.start();

				if (run.isRunning()){
					waitForWorkflowRun(run);
					result = run.getOutputPort("OUT").getDataAsString();
				}
				else{
					showDialog(currentContext, "The Execution is not running.");
				}

				/*File downloadedt2flow = new File(downloadedFilePath);

	    		FileEntity reqEntity = new FileEntity(downloadedt2flow, "application/vnd.taverna.t2flow+xml.");
	    		reqEntity.setChunked(true); // Send in multiple parts if needed
	    		httppost.setEntity(reqEntity);
	    		postResponse = httpClient.execute(httppost);

	    		HttpEntity entity = postResponse.getEntity();
	    		result = EntityUtils.toString(entity);*/
			} 
			catch (Exception e) 
			{
				showDialog(currentContext, e.getMessage());
			}
		}		

		return result;
	}

	// The main method
	private void begin()
	{
		// Get Workflow from MyExperiment in the form of byte array
		// byte[] workFlow = retrieveWorkflowFile(MyExpWorkflowUriBase);
		
		/*************** for simple test only *****************/
		byte[] workFlow = testHelper.loadResource(TEST_WORKFLOW);
		/*************** for simple test only *****************/
		
		// POST the t2flow document to Taverna Server
		String result = runWorkflowOnServer(workFlow);

		TextView testOutput = (TextView) findViewById(R.id.testOutput);
		testOutput.setText(result);

		Log.i("Test print", result);    	
	}
}
