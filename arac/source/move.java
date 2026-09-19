import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

final
public class move
extends Object
implements Serializable
{

	private static boolean verbose=false;
	
	private static final int BVAL=8192;
	private static final int NEGATIVE=-1;
	
	private static final boolean usage (final String [] args)
	{
		if (args.length < 3)
		{
			return false;
		}
		
		return true;
	}
	
	private static final void move (final String sr, final String ds)
	throws IOException
	{
		File src=new File (sr);
		if ((src.exists ()) == false)
		{
			System.out.println (""+sr+" does not exist.");
			return;
		}
		
		File dst=new File (ds);
		
		final String srcname=src.getAbsolutePath ();
		final String dstname=dst.getAbsolutePath ();
		
		if ((src.isDirectory ()) && ((dst.exists ()) && (dst.isDirectory ())))
		{
			File [] files=src.listFiles ();
			java.util.Arrays.sort (files);
			
			final int len=files.length;
			
			File temp=null;
			File kf=null;
			
			for (int i=0; i<len; i++)
			{
				temp=files [i];
				kf=new File (dstname+(File.separator)+(temp.getName ()));
				copy (temp, kf);
			}
		}
		else if ((src.isFile ()) && ((dst.exists ()) && (dst.isDirectory ())))
		{
			File kf=new File (dstname+(File.separator)+(src.getName ()));
			copy (src, kf);
		}
		else if ((src.isDirectory ()) && ((dst.exists ()) && (dst.isFile ())))
		{
			System.out.println ("Directory can not copied to an existing file.");
			return;
		}
		else
		{
			copy (src, dst); //file file
		}
		
	}
	
	private static final void copy (final File src, final File dst)
	throws IOException
	{
		FileInputStream fis=new FileInputStream (src);
		FileOutputStream fos=new FileOutputStream (dst);
		
		FileChannel fci=fis.getChannel ();
		FileChannel fco=fos.getChannel ();
		
		ByteBuffer buffer=ByteBuffer.allocate (BVAL);
		
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
			System.out.println ("Moved: "+(src.getName ())+" ---> "+(dst.getName ())+"");
		}
		
		src.delete ();
		
		return;
	}
	
	public static final void main (final String [] args)
	{
		boolean usg=usage (args);
		
		if (usg == false)
		{
			System.out.println ("Example-1:\n\tjava -cp cls; move -v src.txt dst.txt");
			System.out.println ("Example-2:\n\tjava -cp cls; move -s src.txt dst.txt");
			System.out.println ("Example-1:\n\tjava -cp cls; move -v srcdir dstdir");
			System.out.println ("Example-2:\n\tjava -cp cls; move -s srcdir dstdir");
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
			move (src, dst);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
			System.exit (-1);
		}
		
		System.exit (0x0000);
	}
	
}
