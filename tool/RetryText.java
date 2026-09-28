import java.io.*;

// Change it according your package name
import net.murat.nizar.sayfas.*;

public class RetryText {

	private static final void write() throws IOException {
		File fd = new File("bookText.txt");
		OutputStream fos = new FileOutputStream (fd);
		PrintStream ps = new PrintStream(fos, true, "UTF-8");
		
		ASayfa s = null;
		String[][] lnns = null;
		int lnnslen = -1;
		int j = -1;
		final int MAX = AInfos.SAYFALEN - 1;
		
		for (int i = 1; i < AInfos.SAYFALEN; i++) {
			s = AInfos.SAYFALAR [i];
			lnns = s.getLines();
			lnnslen = lnns.length;
			
			j = -1;
			
			while (++j < lnnslen) {
				ps.println(lnns[j][0]);
			}
			
			System.out.println("Added: " + i + "/" + MAX);
		}
		
		ps.flush();
		ps.close();
		fos.flush();
		fos.close();
		
		System.out.println("");
		System.out.println("Wrote to bookText.txt");
		
		return;
	}
	
	public static void main(String[] args) {
		try {
			write();
			return;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
	
}
