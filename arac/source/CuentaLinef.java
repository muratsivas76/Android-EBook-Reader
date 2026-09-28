import java.awt.*;

import java.awt.image.BufferedImage;

final
public class CuentaLinef
extends Object
implements java.io.Serializable
{
  private static final BufferedImage bimg=new BufferedImage (1, 1, 1);
  private static Graphics2D g2d=bimg.createGraphics ();
  
  private CuentaLinef ()
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
  private static void hesapla (final String [] args)
  throws NumberFormatException
  {
    if (args.length < 2)
    {
      System.out.println ("Syntax: java -cp cls; CuentaLinef font line");
      System.out.println ("Example: java -cp cls; CuentaLinef SansSerif,0,27 Merhaba Dostlar");
      System.exit (-1);
    }
    
    String fstr=args [0];
    String spl []=fstr.split (",");
    
    Font FONT=new Font (spl [0],
      Integer.parseInt (spl [1]),
    Integer.parseInt (spl [2]));
    
    g2d.setFont (FONT);
    FontMetrics FM=g2d.getFontMetrics ();
    
    StringBuffer sb=new StringBuffer ();
    
    int alen=args.length;
    
    for (int i=1; i<alen; i++)
    {
      sb.append (args [i]);
      sb.append (" ");
    }
    
    String line=sb.toString ();
    line=line.trim ();
    
    int EBAT=FM.stringWidth (line);
    
    System.out.println (FONT.toString ());
    System.out.println ("");
    System.out.println (line+"\n");
    System.out.println ("Line FM stringWidth: "+EBAT+"");
    System.out.println ("");
    
    return;
  }
  
  final
  public static void main (final String [] args)
  {
    try
    {
      hesapla (args);
    }
    catch (NumberFormatException bfe)
    {
      bfe.printStackTrace ();
      System.exit (-1);
    }
    
    System.exit (0);
  }
  
}
