package net.murat.sayfas;

import java.util.Vector;

final
public class AInfos
	extends Object
	implements java.io.Serializable {

    private static final Vector<ASayfa> vector = new Vector<ASayfa>();
    private static int vecsize = 0;  
    public static ASayfa[] SAYFALAR=new ASayfa[0];
    public static int SAYFALEN=0;
    
	public AInfos() {
		super ();
	}

    public static final void addSayfa(ASayfa sf) {
		vector.add(sf);
	}
	
	public static final void generate() {
		vecsize = vector.size();
		if (vecsize < 1) {
			//System.out.println("Insufficient pages error...");
			return;
		}
		
		SAYFALAR = new ASayfa[vecsize];
		
		for (int i = 0; i < vecsize; i++) {
			SAYFALAR[i] = vector.get(i);
		}
		
		SAYFALEN = SAYFALAR.length;
		vector.removeAllElements();
	}
	
}//class end
