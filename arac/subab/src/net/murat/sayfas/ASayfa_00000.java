package net.murat.sayfas;

final
public class ASayfa_00000
	extends Object
	implements java.io.Serializable, ASayfa
{

	private final String [][] lnns=
	{
		{"HELP:"},
		{"Exit: Touch  Left Bottom."},
		{"Page: Touch Right Bottom."},
		{"Set/Copy: Touch Left/Right Top."},
		{"<--: Touch  Left Top."},
		{"-->: Touch Right Top."},
		{new java.util.Date().toString()},
		{"../Download/carets/settings.txt"}
	};

	public ASayfa_00000 ()
	{
		super ();
	}

	public String toString ()
	{
		return "ASayfa_00000";
	}

	public String [][] getLines ()
	{
		return lnns;
	}

}//class end
