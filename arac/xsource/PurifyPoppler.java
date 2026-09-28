import java.io.*;

final
public class PurifyPoppler
extends Object
implements Serializable
{
    
    private PurifyPoppler ()
    {
	System.exit (-1);
    }
    
    public String toString ()
    {
	return "PurifyPoppler";
    }
    
    final
    private static boolean purify (final String [] args)
    throws IOException
    {
	File src=new File (args [0]);
	InputStream fis=new FileInputStream (src);
	Reader isr=new InputStreamReader (fis, "UTF-8");
	BufferedReader br=new BufferedReader (isr);
	
	File dst=new File (args [1]);
	OutputStream fos=new FileOutputStream (dst);
	PrintStream ps=new PrintStream (fos, true, "UTF-8");
	
	String line=null;
	
	int index=-1;
	
	while ( (line=br.readLine ()) != null )
	{
	    line=line.trim ();
	    
	    if (line.startsWith ("<span class=\"") == false)
	    {
		continue;
	    }
	    
	    //<span class="ft1">
	    //if (line.startsWith ("<span class=\""))
	    //{
		index=line.indexOf (">", 0);
		if (index >=0)
		{
		    line=line.substring (index+1);
//		    line=line.replace (">", "");
		}
	    //}
	    
	    line=line.replaceAll ("&nbsp;", " ");
	    line=line.replaceAll ("&amp;", " ");
	    line=line.replaceAll ("&quot;", " ");
	    line=line.replaceAll ("<hr>", "\n");
	    line=line.replaceAll ("<br>", "\n");
	    line=line.replaceAll ("</a>", "");
	    line=line.replaceAll ("</span>", "");
	    line=line.replaceAll ("  ", " ");
	    
	    //<a href="simdi.html#9">
	    if (line.startsWith ("<a href=\""))
	    {
		index=line.indexOf (">");
		if (index >=0)
		{
		    line=line.substring (index);
		    line=line.replace (">", "");
		}
	    }
	    
	    ps.println (line);
	}//end while
	
	br.close ();
	isr.close ();
	fis.close ();
	
	ps.flush ();   ps.close ();
	fos.flush (); fos.close ();
	
	return true;
    }
    
    final
    private static boolean usage (final String [] args)
    {
	if (args.length < 2)
	{
	    System.out.println ("Example:\n\tjava PurifyPoppler zsil.html dest.txt");
	    return false;
	}
	
	File f=new File (args [0]);
	if (f == null)
	{
	    System.out.println ("Error: Null File!");
	    return false;
	}
	
	if (f.exists () == false)
	{
	    System.out.println ("Error: File does not exist!");
	    return false;
	}
	
	if (f.isDirectory ())
	{
	    System.out.println ("Error: Is directory!");
	    return false;
	}
	
	return true;
    }
    
    final
    public static void main (final String [] args)
    {
	boolean b=usage (args);
	if (!b)
	{
	    System.exit (-1);
	}
	
	boolean result=false;
	
	try
	{
	    result=purify (args);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	if (result)
	{
	    System.out.println ("Successfull: "+args [0]+" --> "+args [1]+"");
	}
	else
	{
	    System.out.println ("Error: "+args [0]+" --> "+args [1]+"");
	}
	
	System.exit (0);
    }
    
}
//Example GUIDE:
//1-  /opt/poppler/bin/pdftohtml -c -i -noframes -enc UTF-8 -nomerge file.pdf xfile.html
//2-  java GetFT xfile.html ztv.txt [java -cp cproje: GetFT xfile.html ztv.txt]
//3-  java FTDevam xfile.html zsil.html ft0 ft1 ft2... [java -cp proje: FTDevam file.html zsil.html ft0 ft1 ft2...]
//4-  mv -fv zsil.html file.html
//5-  java SadeleHTML file.html zsil.html  [java -cp cproje: SadeleHTML file.html zsil.html]
//6-  java PurifyPoppler zsil.html dest.txt  [java -cp cproje: PurifyPoppler zsil.html dest.txt]
//7-  java -jar /home/muratsivas76/istasyon/jclass/notdefteri_eylul_2020.jar &
//8-  Open dest.txt; edit and "beauty text 2"; replaceAll/eliminate FOOTNOTE:; and convert to HTML for example with dst prefix. This will produce islem/dst.html
//9-  mv -fv islem/dst.html .
//10- java -jar /home/muratsivas76/istasyon/jclass/htmltopdf_140418.jar -f dst.html dst.pdf -p ../../infos/yoli.prop
