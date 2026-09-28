import java.io.*;

final
public class cat
extends Object
implements Serializable
{
	private final static void cat (final String filename)
	throws IOException
	{
		File f=new File (filename);
		
		if (f.isDirectory ())
		{
			System.out.println ("This is a folder, not a file...");
			System.exit (-1);
		}
		
		long len=f.length ();
		
		if (len > 1000000L)
		{
			System.out.println ("File is very large...");
			System.exit (-1);
		}
		
		Reader fr=new FileReader (f);
		BufferedReader br=new BufferedReader (fr);
		
		String line=null;
		
		while ( (line=br.readLine ()) != null)
		{
			System.out.println (line);
		}
		
		//System.out.println ("");
		
		br.close ();
		fr.close ();
		
		return;
	}
		
	public static final void main (final String [] args)
	{
		if (args.length < 1)
		{
			System.out.println ("Example:\n\tjava cat file.txt");
			System.exit (-1);
		}
		
		try
		{
			cat (args [0x0000]);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
		}
		
		System.exit (0x0000);
	}
	
}
