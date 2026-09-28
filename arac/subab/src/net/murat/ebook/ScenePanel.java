package net.murat.ebook;

// Android
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
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.TextPaint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

// Java
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

public class ScenePanel extends SurfaceView implements SurfaceHolder.Callback {

  private Context context;

  private Bitmap bufferBimg = null;
  private final Canvas bufferG2D = new Canvas();

  private int width = 1;
  private int NISF = 1;
  private int SETX = 1;
  private int SETY = 1;
  private int CPX = 1;
  private int CPY = 1;
  private int height = 1;

  float fwidth = (float)(width);
  float fheight = (float)(height);

  float FNISF = 1f;

  float AXISX = 20f;
  float AXISY = 20f;

  // Global state variables tracking formatting styles across line/page boundaries
  private int globalColorActive = 0xFF000000; // Default text color(ARGB Black)
  private boolean globalBoldActive = false;
  private boolean globalItalicActive = false;
  private boolean globalUnderlineActive = false;

  // Heavy object definitions moved to class level to avoid allocation inside loops
  private final TextPaint textPaint = new TextPaint();
  private final StringBuilder currentWord = new StringBuilder(64);

  private EditText successText = null;
  private String globCopyFileName = "";

  private SurfaceHolder holder = null;

  private Activity activity = null;

  private AlertDialog pane = null;
  private AlertDialog errorPane = null;
  private AlertDialog setPane = null;
  private AlertDialog cpPane = null;
  private AlertDialog successPane = null;

  private int PGSYC = 0;

  private int AX = 1;//exit
  private int AY = 1;//exit
  private int BX = 1;//pagenum
  private int BY = 1;//exit

  private int BG = 0xFFFFFFFF;
  private int FG = 0xFF000000;

  private String CHARSET = "ISO-8859-9";
  private String SLINE = "";

  private final Paint fg2d = new Paint();
  private final Paint zg2d = new Paint();
  private final Paint g2d = new Paint();
  private final Paint BORDERPAINT = new Paint();

  private float XCOORD = 11f;
  private float ORIGXCOORD = XCOORD;
  private float YCOORD = 40f;
  private float ORIGYCOORD = YCOORD;
  private float ZCOORD = 1f;
  private float SPACE = 35f;
  private float swc = 0f;

  private RectF rect = new RectF(1f, 1f, 1f, 1f);

  private String caretFileName = "";

  private boolean isCentered = false;

  private boolean isRound = true;
  private boolean portrait = false;

  private String [][] SINIFLAR = null;
  private String [] SINIF = null;

  private int SINIFLARLEN = 0;
  private int SINIFLEN = 0;

  private boolean errorOcurred = false;

  private int MAX = 10;

  private String PATH = "";
  private String packageName = "";
  private String errorMessage = "";

  @SuppressWarnings("deprecation")
  public ScenePanel(Context contxt) {
    super(contxt);

    this.context = contxt;
    activity = (Activity)context;

    successText = new EditText(this.context);

    Display dd = (activity.getWindowManager()).getDefaultDisplay();

    Point sizep = new Point();
    dd.getSize(sizep);

    width = sizep.x;
    height = sizep.y;

    fwidth = (float)(width);
    fheight = (float)(height);

    NISF = (width/2);
    FNISF = fwidth/2f;

    SETX = (NISF/2);
    SETY = (height/5);

    CPX = width-SETX;
    CPY = SETY;

    bufferBimg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bufferG2D.setBitmap(bufferBimg);

    BORDERPAINT.setStyle(Paint.Style.STROKE);

    packageName = context.getPackageName(); //net.murat.ebook
    packageName = packageName.replaceAll("net\\.murat\\.", ""); //abc.ebook
    packageName = packageName.replaceAll("\\.ebook", ""); //abc

    PATH = getMainPathName();

    File ff = new File(PATH+"/carets");
    File ff2 = new File(PATH+"/carets/contents");

    try {
      if (ff.exists()  ==  false) {
        ff.mkdir();
      }

      if (ff2.exists()  ==  false) {
        ff2.mkdir();
      }
    } catch(SecurityException se) {
      errorOcurred = true;
      errorMessage = (se.getMessage());
    }

    caretFileName = (PATH+"/carets/"+packageName+".txt");
    ff = new File(caretFileName);

    if (ff.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(ff));
        String line = (br.readLine()).trim();

        try {
          PGSYC = Integer.parseInt(line);
        } catch(NumberFormatException nfe) {
          PGSYC = 0;
        }

        br.close();
      } catch(IOException ioe) {
        errorOcurred = true;
        errorMessage = (ioe.getMessage());
      } catch(SecurityException se) {
        errorOcurred = true;
        errorMessage = (se.getMessage());
      }
    }
    else {
      try {
        PrintStream ps = new PrintStream(new FileOutputStream(ff), true);
        ps.println("0");
        ps.flush();
        ps.close();
      } catch(IOException ioe) {
        errorOcurred = true;
        errorMessage = (ioe.getMessage());
      } catch(SecurityException se) {
        errorOcurred = true;
        errorMessage = (se.getMessage());
      }
    }

    //////////////////////
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert = alert.setTitle("Enter Page Num:");

    final EditText htime = new EditText(context);
    htime.setInputType(InputType.TYPE_CLASS_NUMBER);

    alert.setView(htime);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          String res = (htime.getText().toString()).trim();

          if (res.length() < 1) return;

          int c = 1;

          try {
            c = Integer.parseInt(res);
            if (c<0) c = 0;
            if (c>MAX) c = MAX;
            PGSYC = c;
          } catch(NumberFormatException e) {
            return;
          }

          htime.setText("");
          setBitmapMe();
          drawx();

          return;
        }
    });

    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          htime.setText("");
          return;
        }
    });

    pane = alert.create();
    ///////////////////
    AlertDialog.Builder cpalert = new AlertDialog.Builder(context);
    cpalert = cpalert.setTitle("Enter page num for copy, ex:15 or 15-20");

    final EditText cphtime = new EditText(context);
    cphtime.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    cphtime.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));

    cpalert.setView(cphtime);

    final int MAXSFLEN = AInfos.SAYFALEN-1;

    cpalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          String res = (cphtime.getText().toString()).trim();

          if (res.length() < 1) return;
          if (res.endsWith("-")) return;

          int strt = 1;
          int end = 1;

          if (res.indexOf("-") > 0) {
            String[] split = res.split("-");
            if (split.length >=  2) {
              try {
                strt = Integer.parseInt(split[0]);
                end = Integer.parseInt(split[1]);
              } catch(NumberFormatException nfe) {
                return;
              }
            }
          }
          else {
            try {
              strt = Integer.parseInt(res);
              end = strt;
            } catch(NumberFormatException nfe) {
              return;
            }
          }

          if (strt < 0) return;
          if (strt > MAXSFLEN) return;
          if (end < 0) return;
          if (end > MAXSFLEN) return;

          if (strt > end) return;

          try {
            copyContent(strt, end);
          } catch(IOException ioe) {}

          return;
        }
    });

    cpalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          cphtime.setText("");
          return;
        }
    });

    cpPane = cpalert.create();
    ///////////////////
    AlertDialog.Builder successAlert = new AlertDialog.Builder(context);
    successAlert = successAlert.setTitle("Successfull!");

    successText.setFocusable(false);
    successText.setFocusableInTouchMode(false);
    successText.setClickable(false);

    successAlert.setView(successText);

    successAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
        }
    });

    successPane = successAlert.create();
    //////////////////
    StringBuffer sb = new StringBuffer();
    sb.append("test.txt; File Name\n");
    sb.append("ISO-8859-9; Char Code\n");
    sb.append("8; Line Count\n");
    sb.append("1600F; Line Float Length\n");
    sb.append("SansSerif,0,48; Font(must be same as above font information)\n");
    sb.append("no; ALIGN-MDK(yes/no)\n");
    sb.append("landscape; orientation(portrait | landscape)\n");
    sb.append("/////////////\n");
    sb.append("landscape; orientation(portrait | landscape)\n");
    sb.append("ISO-8859-9; Char Code\n");
    sb.append("Sans_Serif; Font Name(Monospace/Monospaced, Serif, Sans_Serif/SansSerif)\n");
    sb.append("0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    sb.append("48F; Text size\n");
    sb.append("#FFEEEEEE; BG color(alpha_red_green_blue)\n");
    sb.append("#FF535353; FG color(alpha_red_green_blue)\n");
    sb.append("30F; xcoord\n");
    sb.append("86F; ycoord\n");
    sb.append("75F; space\n");
    sb.append("3F; graphics stroke valor\n");
    sb.append("yes; ALIGN-SCREEN(yes/no [yes signify centered])\n");
    sb.append("roundrect-10F-10F-100F-100F-#FF535353;(alt: rect-10F-10F-#FF535353;)\n");
    sb.append("###############\n");

    String settingsFileName = (PATH+"/carets/settings.txt");
    File fsc = new File(settingsFileName);
    if (fsc.exists()  ==  false) {
      try {
        OutputStream fos = new FileOutputStream(fsc);
        PrintStream xps = new PrintStream(fos, true, "UTF-8");

        xps.print(sb.toString());

        xps.flush();
        xps.close();
        fos.flush();
        fos.close();
      } catch(IOException ioe) {}
      }
    //////////////
    StringBuffer fsb = new StringBuffer();
    fsb.append("Sans_Serif; Font Name(Monospace/Monospaced, Serif, Sans_Serif/SansSerif)\n");
    fsb.append("0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    fsb.append("48F; Text size\n");
    fsb.append("#FFEEEEEE; BG color(alpha_red_green_blue)\n");
    fsb.append("#FF535353; FG color(alpha_red_green_blue)\n");
    fsb.append("86F; ycoord\n");
    fsb.append("75F; space\n");
    fsb.append("2F; graphics stroke valor\n");
    fsb.append("roundrect-10F-10F-100F-100F-#FF535353;(alt: rect-10F-10F-#FF535353;)\n");
    fsb.append("###############\n");
    //////////////////////
    AlertDialog.Builder salert = new AlertDialog.Builder(context);
    salert = salert.setTitle("Settings");

    final EditText shtime = new EditText(context);
    shtime.setText(fsb.toString());

    salert.setView(shtime);

    salert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          String res = (shtime.getText().toString());        
          if (res.length() < 10) return;      
          cinit(res);
		  drawx();

          return;
        }
    });

    salert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int wh) {
          return;
        }
    });

    setPane = salert.create();
    ///////////////////

    rect = new RectF(5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/60F));
    ZCOORD = (rect.bottom)-(10f);

    fg2d.setTextSize(36f);

    holder = getHolder();
    holder.addCallback(this);

    //////////////////////
    if (errorOcurred) {
      AlertDialog.Builder xalert = new AlertDialog.Builder(context);
      xalert = xalert.setTitle("IO||Security Error!");
      xalert.setCancelable(false);

      final EditText xhtime = new EditText(context);
      xhtime.setFocusable(false);
      xhtime.setFocusableInTouchMode(false);
      xhtime.setClickable(false);
      xhtime.setText(errorMessage);

      xalert.setView(xhtime);

      xalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int wh) {
            if (activity !=  null) {
              activity.finishAndRemoveTask();
            }
          }
      });

      xalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int wh) {
            if (activity !=  null) {
              activity.finish();
            }
          }
      });

      errorPane = xalert.create();
      errorPane.show();
    }
    ///////////////////
  }//constructor end

  private final String getMainPathName() {
    File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    String px = path.getAbsolutePath();

    String exclude = "Android/data/"+(context.getPackageName())+"/files/";
    px = px.replace(exclude, "");

    return px;
  }

  /**
   * Main entry point that prompts the user to choose between Plain TXT or HTML export.
   * Runs the dialog UI on the main thread safely.
   */
  private final void copyContent(final int strt, final int end) throws IOException {
    if (activity  ==  null) return;

    activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          AlertDialog.Builder builder = new AlertDialog.Builder(activity);
          builder.setTitle("Export Options");
          builder.setMessage("Select the export format for your pages:");

          builder.setPositiveButton("Plain TXT", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      try {
                        copyContentTXT(strt, end);
                      } catch(IOException e) {
                        e.printStackTrace();
                      }
                    }
                }).start();
              }
          });

          builder.setNegativeButton("HTML", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                      try {
                        copyContentHTML(strt, end);
                      } catch(IOException e) {
                        e.printStackTrace();
                      }
                    }
                }).start();
              }
          });

          builder.setCancelable(true);
          builder.show();
        }
    });
  }

  /**
   * Universal Zero-Garbage Tag Stripper: Safely bypasses any text inside matching < and > brackets.
   * Resolves split/broken font attributes spanning across multiple rows without heap allocations.
   */
  private final void copyContentTXT(int strt, int end) throws IOException {
    File fd = new File(PATH + "/carets/contents/pg_" + packageName + "_" + strt + "-" + end + ".txt");
    globCopyFileName = fd.getAbsolutePath();
    OutputStream fos = new FileOutputStream(fd);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");

    ASayfa s = null;
    String[][] lnns = null;
    int lnnslen = -1;
    int j = -1;

    boolean insideTag = false; // Tracks if a tag opened on a previous row remains unclosed

    for (int i = strt; i <=  end; i++) {
      s = AInfos.SAYFALAR[i];
      lnns = s.getLines();
      lnnslen = lnns.length;
      j = -1;

      while (++j < lnnslen) {
        String rawLine = lnns[j][0];
        if (rawLine  ==  null) continue;

        currentWord.setLength(0); // Clear shared class-level buffer safely
        int cursor = 0;
        int lineLen = rawLine.length();

        while (cursor < lineLen) {
          char c = rawLine.charAt(cursor);

          if (insideTag) {
            if (c  ==  '>') {
              insideTag = false; // Broken tag from previous row finally ended
            }
            cursor++;
            continue;
          }

          if (c  ==  '<') {
            int tagEnd = rawLine.indexOf('>', cursor);
            if (tagEnd  ==  -1) {
              insideTag = true; // Tag opened but severed at line boundary
              break;
            }
            cursor = tagEnd + 1;
            continue;
          }

          // If a rogue unlinked 'color = ' text somehow bypassed structural parsing, intercept it
          if (c  ==  'c' && rawLine.regionMatches(cursor, "color = ", 0, 6)) {
            int closeBracketIdx = rawLine.indexOf('>', cursor);
            if (closeBracketIdx !=  -1) {
              cursor = closeBracketIdx + 1; // Leap ahead past rogue layout attribute leak
              continue;
            }
          }

          currentWord.append(c);
          cursor++;
        }
        ps.println(currentWord);
      }
      ps.print("\n   ");
      ps.print(Integer.toString(i));
      ps.println("\n");
    }

    ps.flush();
    ps.close();
    fos.flush();
    fos.close();

    showSuccessMessage();
  }

  /**
   * Exports raw lines into a clean .html file. Mentally welds severed multi-line font tag chunks
   * back together into structurally legal HTML elements so browsers don't display plain code leaks.
   */
  private final void copyContentHTML(int strt, int end) throws IOException {
    File fd = new File(PATH + "/carets/contents/pg_" + packageName + "_" + strt + "-" + end + ".html");
    globCopyFileName = fd.getAbsolutePath();
    OutputStream fos = new FileOutputStream(fd);
    PrintStream ps = new PrintStream(fos, true, "UTF-8");

    ps.println("<!DOCTYPE html><html><head><meta charset = 'UTF-8'></head><body>");

    ASayfa s = null;
    String[][] lnns = null;
    int lnnslen = -1;
    int j = -1;

    boolean wasPreviousLineBroken = false; // Remembers if the previous row ended inside an incomplete tag

    for (int i = strt; i <=  end; i++) {
      s = AInfos.SAYFALAR[i];
      lnns = s.getLines();
      lnnslen = lnns.length;
      j = -1;

      while (++j < lnnslen) {
        String rawLine = lnns[j][0];
        if (rawLine  ==  null) continue;

        // If the last line left an open tag fragment like '<font', and this row starts with 'color = "red">',
        // do NOT put a newline break(\n) between them. Print them inline so they merge back perfectly.
        if (wasPreviousLineBroken) {
          // Eliminate potential leading whitespaces to ensure strict tag merging syntax correctness
          int firstValidChar = 0;
          while (firstValidChar < rawLine.length() && rawLine.charAt(firstValidChar)  ==  ' ') {
            firstValidChar++;
          }
          ps.print(rawLine.substring(firstValidChar)); // Append directly next to the open bracket
          } else {
          ps.println(rawLine);
        }

        // Track if current line ends with a broken tag layout
        int lastOpen = rawLine.lastIndexOf('<');
        int lastClose = rawLine.lastIndexOf('>');
        wasPreviousLineBroken  = (lastOpen > lastClose);
      }

      ps.print("<br><br>&nbsp;&nbsp;&nbsp;Page: ");
      ps.print(Integer.toString(i));
      ps.println("<br><br>");
    }

    ps.println("</body></html>");

    ps.flush();
    ps.close();
    fos.flush();
    fos.close();

    showSuccessMessage();
  }

  /**
   * Utility method helper to push success alerts onto the UI thread safely.
   */
  private final void showSuccessMessage() {
    if (activity !=  null) {
      activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            if (successPane !=  null) {
              successText.setText(globCopyFileName);
              successPane.show();
            }
          }
      });
    }
  }

  private final int getBrightedColor(int oldcolor) {    
    final int alpha = 255;

    int red = Color.red(oldcolor);
    int green = Color.green(oldcolor);
    int blue = Color.blue(oldcolor);

    float redf = (float)red;
    float greenf = (float)green;
    float bluef = (float)blue;

    final float MVAL = 255F;

    if ((red  ==  0x0000) &&(green  ==  0x0000) &&(blue  ==  0x0000) ) {
      red = 0x0003;
      green = 0x0003;
      blue = 0x0003;
    }
    else {
      //red
      if (red > 0x0002) {
        red = (int)(Math.min(MVAL,(redf/0.7F)));
      }

      if ((red  ==  0x0001) ||(red  ==  0x0002) ) {
        red = 0x0004;
      }

      //green
      if (green > 0x0002) {
        green = (int)(Math.min(MVAL,(greenf/0.7F)));
      }

      if ((green  ==  0x0001) ||(green  ==  0x0002) ) {
        green = 0x0004;
      }

      //blue
      if (blue > 0x0002) {
        blue = (int)(Math.min(MVAL,(bluef/0.7F)));
      }

      if ((blue  ==  0x0001) ||(blue  ==  0x0002) ) {
        blue = 0x0004;
      }
    }

    int argb = Color.argb(alpha, red, green, blue);

    return argb;
  }

  //INIT START
  public void init() {
    try {
      InputStream is = null;

      String fname = (getMainPathName())+"/carets/settings.txt";
      File fdo = new File(fname);

      if (fdo.exists()) {
        is = new FileInputStream(fdo);
      } else {
        is = (context.getAssets()).open("texts/info.txt");
      }

      Reader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr, 1024);

      String line = br.readLine(); //Skip 1
      line = br.readLine(); //Skip 2
      line = br.readLine(); //Skip 3
      line = br.readLine(); //Skip 4
      line = br.readLine(); //Skip 5
      line = br.readLine(); //Skip 6
      line = br.readLine(); //Skip 7
      line = br.readLine(); //Skip 8

      //landscape; orientation(portrait | landscape)
      line = (((br.readLine()).split(";"))[0]).trim(); //8

      if (line.equals("portrait")) {
        portrait = true;
      } else {
        portrait = false;
      }

      //ISO-8859-9; Char Code
      line = (((br.readLine()).split(";"))[0]).trim(); //9
      CHARSET = line;

      Typeface SCTP = Typeface.DEFAULT;
      Typeface tpf = Typeface.DEFAULT;

      //Monospace; Font Family Name(Monospace, Serif, Sans_Serif ....)
      line = (((br.readLine()).split(";"))[0]).trim(); //10
      if (line.equals("Monospace")) {
        tpf = Typeface.MONOSPACE;
      } else if (line.equals("Monospaced")) {
        tpf = Typeface.MONOSPACE;
      } else if (line.equals("Serif")) {
        tpf = Typeface.SERIF;
      } else if (line.equals("Sans_Serif")) {
        tpf = Typeface.SANS_SERIF;
      } else if (line.equals("SansSerif")) {
        tpf = Typeface.SANS_SERIF;
      } else {
        tpf = Typeface.DEFAULT;
      }

      //0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)
      line = (((br.readLine()).split(";"))[0]).trim(); //11
      int num = Integer.parseInt(line);
      int style = Typeface.NORMAL;

      if (num  ==  0) {
        style = Typeface.NORMAL;
      } else if (num  ==  1) {
        style = Typeface.BOLD;
      } else if (num  ==  2) {
        style = Typeface.ITALIC;
      } else if (num  ==  3) {
        style = Typeface.BOLD_ITALIC;
      } else {
        style = Typeface.NORMAL;
      }

      SCTP = Typeface.create(tpf, style);
      g2d.setTypeface(SCTP);

      //24F; Text size
      line = (((br.readLine()).split(";"))[0]).trim(); //12
      float size = Float.parseFloat(line);
      g2d.setTextSize(size);

      //FFFFFF; BG color
      line = (((br.readLine()).split(";"))[0]).trim(); //13
      int bg = Color.parseColor(line);
      BG = bg;

      //000000; FG color
      line = (((br.readLine()).split(";"))[0]).trim(); //14
      int fg = Color.parseColor(line);
      g2d.setColor(fg);
      FG = fg;

      int argb = getBrightedColor(fg);//int argb = Color.argb(alpha, red, green, blue);
      fg2d.setColor(argb);

      //18F; xcoord
      line = (((br.readLine()).split(";"))[0]).trim(); //15
      float xcoord = Float.parseFloat(line);
      XCOORD = xcoord;
      ORIGXCOORD = XCOORD;

      //42F; ycoord
      line = (((br.readLine()).split(";"))[0]).trim(); //16
      float ycoord = Float.parseFloat(line);
      YCOORD = ycoord;
      ORIGYCOORD = YCOORD;

      //43F; space
      line = (((br.readLine()).split(";"))[0]).trim(); //17
      float space = Float.parseFloat(line);
      SPACE = space;

      //2F; graphics stroke valor
      line = (((br.readLine()).split(";"))[0]).trim(); //18
      float stroke = Float.parseFloat(line);
      BORDERPAINT.setStrokeWidth(stroke);

      //no; ALIGN-SCREEN(no-center)
      line = (((br.readLine()).split(";"))[0]).trim(); //19
      String iscenter = line;
      if (iscenter.equals("yes")) {
        isCentered = true;
      } else if (iscenter.equals("no")) {
        isCentered = false;
      } else {
        isCentered = false;
      }

      //roundrect-5F-10F-20F-20F-#FF0000; ect info(alt is, rect-5F-10F-#FF0000)
      line = (((br.readLine()).split(";"))[0]).trim(); //20
      String [] momis = line.split("-");
      if (momis !=  null) {
        int mlen = momis.length;
        if (mlen  ==  6) {
          isRound = true;
          float fx = Float.parseFloat(momis [1]);
          float fy = Float.parseFloat(momis [2]);
          rect = new RectF(fx, fy, fwidth-(fwidth/100F), fheight-(fheight/60F));
          AXISX = Float.parseFloat(momis [3]);
          AXISY = Float.parseFloat(momis [4]);
          int c = Color.parseColor(momis [5]);
          BORDERPAINT.setColor(c);
        } else if (mlen  ==  4) {
          isRound = false;
          float fx = Float.parseFloat(momis [1]);
          float fy = Float.parseFloat(momis [2]);
          rect = new RectF(fx, fy, fwidth-(fwidth/100F), fheight-(fheight/60F));
          int c = Color.parseColor(momis [3]);
          BORDERPAINT.setColor(c);
        } else {
          isRound = false;
          rect = new RectF(5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/5F));
          BORDERPAINT.setColor(0xFF000000);
        }
      } else {
        isRound = false;
        rect = new RectF(5F, 10F, fwidth-(fwidth/100F), fheight-(fheight/5F));
        BORDERPAINT.setColor(0xFF000000);
      }

      br.close();
      isr.close();
      is.close();
    } catch(IOException ioe) {
    } catch(NumberFormatException nfe) {}

    MAX = (AInfos.SAYFALEN)-(1);

    if (portrait) {
      AX = 0;
      AY = (height)-(height/6);

      BX = (width/2);
      BY = AY;
    } else {//landscape
      AX = 0;
      AY = (height)-(height/4)+3;

      BX = (width/2);
      BY = AY;
    }
  }
  //INIT END

  //CINIT START
  public void cinit(String text) {
    try {
      Reader isr = new StringReader(text);
      BufferedReader br = new BufferedReader(isr);

      Typeface SCTP = Typeface.DEFAULT;
      Typeface tpf = Typeface.DEFAULT;

      boolean rvalo = false;

      String xline = null;
      String line = null;

      //Monospace; Font Family Name(Monospace, Serif, Sans_Serif ....)
      xline = br.readLine();//1 //Font name line

      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //1
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      if (line.equalsIgnoreCase("Monospace")) {
        tpf = Typeface.MONOSPACE;
      } else if (line.equalsIgnoreCase("Monospaced")) {
        tpf = Typeface.MONOSPACE;
      } else if (line.equalsIgnoreCase("Serif")) {
        tpf = Typeface.SERIF;
      } else if (line.equalsIgnoreCase("Sans_Serif")) {
        tpf = Typeface.SANS_SERIF;
      } else if (line.equalsIgnoreCase("SansSerif")) {
        tpf = Typeface.SANS_SERIF;
      } else {
        tpf = Typeface.DEFAULT;
      }

      //0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)
      xline = br.readLine();//2
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //2
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      int num = 0;

      try {
        num = Integer.parseInt(line);
      } catch(NumberFormatException nfe) {
        num = 0;
      }

      int style = Typeface.NORMAL;

      if (num  ==  0) {
        style = Typeface.NORMAL;
      } else if (num  ==  1) {
        style = Typeface.BOLD;
      } else if (num  ==  2) {
        style = Typeface.ITALIC;
      } else if (num  ==  3) {
        style = Typeface.BOLD_ITALIC;
      } else {
        style = Typeface.NORMAL;
      }

      SCTP = Typeface.create(tpf, style);
      g2d.setTypeface(SCTP);

      //48F; Text size
      xline = br.readLine();//3
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //3
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      float size = 48f;

      try {
        size = Float.parseFloat(line);
      } catch(NumberFormatException nfe) {
        size = 48f;
      }

      g2d.setTextSize(size);

      //#FFFFFFFF; BG color
      xline = br.readLine();//4
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //4
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, true);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      int bg = BG;

      try {
        bg = Color.parseColor(line);
      } catch(IllegalArgumentException iae) {
        bg = Color.WHITE;
      }

      BG = bg;

      //#FF000000; FG color
      xline = br.readLine();//5
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //5
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, true);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      int fg = Color.BLACK;

      try {
        fg = Color.parseColor(line);
      } catch(IllegalArgumentException iae) {
        fg = Color.BLACK;
      }

      g2d.setColor(fg);
      FG = fg;

      int argb = getBrightedColor(fg);
      fg2d.setColor(argb);

      //86F; ycoord
      xline = br.readLine();//6
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //6
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      float ycoord = ORIGYCOORD;

      try {
        ycoord = Float.parseFloat(line);
      } catch(NumberFormatException nfe) {
        ycoord = ORIGYCOORD;
      }

      YCOORD = ycoord;
      ORIGYCOORD = YCOORD;

      //75F; space
      xline = br.readLine();//7
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //7
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      float space = 43f;

      try {
        space = Float.parseFloat(line);
      } catch(NumberFormatException nfe) {
        space = 43f;
      }

      SPACE = space;

      //2F; graphics stroke valor
      xline = br.readLine();//8
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //8
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      float stroke = 2f;

      try {
        stroke = Float.parseFloat(line);
      } catch(NumberFormatException nfe) {
        stroke = 2f;
      }

      BORDERPAINT.setStrokeWidth(stroke);

      //rect-16F-18F-#FFEF8888;(alt: roundrect-6F-12F-20F-20F-#DDFF00FF)
      xline = br.readLine();//9
      rvalo = isProblemLine(xline);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      line = ((xline.split(";"))[0]).trim(); //9
      line = line.replaceAll(" ", "");
      rvalo = isProblem(line, false);
      if (rvalo) {
        br.close();
        isr.close();
        return;
      }

      String [] momis = line.split("-");
      if (momis !=  null) {
        int mlen = momis.length;
        if (mlen  ==  6) {
          isRound = true;

          float fx = 0f;
          float fy = 0f;

          try {
            fx = Float.parseFloat(momis [1]);
            fy = Float.parseFloat(momis [2]);
            AXISX = Float.parseFloat(momis [3]);
            AXISY = Float.parseFloat(momis [4]);
            rect = new RectF(fx, fy, fwidth-(fwidth/100f), fheight-(fheight/60f));
          } catch(NumberFormatException nfe) {
            rect = new RectF(fx, fy, fwidth-(fwidth/100f), fheight-(fheight/60f));
          }

          try {
            int c = Color.parseColor(momis [5]);
            BORDERPAINT.setColor(c);
          } catch(IllegalArgumentException iae) {
            BORDERPAINT.setColor(Color.RED);
          }
        }
        else if (mlen  ==  4) {
          isRound = false;

          try {
            float fx = Float.parseFloat(momis [1]);
            float fy = Float.parseFloat(momis [2]);
            rect = new RectF(fx, fy, fwidth-(fwidth/100f), fheight-(fheight/60f));
          } catch(NumberFormatException nfe) {
            rect = new RectF(5f, 10f, fwidth-(fwidth/100f), fheight-(fheight/5f));
          }

          try {
            int c = Color.parseColor(momis [3]);
            BORDERPAINT.setColor(c);
          } catch(IllegalArgumentException iae) {
            BORDERPAINT.setColor(Color.RED);
          }
        }
        else {
          isRound = false;
          rect = new RectF(5f, 10f, fwidth-(fwidth/100f), fheight-(fheight/5f));
          BORDERPAINT.setColor(Color.BLACK);
        }
      } else {
        isRound = false;
        rect = new RectF(5f, 10f, fwidth-(fwidth/100f), fheight-(fheight/5f));
        BORDERPAINT.setColor(Color.BLACK);
      }

      br.close();
      isr.close();
    } catch(IOException ioe) {}
      catch(NumberFormatException nfe) {}
  }
  //CINIT END

  private final boolean isProblem(String metin, boolean renk) {
    if (metin  ==  null) return true;
    int metlen = metin.length();
    if (metlen<1) return true;

    if (renk) {
      if ((metlen !=  9) || !(metin.startsWith("#"))) {
        return true;
      }
    }

    return false;
  }

  private final boolean isProblemLine(String metin) {
    if (metin  ==  null) return true;
    int metlen = metin.length();
    if (metlen<2) return true;
    if ((metin.indexOf(";")) < 0) return true;

    return false;
  }

  private final void drawx() {
    Canvas canvas = null;

    try {
      canvas = holder.lockCanvas();
      synchronized(holder) {
        if (holder.getSurface().isValid()) {
          paintNuestraPantalla(width, height, canvas);//boya
        }
      }
    } finally {
      if (canvas !=  null) {
        holder.unlockCanvasAndPost(canvas);
      }
    }
  }//end drawx

  private final void paintNuestraPantalla(int width, int height, Canvas canvas) {
    renderAlign(width, height, bufferG2D);
    canvas.drawBitmap(bufferBimg, 0, 0, zg2d);
  }

  private final void setBitmapMe() {
    SINIFLAR = (AInfos.SAYFALAR [PGSYC]).getLines();
    SINIFLARLEN = SINIFLAR.length;
  }

  public void renderAlign(int xwidth, int xheight, Canvas canvas) {
    // Clear the canvas background
    canvas.drawColor(BG);

    // Render the boundary box based on configuration shape
    if (isRound) {
      canvas.drawRoundRect(rect, AXISX, AXISY, BORDERPAINT);
    } else {
      canvas.drawRect(rect, BORDERPAINT);
    }

    // Reset global formatting states at the start of every page refresh to prevent style leaking
    globalColorActive = FG; // Inherit your custom global FG foreground color
    globalBoldActive = false;
    globalItalicActive = false;
    globalUnderlineActive = false;

    YCOORD = ORIGYCOORD;

    // Reset and inherit configurations from your legacy baseline paint object
    textPaint.set(g2d);
    applyCurrentStyles();

    // Main rendering iteration loop for processing each textual row line-by-line
    for (int i = 0; i < SINIFLARLEN; i++) {
      String line = SINIFLAR[i][0];
      if (line  ==  null) continue;

      int cursor = 0;
      int len = line.length();
      currentWord.setLength(0);

      // --- NATIVE ALIGNMENT CENTER ENGINE ---
      // Zero-Garbage calculation: Measure total width of visible characters ONLY(omitting HTML tags)
      int visibleWidth = 0;
      int measureCursor = 0;
      while (measureCursor < len) {
        char mc = line.charAt(measureCursor);
        if (mc  ==  '<') {
          int mTagEnd = line.indexOf('>', measureCursor);
          if (mTagEnd  ==  -1) break;
          measureCursor = mTagEnd + 1;
          continue;
        }
        // Temporarily accumulate the width of the raw visible character
        visibleWidth +=  textPaint.measureText(line, measureCursor, measureCursor + 1);
        measureCursor++;
      }

      // Apply your exact native centering math formula based on calculated visible text boundaries
      float currentX  = (FNISF) -(visibleWidth / 2f);

      // ANTICIPATORY BOUNDARY PROTECTION: Intercept leading broken tag fragments
      int firstCloseTag = line.indexOf('>');
      int firstOpenTag = line.indexOf('<');

      if (firstCloseTag !=  -1 &&(firstOpenTag  ==  -1 || firstCloseTag < firstOpenTag)) {
        int inlineHexIdx = findHexStartPos(line, 0, firstCloseTag);
        if (inlineHexIdx !=  -1) {
          globalColorActive = parseHexColor(line, inlineHexIdx);
          } else {
          int inlineNamedColor = parseNamedColor(line, 0, firstCloseTag);
          if (inlineNamedColor !=  FG) {
            globalColorActive = inlineNamedColor;
          }
        }
        textPaint.setColor(globalColorActive);
        cursor = firstCloseTag + 1;
      }

      // Inner character scanning stream machine execution layer
      while (cursor < len) {
        char c = line.charAt(cursor);

        if (c  ==  '<') {
          if (currentWord.length() > 0) {
            canvas.drawText(currentWord, 0, currentWord.length(), currentX, YCOORD, textPaint);
            currentX +=  textPaint.measureText(currentWord, 0, currentWord.length());
            currentWord.setLength(0);
          }

          int tagEnd = line.indexOf('>', cursor);

          if (tagEnd  ==  -1) {
            if (line.regionMatches(cursor, "<b", 0, 2)) globalBoldActive = true;
            else if (line.regionMatches(cursor, "<i", 0, 2)) globalItalicActive = true;
            else if (line.regionMatches(cursor, "<u", 0, 2)) globalUnderlineActive = true;
            else if (line.regionMatches(cursor, "<font", 0, 5)) {
              int hexPos = findHexStartPos(line, cursor, len);
              if (hexPos !=  -1) globalColorActive = parseHexColor(line, hexPos);
            }
            break;
          }

          if (line.regionMatches(cursor, "<b>", 0, 3)) {
            globalBoldActive = true;
            textPaint.setFakeBoldText(true);
            } else if (line.regionMatches(cursor, "</b>", 0, 4)) {
            globalBoldActive = false;
            textPaint.setFakeBoldText(false);
          }  else if (line.regionMatches(cursor, "<i>", 0, 3)) {
            globalItalicActive = true;
            textPaint.setTextSkewX(-0.25f);
            } else if (line.regionMatches(cursor, "</i>", 0, 4)) {
            globalItalicActive = false;
            textPaint.setTextSkewX(0f);
          } else if (line.regionMatches(cursor, "<u>", 0, 3)) {
            globalUnderlineActive = true;
            textPaint.setUnderlineText(true);
            } else if (line.regionMatches(cursor, "</u>", 0, 4)) {
            globalUnderlineActive = false;
            textPaint.setUnderlineText(false);
          }
          // Updated font closing routine using your custom global FG variable explicitly
          else if (line.regionMatches(cursor, "</font>", 0, 7)) {
            globalColorActive = FG;
            textPaint.setColor(globalColorActive);
          }  else if (line.regionMatches(cursor, "<font", 0, 5)) {
            int hexPos = findHexStartPos(line, cursor, tagEnd);
            if (hexPos !=  -1) {
              globalColorActive = parseHexColor(line, hexPos);
            } else {
              globalColorActive = parseNamedColor(line, cursor, tagEnd);
            }
            textPaint.setColor(globalColorActive);
          }

          cursor = tagEnd + 1;
          continue;
        }

        currentWord.append(c);
        cursor++;
      }

      if (currentWord.length() > 0) {
        canvas.drawText(currentWord, 0, currentWord.length(), currentX, YCOORD, textPaint);
      }

      YCOORD += SPACE;
    }

    // Draw legacy lower footer metadata content strings to layout canvas
    SLINE = Integer.toString(PGSYC) + "/" + MAX;
    swc = fg2d.measureText(SLINE) / 2f;
    float footerX  = (FNISF) -(swc);

    canvas.drawText(SLINE, footerX, ZCOORD, fg2d);
  }

  /**
   * Synchronizes and applies cached formatting metrics onto target rendering Paint engine context.
   */
  private void applyCurrentStyles() {
    textPaint.setFakeBoldText(globalBoldActive);
    textPaint.setTextSkewX(globalItalicActive ? -0.25f : 0f);
    textPaint.setUnderlineText(globalUnderlineActive);
    textPaint.setColor(globalColorActive);
  }

  /**
   * Finds starting structural baseline offset index for 
   * Hexadecimal notation targets, omitting structural quotes.
   */
  private int findHexStartPos(String line, int start, int end) {
    int colorIdx = line.indexOf("color", start);
    if (colorIdx == -1 || colorIdx > end) return -1;

    int hashIdx = line.indexOf('#', colorIdx);
    if (hashIdx == -1 || hashIdx > end) return -1;

    for (int i = colorIdx + 5; i < hashIdx; i++) {
      char c = line.charAt(i);
      if (c != ' ' && c != '=' && c != '"' && c != '\'') {
        return -1; 
      }
    }

    return hashIdx + 1;
  }

  /**
   * Converts a 6-digit hex color format substring to an ARGB integer value without dynamic runtime heap allocation.
   */
  private int parseHexColor(String line, int startIndex) {
    if (startIndex + 6 > line.length()) return FG; // Fallback to custom global FG on overflow
      int colorResult = 0;
    for (int i = 0; i < 6; i++) {
      char c = line.charAt(startIndex + i);
      int digit = 0;
      if (c >=  '0' && c <=  '9')       digit = c - '0';
      else if (c >=  'a' && c <=  'f')  digit = c - 'a' + 10;
      else if (c >=  'A' && c <=  'F')  digit = c - 'A' + 10;
      else return FG;
      colorResult  = (colorResult << 4) | digit;
    }
    return 0xFF000000 | colorResult;
  }

  /**
   * Scans internal boundaries looking for common keyword 
   * naming descriptors mapping directly to specific hex integers.
   */
  private int parseNamedColor(String line, int start, int end) {
    int colorIdx = line.indexOf("color", start);
    if (colorIdx == -1 || colorIdx > end) return FG;

    int valueStart = colorIdx + 5;
    while (valueStart < end) {
      char c = line.charAt(valueStart);
      if (c == ' ' || c == '=' || c == '"' || c == '\'') {
        valueStart++;
      } else {
        break; 
      }
    }

    if (indexOfRegion(line, "red", valueStart, end))     return 0xFFFF0000;
    if (indexOfRegion(line, "green", valueStart, end))   return 0xFF00FF00;
    if (indexOfRegion(line, "blue", valueStart, end))    return 0xFF0000FF;
    if (indexOfRegion(line, "black", valueStart, end))   return 0xFF000000;
    if (indexOfRegion(line, "white", valueStart, end))   return 0xFFFFFFFF;
    if (indexOfRegion(line, "yellow", valueStart, end))  return 0xFFFFFF00;
    if (indexOfRegion(line, "cyan", valueStart, end))    return 0xFF00FFFF;
    if (indexOfRegion(line, "magenta", valueStart, end)) return 0xFFFF00FF;
    if (indexOfRegion(line, "gray", valueStart, end))    return 0xFF808080;
    if (indexOfRegion(line, "maroon", valueStart, end))  return 0xFF800000;
    if (indexOfRegion(line, "olive", valueStart, end))   return 0xFF808000;
    if (indexOfRegion(line, "purple", valueStart, end))  return 0xFF800080;

    return FG;
  }

  /**
   * Performance-safe substring region locator verifying key patterns inside explicitly defined bounding markers.
   */
  private boolean indexOfRegion(String line, String target, int start, int end) {
    int targetLen = target.length();
    for (int i = start; i <=  end - targetLen; i++) {
      if (line.regionMatches(i, target, 0, targetLen)) return true;
    }
    return false;
  }

  //@Override
  public boolean onTouchEvent(MotionEvent evt) {
    if ((evt.getAction()) !=  MotionEvent.ACTION_DOWN) return false;

    int x = (int)(evt.getX());
    int y = (int)(evt.getY());

    if ((x < NISF) &&(x >=  AX) &&(y > AY)) {//Exit
      if (activity !=  null) {
        try {
          PrintStream ps = new PrintStream(new FileOutputStream(new File(caretFileName)));
          ps.println(Integer.toString(PGSYC));
          ps.flush();
          ps.close();
        } catch(IOException ioe) {}

        activity.finishAndRemoveTask();
      }
    } else if ((x >=  NISF) &&(x <=  width) &&(y > BY)) {//PageNum
      if (pane !=  null) {
        pane.show();
      }
    } else if ((x <=  SETX) &&(y <=  SETY)) {
      if (setPane !=  null) {
        setPane.show();
      }
    } else if ((x >=  CPX) &&(y <=  CPY)) {
      if (cpPane !=  null) {
        cpPane.show();
      }
    } else if (x <=  NISF) {//<--
      --PGSYC;
      if (PGSYC < 0) PGSYC = 0;
      setBitmapMe();
      drawx();
    } else {//-->
      ++PGSYC;
      if (PGSYC > MAX) PGSYC = MAX;
      setBitmapMe();
      drawx();
    }

    return true;
  }

  //@Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

  //@Override
  public void surfaceCreated(SurfaceHolder holder) {
    setBitmapMe();
    drawx();
  }

  //@Override
  public void surfaceDestroyed(SurfaceHolder holder) {}

}
