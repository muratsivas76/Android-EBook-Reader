import java.io.File;
import java.io.Serializable;

final
public class dir 
extends Object
implements Serializable
{

	final
	private static String getFileSize (File f)
	{
		long flen=f.length ();
		
		final long BLON=1024;
		
		String s="";
		
		if (flen < BLON)
		{
			s=""+(Long.toString (flen))+" B";
		
			return s;
		}
		
		flen /= 1024L;
		
		s=""+(Long.toString (flen))+" KB";
		
		return s;
	}
	
	final
	public static void main (final String [] args)
	{
		if (args.length > 0x0000)
		{
			File f=new File (args [0x0000]);
			
			if ((f.exists ()) == false)
			{
				System.out.println (""+args [0x0000]+": does not exists.");
				System.exit (-1);
			}
			
			if (f.isFile ())
			{
				System.out.println ("");
				String fsz=getFileSize (f);
				System.out.println ("File: "+f.getName ()+"   ["+fsz+"]");
			}
			else
			{
				System.out.println ("");
				
				File [] files=f.listFiles ();
				java.util.Arrays.sort (files);
				final int len=files.length;
				
				File temp=null;
				
				for (int i=0; i<len; i++)
				{
					temp=files [i];
					if (temp.isFile ())
					{
						String fsz=getFileSize (temp);
						System.out.println ("File: "+temp.getName ()+"   ["+fsz+"]");
					}
					else
					{
						System.out.println ("Directory: "+temp.getName ()+"");
					}
				}
			}
		}//end if
		
		System.exit (0x0000);
	}//end main
	
}
