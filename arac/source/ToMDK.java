import java.io.*;

import java.awt.*;

import java.awt.image.BufferedImage;

import java.util.HashSet;
import java.util.Iterator;

final
public class ToMDK
extends Object
implements java.io.Serializable
{
  private static FontMetrics FM=null;
  private static int SPACELEN=0;
  private static String CHARSET="ISO-8859-9";
  private static int LCNT=5;
  private static float HCNT=652F;
  private static boolean isAlign=false;
  private static String PACKAGENAME="mypackage";
  private static String srcFileName="test.txt";
  
  private static int DNUM=0x0000;
  
  private static final HashSet <String> set=new HashSet <String> ();
  private static Iterator <String> iter=null;
  
  private ToMDK ()
  {
    super ();
  }
  
  public String toString ()
  {
    return "__--__";
  }
  
  protected Object clone ()
  throws CloneNotSupportedException
  {
    return super.clone ();
  }
  
  final
  private static void usage (final String [] args)
  {
    if (args.length < 7)
    {
      System.out.println ("Syntax: java -cp cls; ToMDK fileName font charset lineCount lineFloatLength alignYesOrNo packageName");
      System.out.println ("Example: java -cp cls; ToMDK test.html SansSerif,0,27 ISO-8859-9 5 652 no mybook");
      System.exit (-1);
    }
  }
  
  final 
  private static String convertedUnicode (String input) 
  {
        StringBuffer msb=new StringBuffer ();
        
        final int len=input.length ();
        char chr='v';
        int cht=0x0000;
		
        for (int i=0; i<len; i++)
        {
    	    chr=input.charAt (i);
			cht=(int)chr;
			
			if (cht == 92)
			{
			  continue;	
			}
			
    	    if (cht == 32)
    	    {
    		msb.append (chr);
    		continue;
    	    }
    	    
			if ((cht == 34) || (cht == 39))
    	    {
			msb.append ("\\");
    		msb.append (chr);// " '
    		continue;
    	    }
			
    	    if ((cht >= 33) && (cht <= 122))
    	    {
    		msb.append (chr);
    		continue;
    	    }
    	    
    	    StringBuffer sb=new StringBuffer ("\\u");
        
    	    String str=String.format("%04x", cht);
    	    str=str.toUpperCase ();
        
    	    sb.append (str);
        
    	    str=sb.toString ();
    	    msb.append (str);
        }
        
        return (msb.toString ());
  }
  
  final
  private static void init (final String [] args)
  throws NumberFormatException
  {
    srcFileName=args [0];
    
    String fstr=args [1];
    String spl []=fstr.split (",");
    
    Font FONT=new Font (spl [0],
      Integer.parseInt (spl [1]),
    Integer.parseInt (spl [2]));
    
    BufferedImage bimg=new BufferedImage (1, 1, 1);
    Graphics2D g2d=bimg.createGraphics ();
    
    g2d.setFont (FONT);
    FM=g2d.getFontMetrics ();
    
    SPACELEN=FM.stringWidth (" ");
    
    CHARSET=args [2];
    LCNT=Integer.parseInt (args [3]);
    
    HCNT=Float.parseFloat (args [4]);
    String ALIGN_MDK=args [5];
    if (ALIGN_MDK.equalsIgnoreCase ("yes"))
    {
      isAlign=true;
    }
    else
    {
      isAlign=false;
    }
    
	PACKAGENAME=args [6];
	
    System.out.println (FONT.toString ());
    System.out.println ("");
    System.out.println ("SRC: "+srcFileName+"");
    System.out.println ("CHARSET: "+CHARSET+"");
    System.out.println ("SPACELEN: "+SPACELEN+"");
    System.out.println ("LCNT: "+LCNT+"");
    System.out.println ("HCNT: "+HCNT+"\n");
    System.out.println ("ALIGN-MDK: "+isAlign+"\n");
	System.out.println ("PACKAGENAME: "+PACKAGENAME+"\n");
    
    return;
  }
  
  final
  static private String getPureString (String str)
  {
    int aindex=-1;
    int bindex=-1;
    
    String STRTAG="<";
    String ENDTAG=">";
    
    int dif=-1;
    int start=0;
    
    String mline=str;
    
    set.clear ();//reset set
    
    while ( (aindex=str.indexOf (STRTAG, start)) >= 0)
    {
      bindex=str.indexOf (ENDTAG, start+1);
      if (bindex < 0) continue;
      
      set.add (str.substring (aindex, bindex+1));
      
      start=bindex+1;
    }
    
    iter=set.iterator ();
    
    String next="";
    while (iter.hasNext ())
    {
      next=iter.next ();
      mline=mline.replaceAll (next, "");
    }
    
    return (mline);
  }
  
  final
  private static void sendMDK ()
  throws IOException
  {
    //File fd=new File ("mdk");
    //if (fd.exists () == false)
    //{
    //  fd.mkdir ();
    //}
    
    InputStream fis=new FileInputStream (new File ("sdk/r00000.txt"));
    Reader isr=new InputStreamReader (fis, CHARSET);
    BufferedReader br=new BufferedReader (isr);
    
    int n=0;
    
   // File fss=new File ("mdk\\r"+String.format ("%05d", ++n)+".dat");
   // OutputStream fos=new FileOutputStream (fss);
  //  PrintStream ps=new PrintStream (fos, true, CHARSET);
    
	File fssx=new File ("../src/net/murat/"+PACKAGENAME+"/sayfas/Sayfa_"+String.format ("%05d", ++n)+".java");
    OutputStream fosx=new FileOutputStream (fssx);
    PrintStream psx=new PrintStream (fosx, true, CHARSET);
	
	String clsname=(fssx.getName ()).replace (".java", "");
	
	preWrite (psx, clsname);
	
    int lnc=0;
    
    String line=null;
    
    //System.out.println ("Wrote: mdk\\"+fss.getName ()+"");
    System.out.println ("Wrote: ../src/net/murat/"+PACKAGENAME+"/sayfas/"+fssx.getName ()+"");
//    boolean isBR=false;
    
    while ( (line=br.readLine ()) != null)
    {
      //++lnc;
      line=line.trim ();
//      if (line.equalsIgnoreCase ("<html><body><br>")) continue;
      
      if (line.length () < 1)
      {
       // ps.println ("");
		psx.println ("\t\t{"+"\""+"\"},");
		
        ++lnc;
        continue;
      }

//      if (line.equals ("<br>") || line.equals ("</br"))
//      {
//        if (isBR) continue;
//        isBR=true;
//      }
//      else
//      {
//        isBR=false;
//      }
      
      ++lnc;
      if (lnc >= LCNT)
      {
       // ps.flush ();
       // ps.close ();
       // fos.flush ();
       // fos.close ();
        endWrite (psx, clsname);
		
		psx.flush ();
		psx.close ();
		fosx.flush ();
		fosx.close ();
		
       // fss=new File ("mdk\\r"+String.format ("%05d", ++n)+".dat");
       // fos=new FileOutputStream (fss);
       // ps=new PrintStream (fos, true, CHARSET);
		
		fssx=new File ("../src/net/murat/"+PACKAGENAME+"/sayfas/Sayfa_"+String.format ("%05d", ++n)+".java");
		fosx=new FileOutputStream (fssx);
		psx=new PrintStream (fosx, true, CHARSET);
		
		clsname=(fssx.getName ()).replace (".java", "");
		
		preWrite (psx, clsname);
		
        lnc=0x0000;
        
       // System.out.println ("Wrote: mdk\\"+fss.getName ()+"");
		System.out.println ("Wrote: ../src/net/murat/"+PACKAGENAME+"/sayfas/"+fssx.getName ()+"");
      }
      
//      line=line.replaceAll (" \\<br\\>", "<br>");
//      if (line.endsWith ("</body></html><br>"))
//      {
//        line=line.replaceAll ("\\</body\\>\\</html\\>\\<br\\>", "");
//      }
      
      if (isAlign)
      {
        line=align (line);
        line=line.trim ();
      }
      
//	  line=line.replaceAll ("\'", "\\\\"+"\'");
//	  line=line.replaceAll ("\"", "\\\\"+"\"");

	  line=convertedUnicode (line);
	  
//      line=line.replaceAll ("_color=\"", " color=\"");
//      line=line.replaceAll ("_face=\"", " face=\"");
//      line=line.replaceAll ("_size=\"", " size=\"");
      
      //ps.println (line);
	  psx.println ("\t\t"+"{\""+line+"\"},");
    }
    
    br.close ();
    isr.close ();
    fis.close ();
    
   // ps.flush ();
   // ps.close ();
   // fos.flush ();
   // fos.close ();
	
	endWrite (psx, clsname);
	
	psx.flush ();
    psx.close ();
    fosx.flush ();
    fosx.close ();
	
	DNUM=n;
	
	System.out.println ("\nPage Count: "+Integer.toString (DNUM)+"\n");
	
	try
	{
		Thread.sleep (2000);
	}
	catch (InterruptedException ie)
	{}
	
    return;
  }
  
  final
  private static void preWrite (PrintStream psc, String name)
  throws IOException
  {
	  //File fssx=new File ("..\\src\\net\\murat\\sayfas\\Sayfa_"+String.format ("%05d", n)+".java");

	  psc.println ("package net.murat."+PACKAGENAME+".sayfas;\n");
	  
	  psc.println ("final");
	  psc.println ("public class "+name+"");
	  psc.println ("\textends Object");
	  psc.println ("\timplements java.io.Serializable, ASayfa");
	  psc.println ("{\n");
	  //psc.println ("\tprivate String allSayfa=\"\"\n");
	  psc.println ("\tprivate final String [][] lnns=");
	  psc.println ("\t{");
	  
	  return;
  }
  
  final
  private static void endWrite (PrintStream psc, String name)
  throws IOException
  {
	//File fssx=new File ("..\\src\\net\\murat\\sayfas\\Sayfa_"+String.format ("%05d", n)+".java");

	psc.println ("\t};\n");

	psc.println ("\tpublic "+name+" ()\n\t{\n\t\tsuper ();\n\t}\n");

	psc.println ("\tpublic String toString ()");
	psc.println ("\t{");
	psc.println ("\t\treturn \""+name+"\";");
	psc.println ("\t}\n");

	psc.println ("\tpublic String [][] getLines ()");
	psc.println ("\t{");
	psc.println ("\t\treturn lnns;");
	psc.println ("\t}\n");

	psc.println ("}//class end");
	  
	return;
  }
  
  final
  private static void taksim ()
  throws IOException
  {
    File fd=new File ("sdk");
    if (fd.exists () == false)
    {
      fd.mkdir ();
    }
    
    String fname=("sdk/r00000.txt");
    OutputStream fos=new FileOutputStream (new File (fname));
    PrintStream ps=new PrintStream (fos, true, CHARSET);
    
    InputStream fis=new FileInputStream (new File (srcFileName));
    Reader isr=new InputStreamReader (fis, CHARSET);
    BufferedReader br=new BufferedReader (isr);
    
    String line=null;
    int CCNT=(int)HCNT;
    
    // Global reusable buffers to maintain state cleanly across character streams
    StringBuilder wordAccumulator = new StringBuilder(128);
    
    while ( (line=br.readLine ()) != null)
    {
      line=line.trim ();
      
      if (line.length () < 1)
      {
        ps.println ("\n");
        continue;
      }
      
      line=line.replaceAll ("   ", " "); // three spaces to space
      line=line.replaceAll ("  ", " ");  // two spaces to space
      
      int len = line.length();
      int cursor = 0;
      int xcoord = 5;
      int rowVisibleCharCount = 0;
      
      boolean insideHtmlTag = false; // Persistent state machine switch for the entire row flow
      int pendingWordWidth = 0;
      int pendingWordChars = 0;
      
      wordAccumulator.setLength(0);
      
      // Strict sequence scanner evaluating raw characters sequentially without dynamic array splits
      while (cursor < len) {
          char c = line.charAt(cursor);
          
          // Detect HTML Tag boundaries dynamically
          if (c == '<') {
              insideHtmlTag = true;
              wordAccumulator.append(c);
              cursor++;
              continue;
          }
          if (c == '>') {
              insideHtmlTag = false;
              wordAccumulator.append(c);
              cursor++;
              continue;
          }
          
          // Always keep tracking and swallowing tag attributes without treating them as visible print layout bytes
          if (insideHtmlTag) {
              wordAccumulator.append(c);
              cursor++;
              continue;
          }
          
          // CRITICAL MILESTONE: If we intercept an outer whitespace delimiter, a full plaintext word interval has finished
          if (Character.isWhitespace(c)) {
              if (wordAccumulator.length() > 0) {
                  int pureLen = (int) (pendingWordWidth * 0.88f) + SPACELEN;
                  
                  // Dual-Safety Rule Check execution
                  if (((xcoord + pureLen) <= CCNT) && ((rowVisibleCharCount + pendingWordChars) <= 60)) {
                      ps.print(wordAccumulator);
                      ps.print(" ");
                      xcoord += pureLen;
                      rowVisibleCharCount += (pendingWordChars + 1);
                  } else {
                      ps.print("\n");
                      xcoord = 5 + pureLen;
                      rowVisibleCharCount = pendingWordChars + 1;
                      ps.print(wordAccumulator);
                      ps.print(" ");
                  }
                  
                  // Empty tracking tokens for the subsequent upcoming layout interval
                  wordAccumulator.setLength(0);
                  pendingWordWidth = 0;
                  pendingWordChars = 0;
              }
              cursor++;
              continue;
          }
          
          // Accumulate raw human-visible plain characters onto metrics tracking variables
          wordAccumulator.append(c);
          pendingWordWidth += FM.charWidth(c);
          pendingWordChars++;
          cursor++;
      }
      
      // Flush residual line segments lingering inside buffers after text termination
      if (wordAccumulator.length() > 0) {
          int pureLen = (int) (pendingWordWidth * 0.88f) + SPACELEN;
          if (((xcoord + pureLen) <= CCNT) && ((rowVisibleCharCount + pendingWordChars) <= 60)) {
              ps.print(wordAccumulator);
          } else {
              ps.print("\n");
              ps.print(wordAccumulator);
          }
      }
      
      ps.print("\n"); // Enforce natural carriage line step at the literal block paragraph endings
    }
    
    System.out.println ("Wrote: "+fname+"\n");
    
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
  private static String align (String old)
  {
    int olen=old.length ();
    if (olen < 1) return old;
    
    // --- ZERO-GARBAGE VISIBLE WIDTH ANALYSIS ---
    // Measure target row width filtering out layout tags so justification math remains pixel-perfect
    int visibleWidth = 0;
    int measureCursor = 0;
    boolean insideHtmlTag = false;
    
    while (measureCursor < olen) {
        char mc = old.charAt(measureCursor);
        
        if (mc == '<') {
            insideHtmlTag = true;
            measureCursor++;
            continue;
        }
        if (mc == '>') {
            insideHtmlTag = false;
            measureCursor++;
            continue;
        }
        if (insideHtmlTag) {
            measureCursor++;
            continue;
        }
        
        visibleWidth += FM.charWidth(mc);
        measureCursor++;
    }
    
    int CCNT=(int)(HCNT);
    if (visibleWidth >= (CCNT)) return old;
    
    int diff=CCNT-visibleWidth;
    int spacelen=FM.stringWidth (" ");
    diff /= spacelen;
    
    // --- HTML SAFE WHITESPACE SCANNER ---
    // Count whitespaces ONLY if they reside strictly outside of HTML/XML tag boundaries
    int spcc=0;
    insideHtmlTag = false;
    for (int i=0; i<olen; i++)
    {
      char chr=old.charAt (i);
      
      if (chr == '<') {
          insideHtmlTag = true;
          continue;
      }
      if (chr == '>') {
          insideHtmlTag = false;
          continue;
      }
      if (insideHtmlTag) {
          continue;
      }
      
      if (Character.isWhitespace (chr))
      {
        ++spcc;
      }
    }
    
    if (spcc < 0x1) return old;
    
    int refc=(diff/spcc); 
    if (refc < 1) return old;
    
    StringBuffer sb=new StringBuffer ();
    insideHtmlTag = false;
    
    // Re-inject padding spaces strictly behind legal outer plaintext word intervals
    for (int i=0; i<olen; i++)
    {
      char chr=old.charAt (i);
      
      if (chr == '<') {
          insideHtmlTag = true;
          sb.append (chr);
          continue;
      }
      if (chr == '>') {
          insideHtmlTag = false;
          sb.append (chr);
          continue;
      }
      
      sb.append (chr);
      
      if (insideHtmlTag) {
          continue;
      }
      
      if (Character.isWhitespace (chr))
      {
        for (int j=0; j<refc; j++)
        {
          sb.append (" ");
        }
      }
    }
    
    sb.append (" ");
    return sb.toString ();
  }

  final
  private static void genASayfa ()
  throws IOException
  {
	File fd=new File ("../src/net/murat/"+PACKAGENAME+"/sayfas/ASayfa.java");
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true);
	
	///////
	ps.println ("package net.murat."+PACKAGENAME+".sayfas;\n");

	ps.println ("public interface ASayfa");
	ps.println ("{\n");
	
    ps.println ("\t//Variables");
    ps.println ("\tpublic static final String NL=\""+"\\"+"\"\""+";");
    ps.println ("\tpublic static final int CERO=0x0000;\n");
    
    ps.println ("\t//Methods");
    //ps.println ("\tpublic String getName ();\n");
    //ps.println ("\tpublic void generate ();\n");
    ps.println ("\tpublic String [][] getLines ();\n");
	
	ps.println ("}");
	////////
	  
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	  
	return;
  }
  
  final
  private static void genAInfos ()
  throws IOException
  {
	File fd=new File ("../src/net/murat/"+PACKAGENAME+"/sayfas/AInfos.java");
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true);
	
	///////
	ps.println ("package net.murat."+PACKAGENAME+".sayfas;\n");
    ps.println ("\nimport java.util.Vector;\n");
	ps.println ("final\npublic class AInfos\n\textends Object\n\timplements java.io.Serializable {\n\n");
	
	ps.println ("    private static final Vector<ASayfa> vector = new Vector<ASayfa>();");
	ps.println ("    private static int vecsize = 0;");
	ps.println ("    public static ASayfa[] SAYFALAR=new ASayfa[0];");
	ps.println ("    public static int SAYFALEN=0;");
	
	ps.println ("\n\tpublic AInfos() {\n\t\tsuper ();\n\t}");
    ps.println ("");
    
    ps.println ("public static final void addSayfa(ASayfa sf) {");
	ps.println ("	vector.add(sf);");
	ps.println ("}");
	ps.println ("");
	ps.println ("public static final void generate() {");
	ps.println ("	vecsize = vector.size();");
	ps.println ("	if (vecsize < 1) {");
	ps.println ("		//System.out.println(\"Insufficient pages error...\");");
	ps.println ("		return;");
	ps.println ("	}");
	ps.println ("	");
	ps.println ("	SAYFALAR = new ASayfa[vecsize];");
	ps.println ("	");
	ps.println ("	for (int i = 0; i < vecsize; i++) {");
	ps.println ("		SAYFALAR[i] = vector.get(i);");
	ps.println ("	}");
	ps.println ("	");
	ps.println ("	SAYFALEN = SAYFALAR.length;");
	ps.println ("   vector.removeAllElements();");
	ps.println ("}");
    ps.println ("");
	ps.println ("}//class end");
	////////
	  
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	 
	System.out.println ("Wrote: ../src/net/murat/"+PACKAGENAME+"/sayfas/AInfos.java");
	
	return;
  }
  
  final
  private static void genCeroSayfa ()
  throws IOException
  {
	File fd=new File ("../src/net/murat/"+PACKAGENAME+"/sayfas/ASayfa_00000.java");
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true);
	
	final String dts = (new java.util.Date()).toString();
	
	///////
	ps.println ("package net.murat."+PACKAGENAME+".sayfas;");
	ps.println ("");
	ps.println ("final");
	ps.println ("public class ASayfa_00000");
	ps.println ("\textends Object");
	ps.println ("\timplements java.io.Serializable, ASayfa");
	ps.println ("{");
	ps.println ("");
	//ps.println ("\tprivate String allSayfa=\"\"");
	ps.println ("\tprivate final String [][] lnns=");
	ps.println ("\t{");
	ps.println ("\t\t{\"HELP:\"},");
	ps.println ("\t\t{\"Exit: Touch  Left Bottom.\"},");
	ps.println ("\t\t{\"Page: Touch Right Bottom.\"},");
	ps.println ("\t\t{\"Set/Copy: Touch Left/Right Top.\"},");
	ps.println ("\t\t{\"[--: Touch  Left Top.\"},");
	ps.println ("\t\t{\"--]: Touch Right Top.\"},");
	ps.println ("\t\t{" + "\"" + dts + "\"" + "},");
	ps.println ("\t\t{\"../Download/carets/settings.txt\"}");
	ps.println ("\t};");
	ps.println ("");
	ps.println ("\tpublic ASayfa_00000 ()\n\t{\n\t\tsuper ();\n\t}");
	ps.println ("");
	ps.println ("\tpublic String toString ()");
	ps.println ("\t{");
	ps.println ("\t\treturn \"ASayfa_00000\";");
	ps.println ("\t}");	
	ps.println ("");
	ps.println ("\tpublic String [][] getLines ()");
	ps.println ("\t{");
	ps.println ("\t\treturn lnns;");
	ps.println ("\t}");
	ps.println ("");
	ps.println ("}//class end");
	////////
	  
	ps.flush ();
	ps.close ();
	fos.flush ();
	fos.close ();
	
	System.out.println ("Wrote: ../src/net/murat/"+PACKAGENAME+"/sayfas/Sayfa_00000.java");

	return;
  }
  
  final
  public static void main (final String [] args)
  {
    usage (args);
    
    try
    {
      init (args);
	  
	  (new File ("../src/net/murat/"+PACKAGENAME+"/sayfas")).mkdirs ();
	  
	  genASayfa ();
	  
      taksim ();
      sendMDK ();
	  
	  System.out.println ("");
	  
	  genAInfos ();
	  
	  genCeroSayfa ();
	  
	  ProduceAdding pa = new ProduceAdding(DNUM, DNUM/7);
	  pa.add();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace ();
      System.exit (-1);
    }
    catch (NumberFormatException bfe)
    {
      bfe.printStackTrace ();
      System.exit (-1);
    }
    
    System.exit (0);
  }
  
}
