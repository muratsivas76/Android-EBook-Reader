import java.io.*;

final
public class UnirLines
extends Object
implements Serializable
{

    private UnirLines ()
    {
	super ();
	System.exit (-1);
    }
    
    final
    private static boolean usage (final String [] args)
    {
	if (args.length < 4)
	{
	    return false;
	}
	
	return true;
    }
    
    final
    private static void write (final String src, final String dst, 
                               final String srcCH, final String dstCH)
    throws IOException
    {
	File sfile=new File (src);
        InputStream fis=new FileInputStream (sfile);
	Reader isr=new InputStreamReader (fis, srcCH);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, dstCH);
	
	String line=null;
	
	//final String NOKTAP="\\.";
	final String NOKTAN=".";
	
	int dlen=-1;
	int syc=0;
	
	//StringBuffer sb=new StringBuffer ();
	//String sbstr="";
	final String NW="\n";
	
	while ( (line=br.readLine ()) != null )
	{
	    ++syc;
	    System.out.println ("Reading "+String.format ("%06d", syc)+". line");
	    
	//    sb=new StringBuffer ();
	    
	    line=line.trim ();
	    if (line.length () < 1)
	    {
		ps.println ("");
		continue;
	    }
	    
	    if (line.indexOf (NOKTAN) < 0)
	    {
		ps.println (line);
		continue;
	    }
	    
//	    sb.append (line);
	//    
	//    sbstr=sb.toString ();
	    //sbstr=sbstr.replaceAll (("Dr"+NOKTAP+NW), ("DR. "));
	    
	    ps.print (line);
	    ps.print (" ");
	}
	
	br.close ();
	isr.close ();
	fis.close ();
	
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	
	System.out.println (""+src+" ---> "+dst+"");
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	boolean b=usage (args);
	if (b == false)
	{
	    System.out.println ("Usage:\n\tjava UnirLines [<src>] [<dst>] [<src_charset>] [<dst_charset>]\n");
	    System.out.println ("Example:\n\tjava UnirLines src.txt dst.txt ISO-8859-9 UTF-8");
	    System.exit (-1);
	}
	
	try
	{
	    write (args [0], args [1], args [2], args [3]);
	}
	catch (IOException ei)
	{
	    ei.printStackTrace ();
	    System.exit (-1);
	}
    }
    
}
