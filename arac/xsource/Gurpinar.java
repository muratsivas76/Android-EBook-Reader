import java.io.*;

public class Gurpinar {

	private static final void write(String src, String dst) 
	throws IOException {
		File fd = new File(src);
		InputStream fis = new FileInputStream(fd);
		Reader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		
		java.util.Vector<String> vec = new java.util.Vector<String>();
		
		String line = null;
		int index = -1;
		final String NKT = ". ";
		String words = "";
		
		while ( (line = br.readLine()) != null) {
			line = line.trim ();
			index = line.indexOf(NKT);
			if (index < 0) continue;
			words = line.substring(index+1);
			words = words.trim();
			vec.add(words);
		}
		
		br.close();
		isr.close();
		fis.close();
		
		final int vsize = vec.size();
		String[] kelimat = new String[vsize];
		
		int mindex = -1;
		String [] split = null;
		
		String word = "";
		String mword = "";
		String abc = "";
		String def = "";
		final String PRE = "<font color=\"#FF0000\" size=\"5\">";
		final String IKINOKTA = ":";
		final String SPACE = " ";
		final String CLOSE = "</font>";
		final String SUFF = "<font color=\"#0000FF\" size=\"5\">";
		final String BR = "<br>";
		
		for (int i = 0; i < vsize; i++) {
			word = vec.get(i);
			mindex = word.indexOf(IKINOKTA);
			if (mindex < 0) {
				kelimat[i] = word;
				continue;
			}
			
			split = word.split(IKINOKTA);
			if (split.length < 2) {
			    kelimat[i] = word;
				continue;
			}
			
			abc = split[0];
			def = split[1];
			
			mword=PRE+abc+CLOSE+IKINOKTA+SPACE+SUFF+def+CLOSE+BR;
			
			kelimat[i] = mword;
		}
		
		java.util.Arrays.sort(kelimat, java.text.Collator.getInstance(new java.util.Locale("tr", "TR")));
		
		File fdx = new File(dst);
		OutputStream fos = new FileOutputStream(fdx);
		PrintStream ps = new PrintStream(fos, true, "UTF-8");
		
		ps.println("<html><body>");
		
		int k = -1;
		
		while ((++k) < vsize) {
			ps.println(kelimat[k]);
		}
		
		ps.print ("\n-SON-<br>\n\n</body></html>\n");
		
		ps.flush();
		ps.close();
		fos.flush();
		fos.close();
		
		System.out.println(src + " --> " + dst + " wrote successfully.");
		
		return;
	}
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("\n\tExample: java -cp cls Gurpinar src.txt dst.html");
			System.exit(-1);
		}
		
		try {
			write(args[0], args[1]);
			return;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
	
}
