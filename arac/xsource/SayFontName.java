import java.awt.*;

import java.io.*;

public class SayFontName
{

    public static void main (String [] args)
    {
	if (args.length < 1)
	{
	    System.out.println ("Example: \n\tjava -cp cls; SayFontName ..\\fonts\\TTF\\comic.ttf");    
	    System.exit (-1);
	}
	
	try
	{
	    InputStream is=new FileInputStream (new File (args [0]));
	    Font f=Font.createFont (Font.PLAIN, is);
	    System.out.println (""+f.getFamily ()+"");
	    is.close ();
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	catch (FontFormatException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}
	
	System.exit (0);
    }
    
}
