import java.io.*;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

import net.murat.shape.*;
import net.murat.material.*;
import net.murat.math.*;
import net.murat.light.*;
import net.murat.util.*;
import net.murat.lovert.*;
import net.murat.gui.*;

/**
 * LogoGen - Configuration-driven scene builder for Murat Ray Tracer
 * 
 * This class parses ICONINFO.txt configuration files and builds complete
 * 3D scenes with lights, shapes, and materials. All color operations use
 * packed ARGB integers for zero-allocation performance.
 * 
 * @author Murat Ray Tracer Team
 * @version 2.0 (GC-free int color system)
 */
public final class LogoGen {
    
    private static String dielStr = "TEXT";
    private static boolean isLastSphere = false;
    
    private LogoGen() {
        super();
    }

    @Override
    public String toString() {
        return "LogoGen";
    }

    // =========================================================================
    // PARSING UTILITIES
    // =========================================================================
    
    /**
     * Extracts a string value from the configuration block.
     * Format: "key = value;"
     */
    private static String parseString(String source, String key) {
        String search = key + " = ";
        int start = source.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = source.indexOf(";", start);
        if (end == -1) end = source.indexOf("\n", start);
        if (end == -1) end = source.length();
        String val = source.substring(start, end).trim();
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        return val;
    }

    private static double parseDouble(String source, String key) {
        String val = parseString(source, key);
        return val != null ? Double.parseDouble(val) : 0.0;
    }

    private static int parseInt(String source, String key) {
        String val = parseString(source, key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    private static boolean parseBoolean(String source, String key) {
        String val = parseString(source, key);
        return val != null && Boolean.parseBoolean(val);
    }

    /**
     * Parses color from #AARRGGBB format to packed ARGB integer.
     * This is the GC-friendly version that avoids java.awt.Color objects.
     * 
     * Format examples:
     *   #FF000000 -> fully opaque black
     *   #80FF0000 -> 50% transparent red
     *   #FFFFFF   -> opaque white (legacy 6-digit format)
     * 
     * @return Packed ARGB integer (0xAARRGGBB)
     */
	private static int parseColorInt(String source, String key) {
		String val = parseString(source, key);
		if (val == null) return 0xFF000000;

		String hex = val.trim();
		if (hex.startsWith("#")) {
			hex = hex.substring(1);
		} else if (hex.startsWith("0x") || hex.startsWith("0X")) {
			hex = hex.substring(2);
		} else {
			return 0xFF000000;
		}

		try {
			if (hex.length() == 8) {
				return (int) Long.parseLong(hex, 16);
			} else if (hex.length() == 6) {
				int rgb = Integer.parseInt(hex, 16);
				return 0xFF000000 | rgb;
			} else {
				return 0xFF000000;
			}
		} catch (Exception e) {
			System.err.println("[Warning] Failed to parse color: " + val);
			return 0xFF000000;
		}
	}

    /**
     * Legacy support for Color objects - converts to packed int.
     * @deprecated Use parseColorInt() for GC-free operation
     */
    @Deprecated
    private static int parseColor(String source, String key) {
        return parseColorInt(source, key);
    }

    private static Point3 parsePoint3(String source, String key) {
        String search = key + " = P(";
        int start = source.indexOf(search);
        if (start == -1) return new Point3(0, 0, 0);
        start += search.length();
        int end = source.indexOf(")", start);
        String[] parts = source.substring(start, end).split(",");
        return new Point3(
            Double.parseDouble(parts[0].trim()),
            Double.parseDouble(parts[1].trim()),
            Double.parseDouble(parts[2].trim())
        );
    }

    private static Vector3 parseVector3(String source, String key) {
        String search = key + " = V(";
        int start = source.indexOf(search);
        if (start == -1) return new Vector3(0, 1, 0);
        start += search.length();
        int end = source.indexOf(")", start);
        String[] parts = source.substring(start, end).split(",");
        return new Vector3(
            Double.parseDouble(parts[0].trim()),
            Double.parseDouble(parts[1].trim()),
            Double.parseDouble(parts[2].trim())
        );
    }

    private static BufferedImage loadImage(String path) {
        try {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                return ImageIO.read(imgFile);
            }
        } catch (Exception e) {
            System.err.println("[Warning] Failed to load image: " + path);
        }
        return null;
    }

    /**
     * Parses transformation matrix pipeline.
     * Format: "translate(x,y,z)*rotate(rx,ry,rz)*scale(sx,sy,sz)"
     * Operations are applied in order (right-to-left in matrix multiplication).
     */
    private static Matrix4 parseTransformMatrix(String source, String key) {
        String val = parseString(source, key);
        Matrix4 finalMatrix = new Matrix4().identity();
        if (val == null || val.isEmpty()) return finalMatrix;
        
        String[] pipeline = val.split("\\*");
        for (int i = 0; i < pipeline.length; i++) {
            String op = pipeline[i].trim();
            int open = op.indexOf("(");
            int close = op.indexOf(")");
            if (open == -1 || close == -1) continue;
            
            String[] p = op.substring(open + 1, close).split(",");
            
            if (op.startsWith("translate")) {
                Matrix4 tMat = new Matrix4().identity().translate(new Vector3(
                    Double.parseDouble(p[0].trim()), 
                    Double.parseDouble(p[1].trim()), 
                    Double.parseDouble(p[2].trim())
                ));
                finalMatrix = finalMatrix.multiply(tMat);
                
            } else if (op.startsWith("rotate")) {
                double rX = Double.parseDouble(p[0].trim());
                double rY = Double.parseDouble(p[1].trim());
                double rZ = Double.parseDouble(p[2].trim());
                
                Matrix4 rotMat = new Matrix4().identity();
                if (rX != 0.0) rotMat = rotMat.multiply(new Matrix4().identity().rotateX(rX));
                if (rY != 0.0) rotMat = rotMat.multiply(new Matrix4().identity().rotateY(rY));
                if (rZ != 0.0) rotMat = rotMat.multiply(new Matrix4().identity().rotateZ(rZ));
                
                finalMatrix = finalMatrix.multiply(rotMat);
                
            } else if (op.startsWith("scale")) {
                Matrix4 sMat = new Matrix4().identity().scale(
                    Double.parseDouble(p[0].trim()), 
                    Double.parseDouble(p[1].trim()), 
                    Double.parseDouble(p[2].trim())
                );
                finalMatrix = finalMatrix.multiply(sMat);
            }
        }
        return finalMatrix;
    }

    // =========================================================================
    // MAIN LOGO GENERATION
    // =========================================================================

    /**
     * Generates a complete scene from a configuration file.
     * All colors are handled as packed ARGB integers for GC-free performance.
     * 
     * @param configPath Path to ICONINFO.txt configuration file
     * @throws IOException If configuration file cannot be read
     */
    public static void generateLogoFromConfig(String configPath) throws IOException {
        Scene scene = new Scene();
        Camera camera = new Camera();
        int renderWidth = 512;
        int renderHeight = 512;
        int renderBgColor = 0xFF000000; // Default: opaque black

        File file = new File(configPath);
        if (!file.exists()) {
            throw new IOException("Configuration file not found: " + file.getAbsolutePath());
        }

        // Read entire configuration file
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        String content = sb.toString();

        // Parse each block (Camera, Renderer, Lights, Shapes)
        int cursor = 0;
        while (true) {
            int openBrace = content.indexOf("{", cursor);
            if (openBrace == -1) break;

            // Extract header (block type)
            int blockStart = content.lastIndexOf("\n", openBrace);
            if (blockStart == -1 || blockStart < cursor) blockStart = cursor;
            String header = content.substring(blockStart, openBrace).trim();

            // Find matching closing brace
            int closeBrace = -1;
            int braceCount = 0;
            for (int i = openBrace; i < content.length(); i++) {
                char ch = content.charAt(i);
                if (ch == '{') {
                    braceCount++;
                } else if (ch == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        closeBrace = i;
                        break;
                    }
                }
            }

            if (closeBrace == -1) break;
            String blockData = content.substring(openBrace + 1, closeBrace);

            // ============ CAMERA BLOCK ============
            if (header.startsWith("Camera")) {
                camera.setCameraPosition(parsePoint3(blockData, "position"));
                camera.setLookAt(parsePoint3(blockData, "lookAt"));
                camera.setUpVector(parseVector3(blockData, "upVector").normalize());
                camera.setFov(parseDouble(blockData, "fov"));
                camera.setOrthographic(parseBoolean(blockData, "orthographic"));
                camera.setMaxRecursionDepth(parseInt(blockData, "maxRecursionDepth"));
                camera.setReflective(parseBoolean(blockData, "reflective"));
                camera.setRefractive(parseBoolean(blockData, "refractive"));
                camera.setShadowsEnabled(parseBoolean(blockData, "shadowsEnabled"));
                camera.setUseBackgroundColor(parseBoolean(blockData, "useBackgroundColor"));
            } 
            
            // ============ RENDERER SETTINGS ============
            else if (header.startsWith("Renderer Settings")) {
                renderWidth = parseInt(blockData, "width");
                renderHeight = parseInt(blockData, "height");
                renderBgColor = parseColorInt(blockData, "backgroundColor");
            } 
            
            // ============ AMBIENT LIGHT ============
            else if (header.startsWith("MuratAmbientLight")) {
                int color = parseColorInt(blockData, "color");
                double intensity = parseDouble(blockData, "intensity");
                scene.addLight(new MuratAmbientLight(color, intensity));
            } 
            
            // ============ POINT LIGHT ============
            else if (header.startsWith("MuratPointLight")) {
                Point3 pos = parsePoint3(blockData, "position");
                int color = parseColorInt(blockData, "color");
                double intensity = parseDouble(blockData, "intensity");
                scene.addLight(new MuratPointLight(pos, color, intensity));
            } 
            
            // ============ PLANE SHAPE ============
            else if (header.startsWith("Plane")) {
                Point3 pop = parsePoint3(blockData, "pointOnPlane");
                Vector3 norm = parseVector3(blockData, "normal");
                Plane planeShape = new Plane(pop, norm);
                
                planeShape.setName(parseString(blockData, "name"));
                planeShape.setTransform(parseTransformMatrix(blockData, "transform"));
                planeShape.setShadowColor(parseColorInt(blockData, "shadowColor"));
                planeShape.setShadowBias(parseDouble(blockData, "shadowBias"));
                planeShape.setVisibleSpecial(parseBoolean(blockData, "isVisibleSpecial"));
                planeShape.setVisible(parseBoolean(blockData, "isVisible"));
                planeShape.setShadowEnable(parseBoolean(blockData, "isShadowEnable"));
                planeShape.setShadowOnly(parseBoolean(blockData, "isShadowOnly"));
                planeShape.setReflective(parseBoolean(blockData, "isReflective"));
                planeShape.setRefractive(parseBoolean(blockData, "isRefractive"));

                // Check for CheckerboardMaterial
                int matIdx = blockData.indexOf("material = CheckerboardMaterial");
                if (matIdx != -1) {
                    String matData = blockData.substring(matIdx);
                    int c1 = parseColorInt(matData, "color1");
                    int c2 = parseColorInt(matData, "color2");
                    double sz = parseDouble(matData, "size");
                    double amb = parseDouble(matData, "ambientCoeff");
                    double diff = parseDouble(matData, "diffuseCoeff");
                    double spec = parseDouble(matData, "specularCoeff");
                    double shin = parseDouble(matData, "shininess");
                    int specCol = parseColorInt(matData, "specularColor");
                    double refl = parseDouble(matData, "reflectivity");
                    double ior = parseDouble(matData, "ior");
                    double trans = parseDouble(matData, "transparency");

                    Material cbMat = new CheckerboardMaterial(
                        c1, c2, sz, amb, diff, spec, shin, specCol, refl, ior, trans, 
                        planeShape.getInverseTransform()
                    );
                    planeShape.setMaterial(cbMat);
                }
                scene.addShape(planeShape);
            } 
            
            // ============ SPHERE SHAPE ============
            else if (header.startsWith("Sphere")) {
                double radius = parseDouble(blockData, "radius");
                Sphere sphereShape = new Sphere(radius);
                
                // Track if this is the last sphere (for dynamic text replacement)
                if (blockData.lastIndexOf("sphere") >= 0) {
                    isLastSphere = true;
                }
                
                sphereShape.setTransform(parseTransformMatrix(blockData, "transform"));
                sphereShape.setShadowColor(parseColorInt(blockData, "shadowColor"));
                sphereShape.setShadowBias(parseDouble(blockData, "shadowBias"));

                // ============ SPHERE WORD TEXTURE MATERIAL ============
                int matIdx = blockData.indexOf("material = SphereWordTextureMaterial");
                if (matIdx != -1) {
                    String matData = blockData.substring(matIdx);
                    
                    // Extract all parameters with GC-friendly int colors
                    String word = parseString(matData, "word");
                    
                    // Dynamic text replacement for last sphere
                    if (isLastSphere) {
                        word = dielStr.toUpperCase(java.util.Locale.US);
                    }
                    
                    int textColor = parseColorInt(matData, "textColor");
                    int gradientColor = parseColorInt(matData, "gradientColor");
                    String gradientType = parseString(matData, "gradientType");
                    int bgColor = parseColorInt(matData, "bgColor");
                    String fontFamily = parseString(matData, "fontFamily");
                    int fontStyle = parseInt(matData, "fontStyle");
                    int fontSize = parseInt(matData, "fontSize");
                    int uOffset = parseInt(matData, "uOffset");
                    int vOffset = parseInt(matData, "vOffset");
                    
                    // Image decal parameters
                    String imagePath = parseString(matData, "imagePath");
                    int imageWidth = parseInt(matData, "imageWidth");
                    int imageHeight = parseInt(matData, "imageHeight");
                    int imageUOffset = parseInt(matData, "imageUOffset");
                    int imageVOffset = parseInt(matData, "imageVOffset");
                    
                    // Material properties
                    double reflectivity = parseDouble(matData, "reflectivity");
                    double ior = parseDouble(matData, "ior");
                    double transparency = parseDouble(matData, "transparency");
                    
                    // Load image if specified
                    BufferedImage imageObject = null;
                    if (imagePath != null && !imagePath.isEmpty()) {
                        imageObject = loadImage(imagePath);
                    }

                    // Create SphereWordTextureMaterial (GC-friendly, uses int colors)
                    SphereWordTextureMaterial swtm = new SphereWordTextureMaterial(
                        word, 
                        textColor, 
                        gradientColor, 
                        gradientType, 
                        bgColor,
                        fontFamily, 
                        fontStyle, 
                        fontSize,
                        reflectivity, 
                        ior, 
                        transparency,
                        uOffset, 
                        vOffset,
                        imageObject, 
                        imageWidth, 
                        imageHeight, 
                        imageUOffset, 
                        imageVOffset
                    );
                    
                    swtm.setImagePath(imagePath);
                    sphereShape.setMaterial(swtm);
                }
                scene.addShape(sphereShape);
            }

            cursor = closeBrace + 1;
        }

        // =========================================================================
        // RENDER AND SAVE
        // =========================================================================
        
        MuratRayTracer rayTracer = new MuratRayTracer(
            scene, renderWidth, renderHeight, renderBgColor
        );
        rayTracer.setCamera(camera);

        long startTime = System.nanoTime();
        BufferedImage renderedImage = rayTracer.render();
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        System.out.printf("[LogoGen] Render processed successfully in %.2f seconds\n", 
                         durationMs / 1000.0);

        // Add "EBook" overlay text
        File outputFile = new File("logo_render.png");
        Graphics2D eg2d = renderedImage.createGraphics();
        eg2d.setFont(new Font("SansSerif", Font.BOLD, 80));
        eg2d.setColor(java.awt.Color.WHITE);
        eg2d.drawString("EBook", 114, 400);
        eg2d.dispose();

        // Save final image
        ImageIO.write(renderedImage, "png", outputFile);
        System.out.println("[LogoGen] Image saved: " + outputFile.getName());
    }

	private static void updateSceneFile(File sceneFile, String newValue) {
		if (sceneFile == null || !sceneFile.exists()) {
			return;
		}
		
		if (newValue == null) {
			return;
		}
		
		if (newValue.length() > 5) {
			newValue = newValue.substring(0, 5);
		} else if (newValue.length() < 5) {
			// Eksik karakterleri başa ve sona eşit dağıt
			int totalPadding = 5 - newValue.length();
			int leftPadding = totalPadding / 2;
			int rightPadding = totalPadding - leftPadding;
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < leftPadding; i++) {
				sb.append("_");
			}
			sb.append(newValue);
			for (int i = 0; i < rightPadding; i++) {
				sb.append("_");
			}
			newValue = sb.toString();
		}
		
		File tempFile = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		boolean success = false;
		
		try {
			tempFile = File.createTempFile("temp_", ".txt");
			tempFile.deleteOnExit();
			
			reader = new BufferedReader(new FileReader(sceneFile));
			writer = new BufferedWriter(new FileWriter(tempFile));
			
			String line;
			StringBuilder content = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
				String trimmedLine = line.trim();
				
				if (trimmedLine.startsWith("text = ") &&
				    trimmedLine.lastIndexOf("EBOOK") < 0) {
					int index = line.indexOf("text = ");
					String indent = index >= 0 ? line.substring(0, index) : "";
					content.append(indent).append("text = \"").append(newValue).append("\"").append(System.lineSeparator());
				} else {
					content.append(line).append(System.lineSeparator());
				}
			}
			
			writer.write(content.toString());
			writer.flush();
			success = true;
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
				if (writer != null) writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (success && tempFile != null && tempFile.exists()) {
			try {
				BufferedReader tempReader = null;
				BufferedWriter origWriter = null;
				
				try {
					tempReader = new BufferedReader(new FileReader(tempFile));
					origWriter = new BufferedWriter(new FileWriter(sceneFile));
					
					String line;
					while ((line = tempReader.readLine()) != null) {
						origWriter.write(line);
						origWriter.newLine();
					}
					origWriter.flush();
					
				} finally {
					if (tempReader != null) tempReader.close();
					if (origWriter != null) origWriter.close();
				}
				
				tempFile.delete();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    // =========================================================================
    // MAIN ENTRY POINT
    // =========================================================================

    /**
     * Main entry point for LogoGen.
     * 
     * Usage: java LogoGen [text]
     *   text - Optional text to display on the last sphere (max 4 chars)
     */
    public static void main(String[] args) {
        try {
            //String configPath = "ICONINFO.txt";
            //if (args.length > 0) {
             //   dielStr = args[0];
             //   if (dielStr.length() > 3) {
             //       dielStr = dielStr.substring(0, 3);
             //   }
            //}
            //generateLogoFromConfig(configPath);
            File sceneFile = new File("ICONINFO.txt");
            if (args.length > 0) {
              updateSceneFile(sceneFile, args[0].toUpperCase(java.util.Locale.US));
		    } else {
              updateSceneFile(sceneFile, "ABCDE");
			}
            SceneParser parser = new SceneParser();
            BufferedImage bimg = parser.renderScene(sceneFile);
            File outputFile = new File("logo_render.png");
            ImageIO.write(bimg, "png", outputFile);
        } catch (Exception e) {
            System.err.println("[Fatal] Parsing cracked: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
}
