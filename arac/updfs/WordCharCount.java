import java.io.*;

import java.util.Vector;

final
public class WordCharCount
extends Object
implements Serializable
{
    
    private static int WORDS=0x0000;
    private static int BOSLUKLU=0x0000;
    private static int BOSLUKSUZ=0x0000;
    
    private WordCharCount ()
    {
	super ();
    }
    
    public String toString ()
    {
	return "Count Words Chars";
    }
    
    final
    private static boolean usage (final String [] args)
    {
	if (args.length < 2)
	{
	    System.out.println ("Usage:\n\tjava WordCharCount [<src>] [<charset>]\n");
	    System.out.println ("Example:\n\tjava WordCharCount src.txt UTF-8");
	    return false;
	}
	
	return true;
    }
    
    final
    private static void readFile (String src, String charset)
    throws IOException
    {
	File fd=new File (src);
	InputStream fis=new FileInputStream (fd);
	Reader isr=new InputStreamReader (fis, charset);
	BufferedReader br=new BufferedReader (isr);
	
	final char SPACEX=(char)(32);
	
	String line=null;
	
	while ( (line=br.readLine ()) != null)
	{
	    final int len=line.length ();
	    if (len < 1) continue;
	    
	    char ch='a';
	    for (int i=0; i<len; i++)
	    {
		ch=line.charAt (i);
		++BOSLUKLU;
		
		if (ch != SPACEX)
		{
		    ++BOSLUKSUZ;
		}
	    }
	    
	    line=line.trim ();
	    
	    for (int i=0; i<10; i++)
	    {
		line=line.replaceAll ("  ", " ");
	    }
	    
	    String [] split=line.split (" ");
	    WORDS += (split.length);
	}
	
	//WORDS, BOSLUKLU, BOSLUKSUZ
	
	br.close ();
	isr.close ();
	fis.close ();
	
	System.out.println ("");
	System.out.println ("Words Count: "+Integer.toString (WORDS)+"");
	System.out.println ("Characters With Spaces Count:"+Integer.toString (BOSLUKLU)+"");
	System.out.println ("Characters Without Spaces Count:"+Integer.toString (BOSLUKSUZ)+"");
	System.out.println ("");
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	boolean usb=usage (args);
	
	if (!usb)
	{
	    System.exit (-1);
	}
	
	try
	{
	    readFile (args [0x0000], args [0x0001]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.exit (0x0000);
    }
    
}
