import java.io.*;

final
public class CleanPP
extends Object
implements Serializable
{

    private static String INCHAR="UTF-8";
    private static String OUCHAR="ISO-8859-9";
    
    private CleanPP ()
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
	    System.out.println ("Example:\n\tjava -cp cls; CleanPP UTF-8 ISO-8859-9 src.html dst.html");
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String src, String dst)
    throws IOException
    {
	File f=new File (src);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, INCHAR);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, OUCHAR);
	
	String line=null;
	
	StringBuffer sbr=new StringBuffer ();
	
	while ( (line=br.readLine ()) != null )
	{
	    sbr.append (line);
	    sbr.append ("\n");
	}

	br.close ();
	isr.close ();
	fis.close ();
	
	String text=sbr.toString ();
	
	String mext=text.replaceAll ("<p><p>", "");//Utils.getBeautifiedText (text);
	
	ps.print (mext);
	ps.print ("\n");
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println (""+src+" --> "+dst+"");
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	INCHAR=args [0];
	OUCHAR=args [1];
	
	try
	{
	    cnt (args [2], args [3]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.exit (0);
    }
    
}
