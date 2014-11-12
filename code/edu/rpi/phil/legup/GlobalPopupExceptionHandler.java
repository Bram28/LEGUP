package edu.rpi.phil.legup;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.swing.JOptionPane;

// modified from http://stackoverflow.com/questions/75218/how-can-i-detect-when-an-exceptions-been-thrown-globally-in-java
public class GlobalPopupExceptionHandler implements Thread.UncaughtExceptionHandler
{
    public static String throwableToString(Throwable t)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(baos));
        return baos.toString();
    }
    public void uncaughtException(Thread t, Throwable e)
    {
        try
        {
            //String message = "Exception in thread " + t.toString() + " " + throwableToString(e);
            String message = "Exception in thread " + t.toString() + " " + e.toString();
            System.err.println(message);
            JOptionPane.showMessageDialog(null, message);
        }
        catch (Throwable e2) {}
    }

    public static void registerExceptionHandler()
    {
        Thread.setDefaultUncaughtExceptionHandler(new GlobalPopupExceptionHandler());
        System.setProperty("sun.awt.exception.handler", GlobalPopupExceptionHandler.class.getName());
    }
}
