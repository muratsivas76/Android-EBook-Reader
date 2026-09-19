import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtractMethods {
    
    private static Set<String> results = new LinkedHashSet<>();
    private static int fileCount = 0;
    private static int memberCount = 0;
    private static final String OUTPUT_FILE = "methods_export.txt";
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ExtractMethods <source-directory>");
            System.exit(1);
        }
        
        File root = new File(args[0]);
        if (!root.exists() || !root.isDirectory()) {
            System.err.println("Error: Please provide a valid directory.");
            System.exit(1);
        }
        
        scanDirectory(root);
        
        writeResults(OUTPUT_FILE);
        
        System.out.println("Scanning completed.");
        System.out.println("Total files processed: " + fileCount);
        System.out.println("Total unique members found: " + memberCount);
        System.out.println("Results saved to: " + OUTPUT_FILE);
    }
    
    private static void scanDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                scanDirectory(files[i]);
            } else if (files[i].getName().endsWith(".java")) {
                processJavaFile(files[i]);
            }
        }
    }
    
    private static void processJavaFile(File javaFile) {
        try {
            String content = readFile(javaFile);
            results.add("\n--- File: " + javaFile.getPath() + " ---");
            
            extractMembers(content, javaFile.getName());
            fileCount++;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    
    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }
    
    private static void extractMembers(String content, String fileName) {
        String className = extractClassName(content, fileName);
        
        // 1. Fields
        Pattern fieldP = Pattern.compile("public\\s+(?:static\\s+)?(?:final\\s+)?[\\w<>\\[\\]]+\\s+(\\w+)(?:\\s*=.*?)?;");
        Matcher fm = fieldP.matcher(content);
        while (fm.find()) {
            String entry = "[FIELD] " + fm.group(1);
            if (results.add(entry)) memberCount++;
        }
        
        // 2. Constructors
        String ctorRegex = "public\\s+" + className + "\\s*\\(([^)]*)\\)\\s*(?:\\{|throws|;)";
        Pattern ctorP = Pattern.compile(ctorRegex);
        Matcher cm = ctorP.matcher(content);
        while (cm.find()) {
            String params = cm.group(1).trim();
            String entry = "[CONSTRUCTOR] " + className + "(" + (params.isEmpty() ? "empty" : params) + ")";
            if (results.add(entry)) memberCount++;
        }
        
        // 3. Methods (Public/Protected, supports @Override)
        Pattern mP = Pattern.compile("(?:@Override\\s+)?(public|protected)\\s+(?:static\\s+)?(?:abstract\\s+)?(?:final\\s+)?[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(?:\\{|throws|;)");
        Matcher mm = mP.matcher(content);
        while (mm.find()) {
            String entry = "[" + mm.group(1).toUpperCase() + " METHOD] " + mm.group(2) + "(" + (mm.group(3).trim().isEmpty() ? "empty" : mm.group(3).trim()) + ")";
            if (results.add(entry)) memberCount++;
        }
    }
    
    private static String extractClassName(String content, String fileName) {
        Pattern p = Pattern.compile("(?:class|interface|enum)\\s+(\\w+)");
        Matcher m = p.matcher(content);
        return m.find() ? m.group(1) : fileName.replace(".java", "");
    }
    
    private static void writeResults(String outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("Java Public API & Member Extractor Report\n");
            writer.write("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
            writer.write("------------------------------------------\n");
            for (String line : results) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
        }
    }
    
}
