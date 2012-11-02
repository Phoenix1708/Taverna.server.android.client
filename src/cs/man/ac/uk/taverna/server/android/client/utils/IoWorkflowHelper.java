package cs.man.ac.uk.taverna.server.android.client.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;

public class IoWorkflowHelper {

	private static Context workingContext;

	public IoWorkflowHelper (Context context)
	{
		workingContext = context;
	}

	// download t2flow from MyExperiment
	public static byte[] DownloadWorkflow(String downloadURL, String fileName, String locationToSave) throws Exception 
	{
		if (downloadURL == null){
			throw new IllegalArgumentException("downloadURL");
		}
		if (fileName == null){
			throw new IllegalArgumentException("fileName");
		}

		//String downloadedFilePath = null;
		File outputFile = null;

		try {

			URL url = new URL(downloadURL); //you can write here any link		

			// log info for debugging
			long startTime = System.currentTimeMillis();
			Log.d("Download", "download begining");
			Log.d("Download", "download URL:" + url);
			Log.d("Download", "downloaded file name:" + fileName);

			/* Open a connection to that URL. */
			URLConnection connection = url.openConnection();

			// Define InputStreams to read from the URLConnection.
			InputStream inputStream = connection.getInputStream();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

			// Read bytes to the Buffer until there is nothing more to read(-1).
			ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(1024);
			int current = 0;
			while ((current = bufferedInputStream.read()) != -1) 
			{
				byteArrayBuffer.append((byte) current);
			}

			/* Convert the Bytes read to a String. 
			 * and store file in application directory*/
			outputFile = new File(locationToSave, fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			fileOutputStream.write(byteArrayBuffer.toByteArray());
			fileOutputStream.flush();
			fileOutputStream.close();

			// log info for debugging
			Log.d("Download", "download complete in" + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

		} catch (IOException e) {
			MessageDialog messageDlg = new MessageDialog(workingContext, e.getMessage());
			messageDlg.show();
		}

		return getBytesFromFile(outputFile);
		// return downloadedFilePath;
	}

	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(File file) throws Exception 
	{
		InputStream inputStream = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			inputStream.close();
			throw new Exception("The file: " + file.getName() + " is too large.");
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0, numRead = 0;
		while (offset < bytes.length && 
				(numRead = inputStream.read(bytes, offset, bytes.length - offset)) >= 0) 
		{
			offset += numRead;
		}

		// Check whether all bytes of the file have been read
		if (offset < bytes.length) {
			inputStream.close();
			throw new IOException("Could not completely read file: " + file.getName());
		}

		// Close the input stream and return bytes
		inputStream.close();
		return bytes;
	}
}
