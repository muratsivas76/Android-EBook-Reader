import java.io.*;

final
public class AnalysMayusculo
extends Object
implements Serializable
{

    private AnalysMayusculo ()
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
	if (args.length < 2)
	{
	    System.out.println ("Example:\n\tjava -cp cls: AnalysMayusculo src.txt analys.txt");
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
	Reader isr=new InputStreamReader (fis, "ISO-8859-9");
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, "ISO-8859-9");
	
	String line=null;
	
	final String NOKTA=".";
	final String UNLEM="!";
	final String SORU="?";
	
	final int CERO=0x0000;
	final int UNO=0x0001;
	final int QUINCE=15;
	int lsyc=0;
	
	char chr='a';
	
	boolean isFound=false;
	
	ps.println ("Broken Mayusculo Line Numbers in "+src+":");
	
	while ( (line=br.readLine ()) != null )
	{
	    ++lsyc;
	    
	    line=line.trim ();
	    if (line.length () < 1) continue;
	    
	    chr=line.charAt (CERO);
	    if ( (Character.isUpperCase (chr)) && isFound)
	    {
		ps.print (""+lsyc+", ");
		if (((lsyc+UNO) % QUINCE) == CERO)
		{
		    ps.print ("\n");
		}
	    }
	    
	    if (line.endsWith (NOKTA) ||
	        line.endsWith (UNLEM) ||
	        line.endsWith (SORU))
	    {
		isFound=false;
	    }
	    else
	    {
		isFound=true;
	    }
	}

	ps.print ("\n");
	
	br.close ();
	isr.close ();
	fis.close ();
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println (""+src+" --> "+dst+"");
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	try
	{
	    cnt (args [0], args [1]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.exit (0);
    }
    
}
