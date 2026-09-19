import java.io.*;

final
public class SadeleHTML
extends Object
implements Serializable
{
    
    private SadeleHTML ()
    {
	System.exit (-1);
    }
    
    public String toString ()
    {
	return "SadeleHTML";
    }
    
    final
    private static boolean purify (final String [] args)
    throws IOException
    {
	File src=new File (args [0]);
	InputStream fis=new FileInputStream (src);
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	File dst=new File (args [1]);
	OutputStream fos=new FileOutputStream (dst);
	PrintStream ps=new PrintStream (fos, true, "UTF-8");
	
	int slen = args.length - 2;
	String[] excludes = new String[slen];
	int sycx = -1;
	
	for (int i = 2; i < args.length; i++)
	{
		excludes [++sycx] = args[i];
	}
	final int exlen = excludes.length;
	String exc = "";
	
	String line=null;
	
	int index=-1;
	
	boolean found = false;
	
	while ( (line=br.readLine ()) != null )
	{
		found = false;
	    line=line.trim ();

		for (int i = 0; i < exlen; i++)
		{
			exc = excludes[i];
			if (line.contains(exc))
			{
				found=true;
				break;
			}
		}
	    
	    if (found) continue;
	    ps.println (line);
	}//end while
	
	br.close ();
	isr.close ();
	fis.close ();
	
	ps.flush ();   ps.close ();
	fos.flush (); fos.close ();
	
	return true;
    }
    
    final
    private static boolean usage (final String [] args)
    {
	if (args.length < 2)
	{
	    System.out.println ("Example:\n\tjava SadeleHTML file.html dest.html ft06 ft09");
	    return false;
	}
	
	File f=new File (args [0]);
	if (f == null)
	{
	    System.out.println ("Error: Null File!");
	    return false;
	}
	
	if (f.exists () == false)
	{
	    System.out.println ("Error: File does not exist!");
	    return false;
	}
	
	if (f.isDirectory ())
	{
	    System.out.println ("Error: Is directory!");
	    return false;
	}
	
	return true;
    }
    
    final
    public static void main (final String [] args)
    {
	boolean b=usage (args);
	if (!b)
	{
	    System.exit (-1);
	}
	
	boolean result=false;
	
	try
	{
	    result=purify (args);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	if (result)
	{
	    System.out.println ("Successfull: "+args [0]+" --> "+args [1]+"");
	}
	else
	{
	    System.out.println ("Error: "+args [0]+" --> "+args [1]+"");
	}
	
	System.exit (0);
    }
    
}
