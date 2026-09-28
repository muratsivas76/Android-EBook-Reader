import java.io.*;

final
public class ConcatTextDir
extends Object
implements Serializable
{

    private ConcatTextDir ()
    {
	super ();
    }
    
    public String toString ()
    {
	return "";
    }
    
    final
    private static void usage (final String [] args)
    {
	if (args.length < 4)
	{
	    System.out.println ("Usage:\n\tjava -cp cls; ConcatTextDir [<srcCharCode>] [<dstCharCode>] [<dst>] [<srcDir>]\n");
	    System.out.println ("Example:\n\tjava -cp cls; ConcatTextDir UTF-8 ISO-8859-9 dest.txt filesDirectory\n");
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String srcCode, String dstCode, String dst, File [] srcs)
    throws IOException
    {
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, dstCode);
	
	int len=srcs.length;
	
	for (int i=0; i<len; i++)
	{
	    File f=srcs [i];
	    InputStream fis=new FileInputStream (f);
	    Reader isr=new InputStreamReader (fis, srcCode);
	    BufferedReader br=new BufferedReader (isr);
	
	    String line=null;
	
	    while ( (line=br.readLine ()) != null )
	    {
		ps.println (line);
	    }
	    
	    ps.println ("\n./...................../.\n");

	    br.close ();
	    isr.close ();
	    fis.close ();
	    
	    System.out.println (""+srcs [i]+" --> "+dst+"");
	}
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	File dir = new File(args[3]);
	
	File[] dizi = dir.listFiles();
	java.util.Arrays.sort(dizi);
	
	try
	{
	    
	    cnt (args [0], args [1], args [2], dizi);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}

	System.exit (0);
    }

}
