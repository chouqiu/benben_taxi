package com.benbentaxi.remoteexception;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;


import android.os.Environment;

public class RemoteExceptionHandler implements UncaughtExceptionHandler {

	
	private UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private String stacktrace;
    public RemoteExceptionHandler ()
    {
    	localPath      = Environment.getExternalStorageDirectory() +"/";
    	this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }
    
    
	public void uncaughtException(Thread thread, Throwable e) {
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        stacktrace = result.toString();
        printWriter.close();
        String filename ="benbentaxi_passenger"+ timestamp + ".stacktrace";

        if (localPath != null) {
            writeToFile(stacktrace, filename);
            RemoteExceptionTask et = new RemoteExceptionTask(stacktrace);
            et.go();
        }
//        try {
//			//Thread.sleep(3000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
        defaultUEH.uncaughtException(thread, e);
	}
	private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
