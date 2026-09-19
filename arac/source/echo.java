final
public class echo 
extends Object
implements java.io.Serializable
{

	final
	public static void main (final String [] args)
	{
		if (args.length > 0x0000)
		{
			final int alen=args.length;
			StringBuffer sb=new StringBuffer ();
			
			for (int i=0; i<alen; i++)
			{
				sb.append (args [i]);
				sb.append (" ");
			}
			
			System.out.println (sb.toString ());
		}
		
		System.exit (0x0000);
	}//end main
	
}
