import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompareFiles {

    private static class Line {
        private final String text;
        private final int number;

        Line(String text, int number) {
            this.text = text;
            this.number = number;
        }
    }

    private static class Diff {
        private final char type;
        private final Line line1;
        private final Line line2;

        Diff(char type, Line line1, Line line2) {
            this.type = type;
            this.line1 = line1;
            this.line2 = line2;
        }
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java CompareFiles file1 file2");
            System.exit(1);
        }

        File file1 = new File(args[0]);
        File file2 = new File(args[1]);

        if (!file1.isFile()) {
            System.err.println("File not found: " + file1.getPath());
            System.exit(2);
        }

        if (!file2.isFile()) {
            System.err.println("File not found: " + file2.getPath());
            System.exit(2);
        }

        try {
            /*
             * Dosyalar burada okunuyor.
             * Böylece karşılaştırma, programın çalıştırıldığı andaki
             * dosya içerikleri üzerinden yapılır.
             */
            List<Line> lines1 = readFile(file1);
            List<Line> lines2 = readFile(file2);

            if (sameContent(lines1, lines2)) {
                System.out.println("Files are identical.");
                return;
            }

            List<Diff> diffs = compare(lines1, lines2);

            System.out.println();
            System.out.println("--- " + file1.getPath());
            System.out.println("+++ " + file2.getPath());
            System.out.println();

            printDiffs(diffs);

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
            System.exit(3);
        }
    }

    /**
     * Dosyayı UTF-8 olarak okur.
     *
     * Eğer dosyaların farklı bir encoding ile okunması gerekiyorsa
     * buradaki "UTF-8" değiştirilebilir.
     */
    private static List<Line> readFile(File file) throws IOException {

        List<Line> lines = new ArrayList<Line>();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file),
                            "UTF-8"
                    )
            );

            String text;
            int number = 1;

            while ((text = reader.readLine()) != null) {
                lines.add(new Line(text, number));
                number++;
            }

        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return lines;
    }

    /**
     * İki dosyanın tamamen aynı olup olmadığını kontrol eder.
     */
    private static boolean sameContent(
            List<Line> lines1,
            List<Line> lines2) {

        if (lines1.size() != lines2.size()) {
            return false;
        }

        for (int i = 0; i < lines1.size(); i++) {
            if (!lines1.get(i).text.equals(lines2.get(i).text)) {
                return false;
            }
        }

        return true;
    }

    /**
     * LCS tabanlı diff.
     *
     * Aynı satırların mümkün olan en uzun ortak dizisini bulur.
     * Böylece araya satır eklenmesi veya satır silinmesi durumunda
     * dosyanın geri kalanını mümkün olduğunca doğru eşleştirir.
     */
    private static List<Diff> compare(
            List<Line> lines1,
            List<Line> lines2) {

        int n = lines1.size();
        int m = lines2.size();

        /*
         * dp[i][j]:
         * lines1'in i. satırından sonrası ile
         * lines2'nin j. satırından sonrası arasındaki
         * ortak satırların maksimum sayısı.
         */
        int[][] dp = new int[n + 1][m + 1];

        for (int i = n - 1; i >= 0; i--) {

            for (int j = m - 1; j >= 0; j--) {

                if (lines1.get(i).text.equals(lines2.get(j).text)) {
                    dp[i][j] = dp[i + 1][j + 1] + 1;

                } else {
                    dp[i][j] = Math.max(
                            dp[i + 1][j],
                            dp[i][j + 1]
                    );
                }
            }
        }

        List<Diff> result = new ArrayList<Diff>();

        int i = 0;
        int j = 0;

        while (i < n && j < m) {

            if (lines1.get(i).text.equals(lines2.get(j).text)) {

                /*
                 * Aynı satır. Diff'e eklemiyoruz.
                 */
                i++;
                j++;

            } else if (dp[i + 1][j] >= dp[i][j + 1]) {

                /*
                 * İlk dosyada bulunan, ikinci dosyada olmayan satır.
                 */
                result.add(
                        new Diff(
                                '-',
                                lines1.get(i),
                                null
                        )
                );

                i++;

            } else {

                /*
                 * İkinci dosyada bulunan, ilk dosyada olmayan satır.
                 */
                result.add(
                        new Diff(
                                '+',
                                null,
                                lines2.get(j)
                        )
                );

                j++;
            }
        }

        while (i < n) {

            result.add(
                    new Diff(
                            '-',
                            lines1.get(i),
                            null
                    )
            );

            i++;
        }

        while (j < m) {

            result.add(
                    new Diff(
                            '+',
                            null,
                            lines2.get(j)
                    )
            );

            j++;
        }

        return result;
    }

    /**
     * Diff sonucunu ekrana basar.
     */
    private static void printDiffs(List<Diff> diffs) {

        int index = 0;

        while (index < diffs.size()) {

            Diff diff = diffs.get(index);

            /*
             * Değişiklik bloklarını grupla:
             *
             * - eski satır
             * + yeni satır
             *
             * şeklinde gösteriyoruz.
             */
            if (diff.type == '-') {

                int start = index;

                while (index < diffs.size()
                        && diffs.get(index).type == '-') {
                    index++;
                }

                int deleteEnd = index;

                int addStart = index;

                while (index < diffs.size()
                        && diffs.get(index).type == '+') {
                    index++;
                }

                int addEnd = index;

                printChangeBlock(
                        diffs,
                        start,
                        deleteEnd,
                        addStart,
                        addEnd
                );

            } else {

                int start = index;

                while (index < diffs.size()
                        && diffs.get(index).type == '+') {
                    index++;
                }

                printChangeBlock(
                        diffs,
                        start,
                        start,
                        start,
                        index
                );
            }
        }
    }

    /**
     * Bir değişiklik bloğunu yazdırır.
     */
    private static void printChangeBlock(
            List<Diff> diffs,
            int deleteStart,
            int deleteEnd,
            int addStart,
            int addEnd) {

        System.out.println("@@");

        for (int i = deleteStart; i < deleteEnd; i++) {

            Line line = diffs.get(i).line1;

            System.out.println(
                    String.format(
                            "- %6d | %s",
                            line.number,
                            line.text
                    )
            );
        }

        for (int i = addStart; i < addEnd; i++) {

            Line line = diffs.get(i).line2;

            System.out.println(
                    String.format(
                            "+ %6d | %s",
                            line.number,
                            line.text
                    )
            );
        }

        System.out.println();
    }
    
}
