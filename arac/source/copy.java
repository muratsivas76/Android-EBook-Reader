import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

final
public class copy
extends Object
implements Serializable
{

	private static boolean verbose=false;
	
	private static final int BVAL=8192;
	
	private static final boolean usage (final String [] args)
	{
		if (args.length < 3)
		{
			return false;
		}
		
		return true;
	}
	
	private static final void copy (final String sr, final String ds)
	throws IOException
	{
		File src=new File (sr);
		if ((src.exists ()) == false)
		{
			System.out.println (""+sr+" does not exist.");
			return;
		}
		
		if (src.isDirectory ())
		{
			System.out.println (""+sr+" is not a regular file.");
			return;
		}
		
		File dst=new File (ds);
		
		if ((dst.exists ()) && (dst.isDirectory ()))
		{
			System.out.println (""+dst+" is an existing folder.");
			return;
		}
		
		FileInputStream fis=new FileInputStream (src);
		FileOutputStream fos=new FileOutputStream (dst);
		
		FileChannel fci=fis.getChannel ();
		FileChannel fco=fos.getChannel ();
		
		ByteBuffer buffer=ByteBuffer.allocate (BVAL);
		
		final int NEGATIVE=-1;
		
		int read=-1;
		
		while ((read=fci.read (buffer)) != NEGATIVE)
		{
			buffer.flip ();
			fco.write (buffer);
			buffer.clear ();
		}
		
		fis.close ();
		
		fos.flush ();
		fos.close ();
		
		if (verbose)
		{
			System.out.println ("Copied: "+sr+" ---> "+ds+"");
		}
		
		return;
	}
	
	public static final void main (final String [] args)
	{
		boolean usg=usage (args);
		
		if (usg == false)
		{
			System.out.println ("Example-1:\n\tjava -cp cls; copy -v src.txt dst.txt");
			System.out.println ("Example-2:\n\tjava -cp cls; copy -s src.txt dst.txt");			
			System.exit (-1);
		}
		
		if (args [0x0000].equals ("-v"))
		{
			verbose=true;
		}
		
		String src=args [0x0001];
		String dst=args [0x0002];
		
		try
		{
			copy (src, dst);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
			System.exit (-1);
		}
		
		System.exit (0x0000);
	}
	
}
