import java.io.*;

final
public class GenerateSH
extends Object
implements Serializable
{
  
  private static String FNAME="test.txt";
  private static String CHARCODE="ISO-8859-9";
  private static String LCNT="5";
  private static String HCNT="642F";
  private static String FONTSTR="SansSerif,0,27";
  private static String ALIGN="no";
  private static String ORIENTATION="landscape";
  
  private GenerateSH ()
  {
    super ();
  }
  
  public String toString ()
  {
    return "";
  }
  
  final
  private static void init (String infoname)
  throws IOException
  {
    Reader fr=new FileReader (new File (infoname));
    BufferedReader br=new BufferedReader (fr);
    
    String line=br.readLine (); //1
    line=(line.split (";")[0]).trim ();
    FNAME=line;
    
    line=br.readLine (); //2
    line=(line.split (";")[0]).trim ();
    CHARCODE=line;
    
    line=br.readLine (); //3
    line=(line.split (";")[0]).trim ();
    LCNT=line;
    
    line=br.readLine (); //4
    line=(line.split (";")[0]).trim ();
    HCNT=line;
    
    line=br.readLine (); //5
    line=(line.split (";")[0]).trim ();
    FONTSTR=line;
    
    line=br.readLine (); //6
    line=(line.split (";")[0]).trim ();
    ALIGN=line;
    
    line=br.readLine (); //7
    line=(line.split (";")[0]).trim ();
    ORIENTATION=line;
    
    return;
  }
  
  final
  private static void generate (String infoDatName)
  throws IOException, NumberFormatException
  {
    OutputStream fos=new FileOutputStream (new File ("auto.bat"));
    PrintStream ps=new PrintStream (fos, true);
    
	ps.println ("SET PATH=%PATH%;C:\\Program Files\\Java\\jdk-1.8\\bin;C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0");
	ps.println ("");
    //ps.println ("echo %PATH%");
    //ps.println ("");
	ps.println ("java -version");
	ps.println ("");
	ps.println ("java -cp cls; del -s ..\\*.keystore* ..\\classes.dex ..\\bin\\*.signed.* ..\\bin\\*.unsigned.* ..\\bin\\EBook*.apk ..\\obj\\* ..\\src\\net\\murat\\ebook\\R.java");
	ps.println ("");
	
	ps.println ("java -cp cls; del -s ..\\obj\\net");
	ps.println ("");
	
	ps.println ("java -cp cls; copy -v ..\\src\\net\\murat\\ebook\\EBook.java origFolder\\src\\net\\murat\\ebook\\EBook.java");
	ps.println ("java -cp cls; copy -v ..\\src\\net\\murat\\ebook\\Values.java origFolder\\src\\net\\murat\\ebook\\Values.java");
	ps.println ("java -cp cls; copy -v ..\\src\\net\\murat\\ebook\\ScenePanel.java origFolder\\src\\net\\murat\\ebook\\ScenePanel.java");
	ps.println ("java -cp cls: copy -v ..\\src\\net\\murat\\ebook\\Adding.java origFolder\\src\\net\\murat\\ebook\\Adding.java");

	ps.println ("");
	
	BufferedReader inr=new BufferedReader (new InputStreamReader (System.in));
    System.out.print ("Enter new package name: ");
    String pn=(inr.readLine ()).trim ();
	
    ps.println ("java -cp cls; ToMDK "+FNAME+" "+FONTSTR+" "+CHARCODE+" "+LCNT+" "+HCNT+" "+ALIGN+" "+pn+"");
    ps.println ("");
    
    ps.println ("::java -cp cls; CountPages");
    ps.println ("");
    ps.println ("::java -cp cls; echo Total Page Count:");
    ps.println ("::java -cp cls; cat cnt.txt");
    
    if (ORIENTATION.equals ("landscape"))
    {
      changeManifest ("..\\AndroidManifest.xml", "portrait", "landscape");
    }
    else
    {
      changeManifest ("..\\AndroidManifest.xml", "landscape", "portrait");
    }
        
    ps.println ("");
    ps.println ("::java -cp cls; ChangeValues");
    ps.println ("");
    
    ps.println ("java -cp cls; del -s ..\\assets\\texts\\*");
    ps.println ("");
    
    ps.println ("java -cp cls; copy -v infos\\r00000.dat ..\\assets\\texts\\r00000.dat");
    
    ps.println ("java -cp cls; copy -v "+infoDatName+" ..\\assets\\texts\\info.txt");
    ps.println ("");
	
    ps.println ("::java -cp cls; move -v mdk ..\\assets\\texts");
    ps.println ("");
    
    changeProgramName ("..\\res\\values\\strings.xml", pn);
    
    ps.println ("java -cp cls; DesignPackage "+pn+"");
    
    ps.println ("java -cp cls; mkdir ..\\src\\net\\murat\\"+pn+"");
    ps.println ("java -cp cls; mkdir ..\\src\\net\\murat\\"+pn+"\\ebook");
    ps.println ("java -cp cls; move -v ..\\src\\net\\murat\\ebook ..\\src\\net\\murat\\"+pn+"\\ebook");
	ps.println ("java -cp cls; del -s ..\\src\\net\\murat\\ebook");
    ps.println ("");
    ps.println ("java -cp cls; LogoGen "+pn+"");
    ps.println ("");
	
	ps.println ("java -cp cls; del -s ..\\*.keystore* ..\\classes.dex ..\\bin\\*.signed.* ..\\bin\\*.unsigned.* ..\\obj\\* ..\\src\\net\\murat\\"+pn+"\\ebook\\R.java");
	ps.println ("");
	
	ps.println ("java -cp cls; del -s ..\\obj\\net");
	
	///////////////////////////////
    ps.println ("");
    ps.println ("cd ..");//ONEMLI
    ps.println ("");
	///////////////////////////////
	
	/////////////////////////
	//START OF CONTROL BAT
//	ps.println ("java -cp arac\\cls; echo Starting Control...");
//    ps.println ("aapt.exe package -v -f -m -S \"res\" -J \"src\" -M \"AndroidManifest.xml\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"");
//	ps.print ("javac -bootclasspath \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"; -sourcepath \"src\"; -cp \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\";\"obj\"; -g:none -proc:none -nowarn -O -Xlint:none ");
//	ps.println ("\"src\\net\\murat\\"+pn+"\\ebook\\EBook.java\""+" -d \"obj\"");
//    
//	ps.println ("");
//	
//	ps.println ("java -cp arac\\cls; del -s *.keystore* classes.dex bin\\*.signed.* bin\\*.unsigned.* obj\\* src\\net\\murat\\"+pn+"\\ebook\\R.java");
//	ps.println ("");
//	ps.println ("java -cp arac\\cls; del -s obj\\net");
//    ps.println ("");
//	ps.println ("java -cp arac\\cls; echo Ending Control...");
//	ps.println ("");
//	//END OF CONTROL BAT
	/////////////////////
	
	ps.println ("keytool -genkey -alias EBook.keystore -keyalg RSA -validity 100000 -dname \"CN=Murat inan, OU=Freelance, O=Traductor, S=Sivas, C=TR\" -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432");
	ps.println ("");
	ps.println ("keytool -importkeystore -srckeystore EBook.keystore -destkeystore EBook.keystore -deststoretype pkcs12");

	ps.println ("");
    ps.println ("aapt.exe package -v -f -m -S \"res\" -J \"src\" -M \"AndroidManifest.xml\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"");
	ps.println ("");
	ps.println ("javac -bootclasspath \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"; -sourcepath \"src\"; -cp \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\";\"obj\"; -g:none -proc:none -nowarn -O -Xlint:none "+"src\\net\\murat\\"+pn+"\\ebook\\EBook.java"+" -d \"obj\"");
    ps.println ("");
	ps.println ("dx.bat --dex --verbose --output=\"classes.dex\" \"obj\"");
	///////
	ps.flush (); ps.close ();
    fos.flush (); fos.close ();
    
	ps=null;
	fos=null;
	
	System.gc ();
	
	fos=new FileOutputStream (new File ("..\\auto2.bat"));
    ps=new PrintStream (fos, true);
	
	ps.println ("aapt.exe package -v -f -M \"AndroidManifest.xml\" -A \"assets\" -S \"res\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\" -F \"bin\\EBook.unsigned.apk\" \"bin\"");
	ps.println ("aapt.exe add \"bin\\EBook.unsigned.apk\" \"classes.dex\"");
	ps.println ("jarsigner -keystore EBook.keystore -storepass 5EmrE432 -keypass 5EmrE432 -signedjar \"bin\\EBook.signed.apk\" \"bin\\EBook.unsigned.apk\" EBook.keystore");
	ps.println ("zipalign.exe -v -f 4 \"bin\\EBook.signed.apk\" \"bin\\EBook.apk\"");
	ps.println ("");
	//ps.println (".\\make2app.bat");
    ps.println ("java -cp arac\\cls; del -s *.keystore*");
	ps.println ("");
	
	ps.println ("java -cp arac\\cls; del -s obj\\net");
	
    ps.println ("java -cp arac\\cls; del -s assets\\texts\\*.*");
	
	///////////////////////////////////
    ps.println ("");
    ps.println ("cd arac"); //DIKKAT
    ps.println ("");
	///////////////////////////////////
    ps.println ("java -cp cls; mkdir ..\\src\\net\\murat\\ebook");
    ps.println ("java -cp cls; move -v ..\\src\\net\\murat\\"+pn+"\\ebook ..\\src\\net\\murat\\ebook");
    
	ps.println ("java -cp cls; del -s ..\\src\\net\\murat\\"+pn+"\\ebook");
	ps.println ("");
    ps.println ("java -cp cls; del -s ..\\src\\net\\murat\\"+pn+"");
    ps.println ("");
    ps.println ("java -cp cls; ReDesignPackage "+pn+"");
    ps.println ("java -cp cls; echo Done.");
	
    ps.println ("java -cp cls: copy -v origFolder\\src\\net\\murat\\ebook\\Adding.java ..\\src\\net\\murat\\ebook\\Adding.java");

    ps.println ("");
    ps.println ("java -cp cls; CompareFiles");
    ps.println ("::java -cp cls; echo Page count:");
    ps.println ("::java -cp cls; cat cnt.txt");
    ps.println ("");
	ps.println ("java -cp cls; del -s ..\\bin\\*signed*.apk");
	ps.println ("java -cp cls; move -v ..\\bin\\Ebook.apk ..\\bin\\Ebook-"+pn+".apk");
    ps.println ("java -cp cls; dir ..\\bin");
    
    inr.close ();
    
    ps.flush (); ps.close ();
    fos.flush (); fos.close ();
    
    System.out.println ("\nNow run .\\auto.bat and ..\\auto2.bat");
    
    return;
  }//generate end
  
  //changeManifest ("../AndroidManifest.xml", "portrait", "landscape");
  final
  private static void changeManifest (String fileName, String src, String dst)
  throws IOException
  {
    StringBuffer sb=new StringBuffer ();
    
    BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
    
    String line=null;
    
    while ( (line=br.readLine ()) != null)
    {
      if (line.indexOf (src) < 0)
      {
        sb.append (line);
        sb.append ("\n");
        continue;
      }
      
      line=line.replaceAll (src, dst);
      
      sb.append (line);
      sb.append ("\n");
    }
    
    br.close ();
    
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    
    ps.print (sb.toString ());
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  //changeEBook ("..\\src\\net\\murat\\ebook\\EBook.java", false);
  final
  private static void changeEBook (String fileName, boolean isPortrait)
  throws IOException
  {//true:portrait; false:landscape
    StringBuffer sb=new StringBuffer ();
    
    BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
    
    String line=null;
    
    String src="ScenePanel panel=new ScenePanel";// (this, false);
    
    while ( (line=br.readLine ()) != null)
    {
      if (line.indexOf (src) < 0)
      {
        sb.append (line);
        sb.append ("\n");
        continue;
      }
      
      if (isPortrait)
      {
        line="\tScenePanel panel=new ScenePanel (this, true);";
      }
      else
      {
        line="\tScenePanel panel=new ScenePanel (this, false);";
      }
      
      sb.append (line);
      sb.append ("\n");
    }
    
    br.close ();
    
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    
    ps.print (sb.toString ());
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  //changeValues ("..\\src\\net\\murat\\ebook\\Values.java", sfnum);
  final
  private static void changeValues (String fileName, String pageCount)
  throws IOException
  {
    StringBuffer sb=new StringBuffer ();
    
    BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
    
    String line=null;
    
    //String src="ScenePanel panel=new ScenePanel";// (this, false);
    String src="final public static int MAX=";
    
    while ( (line=br.readLine ()) != null)
    {
      if (line.indexOf (src) < 0)
      {
        sb.append (line);
        sb.append ("\n");
        continue;
      }
      
      line="    final public static int MAX="+pageCount+";";
      
      sb.append (line);
      sb.append ("\n");
    }
    
    br.close ();
    
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    
    ps.print (sb.toString ());
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  //changeInfos ("infos\\info.dat", snum);
  final
  private static void changeInfos (String fileName, String snum)
  throws IOException
  {
    StringBuffer sb=new StringBuffer ();
    
    BufferedReader br=new BufferedReader (new FileReader (new File (fileName)));
    
    String line=null;
    
    //String src="ScenePanel panel=new ScenePanel";// (this, false);
    String src="Total Page Count";
    
    while ( (line=br.readLine ()) != null)
    {
      if (line.indexOf (src) < 0)
      {
        sb.append (line);
        sb.append ("\n");
        continue;
      }
      
      //139; Total Page Count (mdk\\r*.txt files count)
      line=""+snum+"; Total Page Count (mdk\\r*.txt files count)";
      
      sb.append (line);
      sb.append ("\n");
    }
    
    br.close ();
    
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    
    ps.print (sb.toString ());
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  //../res/values/strings.xml
  final
  private static void changeProgramName (String fileName, String appName)
  throws IOException
  {
    PrintStream ps=new PrintStream (new FileOutputStream (new File (fileName)));
    ps.println ("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
    ps.println ("<resources>");
    ps.println ("    <string name=\"program_name\">"+(appName.toUpperCase (java.util.Locale.ENGLISH))+"</string>");
    ps.println ("</resources>");
    
    ps.flush ();
    ps.close ();
    
    return;
  }
  
  final private static String getTotalPagesCount ()
  {
    String PAG="1";
    
    try
    {
      File fx=new File ("cnt.txt");
      Reader frx=new FileReader (fx);
      BufferedReader br=new BufferedReader (frx);
      String line=br.readLine ();
      PAG=line.trim ();
      br.close ();
      frx.close ();
    }
    catch (IOException ioe)
    {
      PAG="1";
    }
    catch (NumberFormatException nde)
    {
      PAG="1";
    }
    
    return PAG;
  }
  
  final
  public static void main (final String [] args)
  {
    if (args.length < 1)
    {
      System.out.println ("Usage:\n\tjava -cp cls: GenerateSH [<infoFile>]\n");
      System.out.println ("Example:\n\tjava -cp cls: GenerateSH info.dat");
      System.exit (-1);
    }
    
    try
    {
      init (args [0]);
      generate (args [0]);
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace ();
      System.exit (-1);
    }
    catch (NumberFormatException nfe)
    {
      nfe.printStackTrace ();
      System.exit (-1);
    }
    
    System.exit (0);
  }
  
}//class end
