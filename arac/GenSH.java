import java.io.*;

final
public class GenSH
extends Object
implements Serializable
{

    private GenSH ()
    {
	super ();
    }
    
    public String toString ()
    {
	return "";
    }
    
    final
    private static void gen ()
    throws IOException
    {
	String nmdst="genFrom.sh";
	File fd=new File (nmdst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true);
	
	File f=new File ("updfs");
	File [] files=f.listFiles ();
	int len=files.length;
	File temp=null;
	String name="", namex="";
	
	java.util.Arrays.sort (files);
	
	for (int i=0; i<len; i++)
	{
	    temp=files [i];
	    if (temp.isDirectory ()) continue;
	    
	    name=temp.getName ();
	    namex=name.toLowerCase ();
	    if (namex.endsWith (".txt") == false) continue;
	    
	    ps.println ("copy /y updfs\"+name+" test.txt");
	    ps.println ("java -cp cls; GenerateSH infos\info.dat");
	    ps.println (".\auto.bat");
	    ps.println ("move /y ..\dist\E*.apk myapks");
	    ps.println ("");
	}
	
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	try
	{
	    gen ();
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.out.println ("Look at to genFrom.sh");
	System.exit (0);
    }
    
}
