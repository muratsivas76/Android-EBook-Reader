import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import java.awt.font.*;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import java.util.HashSet;
import java.util.Iterator;

public class Aligned
extends Object
{
  
  private float LINEF = 400F;
  private int MAX = (((int)(LINEF))/10)+(((int)(LINEF))/40);
  private int ORIGMAX=MAX;
  
  private final String SPACE= " ";
  private float SPACELEN=5F;
  
  private String mstr="abc";
  
  private String CHARSET="ISO-8859-9";
  
  private final HashSet <String> set=new HashSet <String> ();
  private Iterator <String> iter=null;
  
  private int TAGCOUNT=0;
  
  public Aligned (String chrs)
  {
    super ();
    
    this.CHARSET=chrs;
    
    StringBuffer m=new StringBuffer ();
    for (int i=0; i<MAX; i++)
    {
      m.append ("a");
    }
    mstr=m.toString ();
  }
  
  final public void setLINEF (float yf)
  {
    this.LINEF=yf;
    this.MAX = (((int)(yf))/10)+(((int)(yf))/40);
    ORIGMAX=MAX;
  }
  
  final private String align (String old)
  {
    old=old.trim ();
    old=old.replaceAll ("  ", " ");
    
    int old_length = old.length ();
    if (old_length < 1) return old;
    
    String [] split = old.split (" ");
    if (split==null) return old;
    
    int splen = split.length;
    if (splen < 2) return old;
    
    String pureStr=getPureString (old);
    
    old_length=pureStr.length ();
    if (old_length < 1) return old;
    if (old_length >= MAX) return old;
    
    int diff = MAX-old_length;//original
    
    int space_count=0;
    
    char chr='*';
    
    for (int i=0; i<old_length; i++)
    {
      chr = pureStr.charAt (i);
      //chr=old.charAt (i);
      if (Character.isWhitespace (chr))
      {
        ++space_count;
      }
    }
    
    int len = diff/space_count;
    StringBuffer sb = new StringBuffer (" ");
    
    if (len>=1)
    {
      for (int i=0; i<len; i++)
      {
        sb.append (" ");
      }
    }
    
    String ek = sb.toString ();
    
    StringBuffer sbx=new StringBuffer ();
    
    //    for (int i=0; i<(splen-1); i++)
    for (int i=0; i<(splen); i++)
    {
      sbx.append (split [i]);
      //sbx.append (SPACE);
      sbx.append (ek);
    }
    
    //    sbx.append (split [splen-1]);
    
    String nw = sbx.toString ();
    
    int nwlen = nw.length ();
    nwlen-=TAGCOUNT;
    
    diff = (MAX)-(nwlen);
    
    if (diff < 1) return (nw);
    
    sb = new StringBuffer ();
    
    for (int i=0; i<=diff; i++)
    {
      sb.append (" ");
    }
    
    ek = sb.toString ();
    
    String sef=nw.trim ();//nw.replaceFirst (" ", ek);
    
    return sef;
  }
  
  final public String getPureString (String str)
  {
    int aindex=-1;
    int bindex=-1;
    
    String STRTAG="<";
    String ENDTAG=">";
    
    int dif=-1;
    int start=0;
    
    String mline=str;
    
    set.clear ();//reset set
    
    boolean found=false;
    TAGCOUNT=0;
    
    while ( (aindex=str.indexOf (STRTAG, start)) >= 0)
    {
      bindex=str.indexOf (ENDTAG, start+1);
      if (bindex < 0) continue;
      
      set.add (str.substring (aindex, bindex+1));
      TAGCOUNT += ((bindex+1)-aindex);
      
      start=bindex+1;
      
      found=true;
    }
    
    if (found)
    {
      iter=set.iterator ();
      
      String next="";
      while (iter.hasNext ())
      {
        next=iter.next ();
        mline=mline.replaceAll (next, "");
      }
      
    }
    
    return (mline);
  }
  
  public static void main(String[] args)
  throws Exception
  {
    if (args.length < 2)
    {
      System.out.println ("Example:\n\tjava -cp cls; Aligned ISO-8859-9 400F");
      System.exit (-1);
    }
    
    File [] files=(new File ("mdk")).listFiles ();
    int len=files.length;
    if (len < 1) System.exit (0);
    
    java.util.Arrays.sort (files);
    
    (new File ("cdk")).mkdir ();
    
    String code=args [0];
    float LENF=Float.parseFloat (args [1]);
    
    Aligned xreader=new Aligned (code);
    xreader.setLINEF (LENF);
    
    //    System.out.println ("XREADER MAX: "+xreader.MAX+""); //DEBUG
    
    for (int i=0; i<len; i++)
    {
      File f=files [i];
      InputStream fis=new FileInputStream (f);
      Reader fr=new InputStreamReader (fis, code);
      BufferedReader br=new BufferedReader (fr);
      
      OutputStream fos=new FileOutputStream (new File ("cdk\\"+f.getName ()+""));
      PrintStream ps=new PrintStream (fos, true, xreader.CHARSET);
      
      String line=null;
      
      while ( (line=br.readLine ()) != null)
      {
        line=line.trim ();
        
        line=line.replaceAll (" color=\"", "_color=\"");
        line=line.replaceAll (" face=\"", "_face=\"");
        line=line.replaceAll (" size=\"", "_size=\"");
        
        line=xreader.align (line);
        
        line=line.replaceAll ("_color=\"", " color=\"");
        line=line.replaceAll ("_face=\"", " face=\"");
        line=line.replaceAll ("_size=\"", " size=\"");
        
        //        line=line.replaceAll (xreader.SPACE, "^");
        
        ps.println (line.trim ());
      }
      
      br.close ();
      fr.close ();
      fis.close ();
      
      ps.flush ();  ps.close ();
      fos.flush (); fos.close ();
    }
    
  }
}
