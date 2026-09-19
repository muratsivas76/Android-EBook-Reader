import java.io.*;

final
public class RemoveContainsLines
extends Object
implements Serializable
{

    private RemoveContainsLines ()
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
	if (args.length < 5)
	{
	    System.out.println ("Usage:\n\tjava -cp cls: RemoveContainsLines [<src>] [<dst>] [<srcCharCode>] [<dstCharCode>] [<linesContainsThisExp>] ...\n");
	    System.out.println ("Example:\n\tjava -cp cls: RemoveContainsLines src.txt dst.txt UTF-8 ISO-8859-9 Merhaba_Arkadasss Selam\n");
	    System.out.println ("Explication:\n\tAbove example removes lines contains \"Merhaba Arkadasss\" and \"Selam\" words from src file.\n");
	    System.out.println ("Replace:\n\tiii, sss, ggg, III, SSS, GGG with turkish chars.\n");
	    
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String src, String dst, String srcCode, String dstCode, String [] replace)
    throws IOException
    {
	File f=new File (src);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, srcCode);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, dstCode);
	
	int rlen=replace.length;
	String xreplace="";
	
	for (int i=0; i<rlen; i++)
	{
	    xreplace=islah (replace [i]);
	    replace [i]=xreplace;
	}
	
	String line=null;
	String xline="";
	
	boolean found=false;
	
	while ( (line=br.readLine ()) != null )
	{
	    xline=line.trim ();
	    
	    found=false;
	    
	    for (int j=0; j<rlen; j++)
	    {
		xreplace=replace [j];
		if ((xline.indexOf (xreplace)) >= 0x0000)
		{
		    found=true;
		    break;
		}
	    }
	    
	    if (found) continue;
	    
	    ps.println (line);
	}

	br.close ();
	isr.close ();
	fis.close ();
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println (""+src+" --> "+dst+"");
	
	return;
    }
    
    final
    private static String islah (String rep)
    {
	//Rep: Merhaba_Arkadasss
	String ifade=rep.replaceAll ("_", " ");
	
	ifade=ifade.replaceAll ("iii", "\u0131");
	ifade=ifade.replaceAll ("sss", "\u015F");
	ifade=ifade.replaceAll ("ggg", "\u011F");
	ifade=ifade.replaceAll ("III", "\u0130");
	ifade=ifade.replaceAll ("SSS", "\u015E");
	ifade=ifade.replaceAll ("GGG", "\u011E");
	
	System.out.println ("Expresion: "+ifade+"");
	
	return ifade;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	int alen=args.length;
	int clen=alen-4;
	
	int sycc=-1;
	
	String [] dizi=new String [clen];
	for (int i=4; i<alen; i++)
	{
	    dizi [++sycc]=args [i];
	}
	
	try
	{
	    cnt (args [0], args [1], args [2], args [3], dizi);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}

	System.exit (0);
    }

}
