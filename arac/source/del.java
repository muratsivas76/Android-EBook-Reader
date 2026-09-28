import java.io.*;
import java.util.*;

final
public class del
extends Object
implements Serializable
{

	private static boolean verbose=false;
	
	private static final Vector <File> files=new Vector <File> ();
	private static final Vector <File> filesr=new Vector <File> ();
	
	private static final void collect (String src)
	{
		File f=new File (src);
		
		if ( (f.exists ()) == false) return;
		
		if (f.isFile ())
		{
			files.add (f);
		}
		else
		{
			File [] fs=f.listFiles ();
			final int len=fs.length;
			
			File temp=null;
			
			for (int i=0; i<len; i++)
			{
				temp=fs[i];
				
				if (temp.isFile ())
				{
					files.add (temp);
				}
				else
				{
					collect (temp.getAbsolutePath ());
					filesr.add (f);
				}
			}//for end
		}//else end
		
		return;
	}
	
	private static final void del (File f)
	{
		if ((f.exists ()) == false) return;
		
		f.delete ();
		
		if (verbose)
		{
			System.out.println ("Deleted: "+f.getName ()+"");
		}
		
		return;
	}
	
	private static final void delAll ()
	{
		final int len=files.size ();
		
		if (len < 1) return;
		
		File temp=null;
		
		for (int i=0; i<len; i++)
		{
			temp=files.get (i);
			del (temp);
		}
	}
	
	private static final void delAllR ()
	{
		final int len=filesr.size ();
		
		if (len < 1) return;
		
		File temp=null;
		
		for (int i=0; i<len; i++)
		{
			temp=filesr.get (i);
			
			if (temp.isDirectory ())
			{
				File [] fiz=temp.listFiles ();
				int lz=fiz.length;
			
				if (lz > 0x0000)
				{
					for (int j=0; j<lz; j++)
					{
						fiz [j].delete ();
						if (verbose)
						{
							System.out.println (fiz[j].getName ());
						}
					}
				}
			}
			
			del (temp);
		}
	}
	
	private static final boolean usage (final String [] args)
	{
		if (args.length < 2)
		{
			return false;
		}
		
		return true;
	}
	
	public static final void main (final String [] args)
	{
		boolean useg=usage (args);
		
		if (useg == false)
		{
			System.out.println ("Example-1:\n\tjava -cp cls; del -v file\n");
			System.out.println ("Example-2:\n\tjava -cp cls; del -s files\n");
			System.exit (-1);
		}
		
		if (args [0x0000].equals ("-v"))
		{
			verbose=true;
		}
		
		int alen=args.length;
		
		Vector <String> vs=new Vector <String> ();
		
		for (int i=1; i<alen; i++)
		{
			vs.add (args [i]);
		}
		
		alen=vs.size ();
		
		int j=-1;
		
		while ( (++j) < alen)
		{
			collect (vs.get (j));
		}
		
		delAll ();
		delAllR ();
		
		System.exit (0x0000);
	}
	
}
