import java.io.*;

public final class WriteMaxPageCount
{
   
   private static final void write ()
   throws Exception
   {
      OutputStream fos = new FileOutputStream (new File ("rnd\\sf.utf"));
      OutputStream bos = new BufferedOutputStream (fos);
      DataOutputStream dos = new DataOutputStream (bos);
      
      File f = new File ("cnt.txt");
      Reader fr = new FileReader (f);
      BufferedReader br = new BufferedReader (fr);
      
      String cnt = br.readLine ();
      
      dos.writeUTF ("Sayfa:");
      dos.writeUTF (cnt);
      dos.writeUTF ("Belge");
      
      br.close ();  fr.close ();
      
      dos.flush (); dos.close ();
      bos.flush (); bos.close ();
      fos.flush (); fos.close ();
      
      return;
   }
   
   public final static void main (final String [] args)
   throws Exception
   {
      /*
      if (args.length < 1)
         {
      System.out.println ("Usage:\n\tjava -cp classes: WriteMaxPageCount [<page_nums>]");
      System.out.println ("Example:\n\tjava -cp classes: WriteMaxPageCount 954");
      System.exit (-1);
      }
       */
      write ();
      
      System.out.println ("rnd\\sf.utf");
      System.exit (0x0);
   }
   
}
