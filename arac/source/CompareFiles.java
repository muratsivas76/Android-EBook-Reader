import java.io.*;

final
public class CompareFiles extends Object implements Serializable {

    private static final File [] XFILES=new File [] {
	new File ("../src/net/murat/ebook/EBook.java"),
	new File ("../src/net/murat/ebook/ScenePanel.java"),
	new File ("../src/net/murat/ebook/Adding.java"),
	new File ("../AndroidManifest.xml"),
	new File ("../clean.sh"),
	new File ("../kstore.sh"),
	new File ("../control.sh"),
	new File ("../make.sh")
    };
    
    private static final File [] YFILES=new File [] {
	new File ("origFolder/src/net/murat/ebook/EBook.java"),
	new File ("origFolder/src/net/murat/ebook/ScenePanel.java"),
	new File ("origFolder/src/net/murat/ebook/Adding.java"),
	new File ("origFolder/AndroidManifest.xml"),
	new File ("origFolder/clean.sh"),
	new File ("origFolder/kstore.sh"),
	new File ("origFolder/control.sh"),
	new File ("origFolder/make.sh")
    };
    private static final int FLEN=XFILES.length;
    
    private CompareFiles () {
	super ();
    }
    
    public String toString () {
	return " ";
    }
    
    final
    private static void control (File src, File dst, String info) throws IOException {
	Reader yfr=new FileReader (src);
	BufferedReader ybr=new BufferedReader (yfr);
	
	Reader xfr=new FileReader (dst);
	BufferedReader xbr=new BufferedReader (xfr);
	
	String yline=null;
	String xline=null;
	
	boolean igualito=true;
	
	while ( (yline=ybr.readLine ()) != null &&
	        (xline=xbr.readLine ()) != null ) {
	    if (yline.equals (xline) == false) {
		igualito=false;
		break;
	    }
	}
	
	if (igualito) {
	    System.out.println ("SUCCESS: "+info+src.getName ()+"="+dst.getName ()+"");
	} else {
	    System.out.println ("ERROR: "+info+src.getName ()+"!="+dst.getName ()+"");
	}
	
	if (!igualito) {
	    System.out.println ("You can find original files in origFolder.");
	}
	
	try {
	    ybr.close ();
	    yfr.close ();
	    xbr.close ();
	    xfr.close ();
	} catch (IOException ioe) {}
	  finally {
	    ybr.close ();
	    yfr.close ();
	    xbr.close ();
	    xfr.close ();
	}
	
	return;
    }
    
    final
    public static void main (final String [] args) {	
	for (int i=0; i<FLEN; i++) {
	    try {
		control (YFILES [i], XFILES [i], ("("+(i+1)+"/"+(FLEN)+") ")); 
		} catch (IOException ioe) {
		ioe.printStackTrace ();
		continue;
	    }
	}
	
	System.exit (0);
    }
    
}
