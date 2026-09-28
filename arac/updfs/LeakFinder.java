import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LeakFinder {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java LeakFinder <file_path>");
            return;
        }

        // Fixed by Murat Abi: Accessing the first parameter of the array
        String filePath = args[0];
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Error: File not found.");
            return;
        }

        ArrayList<Integer> openBraceLines = new ArrayList<Integer>();
        int lineNumber = 0;
        boolean insideMultiLineComment = false;

        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8")
            );

            String line;
            System.out.println("--- ULTIMATE MULTI-LINE COMMENT SCAN FOR: " + file.getName() + " ---");

            while ((line = br.readLine()) != null) {
                lineNumber++;
                boolean insideString = false;

                for (int i = 0; i < line.length(); i++) {
                    char ch = line.charAt(i);

                    // 1. TRACK MULTI-LINE COMMENT BLOCK START (/*)
                    if (!insideString && !insideMultiLineComment && i < line.length() - 1 && ch == '/' && line.charAt(i + 1) == '*') {
                        insideMultiLineComment = true;
                        i++; // Skip '*'
                        continue;
                    }

                    // 2. TRACK MULTI-LINE COMMENT BLOCK END (*/)
                    if (insideMultiLineComment && i < line.length() - 1 && ch == '*' && line.charAt(i + 1) == '/') {
                        insideMultiLineComment = false;
                        i++; // Skip '/'
                        continue;
                    }

                    // If we are currently inside /* ... */ block, ignore everything
                    if (insideMultiLineComment) {
                        continue;
                    }

                    // 3. SKIP SINGLE-LINE COMMENTS (//)
                    if (!insideString && i < line.length() - 1 && ch == '/' && line.charAt(i + 1) == '/') {
                        break; 
                    }

                    // 4. TRACK STRINGS ("...")
                    if (ch == '"') {
                        if (i > 0 && line.charAt(i - 1) == '\\') {
                            // Escaped quote, do nothing
                        } else {
                            insideString = !insideString;
                        }
                        continue;
                    }

                    // 5. COUNT BRACES ONLY IN PURE EXECUTABLE CODE
                    if (!insideString) {
                        if (ch == '{') {
                            openBraceLines.add(lineNumber);
                        } else if (ch == '}') {
                            if (openBraceLines.size() > 0) {
                                openBraceLines.remove(openBraceLines.size() - 1);
                            } else {
                                // CRITICAL: This is the absolute fake brace causing the error!
                                System.out.println("\n[THE REAL CRIMINAL CAPTURED!]");
                                System.out.println("Extra fake close brace '}' found at Line " + lineNumber);
                                System.out.println("Line Content -> " + line.trim());
                                System.out.println("Go to this line and remove this extra paranthesis!");
                                br.close();
                                return;
                            }
                        }
                    }
                }
            }
            br.close();

            if (openBraceLines.size() > 0) {
                System.out.println("\n[INFO] Unclosed '{' found at lines: " + openBraceLines.toString());
            } else {
                System.out.println("\n--- PURE CODE IS PERFECTLY BALANCED ---");
                System.out.println("If you see this, the error is inside an ignored block or file structure.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
