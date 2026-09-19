import java.io.*;

final
public class ReDesignPackage
extends Object
implements Serializable
{
    
    private static String OLDNAME="";//"net.murat.ebook";
    private static String OLDNAME2="";//"net_murat_ebook";
	private static String OLDNAME2X="";//"net/murat/ebook";
	private static final String SAFIS=".sayfas.*;";
	
    private ReDesignPackage ()
    {
	super ();
    }
    
    public String toString ()
    {
	return " ";
    }
    
    final
    private static void changeFile (File src, String packageName)
    throws IOException
    {
	Reader fr=new FileReader (src);
	BufferedReader br=new BufferedReader (fr);
	
	int index=-1;
	
	StringBuffer sb=new StringBuffer ();
	
	String line=null;
	
	while ( (line=br.readLine ()) != null)
	{
	    if (line.trim ().length () < 1) 
	    {
		sb.append ("\n");
		continue;
	    }
	    
	    if (line.indexOf (OLDNAME) >= 0)
	    {
		line=line.replaceAll (OLDNAME, "net.murat.ebook");
	    }
	    
	    if (line.indexOf (OLDNAME2X) >= 0)
	    {
		line=getMLine (line);
		line=line.replace (OLDNAME2, "net/murat/ebook");
		line=getTLine (line);
	    }
	    
		if (line.indexOf (SAFIS) >= 0)
		{
			line="import net.murat.sayfas.*;";
		}
		  
	    sb.append (line);
	    sb.append ("\n");
	}
	
	br.close ();
	fr.close ();
	
	OutputStream fos=new FileOutputStream (src);
	PrintStream ps=new PrintStream (fos, true);
	
	String text=sb.toString ();
	ps.print (text);
	
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	
	System.out.println ("Changed: "+src+"");
	
	return;
    }
    
	final
	private static String getMLine (String line)
	{
        final char ACIZ='_';
		final char LSP='/';
	
		char ch='a';
		int mlo=line.length ();
		
		StringBuffer sbr=new StringBuffer ();

		for (int i=0; i<mlo; i++)
		{
			ch=line.charAt (i);
			
			if (ch == LSP)
			{
				sbr.append (ACIZ);
			}
			else
			{
				sbr.append (ch);
			}
		}
				
		String m=sbr.toString ();
		
		return m;
	}
	
	final
	private static String getTLine (String line)
	{
        final char ACIZ='_';
		final char LSP='/';
	
		char ch='a';
		int mlo=line.length ();
		
		StringBuffer sbr=new StringBuffer ();

		for (int i=0; i<mlo; i++)
		{
			ch=line.charAt (i);
			
			if (ch == ACIZ)
			{
				sbr.append (LSP);
			}
			else
			{
				sbr.append (ch);
			}
		}
		
		String m=sbr.toString ();
		
		return m;
	}
	
    final
    public static void main (final String [] args)
    {
	if (args.length < 1)
	{
	    System.out.println ("Usage:\n\tjava -cp cls; ReDesignPackage [<newPackageName>]");
	    System.out.println ("Example:\n\tjava -cp cls; ReDesignPackage atcero");
	    System.exit (-1);
	}
	
	String pn=args [0].toLowerCase ();
	
	OLDNAME="net.murat."+pn+".ebook";
    OLDNAME2="net_murat_"+pn+"_ebook";
	OLDNAME2X="net/murat/"+pn+"/ebook";
	
	final File [] FILES=new File []
	{
	new File ("../src/net/murat/ebook/EBook.java"),
	new File ("../src/net/murat/ebook/ScenePanel.java"),
	new File ("../AndroidManifest.xml"),
    new File ("../clean.sh"),
	new File ("../kstore.sh"),
	new File ("../control.sh"),
	new File ("../make.sh")
	};
	
	final int FLEN=FILES.length;

	for (int i=0; i<FLEN; i++)
	{
	    try
	    {
		changeFile (FILES [i], pn);
	    }
	    catch (IOException ioe)
	    {
		ioe.printStackTrace ();
		continue;
	    }
	}
	
	System.exit (0);
    }
    
}
