import java.io.*;

final
public class V2UTFToISO
extends Object
implements Serializable
{

    private static boolean isTR=false;
    
    final private static String VESTR="ve";
    final private static String KISTR="ki";
    final private static String DESTR="de";
    final private static String DASTR="da";
    final private static String ILESTR="ile";
    final private static String ILASTR="ila";
    
    private V2UTFToISO ()
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
	    System.out.println ("Example-1:\n\tjava -cp cls: V2UTFToISO src.txt dst.txt\n");
	    System.out.println ("Example-2:[Turkish texts]:\n\tjava -cp cls: V2UTFToISO src.txt dst.txt tr\n");
	    System.out.println ("Example-2:[nonTurkish texts in UTF-8]:\n\tjava -cp cls: V2UTFToISO src.txt dst.txt nontr UTF-8");
	    
	    System.exit (-1);
	}
	
	if (args [0x0000].equals (args [0x0001]))
	{
	    System.out.println ("Src and dst files are same. Exiting...");
	    
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String src, String dst, String code)
    throws IOException
    {
	File f=new File (src);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, code);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, "ISO-8859-9");
	
	String line=null;
	
	StringBuffer sbr=new StringBuffer ();
	
	String moline="";
	final String TIRE="-";
	final String YYU=Character.toString ((char)(173));
	final int CERO=0x0000;
	final int UNO=0x0001;
	
	final String NOKTA=".";
	final String UNLEM="!";
	final String SORU="?";
	
	while ( (line=br.readLine ()) != null )
	{
	    //line=line.trim ();
	    moline=line.trim ();
	    moline=moline.replaceAll ("\t", " ");
	    moline=moline.trim ();
	    
	    if (moline.length () < 1) sbr.append ("\n");
	    
	    for (int j=0; j<12; j++)
	    {
		moline=moline.replaceAll ("  ", " ");
	    }
	    
	    if (sfnum (moline)) continue;
	    
	    if (moline.endsWith (TIRE) || moline.endsWith (YYU))
	    {
		line=moline.substring (CERO, moline.length ()-UNO);
		sbr.append (line);
	    }
	    else
	    {
		sbr.append (moline);
		
		if (moline.endsWith (NOKTA) ||
		    moline.endsWith (UNLEM) ||
		    moline.endsWith (SORU)  ||
		    //isTotalUpper (moline)   ||
		    isFirstUpper (moline)   ||
		    isRomen (moline)
		   )
		{
		    sbr.append ("\n");
		}
		else
		{
		    sbr.append (" ");
		}
	    }
	}

	br.close ();
	isr.close ();
	fis.close ();
	
	String text=sbr.toString ();
	//text=text.replaceAll ("-", "");
	
	V2Utils.setTR (isTR);

	String mext=V2Utils.getBeautifiedText (text);
	mext=V2Utils.ygetBeautifiedText (mext);
	
	mext=mext.replaceAll (" \n", "\n");
	mext=mext.replaceAll ("\\. ", ".");
	mext=mext.replaceAll (", ", ",");
	mext=mext.replaceAll ("\\.", ". ");
	mext=mext.replaceAll (",", ", ");
	mext=mext.replaceAll (" \n", "\n");
	
	ps.print (mext);
	ps.print ("\n");
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println ("\n"+src+" --> "+dst+"");
	
	return;
    }
    
    final
    private static boolean isTotalUpper (String xstr)
    {
	boolean issf=true;
    
	String str=xstr;//.trim ();
	//str=xstr.replaceAll (" ", "");
    
	if (str == null) 
	{
	    issf=false;
	    return issf;
	}
	
	int strlen=str.length ();
	if (strlen < 1)
	{
	    issf=false;
	    return issf;
	}
	
	char ch = '*';
	//int chm=0;

	for (int i=0; i<strlen; i++)
	{
    	    ch=str.charAt (i);
    	    if (Character.isLowerCase (ch))
    	    {
    		issf=false;
    		break;
    	    }
	}
    
	return issf;
    }
    
    final
    private static boolean isFirstUpper (String xstr)
    {
	boolean issf=true;
    
	String str=xstr;//.trim ();
	//str=xstr.replaceAll (" ", "");
	
	if (str == null) 
	{
	    issf=false;
	    return issf;
	}
	
	int strlen=str.length ();
	if (strlen < 1)
	{
	    issf=false;
	    return issf;
	}
	
	char ch = '*';
	
	if (str.indexOf (" ") < 0)
	{
	    ch=str.charAt (0);
	    if (Character.isLowerCase (ch))
    	    {
    		issf=false;
    		return issf;
    	    }
	}
	
	//int chm=0;
	
	String [] words=str.split (" ");
	if (words == null)
	{
	    issf=false;
    	    return issf;
	}
	
	strlen=words.length;
	if (strlen < 1)
	{
	    issf=false;
    	    return issf;
	}
	
	issf=true;
	
	String word="";
	
	for (int i=0; i<strlen; i++)
	{
	    word=(words [i]).trim ();
	    
	    if (word.equalsIgnoreCase (VESTR)  ||
		word.equalsIgnoreCase (KISTR)  ||
    		word.equalsIgnoreCase (DESTR)  ||
    		word.equalsIgnoreCase (DASTR)  ||
    		word.equalsIgnoreCase (ILESTR) ||
    		word.equalsIgnoreCase (ILASTR))
    	    {
    		continue;
    	    }
    	    
    	    ch=word.charAt (0);
    	    if (Character.isLowerCase (ch))
    	    {
    		issf=false;
    		break;
    	    }
	}
    
	return issf;
    }
    
    final
    private static boolean isRomen (String xstr)
    {
	boolean issf=false;
    
	String str=xstr.trim ();
	str=xstr.replaceAll (" ", "");
	str=str.toUpperCase ();
	
	if (str.equals ("I")        ||
	    str.equals ("II")       ||
	    str.equals ("III")      ||
	    str.equals ("IV")       ||
	    str.equals ("V")        ||
	    str.equals ("VI")       ||
	    str.equals ("VII")      ||
	    str.equals ("VIII")     ||
	    str.equals ("IX")       ||
	    str.equals ("X")        ||
	    
	    str.equals ("XI")       ||
	    str.equals ("XII")      ||
	    str.equals ("XIII")     ||
	    str.equals ("XIV")      ||
	    str.equals ("XV")       ||
	    str.equals ("XVI")      ||
	    str.equals ("XVII")     ||
	    str.equals ("XVIII")    ||
	    str.equals ("XIX")      ||
	    str.equals ("XX")       ||
	    
	    str.equals ("XXI")      ||
	    str.equals ("XXII")     ||
	    str.equals ("XXIII")    ||
	    str.equals ("XXIV")     ||
	    str.equals ("XXV")      ||
	    str.equals ("XXVI")     ||
	    str.equals ("XXVII")    ||
	    str.equals ("XXVIII")   ||
	    str.equals ("XXIX")     ||
	    str.equals ("XXX")      ||
	    
	    str.equals ("XXXI")     ||
	    str.equals ("XXXII")    ||
	    str.equals ("XXXIII")   ||
	    str.equals ("XXXIV")    ||
	    str.equals ("XXXV")     ||
	    str.equals ("XXXVI")    ||
	    str.equals ("XXXVII")   ||
	    str.equals ("XXXVIII")  ||
	    str.equals ("XXXIX")    ||
	    str.equals ("XXXX")     ||
	    
	    str.equals ("XXXXI")    ||
	    str.equals ("XXXXII")   ||
	    str.equals ("XXXXIII")  ||
	    str.equals ("XXXXIV")   ||
	    str.equals ("XXXXV")    ||
	    str.equals ("XXXXVI")   ||
	    str.equals ("XXXXVII")  ||
	    str.equals ("XXXXVIII") ||
	    str.equals ("XXXXIX")   ||
	    
	    str.equals ("M")        ||
	    str.equals ("C")        ||
	    str.equals ("L")
	   )
	{
	    issf=true;
	}
	
	return issf;
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
	
	String cnt="UTF-8";
	
	if (args.length == 3)
	{
	    cnt="UTF-8";
	}
	else if (args.length > 3)
	{
	    cnt=args [3];
	}
	
	try
	{
	    System.out.println ("Do you want to remove numbers? (Y or N)");
	    BufferedReader br=new BufferedReader (new InputStreamReader (System.in));
	    String line=br.readLine ();
	    if (line.equalsIgnoreCase ("Y"))
	    {
		V2Utils.setBorrowNums (true);
	    }
	    else if (line.equalsIgnoreCase ("N"))
	    {
		V2Utils.setBorrowNums (false);
	    }
	    else
	    {
		V2Utils.setBorrowNums (true);
	    }
	    
	    br.close ();
	}
	catch (IOException ioe)
	{
	    V2Utils.setBorrowNums (true);
	}
	
	try
	{
	    cnt (args [0], args [1], cnt);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}

	System.out.println ("");	
	System.out.println ("Number Borrows: "+(V2Utils.getBorrowNums ()));
	System.out.println ("");
	System.out.println ("Source charset: "+cnt+"");
	
	System.exit (0);
    }
    
}
