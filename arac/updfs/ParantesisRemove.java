import java.io.*;

final
public class ParantesisRemove
extends Object
implements Serializable
{

    private ParantesisRemove ()
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
	    System.out.println ("Usage:\n\tjava -cp cls: ParantesisRemove [<src>] [<dst>] [<srcCharCode>] [<dstCharCode>]\n");
	    System.out.println ("Example:\n\tjava -cp cls: ParantesisRemove src.txt dst.txt UTF-8 ISO-8859-9\n");
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String src, String dst, String srcCode, String dstCode)
    throws IOException
    {
	File f=new File (src);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, srcCode);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, dstCode);
	
	String line=null;
	StringBuffer sb=new StringBuffer ();
	String xline="";
	
	final String HTTP="(https:";
	final String KPPR=")";
	
	int indexa=-1;
	int indexb=-1;
	
	final int CERO=0x0000;
	final int UNO=0x0001;
	final int SIETE=0x0007;
//	final int ENFI=0x0005;
	
	int start=CERO;
	int syc=0;
	
	while ( (line=br.readLine ()) != null )
	{
	    ++syc;
	    start=CERO;
	    
//	    for (int i=0; i<ENFI; i++)
//	    {
		indexa=line.indexOf (HTTP, start);
	    
		if (indexa >= CERO)
		{
		    System.out.println ("Index of HTTPS: "+indexa+""); //DEBUG
		
		    sb=new StringBuffer ();
		
		    sb.append (line.substring (CERO, indexa));
		
		    indexb=line.indexOf (KPPR, (indexa+SIETE));
		    if (indexb >= CERO)
		    {
			sb.append (line.substring (indexb+UNO));
		    }
		    else
		    {
		        System.out.println ("Problem in "+syc+" line.");
		    }
		
		    xline=sb.toString ();
		
		    System.out.println (xline);
		
		    ps.println (xline);
		}
		else
		{
		    ps.println (line);
	        }
	        
//	        start += SIETE;
//	    }//end inner for
	}//end while

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
	    cnt (args [0], args [1], args [2], args [3]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}

	System.exit (0);
    }

}
