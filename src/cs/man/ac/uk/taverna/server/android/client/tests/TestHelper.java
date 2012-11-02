package cs.man.ac.uk.taverna.server.android.client.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class TestHelper {
	
	/*********************** For test purposes only***********************/
	public InputStream getResourceStream(String filename) {
		InputStream is = getClass().getResourceAsStream(filename);

		if (is == null) {
			try {
				throw new IOException("Could not open resource: " + filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return is;
	}

	public byte[] loadResource(String filename) {
		InputStream is = null;
		try {
			is = getResourceStream(filename);
			return IOUtils.toByteArray(is);
		} catch (Exception e) {
			try {
				throw new IOException("Could not open resource: " + filename);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			IOUtils.closeQuietly(is);
		}

		return null;
	}

	public File getResourceFile(String filename) {
		try {
			URL fileURL = getClass().getResource(filename);
			return new File(fileURL.toURI());
		} catch (Exception e) {
			try {
				throw new IOException("Could not get file: " + filename);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return null;
	}
	/***********************end of "For test purposes only"***********************/

}
