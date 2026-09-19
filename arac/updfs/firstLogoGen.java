import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Properties;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class LogoGen {

    /* Default fallback values */
    private static final Color RETCOLOR = Color.BLACK;
    private static final Font RETFONT =
            new Font("SansSerif", Font.BOLD, 100);

    /* Configurable values */
    private static int SIZE = 512;

    private static int OVAL_WIDTH = 502;
    private static int OVAL_HEIGHT = 502;

    private static Color BG_AREA_COLOR =
            new Color(0, 0, 0, 0);  // Default transparent

    private static Paint BG_OVAL_COLOR =
            new GradientPaint(5, 5, Color.BLACK, 75, 75, Color.YELLOW, true);

    private static Paint EBOOK_TEXT_COLOR =
            new GradientPaint(5, 5, Color.RED, 15, 15, Color.BLUE, true);

    private static Paint MAIN_TEXT_COLOR =
            new GradientPaint(5, 5, Color.WHITE, 25, 25, Color.YELLOW, true);

    private static Color LINE_COLOR =
            new Color(255, 215, 0, 200);

    private static Color BORDER_COLOR =
            new Color(255, 255, 255, 100);

    private static Font topFont =
            new Font("SansSerif", Font.BOLD | Font.ITALIC, 70);

    private static Font mainFont =
            new Font("SansSerif", Font.BOLD, 206);

    private LogoGen() {}
	
	private static Paint parseGradientPaint(String value) {
		if (value == null || value.trim().isEmpty())
            return BG_OVAL_COLOR;
        
        try {
			String[] parts = value.trim().split(",");
			int x1 = Integer.parseInt(parts[0].trim());
			int y1 = Integer.parseInt(parts[1].trim());
			Color c1 = new Color(Integer.parseInt(parts[2].trim().toUpperCase(), 0x10));
			
			int x2 = Integer.parseInt(parts[3].trim());
			int y2 = Integer.parseInt(parts[4].trim());
			Color c2 = new Color(Integer.parseInt(parts[5].trim().toUpperCase(), 0x10));
			
			Paint GP = new GradientPaint(x1, y1, c1, x2, y2, c2, true);
			
			return GP;
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return BG_OVAL_COLOR;
		}
	}
	
    /* Parse color string safely */
    private static Color parseColor(String value) {

        if (value == null || value.trim().isEmpty())
            return RETCOLOR;

        try {
            String[] parts = value.trim().split(",");
            boolean isFloat = value.toLowerCase().contains("f");

            if (isFloat) {
                float r = Float.parseFloat(parts[0].replace("f","").trim());
                float g = Float.parseFloat(parts[1].replace("f","").trim());
                float b = Float.parseFloat(parts[2].replace("f","").trim());
                float a = parts.length > 3 ?
                        Float.parseFloat(parts[3].replace("f","").trim()) : 1f;
                return new Color(r, g, b, a);
            } else {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                int a = parts.length > 3 ?
                        Integer.parseInt(parts[3].trim()) : 255;
                return new Color(r, g, b, a);
            }

        } catch (Exception e) {
            System.err.println("Color parse failed: " + value);
            return RETCOLOR;
        }
    }

    /* Parse font safely */
    private static Font parseFont(String value) {

        if (value == null || value.trim().isEmpty())
            return RETFONT;

        try {
            String[] parts = value.trim().split(",");
            String name = parts[0].trim();
            int style = Integer.parseInt(parts[1].trim());
            int size = Integer.parseInt(parts[2].trim());
            return new Font(name, style, size);

        } catch (Exception e) {
            System.err.println("Font parse failed: " + value);
            return RETFONT;
        }
    }

    /* Load configuration from ICONINFO.txt */
    private static void loadConfig() {

        File file = new File("ICONINFO.txt");
        if (!file.exists()) {
            System.out.println("ICONINFO.txt not found. Using defaults.");
            return;
        }

        try {
            Properties props = new Properties();
            props.load(new InputStreamReader(
                    new FileInputStream(file),
                    StandardCharsets.UTF_8));

            if (props.getProperty("SIZE") != null)
                SIZE = Integer.parseInt(props.getProperty("SIZE").trim());

            if (props.getProperty("OVAL_WIDTH") != null)
                OVAL_WIDTH = Integer.parseInt(props.getProperty("OVAL_WIDTH").trim());

            if (props.getProperty("OVAL_HEIGHT") != null)
                OVAL_HEIGHT = Integer.parseInt(props.getProperty("OVAL_HEIGHT").trim());

            BG_AREA_COLOR   = parseColor(props.getProperty("BG_AREA_COLOR"));
            BG_OVAL_COLOR   = parseGradientPaint(props.getProperty("BG_OVAL_COLOR"));
            EBOOK_TEXT_COLOR= parseGradientPaint(props.getProperty("EBOOK_TEXT_COLOR"));
            MAIN_TEXT_COLOR = parseGradientPaint(props.getProperty("MAIN_TEXT_COLOR"));
            LINE_COLOR      = parseColor(props.getProperty("LINE_COLOR"));
            BORDER_COLOR    = parseColor(props.getProperty("BORDER_COLOR"));

            topFont  = parseFont(props.getProperty("TOP_FONT"));
            mainFont = parseFont(props.getProperty("MAIN_FONT"));

            System.out.println("\nConfig file ICONINFO.txt loaded.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Extract first two letters with ENGLISH locale */
    private static String extractLetters(String name) {

        if (name == null || name.isEmpty())
            return "??";

        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray())
            if (Character.isLetter(c))
                sb.append(c);

        String result = sb.length() >= 2 ?
                sb.substring(0, 2) :
                name.substring(0, Math.min(2, name.length()));

        // Use ENGLISH locale to prevent Turkish I problems
        result = result.toUpperCase(Locale.US);
        if (result.equals("AM")) result = "AX";
        
        return result;
    }

    private static void createLogo(String name) throws Exception {
        loadConfig();

        int OVAL_X = (SIZE - OVAL_WIDTH) / 2;
        int OVAL_Y = (SIZE - OVAL_HEIGHT) / 2;

        BufferedImage img =
                new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = img.createGraphics();

        // First clear to transparent (this is critical)
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        g.fillRect(0, 0, SIZE, SIZE);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        /* Draw background area - user can set this to any color including transparent */
        g.setColor(BG_AREA_COLOR);
        g.fillRect(0, 0, SIZE, SIZE);

        /* Draw oval */
        g.setPaint(BG_OVAL_COLOR);
        g.fillOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        /* Calculate positions based on oval size */
        // EBook text at 30% of oval height
        int topY = OVAL_Y + (int)(OVAL_HEIGHT * 0.30);
        
        // Line just below text (shorter line for better look)
        int lineY = topY + 20;
        
        // Main text at 70% of oval height
        int mainY = OVAL_Y + (int)(OVAL_HEIGHT * 0.75);

        /* Draw EBook text */
        g.setFont(topFont);
        FontMetrics topMetrics = g.getFontMetrics();

        String ebook = "EBook";
        int topWidth = topMetrics.stringWidth(ebook);
        int topX = (SIZE - topWidth) / 2;

        g.setPaint(EBOOK_TEXT_COLOR);
        g.drawString(ebook, topX, topY);

        /* Separator line - now shorter and better positioned */
        int lineLength = Math.min(topWidth + 100, OVAL_WIDTH - 80);
        int lineStartX = (SIZE - lineLength) / 2;
        int lineEndX = lineStartX + lineLength;

        g.setStroke(new BasicStroke(4f * (SIZE / 512f)));
        g.setColor(LINE_COLOR);
        g.drawLine(lineStartX, lineY, lineEndX, lineY);

        /* Main letters */
        String letters = extractLetters(name);

        g.setFont(mainFont);
        FontMetrics mainMetrics = g.getFontMetrics();
        int mainWidth = mainMetrics.stringWidth(letters);

        int mainX = (SIZE - mainWidth) / 2;

        g.setPaint(MAIN_TEXT_COLOR);
        g.drawString(letters, mainX, mainY);

        /* Border around oval */
        g.setStroke(new BasicStroke(3f * SIZE / 512f));
        g.setColor(BORDER_COLOR);
        g.drawOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        g.dispose();

        File out = new File("../res/drawable/ebooklogo.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);

        return;
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Usage: java LogoGen packageName");
            System.out.println("Example: java LogoGen atcero");
            return;
        }

        try {
            createLogo(args[0].toLowerCase());
            System.out.println("Icon created according to ICONINFO.txt!\n");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
}
