import java.io.*;

final
public class ToHTML
extends Object
implements Serializable
{
    
    private ToHTML ()
    {
	super ();
    }
    
    final
    private static String cnv (String old)
    {
        old=old.replaceAll ("\u0130", "&#304;");
        old=old.replaceAll ("\u0131", "&#305;");
        old=old.replaceAll ("\u00D6", "&#214;");  
        old=old.replaceAll ("\u00F6", "&#246;"); 
        old=old.replaceAll ("\u00DC", "&#220;");  
        old=old.replaceAll ("\u00FC", "&#252;");  
        old=old.replaceAll ("\u00C7", "&#199;");  
        old=old.replaceAll ("\u00E7", "&#231;"); 
        old=old.replaceAll ("\u011E", "&#286;");  
        old=old.replaceAll ("\u011F", "&#287;");  
        old=old.replaceAll ("\u015E", "&#350;");  
        old=old.replaceAll ("\u015F", "&#351;"); 
        old=old.replaceAll (Character.toString ((char)221), "&#304;");
        old=old.replaceAll (Character.toString ((char)253), "&#305;"); 
        old=old.replaceAll (Character.toString ((char)222), "&#350;"); 
        old=old.replaceAll (Character.toString ((char)254), "&#351;"); 
        old=old.replaceAll (Character.toString ((char)240), "&#287;");
        
        return old;
    };
    
    public String toString ()
    {
	return " ";
    }
    
    final
    private static void convert (final String [] args)
    throws IOException
    {
	if (args.length < 2)
	{
	    System.out.println ("java ToHTML src dest");
	    System.exit (-1);
	}
	
	File f=new File (args [0]);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	File f2=new File (args [1]);
	OutputStream fos=new FileOutputStream (f2);
	PrintStream ps=new PrintStream (fos, true, "UTF-8");
	
	String line=null;
	
	String xline="";
	
	int cnt=0;
	
	while ( (line=br.readLine ()) != null )
	{
	    xline=cnv (line);
	    ps.println (xline);
	    
	    ++cnt;
	    System.out.printf ("Wrote %d. line.\n", cnt);
	}
	
	br.close ();
	isr.close ();
	fis.close ();
	
	ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	try
	{
	    convert (args);
	}
	catch (Exception e)
	{
	    e.printStackTrace ();
	    System.exit (-1);
	}
	
	System.out.println (""+args [0]+" --> "+args [1]+"");
	
	System.exit (0);
    }

}
