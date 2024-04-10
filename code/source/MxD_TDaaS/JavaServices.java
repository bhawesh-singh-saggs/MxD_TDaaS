package MxD_TDaaS;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.io.*;
import java.lang.SecurityException;
import java.util.Properties;
import com.wm.lang.ns.*;
// --- <<IS-END-IMPORTS>> ---

public final class JavaServices

{
	// ---( internal utility methods )---

	final static JavaServices _instance = new JavaServices();

	static JavaServices _newInstance() { return new JavaServices(); }

	static JavaServices _cast(Object o) { return (JavaServices)o; }

	// ---( server methods )---




	public static final void writeToFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(writeToFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required userData
		// [i] field:0:required filename
		// [i] field:0:required appendOverwriteFlag {"append","overwrite","failIfFileExists"}
		// Based on PSUtilities writeFile service
		
		
		IDataCursor idcPipeline = pipeline.getCursor();
		String strUserData = null;
		String strFullFilename = null;
		if (idcPipeline.first("userData"))
		{
		strUserData = (String)idcPipeline.getValue();
		}
		if (idcPipeline.first("filename"))
		{
		strFullFilename = (String)idcPipeline.getValue();
		}
		else
		{
		throw new ServiceException( "filename is null!" );
		}
		idcPipeline.first("appendOverwriteFlag");
		String appendOverwriteFlag = (String)idcPipeline.getValue();
		
		// *** Check if path is on the allowed list ***
		try
		{
		if (!checkPathValidity(strFullFilename, "write"))
		{
		throw new ServiceException("Specified path is not on the write allowed list in the PSUtilities configuration file!");
		}
		}
		catch (Exception e)
		{
		throw new ServiceException(e.getMessage());
		}
		// *** End check ***
		
		// Separate filename into path and filename
		// This is done so that the directory can be written (if necessary)
		String separator = System.getProperty("file.separator");
		int indexSeparator = strFullFilename.lastIndexOf(separator);
		if (indexSeparator == -1)
		{
		// Account for fact that you can use either '\' or '/' in Windows
		indexSeparator = strFullFilename.lastIndexOf('/');
		}
		String strPathName = strFullFilename.substring(0, indexSeparator+1);
		String strFileName = strFullFilename.substring(indexSeparator+1);
		
		FileWriter fw = null;
		try
		{
		File pathToBeWritten = new File(strPathName);
		//		System.out.println("canonical path = " + pathToBeWritten.getCanonicalPath());
		
		// Write the directory...
		if (pathToBeWritten.exists() == false)
		{
		throw new ServiceException("Path does not exist!");
		}
		
		// Check if file exists
		File fileToBeWritten = new File(strFullFilename);
		if (fileToBeWritten.exists() == true && appendOverwriteFlag != null && appendOverwriteFlag.equals("failIfFileExists"))
		{
		throw new ServiceException("File " + strFullFilename + " already exists!");
		}
		
		// Write the file...
		if (appendOverwriteFlag != null && appendOverwriteFlag.equals("overwrite"))
		{
		// overwrite
		fw = new FileWriter(strFullFilename, false);
		}
		else
		{
		// append
		fw = new FileWriter(strFullFilename, true);
		}
		fw.write(strUserData);
		}
		catch (Exception e)
		{
		throw new ServiceException(e.getMessage());
		}
		finally
		{
		// Close the output stream....
		try
		{
		fw.close();
		}
		catch (Exception e)
		{}
		
		idcPipeline.destroy();
		}
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static final boolean checkPathValidity(String strPath, String strAction)
	  throws Exception
	{
		try
		{
			// *** Check if service is on the allowed list ***
			IData in = IDataFactory.create();
			IDataCursor idcIn = in.getCursor();
			idcIn.insertAfter("path", strPath);
			idcIn.insertAfter("action", strAction);
			NSName nsCheckServiceName = NSName.create("PSUtilities.config:checkPathValidity");
			idcIn.destroy();
			IData out;
			out = Service.doInvoke(nsCheckServiceName, in);
			IDataCursor idcOut = out.getCursor();
			String strValid = null;
			if (idcOut.first("valid"))
			{
				strValid = (String)idcOut.getValue();
			}
			idcOut.destroy();
	
			if (strValid.equals("false"))
			{
				return false;
			}
			return true;
			// *** End check ***
		}
		catch (Exception e)
		{
			throw e;
		}
	}
		
	// --- <<IS-END-SHARED>> ---
}

