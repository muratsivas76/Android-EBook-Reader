import java.io.*;

final
public class GenerateScripts
extends Object
implements Serializable
{

	private static String APPNAME="";
	
	private GenerateScripts ()
	{
		super ();
	}
	
	public String toString ()
	{
		return "Gen Scripts";
	}
	
	final
	private static String getAppName ()
	{
		char chr=APPNAME.charAt (0x0000);
		String fharf=Character.toString (chr);
		fharf=fharf.toUpperCase ();
		
		int alen=APPNAME.length ();
		
		StringBuffer sb=new StringBuffer ();
		
		sb.append (fharf);
		
		for (int i=1; i<alen; i++)
		{
			sb.append (APPNAME.charAt (i));
		}
		
		return sb.toString ();
	}
	
	//1.
	final
	private static void genManifest ()
	throws IOException
	{
		File fd=new File ("AndroidManifest.xml");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
			sb.append ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
			sb.append ("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n");
			sb.append ("\t\tpackage=\"net.murat."+APPNAME+"\"\n");
			sb.append ("\t\tandroid:versionCode=\"1\"\n");
			sb.append ("\t\tandroid:versionName=\"1.0\">\n");
			sb.append ("\n");
			sb.append ("\t<uses-permission android:name=\"android.permission.READ_EXTERNAL_STORAGE\" />\n");
			sb.append ("\t<uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\" />\n");
			sb.append ("\n");
			sb.append ("\t<uses-sdk android:minSdkVersion=\"29\" />\n");
			sb.append ("\n");
			sb.append ("\t<application android:icon=\"@drawable/"+APPNAME+"logo"+"\"\n");
			sb.append ("\t\t\t\tandroid:label=\"@string/program_name\"\n");
			sb.append ("\t\t\t\tandroid:requestLegacyExternalStorage=\"true\">\n");
			sb.append ("\n");
			sb.append ("\t\t<activity android:name=\"net.murat."+APPNAME+"."+(getAppName ())+"\"\n");
	        sb.append ("\t\t\t\t\tandroid:label=\"@string/program_name\"\n");
			sb.append ("\t\t\t\t\tandroid:screenOrientation=\"portrait\"> <!-- landscape | portrait with lowerCase-->\n");
			sb.append ("\n");
			sb.append ("\t\t\t<intent-filter>\n");
			sb.append ("\t\t\t\t<action android:name=\"android.intent.action.MAIN\" />\n");
			sb.append ("\t\t\t\t<category android:name=\"android.intent.category.LAUNCHER\" />\n");
			sb.append ("\t\t\t</intent-filter>\n");
			sb.append ("\n");
			sb.append ("\t\t</activity>\n");
			sb.append ("\n");
			sb.append ("\t</application>\n");
			sb.append ("\n");
			sb.append ("</manifest>");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("\nGenerated: AndroidManifest.xml");
		
		return;
	}
	
	//2.
	final
	private static void genMake1 ()
	throws IOException
	{
		File fd=new File ("make1app.bat");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
			sb.append ("del /f /s /q bin\\*.apk\n");
			sb.append ("\n");
			sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\aapt.exe\" package -v -f -m -S \"res\" -J \"src\" -M \"AndroidManifest.xml\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"\n");
			sb.append ("\n");
			sb.append ("\"C:\\Program Files\\Java\\jdk-1.8\\bin\\javac.exe\" -bootclasspath \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"; -sourcepath \"src\"; -cp \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\";\"obj\"; -g:none -proc:none -nowarn -O -Xlint:all \"src\\net\\murat\\"+APPNAME+"\\"+(getAppName ())+".java\" -d \"obj\"\n");
			sb.append ("\n");
			sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\dx.bat\" --dex --verbose --output=\"classes.dex\" \"obj\"");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("Generated: make1app.bat");
		
		return;
	}
	
	//3.
	final
	private static void genMake2 ()
	throws IOException
	{
		File fd=new File ("make2app.bat");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
			sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\aapt.exe\" package -v -f -M \"AndroidManifest.xml\" -A \"assets\" -S \"res\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\" -F \"bin"+"\\"+(getAppName ())+".unsigned.apk\" \"bin\"\n");
			sb.append ("\n");
			sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\aapt.exe\" add \"bin"+"\\"+(getAppName ())+".unsigned.apk\" \"classes.dex\"\n");
			sb.append ("\n");
			sb.append ("\"C:\\Program Files\\java\\jdk-1.8\\bin\\jarsigner.exe\" -keystore "+(getAppName ())+".keystore -storepass 5EmrE432 -keypass 5EmrE432 -signedjar \"bin"+"\\"+(getAppName ())+".signed.apk\" \"bin\\"+(getAppName ())+".unsigned.apk\" "+(getAppName ())+".keystore\n");
			sb.append ("\n");
			sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\zipalign.exe\" -v -f 4 \"bin"+"\\"+(getAppName ())+".signed.apk\" "+"\"bin"+"\\"+(getAppName ())+".apk\"\n");
			sb.append ("\n");
			sb.append ("del /f /s /q *.keystore.old\n");
			sb.append ("del /f /s /q classes.dex\n");
			sb.append ("del /f /s /q bin\\*.signed.*\n");
			sb.append ("del /f /s /q bin\\*.unsigned.*\n");
			sb.append ("del /f /s /q src\\net\\murat\\"+APPNAME+"\\R.java\n");
			sb.append ("del /f /s /q obj\\*\n");
			sb.append ("rmdir /s /q obj\\net\n");
			sb.append ("\n");
			sb.append ("dir bin");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("Generated: make2app.bat");
		
		return;
	}
	
	//4.
	final
	private static void genKStore ()
	throws IOException
	{
		File fd=new File ("kstore.bat");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
			sb.append ("del /f /s /q *.keystore*\n");
			sb.append ("del /f /s /q classes.dex\n");
			sb.append ("del /f /s /q bin\\*.signed.*\n");
			sb.append ("del /f /s /q bin\\*.unsigned.*\n");
			sb.append ("del /f /s /q bin\\*.apk\n");
			sb.append ("del /f /s /q src\\net\\murat\\"+APPNAME+"\\R.java\n");
			sb.append ("del /f /s /q obj\\*\n");
			sb.append ("rmdir /s /q obj\\net\n");
			sb.append ("\n");
			sb.append ("\"C:\\Program Files\\java\\jdk-1.8\\bin\\keytool.exe\" -genkey -alias "+(getAppName ())+".keystore -keyalg RSA -validity 100000 -dname \"CN=Murat inan, OU=Freelance, O=Traductor, S=Sivas, C=TR\" -keystore "+(getAppName ())+".keystore -storepass 5EmrE432 -keypass 5EmrE432\n");
			sb.append ("\n");
			sb.append ("\"C:\\Program Files\\java\\jdk-1.8\\bin\\keytool.exe\" -importkeystore -srckeystore "+(getAppName ())+".keystore -destkeystore "+(getAppName ())+".keystore -deststoretype pkcs12");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("Generated: kstore.bat");
		
		return;
	}
	
	//5.
	final
	private static void genClean ()
	throws IOException
	{
		File fd=new File ("clean.bat");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
			sb.append ("del /f /s /q *.keystore.old\n");
			sb.append ("del /f /s /q classes.dex\n");
			sb.append ("del /f /s /q bin\\*.signed.*\n");
			sb.append ("del /f /s /q bin\\*.unsigned.*\n");
			sb.append ("del /f /s /q obj\\*\n");
			sb.append ("del /f /s /q src\\net\\murat\\"+APPNAME+"\\R.java\n");
			sb.append ("rmdir /s /q obj\\net");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("Generated: clean.bat");
		
		return;
	}
	
	//6. -end
	final
	private static void genControl ()
	throws IOException
	{
		File fd=new File ("control.bat");
		//InputStream fis=new FileInputStream (fd);
		//Reader isr=new InputStreamReader (fis, "UTF-8");
		//BufferedReader br=new BufferedReader (isr);
		
		StringBuffer sb=new StringBuffer ();
		
		//String line=null;
		
		//while ( (line=br.readLine ()) != null )
		//{
		sb.append ("del /f /s /q *.keystore*\n");
		sb.append ("del /f /s /q classes.dex\n");
		sb.append ("del /f /s /q bin\\*.signed.*\n");
		sb.append ("del /f /s /q bin\\*.unsigned.*\n");
		sb.append ("del /f /s /q src\\net\\murat\\"+APPNAME+"\\R.java\n");
		sb.append ("rmdir /s /q obj\\net\n");
		sb.append ("\n");
		sb.append ("\"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\build-tools\\29.0.0\\aapt.exe\" package -v -f -m -S \"res\" -J \"src\" -M \"AndroidManifest.xml\" -I \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"\n");
		sb.append ("\n");
		sb.append ("\"C:\\Program Files\\Java\\jdk-1.8\\bin\\javac.exe\" -bootclasspath \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\"; -sourcepath \"src\"; -cp \"C:\\Users\\murat\\AppData\\Local\\Android\\Sdk\\platforms\\android-29\\android.jar\";\"obj\"; -g:none -proc:none -nowarn -O -Xlint:all \"src\\net\\murat\\"+APPNAME+"\\"+(getAppName ())+".java\" -d \"obj\"\n");
		sb.append ("\n");
		sb.append ("del /f /s /q *.keystore*\n");
		sb.append ("del /f /s /q classes.dex\n");
		sb.append ("del /f /s /q bin\\*.signed.*\n");
		sb.append ("del /f /s /q bin\\*.unsigned.*\n");
		sb.append ("del /f /s /q obj\\*\n");
		sb.append ("del /f /s /q src\\net\\murat\\"+APPNAME+"\\R.java\n");
		sb.append ("rmdir /s /q obj\\net");
		//}
		
		//br.close ();
		//isr.close ();
		//fis.close ();
		
		OutputStream fos=new FileOutputStream (fd);
		PrintStream ps=new PrintStream (fos, true, "UTF-8");
		
		ps.println (sb.toString ());
		
		ps.flush ();  ps.close ();
		fos.flush (); fos.close ();
		
		System.out.println ("Generated: control.bat");
		
		return;
	}
	
	final
	public static void main (final String [] args)
	{
		if (args.length < 1)
		{
			System.out.println ("\nUsage:\n\tjava GenerateScripts [<appName>]\n");
			System.out.println ("Example:\n\tjava GenerateScripts wzip\n");
			System.exit (-1);
		}
		
		APPNAME=args [0x0000];
		
		try
		{
			genManifest ();
			genMake1 ();
			genMake2 ();
			genKStore ();
			genClean ();
			genControl ();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace ();
			System.exit (-1);
		}
		
		System.exit (0x0000);
	}
	
}
