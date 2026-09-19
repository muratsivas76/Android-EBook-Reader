import java.io.FileWriter;
import java.io.PrintWriter;

public class ProduceAdding {
	private int toplamSayfa = 0;
	private int grupBoyutu = 0;
	private int syc = 0;
	
	public ProduceAdding(int toplamSayfa, int grupBoyutu) {
	    super();
	    
	    this.toplamSayfa = toplamSayfa;
	    this.grupBoyutu = grupBoyutu;	
	}
	
    public void add() {
		String dosyaAdi = "../src/net/murat/ebook/Adding.java";

        try (PrintWriter out = new PrintWriter(new FileWriter(dosyaAdi))) {
            out.println("package net.murat.ebook;");
            out.println();
            out.println("import net.murat.sayfas.*;");
            out.println();
            out.println("public class Adding {");
            out.println();
            
            // Constructor
            out.println("    public Adding() {\n        super();");
            //for (int i = 0; i < Math.ceil((double) toplamSayfa / grupBoyutu); i++) {
            //    int bas = (i * grupBoyutu) + 1;
             //   out.println("        if (sayfaSayisi >= " + bas + ") grup" + (i + 1) + "();");
            //}
            out.println("    }");
            out.println();

            // Gruplar
            for (int i = 0; i < Math.ceil((double) toplamSayfa / grupBoyutu); i++) {
                out.println("    private void grup" + (i + 1) + "() {");
                ++syc;
                for (int j = 1; j <= grupBoyutu; j++) {
                    int sayfaNo = (i * grupBoyutu) + j;
                    if (sayfaNo > toplamSayfa) break;
                    
                    // Sayfa_00001 formatinda uretim
                    String sayfaAdi = String.format("Sayfa_%05d", sayfaNo);
                    out.println("        AInfos.addSayfa(new " + sayfaAdi + "());");
                    //if ((j%2) == 0) out.print("\n");
                }
                out.println("\n    }");
                out.println();
            }
            
            out.println("    public void addAll() {");
            for (int i = 0; i < syc; i++) {
				out.println("        grup" + (i+1) + "();");
			}
			out.println("    }\n");
			
            out.println("}");
            
            
            System.out.println("\n" + dosyaAdi + " created succesfully!\n");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
		int t = 1000;
		int g = 250;
		
		if (args.length >= 2) {
			try {
				t = Integer.parseInt(args[0]);
			} catch (NumberFormatException nfe) {
				t = 1000;
			}
			
			try {
				g = Integer.parseInt(args[1]);
			} catch (NumberFormatException nfe) {
				g = 250;
			}
		}
		
		ProduceAdding pra = new ProduceAdding(t, g);
		pra.add();
		
		return;
	}
	
}
