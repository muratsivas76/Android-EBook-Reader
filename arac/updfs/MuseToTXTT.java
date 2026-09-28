import java.io.*;

import java.util.Vector;

final
public class MuseToTXTT
extends Object
implements Serializable
{
    private static String [] srcs=null;
    private static String [] dsts=null;
    private static int slen=-1;
    private static int dlen=-1;
    
    private static boolean ocurredError=false;
    
    private MuseToTXTT ()
    {
	super ();
    }
    
    public String toString ()
    {
	return "Muse to TXTT Convertion";
    }
    
    final
    private static boolean usage (final String [] args)
    {
	if (args.length < 3)
	{
	    System.out.println ("Usage:\n\tjava MuseToTXTT [<reglas>] [<src>] [<dst>]\n");
	    System.out.println ("Example:\n\tjava MuseToTXTT reglasReplace.txt metin.muse metin.txtt");
	    return false;
	}
	
	return true;
    }
    
    final
    private static void xinit (String fname)
    throws IOException
    {
	Vector <String> va=new Vector <String> ();
	Vector <String> vb=new Vector <String> ();
	
	File fd=new File (fname);
	InputStream fis=new FileInputStream (fd);
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	final String ENDFLAG="###END";
	final String INOK=":";
	
	final int CERO=0x0000;
	final int UNO=0x0001;
	final int DOS=0x0002;
	
	String line=null;
	String xline="";
	
	line=br.readLine (); //skip first line
	line=br.readLine (); //skip second line
	
	while ( (line=br.readLine ()) != null)
	{
	    xline=line.trim ();
	    
	    if (xline.startsWith (ENDFLAG)) break;
	    
	    if (xline.indexOf (INOK) < CERO) continue;
	    
	    String [] split=xline.split (INOK);
	    if (split == null) continue;
	    if (split.length < DOS) continue;
	    
	    va.add ((split [CERO]).trim ());
	    vb.add ((split [UNO]).trim ());
	}
	
	int vasize=va.size ();
	int vbsize=vb.size ();
	
	if (vasize != vbsize)
	{
	    System.out.println ("VAS != VBS Error!");
	    ocurredError=true;
	}
	
	if (!ocurredError)
	{
	    srcs=new String [vasize];
	    dsts=new String [vbsize];
	    
	    for (int i=0; i<vasize; i++)
	    {
		srcs [i]=va.get (i);
		dsts [i]=vb.get (i);
	    }
	    
	    slen=srcs.length;
	    dlen=dsts.length;
	    
	    System.out.println ("");
	    
	    for (int i=0; i<vasize; i++)
	    {
		System.out.println (""+(i+1)+"/"+vasize+"; "+srcs [i]+":"+dsts [i]+"");
	    }
	}
	
	//Source:Dest
	//#########
	//â:"
	//â
	//â:-
	//â:-
	//â:'
	//<em>:
	//###END###
	
	br.close ();
	isr.close ();
	fis.close ();
	
	return;
    }
    
    final
    private static void readFile (String src, String dst)
    throws IOException
    {
	File fd=new File (src);
	InputStream fis=new FileInputStream (fd);
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	File fz=new File (dst);
	OutputStream fos=new FileOutputStream (fz);
	PrintStream ps=new PrintStream (fos, true, "UTF-8");
	
	String line=null;
	String xline="";
	
	final String remove="remove";
	
	while ( (line=br.readLine ()) != null)
	{
	    xline=line.trim ();
	    
	    for (int i=0; i<slen; i++)
	    {
		if (dsts [i].equals (remove))
		{
		    xline=xline.replaceAll (srcs [i], "");
		}
		else
		{
		    xline=xline.replaceAll (srcs [i], dsts [i]);
		}
	    }
	    
	    ps.println (xline);
	}
	
	br.close ();
	isr.close ();
	fis.close ();
	
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	boolean usb=usage (args);
	
	if (!usb || ocurredError)
	{
	    System.exit (-1);
	}
	
	try
	{
	    xinit (args [0x0000]);
	    readFile (args [0x0001], args [0x0002]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.out.println ("\n"+args [1]+" --> "+args [2]+"");
	System.exit (0x0000);
    }
    
}
