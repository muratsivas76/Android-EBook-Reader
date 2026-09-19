package net.murat.sayfas;

final
public class AInfos
	extends Object
	implements java.io.Serializable
{

	public AInfos ()
	{
		super ();
	}

	public static final ASayfa [] SAYFALAR=new ASayfa []
	{
		new ASayfa_00000 (),
		new Sayfa_00001 (), new Sayfa_00002 (), new Sayfa_00003 (), 
		new Sayfa_00004 (), new Sayfa_00005 ()
	};

	public static final int SAYFALEN=SAYFALAR.length;

}//class end
