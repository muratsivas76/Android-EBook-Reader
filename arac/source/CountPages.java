import java.io.*;

public final class CountPages
{
  
  private static final void write (int num)
  throws IOException
  {
    OutputStream fos = new FileOutputStream (new File ("cnt.txt"));
    PrintStream dos = new PrintStream (fos, true);
    
    dos.println (Integer.toString (num));
    
    dos.flush (); dos.close ();
    fos.flush (); fos.close ();
    
    return;
  }
  
  public final static void main (final String [] args)
  {
    File f=new File ("mdk");
    File [] files=f.listFiles ();
    
    int num=files.length;
    
    try
    {
      write (num);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace ();
      System.exit (-1);
    }
    
    System.out.println ("Wrote to: cnt.txt, "+num+" pages.");
    
    System.exit (0x0);
  }
  
}
