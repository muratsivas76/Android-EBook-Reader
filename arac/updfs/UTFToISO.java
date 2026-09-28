import java.io.*;

final
public class UTFToISO
extends Object
implements Serializable
{

    private static boolean isTR=false;
    
    private UTFToISO ()
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
	    System.out.println ("Example-1:\n\tjava -cp cls: UTFToISO src.txt dst.txt\n");
	    System.out.println ("Example-2:[turkish texts]:\n\tjava -cp cls: UTFToISO src.txt dst.txt tr");
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
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, "ISO-8859-9");
	
	String line=null;
	
	StringBuffer sbr=new StringBuffer ();
	
	while ( (line=br.readLine ()) != null )
	{
	    line=line.trim ();
	    if (sfnum (line)) continue;
	    sbr.append (line);
	    sbr.append ("\n");
	}

	br.close ();
	isr.close ();
	fis.close ();
	
	String text=sbr.toString ();
	
	Utils.setTR (isTR);

	String mext=Utils.getBeautifiedText (text);
	mext=Utils.ygetBeautifiedText (mext);
	
	ps.print (mext);
	ps.print ("\n");
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println (""+src+" --> "+dst+"");
	
	return;
    }
    
    final
    private static boolean sfnum (String xstr)
    {
	boolean issf=true;
    
	String str=xstr.trim ();
	str=xstr.replaceAll (" ", "");
    
	int strlen=str.length ();
	char ch = '*';
	int chm=0;

	for (int i=0; i<strlen; i++)
	{
    	    ch=str.charAt (i);
    	    chm=(int)ch;
    	    if (!(chm>=0x30 && chm<=0x39))
    	    {
    		issf=false;
    		break;
    	    }
	}
    
	return issf;
    }
  
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	if (args.length > 2)
	{
	    if (args [2].equals ("tr"))
	    {
		isTR=true;
	    }
	}
	
	System.out.println ("isTR: "+Boolean.toString (isTR));
	
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
