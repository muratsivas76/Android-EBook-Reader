import java.io.*;

public class EkleDizin
{

    private static boolean isAll=true;
    private static String [] FORMATS=null;
    private static int FORMATSLEN=-1;
    
    private static void ekle (String src, String dst, String inchar, String outchar)
    throws IOException
    {
	File file=new File (src);
	if (file.isDirectory () == false) return;
	
	File [] files=file.listFiles ();
	java.util.Arrays.sort (files);
	int len=files.length;
	
	if (len < 1) return;
	
	File dest=new File (dst);
	OutputStream fos=new FileOutputStream (dest);
	PrintStream ps=new PrintStream (fos, true, outchar);
	
	File temp=null;
	String name="";
	
	boolean devam=false;
	
	for (int i=0; i<len; i++)
	{
	    temp=files [i];
	    if (temp.isDirectory ()) continue;
	    name=(temp.getName ()).toLowerCase ();
	    
	    devam=false;
	    
	    if (!isAll)
	    {
		for (int j=0; j<FORMATSLEN; j++)
		{
		    if (name.endsWith (FORMATS [j]))
		    {
			devam=true;
			break;
		    }
		}
	    }
	    else
	    {
		devam=true;
	    }
	    
	    if (!devam)
	    {
		System.out.println ("Skipped: "+(i+1)+"/"+len+": "+name+"");
		continue;
	    }
	    
	    InputStream fis=new FileInputStream (temp);
	    Reader isr=new InputStreamReader (fis, inchar);
	    BufferedReader br=new BufferedReader (isr);
	    
	    String line = null;
	    
	    while ( (line=br.readLine ()) != null)
	    {
		ps.println (line);
	    }
	    
	    ps.println ("");
	    
	    br.close ();
	    isr.close ();
	    fis.close ();
	    
	    System.out.println ("Processed: "+(i+1)+"/"+len+": "+name+"");
	}
	
	ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println ("Done.");
    }
    
    public static void main (String [] args)
    {
	int alen=args.length;

	if (alen < 4)
	{
	    System.out.println ("Usage: \n\tjava EkleDizin [<srcFolder>] [<destFile>] [<srcCharCode>] [<destCharCode>] [optional-formats ...\n");
	    System.out.println ("Example-1: \n\tjava EkleDizin folder zresult.txt UTF-8 UTF-8");
	    System.out.println ("Example-2: \n\tjava EkleDizin folder zresult.txt UTF-8 UTF-8 txt html xhtml");
	    
	    System.exit (-1);
	}
	
	isAll=true;
	if (alen > 4) isAll=false;
	
	if (isAll == false)
	{
	    int syc=3;
	    int forlen=alen-4;
	    FORMATS=new String [forlen];
	    for (int i=0; i<forlen; i++)
	    {
		FORMATS [i]=((".")+(args [++syc])).toLowerCase ();
	    }
	    
	    FORMATSLEN=FORMATS.length;
	    
	    for (int i=0; i<FORMATSLEN; i++)
	    {
		System.out.print (""+FORMATS [i]+"; ");
	    }
	    
	    System.out.println ("");
	}
	
	try
	{
	    ekle (args [0], args [1], args [2], args [3]);
	    System.out.println ("Look at "+args [1]+"");
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
    }
}
