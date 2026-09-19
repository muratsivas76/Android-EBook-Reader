import java.io.*;

final
public class IrfaniBooksScript extends Object implements Serializable {

  private IrfaniBooksScript() {
	super();  
  }
  
  final
  private static void genScript() throws IOException {
	  File dir = new File("/home/muratsivas76/istasyon/pdf/html/irfan");
	  File[] files = dir.listFiles();
	  java.util.Arrays.sort(files);
	  
	  File temp = null;
	  final int len = files.length;
	  
	  final String suffix = ".txt";
	  
	  int syc = 0;
	  
	  File fd = new File("irfani"+(++syc)+".sh");
	  OutputStream fos = new FileOutputStream(fd);
	  PrintStream ps = new PrintStream(fos, true, "UTF-8");
	  
	  String name = "";
	  
	  for (int i = 0; i < len; i++) {
		  if ((i+1) % 25 == 0) {
			ps.flush();
			ps.close();
			fos.flush();
			fos.close();
			ps = null;
			fos = null;
			fd = new File("irfani"+(++syc)+".sh");
			fos = new FileOutputStream(fd);
			ps = new PrintStream(fos, true, "UTF-8");
		  }
		  
		  temp = files[i];
		  name = temp.getName();
		  if (name.endsWith(suffix) == false) continue;
		  
		  ps.println("echo " + "\"" + (i+1) + "/" + len + "\"");
		  ps.println("cp -fv " + temp.getAbsolutePath() + " test.txt");
		  ps.println("java -cp cls LinGenerateSH customInfo.dat");
		  ps.println("./linauto.sh");
		  ps.println("mv -fv ../bin/EBook-*.apk /home/muratsivas76/istasyon/irsal/");
		  ps.println("");
		  
		  System.out.println("" + (i+1) + "/" + len + ": " + name);
	  }
	  
	  ps.flush();
	  ps.close();
	  fos.flush();
	  fos.close();
	  
	  System.out.println("irfaniX.sh files are ready.");
	  
	  return;
  }
  	
  final
  public static void main(final String[] args) {
    try {
		genScript();
	} catch (IOException ioe) {
		ioe.printStackTrace();
		System.exit(-1);
	}	  
  }
  
}
