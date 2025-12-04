package se320;

import java.util.Random;

public class Benchmark {

    // number of operations
    public static int OPS = 1000000;

    // Random object
    public static Random rand = new Random();

    // main func
    public static void main(String[] args) {

        // actions
        int reads = 100;
        int inserts = 0;
        int deletes = 0;

        // print profile
        System.out.printf("PROFILE: reads=%d, inserts=%d, deletes=%d%n",
                  reads, inserts, deletes);

        // 
        try {
            System.out.println("============Q4. VisualVM key to benchmark.============");
            System.in.read();   
        } catch (Exception e) {
            e.printStackTrace();
        }

        // warmup start time
        long warmupStart = System.nanoTime();
        
        // warmup run
        runOps(reads, inserts, deletes);
        
        // warmup elapsed time
        long warmupElapsed = System.nanoTime() - warmupStart;
        System.out.println("Warmup round:    "+String.format("%12s",warmupElapsed)+"ns");

        // benchmark runs
        long[] elapsed = new long[10];
        for (int i = 0; i < 10; i++) {
            // start time
            long startTime = System.nanoTime();
            runOps(reads, inserts, deletes);
            long estimated = System.nanoTime() - startTime;
            elapsed[i] = estimated;
        }

        /* Print the times, as well as the max, min, and average */
        long total = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (int i = 0; i < 10; i++) {
            System.out.println("Benchmark run "+i+": "+String.format("%12s",elapsed[i])+"ns");
            total += elapsed[i];
            if (elapsed[i] < min) min = elapsed[i];
            if (elapsed[i] > max) max = elapsed[i];
        }
        System.out.println("Average for "+OPS+" operations: "+String.format("%12s",(total/10))+"ns");
        System.out.println("Minimum for "+OPS+" operations: "+String.format("%12s",min)+"ns");
        System.out.println("Maximum for "+OPS+" operations: "+String.format("%12s",max)+"ns");
    }

    public static void runOps(int reads, int inserts, int deletes) {
        // create new red-black tree
        RedBlackBST<Integer,Integer> tree = new RedBlackBST<Integer,Integer>();
        
        // total bound for inserts
        int insert_bound = reads + inserts;

        // last used number
        int lastn = 0;

        // perform operations 
        for(int i = 0; i < OPS; i++) {
            int v = rand.nextInt(100);
            if (v < reads) {
                // do read
                tree.get(lastn);
            } else if (v < insert_bound) {
                // do insertion of random key
                tree.put(lastn, lastn);
            } else {
                // do deletion of random key
                tree.delete(lastn);
            }
            lastn = v;
        }
    }
}




