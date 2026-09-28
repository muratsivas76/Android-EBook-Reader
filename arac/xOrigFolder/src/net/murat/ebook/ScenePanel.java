package net.murat.ebook;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

import android.os.Environment;

//import android.util.DisplayMetrics;

import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

//Nested
import net.murat.sayfas.*;

public class ScenePanel
extends SurfaceView
implements SurfaceHolder.Callback
{

  private Context context;

  private Bitmap bufferBimg=null;
  private final Canvas bufferG2D=new Canvas ();

  private int width=Values.UNO;
  private int NISF=Values.UNO;
  private int SETX=Values.UNO;
  private int SETY=Values.UNO;
  private int CPX=Values.UNO;
  private int CPY=Values.UNO;
  private int height=Values.UNO;

  float fwidth=(float)(width);
  float fheight=(float)(height);

  float FNISF=Values.UNOF;

  float AXISX=Values.VEINTEF;
  float AXISY=Values.VEINTEF;

  private SurfaceHolder holder=null;

  private Activity activity=null;

  private AlertDialog pane=null;
  private AlertDialog errorPane=null;
  private AlertDialog setPane=null;
  private AlertDialog cpPane=null;
  private AlertDialog successPane = null;

  private int PGSYC=Values.CERO;

  private int AX=Values.UNO;//exit
  private int AY=Values.UNO;//exit
  private int BX=Values.UNO;//pagenum
  private int BY=Values.UNO;//exit

  //private InputStream IS=null;

  private int BG=0xFFFFFF;
  private String CHARSET="ISO-8859-9";
  private String SLINE="";

  private final Paint fg2d=new Paint ();
  private final Paint zg2d=new Paint ();
  private final Paint g2d=new Paint ();
  private final Paint BORDERPAINT=new Paint ();

  private float XCOORD=Values.ONCEF;
  private float ORIGXCOORD=XCOORD;
  private float YCOORD=Values.CUARF;
  private float ORIGYCOORD=YCOORD;
  private float ZCOORD=Values.UNOF;
  private float SPACE=Values.OBEF;
  private float swc=Values.CEROF;

  private RectF rect=new RectF (Values.UNOF, Values.UNOF, Values.UNOF, Values.UNOF);

  private String caretFileName="";

  private boolean isCentered=false;

  private boolean isRound=true;
  private boolean portrait=false;

  private String [][] SINIFLAR=null;
  private String [] SINIF=null;

  private int SINIFLARLEN=Values.CERO;
  private int SINIFLEN=Values.CERO;

  private boolean errorOcurred=false;

  private String PATH = "";
  private String packageName = "";
  private String errorMessage="";

  public ScenePanel (Context contxt)
  {
    super (contxt);

    this.context=contxt;

    activity=(Activity)context;

    Display dd=(activity.getWindowManager ()).getDefaultDisplay ();

    Point sizep=new Point ();
    dd.getSize (sizep);

    width=sizep.x;
    height=sizep.y;

    fwidth=(float)(width);
    fheight=(float)(height);

    NISF=(width/Values.DOS);
    FNISF=fwidth/Values.DOSF;

    SETX=(NISF/Values.DOS);
    SETY=(height/Values.CINCO);

    CPX=width-(width/Values.CUATRO);
    CPY=height/Values.DIEZ;

    bufferBimg = Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);
    bufferG2D.setBitmap (bufferBimg);

    BORDERPAINT.setStyle (Paint.Style.STROKE);

    packageName=context.getPackageName (); //net.murat.ebook
    packageName=packageName.replaceAll ("net\\.murat\\.", ""); //abc.ebook
    packageName=packageName.replaceAll ("\\.ebook", ""); //abc

    PATH=getMainPathName ();

    File ff=new File (PATH+"/carets");
    File ff2=new File (PATH+"/carets/contents");

    try
    {
      if (ff.exists () == false)
      {
        ff.mkdir ();
      }

      if (ff2.exists () == false)
      {
        ff2.mkdir ();
      }
    }
    catch (SecurityException se)
    {
      errorOcurred=true;
      errorMessage=(se.getMessage ());
    }

    caretFileName=(PATH+"/carets/"+packageName+".txt");
    ff=new File (caretFileName);

    if (ff.exists ())
    {
      try
      {
        BufferedReader br=new BufferedReader (new FileReader (ff));
        String line=(br.readLine ()).trim ();

        try
        {
          PGSYC=Integer.parseInt (line);
        }
        catch (NumberFormatException nfe)
        {
          PGSYC=0x0000;
        }

        br.close ();
      }
      catch (IOException ioe)
      {
        errorOcurred=true;
        errorMessage=(ioe.getMessage ());
      }
      catch (SecurityException se)
      {
        errorOcurred=true;
        errorMessage=(se.getMessage ());
      }
    }
    else
    {
      try
      {
        PrintStream ps=new PrintStream (new FileOutputStream (ff), true);
        ps.println ("0");
        ps.flush ();
        ps.close ();
      }
      catch (IOException ioe)
      {
        errorOcurred=true;
        errorMessage=(ioe.getMessage ());
      }
      catch (SecurityException se)
      {
        errorOcurred=true;
        errorMessage=(se.getMessage ());
      }
    }

    //////////////////////
    AlertDialog.Builder alert=new AlertDialog.Builder (context);
    alert=alert.setTitle (Values.ENTERNUM);

    final EditText htime=new EditText (context);
    htime.setInputType (android.text.InputType.TYPE_CLASS_NUMBER);

    alert.setView (htime);

    alert.setPositiveButton (Values.OK, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          String res=(htime.getText ().toString ()).trim ();

          if (res.length () < Values.UNO) return;

          int c=Values.UNO;

          try
          {
            c=Integer.parseInt (res);
            if (c<Values.CERO) c=Values.CERO;
            if (c>Values.MAX) c=Values.MAX;
            PGSYC=c;
          }
          catch (NumberFormatException e)
          {
            return;
          }

          htime.setText ("");
          setBitmapMe ();
          drawx ();

          return;
        }
    });

    alert.setNegativeButton (Values.CANCEL, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          htime.setText ("");
          return;
        }
    });

    pane = alert.create();
    ///////////////////
    AlertDialog.Builder cpalert=new AlertDialog.Builder (context);
    cpalert=cpalert.setTitle (Values.CPNUM);

    final EditText cphtime=new EditText (context);
    //htime.setInputType (android.text.InputType.TYPE_CLASS_NUMBER);

    cpalert.setView (cphtime);

	final int MAXSFLEN=AInfos.SAYFALEN-1;

    cpalert.setPositiveButton (Values.OK, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          String res=(cphtime.getText ().toString ()).trim ();

          if (res.length () < Values.UNO) return;

          int strt=Values.UNO;
          int end=Values.UNO;

          if (res.indexOf(Values.TIRE) > 0)
          {
            String[] split=res.split(Values.TIRE);
            if (split.length >= 2)
            {
              try
              {
                strt=Integer.parseInt(split[0]);
                end=Integer.parseInt(split[1]);
              }
              catch (NumberFormatException nfe)
              {
                return;
              }
            }
          }
          else
          {
            try
            {
              strt=Integer.parseInt(res);
              end=strt;
            }
            catch (NumberFormatException nfe)
            {
              return;
            }
          }

          if (strt < Values.CERO) strt = Values.CERO;
          if (strt > MAXSFLEN) strt = MAXSFLEN;
          if (end < Values.CERO) end = Values.CERO;
          if (end > MAXSFLEN) end = MAXSFLEN;

          if (strt > end) return;
		  //if (end < strt) return;

          try
          {
            copyContent(strt, end);
          }
          catch (IOException ioe)
            {}

          return;
        }
    });

    cpalert.setNegativeButton (Values.CANCEL, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          cphtime.setText ("");
          return;
        }
    });

    cpPane = cpalert.create();
    ///////////////////
    AlertDialog.Builder successAlert = new AlertDialog.Builder(context);
    successAlert = successAlert.setTitle(Values.SUCCESS_TITLE);

    final EditText successText = new EditText(context);
    successText.setFocusable(false);
    successText.setFocusableInTouchMode(false);
    successText.setClickable(false);
    successText.setText(Values.SUCCESS_MESSAGE);

    successAlert.setView(successText);

    successAlert.setPositiveButton(Values.OK, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
        }
    });

    successPane = successAlert.create();
    //////////////////
    StringBuffer sb=new StringBuffer ();
    sb.append ("test.txt; File Name\n");
    sb.append ("ISO-8859-9; Char Code\n");
    sb.append ("8; Line Count\n");
    sb.append ("1600F; Line Float Length\n");
    sb.append ("SansSerif,0,48; Font (must be same as above font information)\n");
    sb.append ("no; ALIGN-MDK (yes/no)\n");
    sb.append ("landscape; orientation (portrait | landscape)\n");
    sb.append ("/////////////\n");
    sb.append ("landscape; orientation (portrait | landscape)\n");
    sb.append ("ISO-8859-9; Char Code\n");
    sb.append ("Sans_Serif; Font Name (Monospace/Monospaced, Serif, Sans_Serif/SansSerif)\n");
    sb.append ("0; Font style (0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    sb.append ("48F; Text size\n");
    sb.append ("#FFEEEEEE; BG color (alpha_red_green_blue)\n");
    sb.append ("#FF535353; FG color (alpha_red_green_blue)\n");
    sb.append ("30F; xcoord\n");
    sb.append ("86F; ycoord\n");
    sb.append ("75F; space\n");
    sb.append ("2F; graphics stroke valor\n");
    sb.append ("yes; ALIGN-SCREEN (yes/no [yes signify centered])\n");
    sb.append ("rect-16F-18F-#FFEF8888; (alt: roundrect-6F-12F-20F-20F-#DDFF00FF)\n");
    sb.append ("###############\n");

    String settingsFileName=(PATH+"/carets/settings.txt");
	File fsc=new File (settingsFileName);
	if (fsc.exists () == false)
	{
		try
		{
		  OutputStream fos=new FileOutputStream (fsc);
		  PrintStream xps=new PrintStream (fos, true, "UTF-8");

		  xps.print (sb.toString ());

		  xps.flush ();
		  xps.close ();
		  fos.flush ();
		  fos.close ();
		}
		catch (IOException ioe)
		{}
	}
    //////////////
    StringBuffer fsb=new StringBuffer ();
    fsb.append ("Sans_Serif; Font Name (Monospace/Monospaced, Serif, Sans_Serif/SansSerif)\n");
    fsb.append ("0; Font style (0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    fsb.append ("48F; Text size\n");
    fsb.append ("#FFEEEEEE; BG color (alpha_red_green_blue)\n");
    fsb.append ("#FF535353; FG color (alpha_red_green_blue)\n");
    fsb.append ("86F; ycoord\n");
    fsb.append ("75F; space\n");
    fsb.append ("2F; graphics stroke valor\n");
    fsb.append ("rect-16F-18F-#FFEF8888; (alt: roundrect-6F-12F-20F-20F-#DDFF00FF)\n");
    fsb.append ("###############\n");
    //////////////////////
    AlertDialog.Builder salert=new AlertDialog.Builder (context);
    salert=salert.setTitle (Values.SETTINGS);

    final EditText shtime=new EditText (context);
    //shtime.setInputType (android.text.InputType.TYPE_CLASS_NUMBER);
    shtime.setText (fsb.toString ());

    salert.setView (shtime);

    salert.setPositiveButton (Values.OK, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          String res=(shtime.getText ().toString ());

          if (res.length () < Values.DIEZ) return;

          cinit (res);

          return;
        }
    });

    salert.setNegativeButton (Values.CANCEL, new DialogInterface.OnClickListener ()
      {
        public void onClick (DialogInterface dialog, int wh)
        {
          return;
        }
    });

    setPane = salert.create();
    ///////////////////
    /////////////

    rect=new RectF (5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/60F));
    ZCOORD=(rect.bottom)-(Values.DECR);

    fg2d.setTextSize (Values.FG2TS);

    holder=getHolder ();
    holder.addCallback (this);

    //////////////////////
    if (errorOcurred)
    {
      AlertDialog.Builder xalert=new AlertDialog.Builder (context);
      xalert=xalert.setTitle (Values.ERRORED);
      xalert.setCancelable (false);

      final EditText xhtime=new EditText (context);
      xhtime.setFocusable (false);
      xhtime.setFocusableInTouchMode (false);
      xhtime.setClickable (false);
      xhtime.setText (errorMessage);
      //xhtime.setInputType (android.text.InputType.TYPE_CLASS_NUMBER);

      xalert.setView (xhtime);

      xalert.setPositiveButton (Values.OK, new DialogInterface.OnClickListener ()
        {
          public void onClick (DialogInterface dialog, int wh)
          {
            if (activity != null)
            {
              activity.finish ();
            }
          }
      });

      xalert.setNegativeButton (Values.CANCEL, new DialogInterface.OnClickListener ()
        {
          public void onClick (DialogInterface dialog, int wh)
          {
            if (activity != null)
            {
              activity.finish ();
            }
          }
      });

      errorPane = xalert.create();
      errorPane.show ();
    }
    ///////////////////
  }//constructor end

  private final String getMainPathName ()
  {
    File path=context.getExternalFilesDir (Environment.DIRECTORY_DOWNLOADS);
    String px=path.getAbsolutePath ();

    String exclude="Android/data/"+(context.getPackageName ())+"/files/";
    px=px.replace (exclude, "");

    return px;
  }

  private final void copyContent(int strt, int end) throws IOException {
    File fd=new File (PATH+"/carets/contents/pg_"+packageName+"_"+strt+"-"+end+".txt");

    OutputStream fos = new FileOutputStream (fd);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");

    ASayfa s = null;
    String[][] lnns = null;
    int lnnslen = -1;
    int j = -1;
    final int MAX = AInfos.SAYFALEN - 1;

    for (int i = strt; i <= end; i++) {
      s = AInfos.SAYFALAR [i];
      lnns = s.getLines();
      lnnslen = lnns.length;

      j = -1;

      while (++j < lnnslen) {
        ps.println(lnns[j][0]);
      }

	  ps.print("\n   ");
	  ps.print (Integer.toString(i));
	  ps.println("\n");
      //System.out.println("Added: " + i + "/" + MAX);
    }

    ps.flush();
    ps.close();
    fos.flush();
    fos.close();

    //System.out.println("");
    //System.out.println("Wrote to bookText.txt");

    if (activity != null) {
      activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (successPane != null) {
              successPane.show();
            }
          }
      });
    }

    return;
  }

  private final int getBrightedColor (int oldcolor)
  {
    //Example oldcolor=0x000000 //black

    final int alpha=255;

    int red=Color.red (oldcolor);
    int green=Color.green (oldcolor);
    int blue=Color.blue (oldcolor);

    float redf=(float)red;
    float greenf=(float)green;
    float bluef=(float)blue;

    final float MVAL=255F;

    if ( (red == 0x0000) && (green == 0x0000) && (blue == 0x0000) )
    {
      red=0x0003;
      green=0x0003;
      blue=0x0003;
    }
    else
    {
      //red
      if (red > 0x0002)
      {
        red=(int) (Math.min (MVAL, (redf/0.7F)));
      }

      if ( (red == 0x0001) || (red == 0x0002) )
      {
        red=0x0004;
      }

      //green
      if (green > 0x0002)
      {
        green=(int) (Math.min (MVAL, (greenf/0.7F)));
      }

      if ( (green == 0x0001) || (green == 0x0002) )
      {
        green=0x0004;
      }

      //blue
      if (blue > 0x0002)
      {
        blue=(int) (Math.min (MVAL, (bluef/0.7F)));
      }

      if ( (blue == 0x0001) || (blue == 0x0002) )
      {
        blue=0x0004;
      }
    }

    int argb=Color.argb (alpha, red, green, blue);

    return argb;
  }

  //INIT START
  public void init ()
  {
    try
    {
      InputStream is=null;

      String fname=(getMainPathName ())+"/carets/settings.txt";
      File fdo=new File (fname);

      if(fdo.exists ())
      {
        is=new FileInputStream (fdo);
      }
      else
      {
        is=(context.getAssets()).open ("texts/info.txt");
      }

      Reader isr=new InputStreamReader (is);
      BufferedReader br=new BufferedReader (isr, Values.SKB);

      String line=br.readLine (); //Skip 1
      line=br.readLine (); //Skip 2
      line=br.readLine (); //Skip 3
      line=br.readLine (); //Skip 4
      line=br.readLine (); //Skip 5
      line=br.readLine (); //Skip 6
      line=br.readLine (); //Skip 7
      line=br.readLine (); //Skip 8

      //landscape; orientation (portrait | landscape)
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //8

      if (line.equals ("portrait"))
      {
        portrait=true;
      }
      else
      {
        portrait=false;
      }

      //ISO-8859-9; Char Code
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //9
      CHARSET=line;

      Typeface SCTP=Typeface.DEFAULT;
      Typeface tpf=Typeface.DEFAULT;

      //Monospace; Font Family Name (Monospace, Serif, Sans_Serif ....)
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //10
      if (line.equals ("Monospace"))
      {
        tpf=Typeface.MONOSPACE;
      }
      else if (line.equals ("Monospaced"))
      {
        tpf=Typeface.MONOSPACE;
      }
      else if (line.equals ("Serif"))
      {
        tpf=Typeface.SERIF;
      }
      else if (line.equals ("Sans_Serif"))
      {
        tpf=Typeface.SANS_SERIF;
      }
      else if (line.equals ("SansSerif"))
      {
        tpf=Typeface.SANS_SERIF;
      }
      else
      {
        tpf=Typeface.DEFAULT;
      }

      //0; Font style (0, 1, 2, 3)(plain, bold, italic, boldItalic)
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //11
      int num=Integer.parseInt (line);
      int style=Typeface.NORMAL;

      if (num == 0)
      {
        style=Typeface.NORMAL;
      }
      else if (num == 1)
      {
        style=Typeface.BOLD;
      }
      else if (num == 2)
      {
        style=Typeface.ITALIC;
      }
      else if (num == 3)
      {
        style=Typeface.BOLD_ITALIC;
      }
      else
      {
        style=Typeface.NORMAL;
      }

      SCTP=Typeface.create (tpf, style);
      g2d.setTypeface (SCTP);

      //24F; Text size
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //12
      float size=Float.parseFloat (line);
      g2d.setTextSize (size);

      //FFFFFF; BG color
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //13
      int bg=Color.parseColor (line);
      BG=bg;

      //000000; FG color
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //14
      int fg=Color.parseColor (line);
      g2d.setColor (fg);

      int argb=getBrightedColor (fg);//int argb=Color.argb (alpha, red, green, blue);
      fg2d.setColor (argb);

      //18F; xcoord
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //15
      float xcoord=Float.parseFloat (line);
      XCOORD=xcoord;
      ORIGXCOORD=XCOORD;

      //42F; ycoord
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //16
      float ycoord=Float.parseFloat (line);
      YCOORD=ycoord;
      ORIGYCOORD=YCOORD;

      //43F; space
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //17
      float space=Float.parseFloat (line);
      SPACE=space;

      //2F; graphics stroke valor
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //18
      float stroke=Float.parseFloat (line);
      //g2d.setStrokeWidth (stroke);
      BORDERPAINT.setStrokeWidth (stroke);

      //no; ALIGN-SCREEN (no-center)
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //19
      String iscenter=line;
      if (iscenter.equals ("yes"))
      {
        isCentered=true;
      }
      else if (iscenter.equals ("no"))
      {
        isCentered=false;
      }
      else
      {
        isCentered=false;
      }

      //roundrect-5F-10F-20F-20F-#FF0000; ect info (alt is, rect-5F-10F-#FF0000)
      line=(((br.readLine ()).split (Values.IKINOKTA))[Values.CERO]).trim (); //20
      String [] momis=line.split ("-");
      if (momis != null)
      {
        int mlen=momis.length;
        if (mlen == 6)
        {
          isRound=true;
          float fx=Float.parseFloat (momis [1]);
          float fy=Float.parseFloat (momis [2]);
          rect=new RectF (fx, fy, fwidth-(fwidth/100F), fheight-(fheight/60F));
          AXISX=Float.parseFloat (momis [3]);
          AXISY=Float.parseFloat (momis [4]);
          int c=Color.parseColor (momis [5]);
          BORDERPAINT.setColor (c);
        }
        else if (mlen == 4)
        {
          isRound=false;
          float fx=Float.parseFloat (momis [1]);
          float fy=Float.parseFloat (momis [2]);
          rect=new RectF (fx, fy, fwidth-(fwidth/100F), fheight-(fheight/60F));
          int c=Color.parseColor (momis [3]);
          BORDERPAINT.setColor (c);
        }
        else
        {
          isRound=false;
          rect=new RectF (5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/5F));
          BORDERPAINT.setColor (0xFF000000);
        }
      }
      else
      {
        isRound=false;
        rect=new RectF (5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/5F));
        BORDERPAINT.setColor (0xFF000000);
      }

      br.close ();
      isr.close ();
      is.close ();
    }
    catch (IOException ioe)
    {
      //      ioe.printStackTrace ();
    }
    catch (NumberFormatException nfe)
      {}

    Values.MAX=(AInfos.SAYFALEN)-(Values.UNO);

    if (portrait)
    {
      AX=Values.CERO;
      AY=(height)-(height/Values.SEIS);

      BX=(width/Values.DOS);
      BY=AY;
    }
    else
    {//landscape
      AX=Values.CERO;
      AY=(height)-(height/Values.CUATRO)+3;

      BX=(width/Values.DOS);
      BY=AY;
    }
  }
  //INIT END

  //CINIT START
  public void cinit (String text)
  {
    try
    {
      Reader isr=new StringReader (text);
      BufferedReader br=new BufferedReader (isr);

      Typeface SCTP=Typeface.DEFAULT;
      Typeface tpf=Typeface.DEFAULT;

      boolean rvalo=false;

      String xline=null;
      String line=null;

      //Monospace; Font Family Name (Monospace, Serif, Sans_Serif ....)
      xline=br.readLine ();//1 //Font name line

      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //1
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      if (line.equalsIgnoreCase (Values.MONOSPACE))
      {
        tpf=Typeface.MONOSPACE;
      }
      else if (line.equalsIgnoreCase (Values.MONOSPACED))
      {
        tpf=Typeface.MONOSPACE;
      }
      else if (line.equalsIgnoreCase (Values.SERIF))
      {
        tpf=Typeface.SERIF;
      }
      else if (line.equalsIgnoreCase (Values.SANS_SERIF))
      {
        tpf=Typeface.SANS_SERIF;
      }
      else if (line.equalsIgnoreCase (Values.SANSSERIF))
      {
        tpf=Typeface.SANS_SERIF;
      }
      else
      {
        tpf=Typeface.DEFAULT;
      }

      //0; Font style (0, 1, 2, 3)(plain, bold, italic, boldItalic)
      xline=br.readLine ();//2
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //2
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      int num=Values.CERO;

      try
      {
        num=Integer.parseInt (line);
      }
      catch (NumberFormatException nfe)
      {
        num=Values.CERO;
      }

      int style=Typeface.NORMAL;

      if (num == Values.CERO)
      {
        style=Typeface.NORMAL;
      }
      else if (num == Values.UNO)
      {
        style=Typeface.BOLD;
      }
      else if (num == Values.DOS)
      {
        style=Typeface.ITALIC;
      }
      else if (num == Values.TRES)
      {
        style=Typeface.BOLD_ITALIC;
      }
      else
      {
        style=Typeface.NORMAL;
      }

      SCTP=Typeface.create (tpf, style);
      g2d.setTypeface (SCTP);

      //48F; Text size
      xline=br.readLine ();//3
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //3
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      float size=Values.KFS;

      try
      {
        size=Float.parseFloat (line);
      }
      catch (NumberFormatException nfe)
      {
        size=Values.KFS;
      }

      g2d.setTextSize (size);

      //#FFFFFFFF; BG color
      xline=br.readLine ();//4
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //4
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, true);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      int bg=BG;

      try
      {
        bg=Color.parseColor (line);
      }
      catch (IllegalArgumentException iae)
      {
        bg=Color.WHITE;
      }

      BG=bg;

      //#FF000000; FG color
      xline=br.readLine ();//5
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //5
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, true);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      int fg=Color.BLACK;

      try
      {
        fg=Color.parseColor (line);
      }
      catch (IllegalArgumentException iae)
      {
        fg=Color.BLACK;
      }

      g2d.setColor (fg);

      int argb=getBrightedColor (fg);//int argb=Color.argb (alpha, red, green, blue);
      fg2d.setColor (argb);

      //xline=br.readLine(); //skip xcoord, 16

      //86F; ycoord
      xline=br.readLine ();//6
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //6
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      float ycoord=ORIGYCOORD;

      try
      {
        ycoord=Float.parseFloat (line);
      }
      catch (NumberFormatException nfe)
      {
        ycoord=ORIGYCOORD;
      }

      YCOORD=ycoord;
      ORIGYCOORD=YCOORD;

      //75F; space
      xline=br.readLine ();//7
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //7
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      float space=Values.SPACEF;

      try
      {
        space=Float.parseFloat (line);
      }
      catch (NumberFormatException nfe)
      {
        space=Values.SPACEF;
      }

      SPACE=space;

      //2F; graphics stroke valor
      xline=br.readLine ();//8
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //8
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      float stroke=Values.DOSF;

      try
      {
        stroke=Float.parseFloat (line);
      }
      catch (NumberFormatException nfe)
      {
        stroke=Values.DOSF;
      }

      //g2d.setStrokeWidth (stroke);
      BORDERPAINT.setStrokeWidth (stroke);

      //xline=br.readLine (); //skip 20, yes align

      //rect-16F-18F-#FFEF8888; (alt: roundrect-6F-12F-20F-20F-#DDFF00FF)
      xline=br.readLine ();//9
      rvalo=isProblemLine (xline);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      line=((xline.split (Values.IKINOKTA))[Values.CERO]).trim (); //9
      line=line.replaceAll (Values.SPACE, "");
      rvalo=isProblem (line, false);
      if (rvalo)
      {
        br.close ();
        isr.close ();
        return;
      }

      String [] momis=line.split (Values.TIRE);
      if (momis != null)
      {
        int mlen=momis.length;
        if (mlen == Values.SEIS)
        {
          isRound=true;

          float fx=Values.CEROF;
          float fy=Values.CEROF;

          try
          {
            fx=Float.parseFloat (momis [Values.UNO]);
            fy=Float.parseFloat (momis [Values.DOS]);
            AXISX=Float.parseFloat (momis [Values.TRES]);
            AXISY=Float.parseFloat (momis [Values.CUATRO]);
            rect=new RectF (fx, fy, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.SESEF));
          }
          catch (NumberFormatException nfe)
          {
            rect=new RectF (fx, fy, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.SESEF));
          }

          try
          {
            int c=Color.parseColor (momis [Values.CINCO]);
            BORDERPAINT.setColor (c);
          }
          catch (IllegalArgumentException iae)
          {
            BORDERPAINT.setColor (Color.RED);
          }
        }
        else if (mlen == Values.CUATRO)
        {
          isRound=false;

          try
          {
            float fx=Float.parseFloat (momis [Values.UNO]);
            float fy=Float.parseFloat (momis [Values.DOS]);
            rect=new RectF (fx, fy, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.SESEF));
          }
          catch (NumberFormatException nfe)
          {
            rect=new RectF (Values.CINCOF, Values.DIEZF, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.CINCOF));
          }

          try
          {
            int c=Color.parseColor (momis [Values.TRES]);
            BORDERPAINT.setColor (c);
          }
          catch (IllegalArgumentException iae)
          {
            BORDERPAINT.setColor (Color.RED);
          }
        }
        else
        {
          isRound=false;
          rect=new RectF (Values.CINCOF, Values.DIEZF, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.CINCOF));
          BORDERPAINT.setColor (Color.BLACK);
        }
      }
      else
      {
        isRound=false;
        rect=new RectF (Values.CINCOF, Values.DIEZF, fwidth-(fwidth/Values.CIENF), fheight-(fheight/Values.CINCOF));
        BORDERPAINT.setColor (Color.BLACK);
      }

      br.close ();
      isr.close ();
    }
    catch (IOException ioe)
      {}
    catch (NumberFormatException nfe)
      {}
  }
  //CINIT END

  private final boolean isProblem (String metin, boolean renk)
  {
    if (metin == null) return true;

    //if (metin.equals ("")) return true;

    int metlen=metin.length ();

    if (metlen<Values.UNO) return true;

    if (renk)
    {
      if (metlen != Values.NUEVE) //Example: #FFABABAB
      {
        return true;
      }

      if ((metin.startsWith (Values.DIYEZ)) == false)
      {
        return true;
      }
    }

    return false;
  }

  private final boolean isProblemLine (String metin)
  {
    if (metin == null) return true;

    //if (metin.equals ("")) return true;

    int metlen=metin.length ();

    if (metlen<Values.DOS) return true;

    if ((metin.indexOf (Values.IKINOKTA)) < Values.CERO) return true;

    return false;
  }

  private final void drawx ()
  {
    Canvas canvas=null;

    try
    {
      canvas = holder.lockCanvas ();

      synchronized (holder)
      {
        if (holder.getSurface ().isValid ())
        {
          paintNuestraPantalla (width, height, canvas);//boya
        }
      }
    }
    finally
    {
      if (canvas != null)
      {
        holder.unlockCanvasAndPost (canvas);
      }
    }
  }//end drawx

  private final void paintNuestraPantalla (int width, int height,
  Canvas canvas)
  {
    if (isCentered)
    {
      renderAlign (width, height, bufferG2D);
    }
    else
    {
      render (width, height, bufferG2D);
    }

    canvas.drawBitmap (bufferBimg, Values.CERO, Values.CERO, zg2d);
  }

  private final void setBitmapMe ()
  {
    SINIFLAR=(AInfos.SAYFALAR [PGSYC]).getLines ();
    SINIFLARLEN=SINIFLAR.length;
  }

  public void renderAlign (int xwidth, int xheight, Canvas canvas)
  {
    canvas.drawColor (BG);//bg

    if (isRound)
    {
      canvas.drawRoundRect (rect, AXISX, AXISY, BORDERPAINT);
    }
    else
    {
      canvas.drawRect (rect, BORDERPAINT);
    }

    XCOORD=ORIGXCOORD;
    YCOORD=ORIGYCOORD;

    String line=null;

    for (int i=Values.CERO; i<SINIFLARLEN; i++)
    {
      line=SINIFLAR [i][Values.CERO];
      swc=(g2d.measureText (line))/Values.DOSF;
      XCOORD=(FNISF)-(swc);
      canvas.drawText (line, XCOORD, YCOORD, g2d);
      YCOORD += SPACE;
    }

    SLINE=(""+Integer.toString (PGSYC)+"/"+Values.MAX+"");
    swc=(fg2d.measureText (SLINE))/Values.DOSF;
    XCOORD=(FNISF)-(swc);

    canvas.drawText (SLINE, XCOORD, ZCOORD, fg2d);

    return;
  }

  public void render (int xwidth, int xheight, Canvas canvas)
  {
    canvas.drawColor (BG);//bg

    if (isRound)
    {
      canvas.drawRoundRect (rect, AXISX, AXISY, BORDERPAINT);
    }
    else
    {
      canvas.drawRect (rect, BORDERPAINT);
    }

    XCOORD=ORIGXCOORD;
    YCOORD=ORIGYCOORD;

    String line=null;

    for (int i=Values.CERO; i<SINIFLARLEN; i++)
    {
      line=SINIFLAR [i][Values.CERO];
      swc=(g2d.measureText (line))/Values.DOSF;
      XCOORD=(FNISF)-(swc);
      canvas.drawText (line, XCOORD, YCOORD, g2d);
      YCOORD += SPACE;
    }

    SLINE=(""+Integer.toString (PGSYC)+"/"+Values.MAX+"");
    swc=(fg2d.measureText (SLINE))/Values.DOSF;
    XCOORD=(FNISF)-(swc);

    canvas.drawText (SLINE, XCOORD, ZCOORD, fg2d);

    return;
  }

  //@Override
  public boolean onTouchEvent (MotionEvent evt)
  {
    if ((evt.getAction ()) != MotionEvent.ACTION_DOWN) return false;

    int x=(int)(evt.getX ());
    int y=(int)(evt.getY ());

    if ((x < NISF) && (x >= AX) && (y > AY))
    {//Exit
      if (activity != null)
      {
        try
        {
          PrintStream ps=new PrintStream (new FileOutputStream (new File (caretFileName)));
          ps.println (Integer.toString (PGSYC));
          ps.flush ();
          ps.close ();
        }
        catch (IOException ioe)
          {}

        activity.finish ();
      }
    }
    else if ((x >= NISF) && (x <= width) && (y > BY))
    {//PageNum
      if (pane != null)
      {
        pane.show ();
      }
    }
    else if ((x <= SETX) && (y <= SETY))
    {
      if (setPane != null)
      {
        setPane.show ();
      }
    }
    else if ((x >= CPX) && (y <= CPY))
    {
      if (cpPane != null)
      {
        cpPane.show();
      }
    }
    else if (x <= NISF)
    {//<--
      --PGSYC;
      if (PGSYC < Values.CERO) PGSYC=Values.CERO;
      setBitmapMe ();
      drawx ();
    }
    else
    {//-->
      ++PGSYC;
      if (PGSYC > Values.MAX) PGSYC=Values.MAX;
      setBitmapMe ();
      drawx ();
    }

    return true;
  }

  //@Override
  public void surfaceChanged (SurfaceHolder holder, int format,
  int width, int height)
  {}

  //@Override
  public void surfaceCreated (SurfaceHolder holder)
  {
    setBitmapMe ();
    drawx ();
  }

  //@Override
  public void surfaceDestroyed (SurfaceHolder holder)
  {}

}
