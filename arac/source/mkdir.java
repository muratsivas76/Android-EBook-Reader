import java.io.File;
import java.io.Serializable;

final
public class mkdir 
extends Object
implements Serializable
{

	final
	public static void main (final String [] args)
	{
		if (args.length > 0x0000)
		{
			File f=new File (args [0x0000]);
			f.mkdir ();
			
			System.out.println ("Created directory: "+f.getName ()+"");
		}
		
		System.exit (0x0000);
	}//end main
	
}
