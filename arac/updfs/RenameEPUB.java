import java.io.*;

final
public class RenameEPUB
extends Object
implements Serializable
{

    private RenameEPUB ()
    {
	super ();
	System.exit (-1);
    }
    
    public String toString ()
    {
	return "Ex: Rename EPUB chapter1 file like chapter0001";
    }
    
    final
    private static void usage (final String [] args)
    {
	if (args.length < 1)
	{
	    System.out.println ("Example usage:\n\tjava RenameEPUB epubdir/OEBPS/Text");
	    System.exit (-1);
	}
    }
    
    final public static void rename (String srcdir)
    {
	File f=new File (srcdir);
	
	final String prefix=(""+srcdir+"/");
	
	boolean varro=true;
	
	if ((f.exists () == false) ||
	    (f.isFile ()) )
	{
	    varro=false;
	}
	
	if (!varro)
	{
	    System.out.println (""+srcdir+" does not exist or it is not a directory error.");
	    System.exit (-1);
	}
	
	String sart1="htm";
	String sart2=".";
	
	File [] files=f.listFiles ();
	final int flen=files.length;
	
	File temp=null;
	String name="";
	
	File dest=null;
	
	final int CERO=0x0000;
	
	for (int i=CERO; i<flen; i++)
	{
	    temp=files [i];
	    if (temp.isDirectory ()) continue;
	    
	    name=temp.getName ();
	    
	    if ((name.indexOf (sart1) < CERO) &&
	        (name.indexOf (sart2) < CERO) )
	    {
		continue;
	    }
	    
	    dest=rto (prefix, name);
	    
	    temp.renameTo (dest);
	    
	    System.out.println (name+" --> "+dest.getName ()+"");
	}
	
	return;
    }
    
    final
    private static File rto (String prefi, String nm)
    throws NumberFormatException
    {
	if (nm == null) return null;
	if (nm.length () < 5) return null;//a.htm -> 5 chars minimum
	
	//prefi=epubdir/OEBPS/Text/
	//nm=chapter1.xhtml
	
	StringBuffer pre=new StringBuffer ();
	StringBuffer num=new StringBuffer ();
	StringBuffer suf=new StringBuffer ();
	
	char [] chars=nm.toCharArray ();
	final int len=chars.length;
	
	boolean nkfound=false;
	
	final char PUNTO='.';
	
	char ch='a';
	
	for (int i=0; i<len; i++)
	{
	    ch=chars [i];
	    if (ch == PUNTO)
	    {
		nkfound=true;
		continue;
	    }
	    
	    if (nkfound)
	    {
		suf.append (ch);
		continue;
	    }
	    else
	    {
		if (Character.isDigit (ch))
		{
		    num.append (ch);
		}
		else
		{
		    pre.append (ch);
		}
	    }
	}
	
	String prestr=pre.toString ();
	String numstr=num.toString ();
	String sufstr=suf.toString ();
	
	if (numstr.length () > 0x0000)
	{
	    int number=Integer.parseInt (numstr);
	    numstr=String.format ("%04d", number);
	}
	
	String name=(""+prefi+prestr+numstr+PUNTO+sufstr+"");
	
	File hh=new File (name);
	
	return hh;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);
	
	try
	{
	    rename (args [0]);
	}
	catch (NumberFormatException nfe)
	{
	    nfe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.out.println ("\nDone.\n");
	
	System.exit (0);
    }
    
}
