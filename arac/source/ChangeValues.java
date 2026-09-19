import java.io.*;

final
public class ChangeValues
extends Object
implements Serializable
{
  
  
  final
  public static void changeValues ()
  {
    String fileName="../src/net/murat/ebook/Values.java";
    String PAGECOUNT="1";
    try
    {
      File fx=new File ("cnt.txt");
      Reader frx=new FileReader (fx);
      BufferedReader br=new BufferedReader (frx);
      String line=br.readLine ();
      PAGECOUNT=line.trim ();
      br.close ();
      frx.close ();
    }
    catch (NumberFormatException nde)
    {
      PAGECOUNT="1";
    }
    catch (IOException ioe)
      {}
    
    String sfnum=PAGECOUNT;
    
    StringBuffer sb=new StringBuffer ();
    
    try
    {
      BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
      
      String line=null;
      
      //String src="ScenePanel panel=new ScenePanel";// (this, false);
      String src="final public static int MAX=";
      
      while ( (line=br.readLine ()) != null)
      {
        if (line.indexOf (src) < 0)
        {
          sb.append (line);
          sb.append ("\n");
          continue;
        }
        
        line="  final public static int MAX="+sfnum+";";
        
        sb.append (line);
        sb.append ("\n");
      }
      
      br.close ();
      
      PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
      
      ps.print (sb.toString ());
      
      ps.flush ();
      ps.close ();
    }
    catch (IOException ioe)
      {}
    
	System.out.println ("Changed Values.java according "+PAGECOUNT+" value."); 
    return;
  }
  
  //changeInfos ("infos/info.dat", snum);
  final
  private static void changeInfos (String snum)
  throws IOException
  {
    String fileName="infos/info.dat";
    StringBuffer sb=new StringBuffer ();
    
    BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
    
    String line=null;
    
    //String src="ScenePanel panel=new ScenePanel";// (this, false);
    String src="Total Page Count";
    
    while ( (line=br.readLine ()) != null)
    {
      if (line.indexOf (src) < 0)
      {
        sb.append (line);
        sb.append ("\n");
        continue;
      }
      
      //139; Total Page Count (mdk/r*.txt files count)
      line=""+snum+"; Total Page Count (mdk/r*.txt files count)";
      
      sb.append (line);
      sb.append ("\n");
    }
    
    br.close ();
    
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    
    ps.print (sb.toString ());
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  final private static String getTotalPagesCount ()
  {
    String PAG="1";
    
    try
    {
      File fx=new File ("cnt.txt");
      Reader frx=new FileReader (fx);
      BufferedReader br=new BufferedReader (frx);
      String line=br.readLine ();
      PAG=line.trim ();
      br.close ();
      frx.close ();
    }
    catch (IOException ioe)
    {
      PAG="1";
    }
    catch (NumberFormatException nde)
    {
      PAG="1";
    }
    
    return PAG;
  }
  
  final
  public static void main (String [] args)
  {
    changeValues ();
    System.exit (0);
  }
  
}
