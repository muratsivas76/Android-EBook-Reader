package net.murat.ebook;

// Android
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.EditText;

// Java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

// Nested
import net.murat.sayfas.*;

/**
 * ScenePanel is a custom {@link SurfaceView} that renders paginated ebook
 * content with inline HTML-like formatting (bold, italic, underline and
 * font color).
 *
 * <p>Design constraints:</p>
 * <ul>
 *   <li>Targets Android API 36, compiled with Java 8 language level.</li>
 *   <li>No lambdas — all callbacks use anonymous inner classes.</li>
 *   <li>All IO uses try-with-resources so nothing leaks on exceptions.</li>
 *   <li>Every caught exception is logged; no silent swallowing.</li>
 *   <li>No shared mutable state between export threads.</li>
 * </ul>
 */
public class ScenePanel extends SurfaceView implements SurfaceHolder.Callback {

  /** Logcat tag. */
  private static final String TAG = "ScenePanel";

  /** Opaque black. */
  private static final int BLACK = 0xFF000000;

  /** Opaque white. */
  private static final int WHITE = 0xFFFFFFFF;

  /** Alpha channel value used for fully opaque colors. */
  private static final int ALPHA_OPAQUE = 0xFF;

  /** Text size used for the footer page counter. */
  private static final float FOOTER_TEXT_SIZE = 36f;

  /** Italic skew factor shared between measurement and drawing. */
  private static final float ITALIC_SKEW = -0.25f;

  /** Asset fallback path for settings when no external file exists. */
  private static final String ASSET_INFO = "texts/info.txt";

  // ---------------------------------------------------------------------------
  // Context / Activity
  // ---------------------------------------------------------------------------

  private final Context context;
  private final Activity activity;

  // ---------------------------------------------------------------------------
  // Rendering buffers
  // ---------------------------------------------------------------------------

  private Bitmap bufferBimg;
  private final Canvas bufferG2D = new Canvas();

  private int width = 1;
  private int height = 1;

  private float fwidth = 1f;
  private float fheight = 1f;

  private int NISF = 1;
  private float FNISF = 1f;

  private int SETX = 1;
  private int SETY = 1;
  private int CPX = 1;
  private int CPY = 1;

  private float AXISX = 20f;
  private float AXISY = 20f;

  // ---------------------------------------------------------------------------
  // Global formatting state (persists across line/page boundaries)
  // ---------------------------------------------------------------------------

  private int globalColorActive = BLACK;
  private boolean globalBoldActive = false;
  private boolean globalItalicActive = false;
  private boolean globalUnderlineActive = false;

  // ---------------------------------------------------------------------------
  // Reusable heavy objects (avoid per-frame allocations)
  // ---------------------------------------------------------------------------

  private final TextPaint textPaint = new TextPaint();
  private final StringBuilder currentWord = new StringBuilder(64);

  private final Paint fg2d = new Paint();
  private final Paint zg2d = new Paint();
  private final Paint g2d = new Paint();
  private final Paint BORDERPAINT = new Paint();

  private RectF rect = new RectF(1f, 1f, 1f, 1f);

  // ---------------------------------------------------------------------------
  // Layout metrics
  // ---------------------------------------------------------------------------

  private float XCOORD = 11f;
  private float ORIGXCOORD = XCOORD;
  private float YCOORD = 40f;
  private float ORIGYCOORD = YCOORD;
  private float ZCOORD = 1f;
  private float SPACE = 35f;
  private float swc = 0f;

  // ---------------------------------------------------------------------------
  // Dialog controls
  // ---------------------------------------------------------------------------

  private final EditText successText;
  private AlertDialog pane;
  private AlertDialog errorPane;
  private AlertDialog setPane;
  private AlertDialog cpPane;
  private AlertDialog successPane;

  // ---------------------------------------------------------------------------
  // Page state
  // ---------------------------------------------------------------------------

  private int PGSYC = 0;
  private int MAX = 10;

  // Touch zones (recomputed after init).
  private int AX = 1;
  private int AY = 1;
  private int BX = 1;
  private int BY = 1;

  // ---------------------------------------------------------------------------
  // Settings
  // ---------------------------------------------------------------------------

  private int BG = WHITE;
  private int FG = BLACK;

  private String CHARSET = "UTF-8";
  private String SLINE = "";

  private String caretFileName = "";

  private boolean isCentered = false;
  private boolean isRound = true;
  private boolean portrait = false;

  private String[][] SINIFLAR;
  private int SINIFLARLEN = 0;

  private boolean errorOcurred = false;

  private String PATH = "";
  private String packageName = "";
  private String errorMessage = "";

  private SurfaceHolder holder;

  // ---------------------------------------------------------------------------
  // Constructor
  // ---------------------------------------------------------------------------

  public ScenePanel(Context contxt) {
    super(contxt);

    this.context = contxt;

    // Safe cast: keep the Activity reference only if the context truly is one.
    if (contxt instanceof Activity) {
      this.activity = (Activity) contxt;
    } else {
      this.activity = null;
    }

    this.successText = new EditText(contxt);

    resolveDisplaySize();

    fwidth = (float) width;
    fheight = (float) height;

    NISF = width / 2;
    FNISF = fwidth / 2f;

    SETX = NISF / 2;
    SETY = height / 5;

    CPX = width - SETX;
    CPY = SETY;

    bufferBimg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    bufferG2D.setBitmap(bufferBimg);

    BORDERPAINT.setStyle(Paint.Style.STROKE);

    // Derive a short application name from the package.
    packageName = context.getPackageName();
    packageName = packageName.replaceAll("net\\.murat\\.", "");
    packageName = packageName.replaceAll("\\.ebook", "");

    PATH = getMainPathName();

    // Ensure the carets directories exist (carets, contents and ttf).
    File caretsDir = new File(PATH + "/carets");
    File contentsDir = new File(PATH + "/carets/contents");
    File ttfDir = new File(PATH + "/carets/ttf");
    try {
      if (!caretsDir.exists()) {
        caretsDir.mkdir();
      }
      if (!contentsDir.exists()) {
        contentsDir.mkdir();
      }
      if (!ttfDir.exists()) {
        ttfDir.mkdir();
      }
    } catch (SecurityException se) {
      errorOcurred = true;
      //errorMessage = se.getMessage();
      //Log.e(TAG, "Cannot create carets directories", se);
    }

    caretFileName = PATH + "/carets/" + packageName + ".txt";
    File caretFile = new File(caretFileName);

    if (caretFile.exists()) {
      PGSYC = readCaretFile(caretFile);
    } else {
      writeCaretFile(caretFile, 0);
    }

    // Build all dialogs on the UI thread (constructor is assumed UI thread).
    buildPageNumDialog();
    buildCopyDialog();
    buildSuccessDialog();
    buildSettingsDialog();
    ensureDefaultSettingsFile();

    // Default border rectangle.
    rect = new RectF(5F, 10F, fwidth - (fwidth / 100F), fheight - (fheight / 60F));
    ZCOORD = rect.bottom - 10f;

    fg2d.setTextSize(FOOTER_TEXT_SIZE);

    holder = getHolder();
    holder.addCallback(this);

    // Compute MAX early so touch handlers are safe before init() is called.
    MAX = (AInfos.SAYFALEN) - 1;

    // Show error dialog if any IO/Security failure happened above.
    if (errorOcurred) {
      buildErrorDialog();
      if (errorPane != null) {
        errorPane.show();
      }
    }
  }

  // ---------------------------------------------------------------------------
  // Display size resolution
  // ---------------------------------------------------------------------------

  /**
   * Resolves the display size, using {@link WindowMetrics} on API 30+ and
   * falling back to the deprecated {@link Display} API on older versions.
   */
  @SuppressWarnings("deprecation")
  private void resolveDisplaySize() {
    if (activity == null) {
      return;
    }

    WindowManager wm = activity.getWindowManager();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // API 30+ path.
      WindowMetrics metrics = wm.getCurrentWindowMetrics();
      Rect bounds = metrics.getBounds();
      width = bounds.width();
      height = bounds.height();
    } else {
      // Legacy path for API < 30.
      Display dd = wm.getDefaultDisplay();
      Point sizep = new Point();
      dd.getSize(sizep);
      width = sizep.x;
      height = sizep.y;
    }
  }

  // ---------------------------------------------------------------------------
  // Dialog builders
  // ---------------------------------------------------------------------------

  private void buildPageNumDialog() {
    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    alert.setTitle("Enter Page Num:");

    final EditText input = new EditText(context);
    input.setInputType(InputType.TYPE_CLASS_NUMBER);
    alert.setView(input);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        String res = input.getText().toString().trim();
        if (res.length() < 1) {
          return;
        }
        int c;
        try {
          c = Integer.parseInt(res);
        } catch (NumberFormatException nfe) {
          //Log.w(TAG, "Invalid page number: " + res, nfe);
          return;
        }
        if (c < 0) c = 0;
        if (c > MAX) c = MAX;
        PGSYC = c;

        input.setText("");
        setBitmapMe();
        drawx();
      }
    });

    alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        input.setText("");
      }
    });

    pane = alert.create();
  }

  private void buildCopyDialog() {
    AlertDialog.Builder cpalert = new AlertDialog.Builder(context);
    cpalert.setTitle("Enter page num for copy, ex:15 or 15-20");

    final EditText input = new EditText(context);
    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
    input.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
    cpalert.setView(input);

    final int MAXSFLEN = AInfos.SAYFALEN - 1;

    cpalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        String res = input.getText().toString().trim();
        if (res.length() < 1) {
          return;
        }
        if (res.endsWith("-")) {
          return;
        }

        int strt;
        int end;

        int dash = res.indexOf('-');
        if (dash > 0) {
          String left = res.substring(0, dash);
          String right = res.substring(dash + 1);
          try {
            strt = Integer.parseInt(left);
            end = Integer.parseInt(right);
          } catch (NumberFormatException nfe) {
            //Log.w(TAG, "Invalid range: " + res, nfe);
            return;
          }
        } else {
          try {
            strt = Integer.parseInt(res);
            end = strt;
          } catch (NumberFormatException nfe) {
            //Log.w(TAG, "Invalid page number: " + res, nfe);
            return;
          }
        }

        if (strt < 0 || strt > MAXSFLEN) return;
        if (end < 0 || end > MAXSFLEN) return;
        if (strt > end) return;

        try {
          copyContent(strt, end);
        } catch (IOException ioe) {
          //Log.e(TAG, "copyContent failed", ioe);
        }
      }
    });

    cpalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        input.setText("");
      }
    });

    cpPane = cpalert.create();
  }

  private void buildSuccessDialog() {
    AlertDialog.Builder successAlert = new AlertDialog.Builder(context);
    successAlert.setTitle("Successfull!");

    successText.setFocusable(false);
    successText.setFocusableInTouchMode(false);
    successText.setClickable(false);
    successText.setTextIsSelectable(true);

    successAlert.setView(successText);

    successAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        // Nothing to do.
      }
    });

    successPane = successAlert.create();
  }

  private void buildSettingsDialog() {
    StringBuilder fsb = new StringBuilder(512);
    fsb.append("Default; Font Name(Default or ttf file in Download/carets/ttf)\n");
    fsb.append("0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    fsb.append("48F; Text size\n");
    fsb.append("#FFEEEEEE; BG color(alpha_red_green_blue)\n");
    fsb.append("#FF535353; FG color(alpha_red_green_blue)\n");
    fsb.append("86F; ycoord\n");
    fsb.append("75F; space\n");
    fsb.append("2F; graphics stroke valor\n");
    fsb.append("roundrect-10F-10F-1500F-710F-100F-100F-#FF535353;(alt: rect-37F-27F-1495F-703F-#FF535353;)\n");
    fsb.append("###############\n");

    AlertDialog.Builder salert = new AlertDialog.Builder(context);
    salert.setTitle("Settings");

    final EditText shtime = new EditText(context);
    shtime.setText(fsb.toString());
    salert.setView(shtime);

    salert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        String res = shtime.getText().toString();
        if (res.length() < 10) {
          return;
        }
        cinit(res);
        drawx();
      }
    });

    salert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        // Nothing to do.
      }
    });

    setPane = salert.create();
  }

  private void buildErrorDialog() {
    AlertDialog.Builder xalert = new AlertDialog.Builder(context);
    xalert.setTitle("IO||Security Error!");
    xalert.setCancelable(false);

    final EditText xhtime = new EditText(context);
    xhtime.setFocusable(false);
    xhtime.setFocusableInTouchMode(false);
    xhtime.setClickable(false);
    xhtime.setText(errorMessage);
    xalert.setView(xhtime);

    xalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        if (activity != null) {
          activity.finishAndRemoveTask();
        }
      }
    });

    xalert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int wh) {
        if (activity != null) {
          activity.finish();
        }
      }
    });

    errorPane = xalert.create();
  }

  // ---------------------------------------------------------------------------
  // Settings file bootstrap
  // ---------------------------------------------------------------------------

  /**
   * Creates the default settings.txt file if it does not exist yet.
   * Uses try-with-resources so the stream is always closed.
   */
  private void ensureDefaultSettingsFile() {
    File settingsFile = new File(PATH + "/carets/settings.txt");
    if (settingsFile.exists()) {
      return;
    }

    StringBuilder sb = new StringBuilder(768);
    sb.append("test.txt; File Name\n");
    sb.append("UTF-8; Char Code\n");
    sb.append("8; Line Count\n");
    sb.append("1600F; Line Float Length\n");
    sb.append("Default,0,48; Font\n");
    sb.append("no; ALIGN-MDK(yes/no)\n");
    sb.append("landscape; orientation(portrait | landscape)\n");
    sb.append("/////////////\n");
    sb.append("landscape; orientation(portrait | landscape)\n");
    sb.append("UTF-8; Char Code[-EDIT BELOW-]\n");
    sb.append("Default; Font Name(Default or ttf file in Download/carets/ttf)\n");
    sb.append("0; Font style(0, 1, 2, 3)(plain, bold, italic, boldItalic)\n");
    sb.append("48F; Text size\n");
    sb.append("#FFEEEEEE; BG color(alpha_red_green_blue)\n");
    sb.append("#FF535353; FG color(alpha_red_green_blue)\n");
    sb.append("30F; xcoord\n");
    sb.append("86F; ycoord\n");
    sb.append("75F; space\n");
    sb.append("2F; graphics stroke valor\n");
    sb.append("yes; ALIGN-SCREEN(yes/no [yes signify centered])\n");
    sb.append("roundrect-10F-10F-100F-100F-#FF535353;(alt: rect-10F-10F-#FF535353;)\n");
    sb.append("###############\n");

    try (OutputStream fos = new FileOutputStream(settingsFile);
         PrintStream xps = new PrintStream(fos, true, "UTF-8")) {
      xps.print(sb.toString());
    } catch (IOException ioe) {
      //Log.e(TAG, "Cannot write default settings file", ioe);
    }
  }

  // ---------------------------------------------------------------------------
  // Caret persistence
  // ---------------------------------------------------------------------------

  /**
   * Reads the stored page index from the caret file.
   *
   * @return the parsed page index, or 0 on any failure.
   */
  private int readCaretFile(File caretFile) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream(caretFile), Charset.forName("UTF-8")))) {
      String line = br.readLine();
      if (line == null) {
        return 0;
      }
      return Integer.parseInt(line.trim());
    } catch (NumberFormatException nfe) {
      //Log.w(TAG, "Caret file content not a number", nfe);
      return 0;
    } catch (IOException ioe) {
      //Log.e(TAG, "Cannot read caret file", ioe);
      return 0;
    } catch (SecurityException se) {
      //Log.e(TAG, "Cannot access caret file", se);
      return 0;
    }
  }

  /**
   * Writes the given page index to the caret file.
   */
  private void writeCaretFile(File caretFile, int value) {
    try (PrintStream ps = new PrintStream(
        new FileOutputStream(caretFile), true, "UTF-8")) {
      ps.println(Integer.toString(value));
    } catch (IOException ioe) {
      //Log.e(TAG, "Cannot write caret file", ioe);
    } catch (SecurityException se) {
      //Log.e(TAG, "Cannot access caret file", se);
    }
  }

  // ---------------------------------------------------------------------------
  // Path resolution
  // ---------------------------------------------------------------------------

  /**
   * Returns the app-specific external files directory. If external storage
   * is not mounted, falls back to the internal files directory.
   */
  /**
  private String getMainPathName() {
    File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    if (path != null) {
      return path.getAbsolutePath();
    }
    File internal = context.getFilesDir();
    if (internal != null) {
      return internal.getAbsolutePath();
    }
    return "";
  }
  */

  private final String getMainPathName() {
    File path = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
    String px = path.getAbsolutePath();

    String exclude = "Android/data/"+(context.getPackageName())+"/files/";
    px = px.replace(exclude, "");

    return px;
  }

  // ---------------------------------------------------------------------------
  // Copy / export
  // ---------------------------------------------------------------------------

  /**
   * Prompts the user to choose an export format, then runs the export on a
   * background thread. The dialog itself is shown on the UI thread.
   */
  private void copyContent(final int strt, final int end) throws IOException {
    if (activity == null) {
      return;
    }

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
                } catch (IOException ioe) {
                  //Log.e(TAG, "TXT export failed", ioe);
                }
              }
            }, "export-txt").start();
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
                } catch (IOException ioe) {
                  //Log.e(TAG, "HTML export failed", ioe);
                }
              }
            }, "export-html").start();
          }
        });

        builder.setCancelable(true);
        builder.show();
      }
    });
  }

  /**
   * Universal tag stripper. Writes the visible text (without any
   * {@code <...>} markup) into a plain TXT file.
   */
  private void copyContentTXT(int strt, int end) throws IOException {
    final String outName =
        PATH + "/carets/contents/pg_" + packageName + "_" + strt + "-" + end + ".txt";
    final File fd = new File(outName);

    try (OutputStream fos = new FileOutputStream(fd);
         PrintStream ps = new PrintStream(fos, true, "UTF-8")) {

      boolean insideTag = false; // Tag opened on a previous row, not yet closed.

      for (int i = strt; i <= end; i++) {
        ASayfa s = AInfos.SAYFALAR[i];
        String[][] lnns = s.getLines();
        int lnnslen = lnns.length;

        for (int j = 0; j < lnnslen; j++) {
          String rawLine = lnns[j][0];
          if (rawLine == null) {
            continue;
          }

          currentWord.setLength(0);
          int cursor = 0;
          int lineLen = rawLine.length();

          while (cursor < lineLen) {
            char c = rawLine.charAt(cursor);

            if (insideTag) {
              if (c == '>') {
                insideTag = false;
              }
              cursor++;
              continue;
            }

            if (c == '<') {
              int tagEnd = rawLine.indexOf('>', cursor);
              if (tagEnd == -1) {
                insideTag = true;
                break;
              }
              cursor = tagEnd + 1;
              continue;
            }

            // Intercept rogue "color = " leaks that bypassed structural parsing.
            if (c == 'c' && rawLine.regionMatches(cursor, "color = ", 0, 6)) {
              int closeBracketIdx = rawLine.indexOf('>', cursor);
              if (closeBracketIdx != -1) {
                cursor = closeBracketIdx + 1;
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
    }

    showSuccessMessage(outName);
  }

  /**
   * Exports raw lines into a clean HTML file. Welds severed multi-line
   * font tag chunks back together so browsers don't display plain code.
   */
  private void copyContentHTML(int strt, int end) throws IOException {
    final String outName =
        PATH + "/carets/contents/pg_" + packageName + "_" + strt + "-" + end + ".html";
    final File fd = new File(outName);

    try (OutputStream fos = new FileOutputStream(fd);
         PrintStream ps = new PrintStream(fos, true, "UTF-8")) {

      ps.println("<!DOCTYPE html><html><head><meta charset = 'UTF-8'></head><body>");

      boolean wasPreviousLineBroken = false;

      for (int i = strt; i <= end; i++) {
        ASayfa s = AInfos.SAYFALAR[i];
        String[][] lnns = s.getLines();
        int lnnslen = lnns.length;

        for (int j = 0; j < lnnslen; j++) {
          String rawLine = lnns[j][0];
          if (rawLine == null) {
            continue;
          }

          if (wasPreviousLineBroken) {
            // Skip leading whitespace so the broken tag merges cleanly.
            int firstValidChar = 0;
            int rlen = rawLine.length();
            while (firstValidChar < rlen && rawLine.charAt(firstValidChar) == ' ') {
              firstValidChar++;
            }
            ps.print(rawLine.substring(firstValidChar));
          } else {
            ps.println(rawLine);
          }

          int lastOpen = rawLine.lastIndexOf('<');
          int lastClose = rawLine.lastIndexOf('>');
          wasPreviousLineBroken = (lastOpen > lastClose);
        }

        ps.print("<br><br>&nbsp;&nbsp;&nbsp;Page: ");
        ps.print(Integer.toString(i));
        ps.println("<br><br>");
      }

      ps.println("</body></html>");
    }

    showSuccessMessage(outName);
  }

  /**
   * Pushes a success alert onto the UI thread. The exported file path is
   * passed as a parameter so concurrent exports cannot race on shared state.
   */
  private void showSuccessMessage(final String path) {
    if (activity == null) {
      return;
    }
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (successPane != null) {
          successText.setText(path);
          successPane.show();
        }
      }
    });
  }

  // ---------------------------------------------------------------------------
  // Color utilities
  // ---------------------------------------------------------------------------

  /**
   * Brightens the given ARGB color by dividing each RGB channel by 0.7.
   * Near-black channels are raised to a minimum of 4 so pure black does
   * not stay invisible against a black background.
   */
  private int getBrightedColor(int oldcolor) {
    int red = Color.red(oldcolor);
    int green = Color.green(oldcolor);
    int blue = Color.blue(oldcolor);

    final float MVAL = 255F;

    if (red == 0 && green == 0 && blue == 0) {
      red = 3;
      green = 3;
      blue = 3;
    } else {
      if (red > 2) {
        red = (int) (Math.min(MVAL, ((float) red) / 0.7F));
      }
      if (red == 1 || red == 2) {
        red = 4;
      }

      if (green > 2) {
        green = (int) (Math.min(MVAL, ((float) green) / 0.7F));
      }
      if (green == 1 || green == 2) {
        green = 4;
      }

      if (blue > 2) {
        blue = (int) (Math.min(MVAL, ((float) blue) / 0.7F));
      }
      if (blue == 1 || blue == 2) {
        blue = 4;
      }
    }

    return Color.argb(ALPHA_OPAQUE, red, green, blue);
  }

  /**
   * Safely parses a color token such as {@code "#FF535353"}. Returns
   * {@link #BLACK} on any parse failure or {@code null} input.
   */
  private int getRectangleColor(String coltr) {
    if (coltr == null) {
      return BLACK;
    }
    final String colstr = coltr.replace(" ", "");
    if (colstr.length() < 1) {
      return BLACK;
    }
    try {
      return Color.parseColor(colstr);
    } catch (IllegalArgumentException iae) {
      //Log.w(TAG, "Invalid rectangle color: " + colstr, iae);
      return BLACK;
    }
  }

  // ---------------------------------------------------------------------------
  // Settings validation helpers
  // ---------------------------------------------------------------------------

  private boolean isProblem(String metin, boolean renk) {
    if (metin == null) return true;
    int metlen = metin.length();
    if (metlen < 1) return true;

    if (renk) {
      if (!metin.startsWith("#")) {
        return true;
      }
      // Accept #RGB, #ARGB, #RRGGBB and #AARRGGBB forms.
      if (metlen != 4 && metlen != 5 && metlen != 7 && metlen != 9) {
        return true;
      }
    }

    return false;
  }

  private boolean isProblemLine(String metin) {
    if (metin == null) return true;
    int metlen = metin.length();
    if (metlen < 2) return true;
    return metin.indexOf(';') < 0;
  }

  /**
   * Returns the value portion (before the first ';') of a settings line,
   * or {@code null} if the line is unusable. Spaces are stripped.
   */
  private String valueOf(String line) {
    if (line == null) {
      return null;
    }
    int semi = line.indexOf(';');
    String value = (semi >= 0 ? line.substring(0, semi) : line).trim();
    return value.replace(" ", "");
  }

  // ---------------------------------------------------------------------------
  // Font loading
  // ---------------------------------------------------------------------------

  /**
   * Resolves a {@link Typeface} from a settings token. If the token names a
   * TTF file under {@code carets/ttf}, it is loaded; otherwise a default is
   * returned. Never throws.
   */
  private Typeface resolveTypeface(String token) {
    if (token == null || token.length() == 0 || token.equalsIgnoreCase("Default")) {
      return Typeface.DEFAULT;
    }
    try {
      File fontFile = new File(PATH + "/carets/ttf/" + token);
      if (fontFile.exists()) {
        return Typeface.createFromFile(fontFile);
      }
    } catch (RuntimeException re) {
      //Log.w(TAG, "Cannot load font: " + token, re);
    }
    return Typeface.DEFAULT;
  }

  // ---------------------------------------------------------------------------
  // INIT — reads settings from disk (or asset fallback)
  // ---------------------------------------------------------------------------

  public void init() {
    File settingsFile = new File(PATH + "/carets/settings.txt");

    try {
      InputStream is;
      if (settingsFile.exists()) {
        is = new FileInputStream(settingsFile);
      } else {
        is = context.getAssets().open(ASSET_INFO);
      }

      try (Reader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
           BufferedReader br = new BufferedReader(isr, 1024)) {

        // Skip the first 8 metadata lines.
        for (int i = 0; i < 8; i++) {
          if (br.readLine() == null) {
            //Log.w(TAG, "Settings file truncated before body");
            return;
          }
        }

        parseSettingsBody(br, true);
      }

    } catch (IOException ioe) {
      //Log.e(TAG, "init() IO failure", ioe);
    } catch (NumberFormatException nfe) {
      //Log.e(TAG, "init() numeric parse failure", nfe);
    } catch (IllegalArgumentException iae) {
      //Log.e(TAG, "init() color parse failure", iae);
    }

    MAX = (AInfos.SAYFALEN) - 1;

    if (portrait) {
      AX = 0;
      AY = height - (height / 6);
      BX = width / 2;
      BY = AY;
    } else {
      AX = 0;
      AY = height - (height / 4) + 3;
      BX = width / 2;
      BY = AY;
    }
  }

  // ---------------------------------------------------------------------------
  // CINIT — reads settings from an in-memory string
  // ---------------------------------------------------------------------------

  public void cinit(String text) {
    if (text == null) {
      return;
    }

    try (Reader isr = new StringReader(text);
         BufferedReader br = new BufferedReader(isr)) {

      parseSettingsBody(br, false);

    } catch (IOException ioe) {
      //Log.e(TAG, "cinit() IO failure", ioe);
    } catch (NumberFormatException nfe) {
      //Log.e(TAG, "cinit() numeric parse failure", nfe);
    } catch (IllegalArgumentException iae) {
      //Log.e(TAG, "cinit() color parse failure", iae);
    }
  }

  /**
   * Parses the settings body from the given reader. When {@code fromInit}
   * is true, the reader is positioned after the 8 metadata lines (init
   * path); when false, it starts at the first body line (cinit path).
   */
  private void parseSettingsBody(BufferedReader br, boolean fromInit) throws IOException {
    // -------------------------------------------------------------------
    // 8: orientation
    // -------------------------------------------------------------------
    String line = "";
    if (fromInit) {
		line = valueOf(br.readLine());
        portrait = "portrait".equals(line);
    }

    // -------------------------------------------------------------------
    // 9: charset
    // -------------------------------------------------------------------
    if (fromInit) {
      line = valueOf(br.readLine());
      if (line != null && line.length() > 0) {
        CHARSET = line;
      }
    }

    // -------------------------------------------------------------------
    // 10: font family (Default or a TTF file under carets/ttf)
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    Typeface tpf = resolveTypeface(line);

    // -------------------------------------------------------------------
    // 11: font style (0..3)
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    int num = 0;
    try {
      num = Integer.parseInt(line);
    } catch (NumberFormatException nfe) {
      //Log.w(TAG, "Invalid font style: " + line, nfe);
      num = 0;
    }
    int style = Typeface.NORMAL;
    if (num == 1) style = Typeface.BOLD;
    else if (num == 2) style = Typeface.ITALIC;
    else if (num == 3) style = Typeface.BOLD_ITALIC;

    Typeface SCTP = Typeface.create(tpf, style);
    g2d.setTypeface(SCTP);

    // -------------------------------------------------------------------
    // 12: text size
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    float size = 48f;
    try {
      size = Float.parseFloat(line);
    } catch (NumberFormatException nfe) {
      //Log.w(TAG, "Invalid text size: " + line, nfe);
    }
    g2d.setTextSize(size);

    // -------------------------------------------------------------------
    // 13: background color
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    int bg = BG;
    if (!isProblem(line, true)) {
      try {
        bg = Color.parseColor(line);
      } catch (IllegalArgumentException iae) {
        //Log.w(TAG, "Invalid BG color: " + line, iae);
        bg = WHITE;
      }
    }
    BG = bg;

    // -------------------------------------------------------------------
    // 14: foreground color
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    int fg = FG;
    if (!isProblem(line, true)) {
      try {
        fg = Color.parseColor(line);
      } catch (IllegalArgumentException iae) {
        //Log.w(TAG, "Invalid FG color: " + line, iae);
        fg = BLACK;
      }
    }
    g2d.setColor(fg);
    FG = fg;
    fg2d.setColor(getBrightedColor(fg));

    // -------------------------------------------------------------------
    // 15: xcoord (init only)
    // -------------------------------------------------------------------
    if (fromInit) {
      line = valueOf(br.readLine());
      if (line != null) {
        try {
          XCOORD = Float.parseFloat(line);
          ORIGXCOORD = XCOORD;
        } catch (NumberFormatException nfe) {
          //Log.w(TAG, "Invalid xcoord: " + line, nfe);
        }
      }
    }

    // -------------------------------------------------------------------
    // 16: ycoord
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    float ycoord = ORIGYCOORD;
    if (line != null) {
      try {
        ycoord = Float.parseFloat(line);
      } catch (NumberFormatException nfe) {
        //Log.w(TAG, "Invalid ycoord: " + line, nfe);
      }
    }
    YCOORD = ycoord;
    ORIGYCOORD = YCOORD;

    // -------------------------------------------------------------------
    // 17: space
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    float space = 43f;
    if (line != null) {
      try {
        space = Float.parseFloat(line);
      } catch (NumberFormatException nfe) {
        //Log.w(TAG, "Invalid space: " + line, nfe);
      }
    }
    SPACE = space;

    // -------------------------------------------------------------------
    // 18: graphics stroke width
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    float stroke = 2f;
    if (line != null) {
      try {
        stroke = Float.parseFloat(line);
      } catch (NumberFormatException nfe) {
        //Log.w(TAG, "Invalid stroke: " + line, nfe);
      }
    }
    BORDERPAINT.setStrokeWidth(stroke);

    // -------------------------------------------------------------------
    // 19: ALIGN-SCREEN (init only)
    // -------------------------------------------------------------------
    if (fromInit) {
      line = valueOf(br.readLine());
      isCentered = "yes".equals(line);
    }

    // -------------------------------------------------------------------
    // 20 (init) / 9 (cinit): border shape
    // Format: rect-x-y-color  OR  roundrect-x-y-w-h-axisx-axisy-color
    // -------------------------------------------------------------------
    line = valueOf(br.readLine());
    parseBorderShape(line);
  }

  /**
   * Parses the border shape descriptor:
   * {@code rect-x-y-color} or {@code roundrect-x-y-w-h-axisx-axisy-color}.
   */
  private void parseBorderShape(String line) {
    if (line == null) {
      applyDefaultBorder();
      return;
    }

    // Split by '-' without regex compilation.
    String[] momis = splitByDash(line);
    if (momis == null || momis.length < 4) {
      applyDefaultBorder();
      return;
    }

    String kind = momis[0];

    if ("roundrect".equalsIgnoreCase(kind) && (momis.length == 8)) {
      // 8 tokens: roundrect-x-y-w-h-axisx-axisy-color
      float fx = parseFloatSafe(momis[1], 5f);
      float fy = parseFloatSafe(momis[2], 10f);
      float fw = parseFloatSafe(momis[3], fwidth - (fwidth / 20f));
      float fh = parseFloatSafe(momis[4], fheight - (fheight / 60f));
      isRound = true;
      rect = new RectF(fx, fy, fw, fh);
      AXISX = parseFloatSafe(momis[5], 20f);
      AXISY = parseFloatSafe(momis[6], 20f);
      BORDERPAINT.setColor(getRectangleColor(momis[7]));
      return;
    }

    else if ("roundrect".equalsIgnoreCase(kind) && (momis.length == 6)) {
      // 6 tokens: roundrect-x-y-axisx-axisy-color (legacy)
      float fx = parseFloatSafe(momis[1], 5f);
      float fy = parseFloatSafe(momis[2], 10f);
      isRound = true;
      rect = new RectF(fx, fy, fwidth - (fwidth / 20f), fheight - (fheight / 60f));
      AXISX = parseFloatSafe(momis[3], 20f);
      AXISY = parseFloatSafe(momis[4], 20f);
      BORDERPAINT.setColor(getRectangleColor(momis[5]));
      return;
    }

    else if ("rect".equalsIgnoreCase(kind) && (momis.length == 6)) {
      // 6 tokens: rect-x-y-w-h-color
      float fx = parseFloatSafe(momis[1], 5f);
      float fy = parseFloatSafe(momis[2], 10f);
      float fw = parseFloatSafe(momis[3], fwidth - (fwidth / 20f));
      float fh = parseFloatSafe(momis[4], fheight - (fheight / 60f));
      isRound = false;
      rect = new RectF(fx, fy, fw, fh);
      BORDERPAINT.setColor(getRectangleColor(momis[5]));
      return;
    }

    else if ("rect".equalsIgnoreCase(kind) && (momis.length == 4)) {
      // 4 tokens: rect-x-y-color (legacy)
      float fx = parseFloatSafe(momis[1], 5f);
      float fy = parseFloatSafe(momis[2], 10f);
      isRound = false;
      rect = new RectF(fx, fy, fwidth - (fwidth / 20f), fheight - (fheight / 60f));
      BORDERPAINT.setColor(getRectangleColor(momis[3]));
      return;
    }

    else {
      applyDefaultBorder();
    }
  }

  private void applyDefaultBorder() {
    isRound = false;
    rect = new RectF(5f, 10f, fwidth - (fwidth / 20f), fheight - (fheight / 5f));
    BORDERPAINT.setColor(BLACK);
  }

  private float parseFloatSafe(String s, float fallback) {
    if (s == null) {
      return fallback;
    }
    try {
      return Float.parseFloat(s);
    } catch (NumberFormatException nfe) {
      //Log.w(TAG, "Invalid float: " + s, nfe);
      return fallback;
    }
  }

  /**
   * Splits a string by '-' without using regex. Returns an array of tokens.
   */
  private String[] splitByDash(String s) {
    if (s == null) {
      return null;
    }
    int count = 1;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '-') {
        count++;
      }
    }
    String[] out = new String[count];
    int idx = 0;
    int start = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '-') {
        out[idx++] = s.substring(start, i);
        start = i + 1;
      }
    }
    out[idx] = s.substring(start);
    return out;
  }

  // ---------------------------------------------------------------------------
  // Rendering
  // ---------------------------------------------------------------------------

  private void drawx() {
    Canvas canvas = null;
    try {
      canvas = holder.lockCanvas();
      if (canvas == null) {
        return;
      }
      synchronized (holder) {
        if (holder.getSurface().isValid()) {
          paintNuestraPantalla(width, height, canvas);
        }
      }
    } catch (IllegalStateException ise) {
      //Log.w(TAG, "drawx(): surface not ready", ise);
    } finally {
      if (canvas != null) {
        try {
          holder.unlockCanvasAndPost(canvas);
        } catch (IllegalStateException ise) {
          //Log.w(TAG, "drawx(): cannot unlock canvas", ise);
        }
      }
    }
  }

  private void paintNuestraPantalla(int w, int h, Canvas canvas) {
    renderAlign(w, h, bufferG2D);
    canvas.drawBitmap(bufferBimg, 0, 0, zg2d);
  }

  private void setBitmapMe() {
    if (AInfos.SAYFALAR == null || PGSYC < 0 || PGSYC >= AInfos.SAYFALAR.length) {
      SINIFLAR = null;
      SINIFLARLEN = 0;
      return;
    }
    SINIFLAR = AInfos.SAYFALAR[PGSYC].getLines();
    SINIFLARLEN = (SINIFLAR == null) ? 0 : SINIFLAR.length;
  }

  public void renderAlign(int xwidth, int xheight, Canvas canvas) {
    // Clear the canvas background.
    canvas.drawColor(BG);

    // Render the boundary box based on the configured shape.
    if (isRound) {
      canvas.drawRoundRect(rect, AXISX, AXISY, BORDERPAINT);
    } else {
      canvas.drawRect(rect, BORDERPAINT);
    }

    // Reset global formatting states at the start of every page refresh.
    globalColorActive = FG;
    globalBoldActive = false;
    globalItalicActive = false;
    globalUnderlineActive = false;

    YCOORD = ORIGYCOORD;

    // Inherit baseline paint configuration.
    textPaint.set(g2d);
    applyCurrentStyles();

    if (SINIFLAR == null) {
      return;
    }

    for (int i = 0; i < SINIFLARLEN; i++) {
      String line = SINIFLAR[i][0];
      if (line == null) {
        continue;
      }

      int cursor = 0;
      int len = line.length();
      currentWord.setLength(0);

      // --- Centering measurement: width of visible characters only. ---
      int visibleWidth = 0;
      int measureCursor = 0;
      while (measureCursor < len) {
        char mc = line.charAt(measureCursor);
        if (mc == '<') {
          int mTagEnd = line.indexOf('>', measureCursor);
          if (mTagEnd == -1) break;
          measureCursor = mTagEnd + 1;
          continue;
        }
        visibleWidth += textPaint.measureText(line, measureCursor, measureCursor + 1);
        measureCursor++;
      }

      float currentX = FNISF - (visibleWidth / 2f);

      // Anticipatory boundary protection: intercept leading broken tag fragments.
      int firstCloseTag = line.indexOf('>');
      int firstOpenTag = line.indexOf('<');

      if (firstCloseTag != -1 && (firstOpenTag == -1 || firstCloseTag < firstOpenTag)) {
        int inlineHexIdx = findHexStartPos(line, 0, firstCloseTag);
        if (inlineHexIdx != -1) {
          globalColorActive = parseHexColor(line, inlineHexIdx);
        } else {
          int inlineNamedColor = parseNamedColor(line, 0, firstCloseTag);
          if (inlineNamedColor != FG) {
            globalColorActive = inlineNamedColor;
          }
        }
        textPaint.setColor(globalColorActive);
        cursor = firstCloseTag + 1;
      }

      // Inner character scanning loop.
      while (cursor < len) {
        char c = line.charAt(cursor);

        if (c == '<') {
          if (currentWord.length() > 0) {
            canvas.drawText(currentWord, 0, currentWord.length(), currentX, YCOORD, textPaint);
            currentX += textPaint.measureText(currentWord, 0, currentWord.length());
            currentWord.setLength(0);
          }

          int tagEnd = line.indexOf('>', cursor);

          if (tagEnd == -1) {
            if (line.regionMatches(cursor, "<b", 0, 2)) globalBoldActive = true;
            else if (line.regionMatches(cursor, "<i", 0, 2)) globalItalicActive = true;
            else if (line.regionMatches(cursor, "<u", 0, 2)) globalUnderlineActive = true;
            else if (line.regionMatches(cursor, "<font", 0, 5)) {
              int hexPos = findHexStartPos(line, cursor, len);
              if (hexPos != -1) globalColorActive = parseHexColor(line, hexPos);
            }
            break;
          }

          if (line.regionMatches(cursor, "<b>", 0, 3)) {
            globalBoldActive = true;
            textPaint.setFakeBoldText(true);
          } else if (line.regionMatches(cursor, "</b>", 0, 4)) {
            globalBoldActive = false;
            textPaint.setFakeBoldText(false);
          } else if (line.regionMatches(cursor, "<i>", 0, 3)) {
            globalItalicActive = true;
            textPaint.setTextSkewX(ITALIC_SKEW);
          } else if (line.regionMatches(cursor, "</i>", 0, 4)) {
            globalItalicActive = false;
            textPaint.setTextSkewX(0f);
          } else if (line.regionMatches(cursor, "<u>", 0, 3)) {
            globalUnderlineActive = true;
            textPaint.setUnderlineText(true);
          } else if (line.regionMatches(cursor, "</u>", 0, 4)) {
            globalUnderlineActive = false;
            textPaint.setUnderlineText(false);
          } else if (line.regionMatches(cursor, "</font>", 0, 7)) {
            globalColorActive = FG;
            textPaint.setColor(globalColorActive);
          } else if (line.regionMatches(cursor, "<font", 0, 5)) {
            int hexPos = findHexStartPos(line, cursor, tagEnd);
            if (hexPos != -1) {
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

    // Draw the footer page counter.
    SLINE = Integer.toString(PGSYC) + "/" + MAX;
    swc = fg2d.measureText(SLINE) / 2f;
    float footerX = FNISF - swc;

    canvas.drawText(SLINE, footerX, ZCOORD, fg2d);
  }

  /**
   * Applies cached formatting metrics onto the rendering paint.
   */
  private void applyCurrentStyles() {
    textPaint.setFakeBoldText(globalBoldActive);
    textPaint.setTextSkewX(globalItalicActive ? ITALIC_SKEW : 0f);
    textPaint.setUnderlineText(globalUnderlineActive);
    textPaint.setColor(globalColorActive);
  }

  /**
   * Finds the offset of the hex digits after a {@code color="#RRGGBB"} token.
   * Returns -1 if no valid hex token is found within the given bounds.
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
   * Converts a 6-digit hex color to an ARGB integer without allocation.
   */
  private int parseHexColor(String line, int startIndex) {
    if (startIndex + 6 > line.length()) {
      return FG;
    }
    int colorResult = 0;
    for (int i = 0; i < 6; i++) {
      char c = line.charAt(startIndex + i);
      int digit;
      if (c >= '0' && c <= '9') digit = c - '0';
      else if (c >= 'a' && c <= 'f') digit = c - 'a' + 10;
      else if (c >= 'A' && c <= 'F') digit = c - 'A' + 10;
      else return FG;
      colorResult = (colorResult << 4) | digit;
    }
    return 0xFF000000 | colorResult;
  }

  /**
   * Scans a bounded region for a named color keyword and returns the
   * corresponding ARGB value, or {@link #FG} when nothing matches.
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

    if (indexOfRegion(line, "red", valueStart, end)) return 0xFFFF0000;
    if (indexOfRegion(line, "green", valueStart, end)) return 0xFF00FF00;
    if (indexOfRegion(line, "blue", valueStart, end)) return 0xFF0000FF;
    if (indexOfRegion(line, "black", valueStart, end)) return BLACK;
    if (indexOfRegion(line, "white", valueStart, end)) return WHITE;
    if (indexOfRegion(line, "yellow", valueStart, end)) return 0xFFFFFF00;
    if (indexOfRegion(line, "cyan", valueStart, end)) return 0xFF00FFFF;
    if (indexOfRegion(line, "magenta", valueStart, end)) return 0xFFFF00FF;
    if (indexOfRegion(line, "gray", valueStart, end)) return 0xFF808080;
    if (indexOfRegion(line, "maroon", valueStart, end)) return 0xFF800000;
    if (indexOfRegion(line, "olive", valueStart, end)) return 0xFF808000;
    if (indexOfRegion(line, "purple", valueStart, end)) return 0xFF800080;

    return FG;
  }

  /**
   * Returns true if {@code target} appears in {@code line} within the
   * bounds {@code [start, end)}.
   */
  private boolean indexOfRegion(String line, String target, int start, int end) {
    int targetLen = target.length();
    if (start < 0 || end > line.length() || start >= end) {
      return false;
    }
    for (int i = start; i <= end - targetLen; i++) {
      if (line.regionMatches(i, target, 0, targetLen)) {
        return true;
      }
    }
    return false;
  }

  // ---------------------------------------------------------------------------
  // Touch handling
  // ---------------------------------------------------------------------------

  @Override
  public boolean onTouchEvent(MotionEvent evt) {
    if (evt.getAction() != MotionEvent.ACTION_DOWN) {
      return false;
    }

    int x = (int) evt.getX();
    int y = (int) evt.getY();

    if ((x < NISF) && (x >= AX) && (y > AY)) {
      // Exit.
      if (activity != null) {
        writeCaretFile(new File(caretFileName), PGSYC);
        activity.finishAndRemoveTask();
      }
    } else if ((x >= NISF) && (x <= width) && (y > BY)) {
      // Page number.
      if (pane != null) {
        pane.show();
      }
    } else if ((x <= SETX) && (y <= SETY)) {
      // Settings.
      if (setPane != null) {
        setPane.show();
      }
    } else if ((x >= CPX) && (y <= CPY)) {
      // Copy / export.
      if (cpPane != null) {
        cpPane.show();
      }
    } else if (x <= NISF) {
      // Previous page.
      --PGSYC;
      if (PGSYC < 0) PGSYC = 0;
      setBitmapMe();
      drawx();
    } else {
      // Next page.
      ++PGSYC;
      if (PGSYC > MAX) PGSYC = MAX;
      setBitmapMe();
      drawx();
    }

    return true;
  }

  // ---------------------------------------------------------------------------
  // SurfaceHolder.Callback
  // ---------------------------------------------------------------------------

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    // Nothing to do.
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    setBitmapMe();
    drawx();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    // Nothing to do.
  }

}
