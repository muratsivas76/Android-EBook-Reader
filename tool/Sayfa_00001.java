package net.murat.sayfas;

final
public class Sayfa_00001
	extends Object
	implements java.io.Serializable, ASayfa
{

	private final String [][] lnns=
	{
		// Test 1: Red color starts here but DOES NOT close at the end of this line
		{"<font color=\"red\">Bal rengi g\u00F6zler yeniden gurbet d\u00FCnyas\u0131na a\u00E7\u0131ld\u0131\u011F\u0131nda"},
		
		// Test 2: Text stays RED from previous line, and we add Bold style in the middle
		{"ye\u011Feninin topraktan gelen <b>cans\u0131z bedeni</b> Seyyide\'nin"},
		
		// Test 3: Red and Bold closes here, then Italic and Underline starts and bleeds into next line
		{"sinesindeydi. </font> Mazlum Rukayye bir <i><u>eliyle de babac\u0131\u011F\u0131n\u0131n ser-i"},
		
		// Test 4: Italic and Underline safely carries over and finally closes at the end
		{"saadetine sar\u0131lm\u0131\u015Ft\u0131 s\u0131ms\u0131k\u0131...</u></i>"},
		{""},
		{"Evet!. ."},
		{""},
	};

	public Sayfa_00001 ()
	{
		super ();
	}

	public String toString ()
	{
		return "Sayfa_00001";
	}

	public String [][] getLines ()
	{
		return lnns;
	}

}//class end
