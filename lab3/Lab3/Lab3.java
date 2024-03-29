import java.util.stream.Stream;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.*;

// The main plagiarism detection program.
// You only need to change buildIndex() and findSimilarity().
public class Lab3 {
    public static void main(String[] args) {
        try {
            String directory;
            if (args.length == 0) {
                System.out.print("Name of directory to scan: ");
                System.out.flush();
                directory = new Scanner(System.in).nextLine();
            } else directory = args[0];
            Path[] paths = Files.list(Paths.get("lab3/documents/" + directory)).toArray(Path[]::new);
            Arrays.sort(paths);

            // Stopwatches time how long each phase of the program
            // takes to execute.
            Stopwatch stopwatch = new Stopwatch();
            Stopwatch stopwatch2 = new Stopwatch();

            // Read all input files
            BST<Path, Ngram[]> files = readPaths(paths);
            stopwatch.finished("Reading all input files");

            // Build index of n-grams (not implemented yet)
            BST<Ngram, ArrayList<Path>> index = buildIndex(files);
            stopwatch.finished("Building n-gram index");

            // Compute similarity of all file pairs
            BST<PathPair, Integer> similarity = findSimilarity(files, index);
            stopwatch.finished("Computing similarity scores");

            // Find most similar file pairs, arranged in
            // decreasing order of similarity
            ArrayList<PathPair> mostSimilar = findMostSimilar(similarity);
            stopwatch.finished("Finding the most similar files");
            stopwatch2.finished("In total the program");

            // Print out some statistics
            System.out.println("\nBST balance statistics:");
            System.out.printf("  files: size %d, height %d\n", files.size(), files.height());
            System.out.printf("  index: size %d, height %d\n", index.size(), index.height());
            System.out.printf("  similarity: size %d, height %d\n", similarity.size(), similarity.height());
            System.out.println("");

            // Print out the plagiarism report!
            System.out.println("Plagiarism report:");
            for (PathPair pair : mostSimilar)
                System.out.printf("%5d similarity: %s\n", similarity.get(pair), pair);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Phase 1: Read in each file and chop it into n-grams.
    static BST<Path, Ngram[]> readPaths(Path[] paths) throws IOException {
        BST<Path, Ngram[]> files = new BST<>();
        for (Path path : paths) {
            String contents = new String(Files.readAllBytes(path));
            Ngram[] ngrams = Ngram.ngrams(contents, 5);
            // Remove duplicates from the ngrams list
            // Uses the Java 8 streams API - very handy Java feature
            // which we don't cover in the course. If you want to
            // learn about it, see e.g.
            // https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#package.description
            // or https://stackify.com/streams-guide-java-8/
            ngrams = Arrays.stream(ngrams).distinct().toArray(Ngram[]::new);
            files.put(path, ngrams);
        }

        return files;
    }

    // Phase 2: build index of n-grams (not implemented yet)
    static BST<Ngram, ArrayList<Path>> buildIndex(BST<Path, Ngram[]> files) {
        BST<Ngram, ArrayList<Path>> index = new BST<>();

        for (Path key : files.keys()) {
            Ngram[] ngrams = files.get(key);
            for (Ngram n : ngrams) {
                if (index.contains(n)) {
                    index.get(n).add(key);
                } else {

                    index.put(n, new ArrayList<>() {{
                        add(key);
                    }});
                }
            }
        }
        return index;
    }

    // Phase 3: Count how many n-grams each pair of files has in common.
    static BST<PathPair, Integer> findSimilarity(BST<Path, Ngram[]> files, BST<Ngram, ArrayList<Path>> index) {
        // TO DO: use index to make this loop much more efficient
        // N.B. Path is Java's class for representing filenames
        // PathPair represents a pair of Paths (see PathPair.java)
        BST<PathPair, Integer> similarity = new BST<>();
        /*for (Path path1: files.keys()) {
            for (Path path2: files.keys()) {
                if (path1.equals(path2)) continue;
                for (Ngram ngram1: files.get(path1)) {
                    for (Ngram ngram2: files.get(path2)) {
                        if (ngram1.equals(ngram2)) {
                            PathPair pair = new PathPair(path1, path2);

                            if (!similarity.contains(pair))
                                similarity.put(pair, 0);

                            similarity.put(pair, similarity.get(pair)+1);
                        }
                    }
                }
            }
        }*/

       /* List<Path> paths = new ArrayList<>();
        for (Path path : files.keys()){
            paths.add(path);
        }
        int amount = 0;
        Path path1;
        Path path2;
        for (int i = 0; i < paths.size(); i++){
            path1 = paths.get(i);
            for (int j = i+1; j < paths.size(); j++){
                amount = 0;
                path2 = paths.get(j);
                for (Ngram ngram : index.keys()){
                    if (index.get(ngram).contains(path1) && index.get(ngram).contains(path2)){
                        amount++;
                    }
                }
                similarity.put(new PathPair(path1, path2), amount);
            }

        }*/

        for (Ngram ngram : index.keys()) {
            ArrayList<Path> paths = index.get(ngram);
            for (int i = 0; i < paths.size(); i++) {
                for (int j = 0; j < paths.size(); j++) {
                    if (i == j)
                        continue;
                    PathPair pp = new PathPair(paths.get(i), paths.get(j));
                    if (similarity.contains(pp)) {
                        similarity.put(pp, 1 + similarity.get(pp));
                    } else {
                        similarity.put(pp, 1);
                    }
                }
            }

        }

        return similarity;
    }

    // Phase 4: find all pairs of files with more than 30 n-grams
    // in common, sorted in descending order of similarity.
    static ArrayList<PathPair> findMostSimilar(BST<PathPair, Integer> similarity) {
        // Find all pairs of files with more than 100 n-grams in common.
        ArrayList<PathPair> mostSimilar = new ArrayList<>();
        for (PathPair pair : similarity.keys()) {
            if (similarity.get(pair) < 30) continue;
            // Only consider each pair of files once - (a, b) and not
            // (b,a) - and also skip pairs consisting of the same file twice
            if (pair.path1.compareTo(pair.path2) <= 0) continue;

            mostSimilar.add(pair);
        }

        // Sort to have the most similar pairs first.
        Collections.sort(mostSimilar, Comparator.comparing((PathPair pair) -> similarity.get(pair)));
        Collections.reverse(mostSimilar);
        return mostSimilar;
    }

    private HashMap<Path, Ngram[]> flattenBST(BST<Path, Ngram[]> bst) {

        TreeMap<Path, Ngram[]> hm = new TreeMap<>();

        for (Path path : bst.keys()) {
            hm.put(path, bst.get(path)) {

            }
        }

    }

    private class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>> implements Comparable<Pair<T1, T2>> {

        public final T1 first;
        public final T2 second;

        Pair(T1 t1, T2 t2) {
            first = t1;
            second = t2;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int compareTo(Pair<T1, T2> t1T2Pair) {
            return first.compareTo(t1T2Pair.first);
        }
    }


}
