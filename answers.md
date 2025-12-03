1. A short (one paragraph) explanation of why the benchmark harness is structured as it is. Why is there an initial run that isn’t counted in the min/max/average? Why are we measuring multiple runs?

    The bencharmk does one warmup run that is not included in the statistics because the first execution will include JVM overhead. This overhaed is class loading, bytecode interpretation, and JIT compilation. With these included in the run it will make the first run artifically slow and skew the output results. After warming up the, the JVM is now able to execute optimizes native code. We are meassuginr multiple runs because it will help out even out noise from the OS such as scheduling, caching, garbage collection, and random background activity that could appear. Because I am doing this on tux, there are a multitude of things that could appear while running the script. If we take multiple samples we are able to compute a mor stable min, average, and max and get a realistic sense of performance.

2. Reasonable operational profiles for use of the RedBlackBST in the following use cases. Each of these profiles, for our purposes, is simply a percentage of insert/delete/lookup operations (3 numbers that sum to 100 in each case).
    1. A logging data structure, which mostly records new data, with occasional queries and edits (deletions)
        Reads: 10%
        Inserts: 85%
        Deletes: 5%
    2. A read-heavy database, with occasional updates and deletions.
        Reads: 90%
        Inserts: 5%
        Deletes: 5%
    3. A read-only database, with no modifications performed.
        Reads: 100%
        Inserts: 0%
        Deletes: 0%

3. For each of your operational profiles, report the following, which should be produced by Benchmark.main() once you update the hard-coded operational profile.
    
    ### Profile (A) - Logging (10/85/5)
    1. Warmup time: 214,116,214 ns 
    2. Minimum iteration time: 125,270,128 ns
    3. Maximum iteration time: 142,527,323 ns
    4. Average iteration time: 129,807,567 ns

    ### Profile (B) - Read-Heavy (90/5/5)
    1. Warmup time: 125,875,768 ns
    2. Minimum iteration time: 66,217,320 ns
    3. Maximum iteration: 90,890,687 ns
    4. Average iteration time: 72,380,980 ns
    
    ### Profile (C) - Read-Only (100/0/0)
    1. Warmup time: 23,988,867 ns
    2. Minimum iteration time: 6,447,372 ns
    3. Maximum iteration time: 14,559,245 ns
    4. Average iteration time: 8,242,793 ns

4. Which of your operational profiles has higher throughput (i.e., performs work work per unit time)? What about the red-black tree might explain this outcome? Confirm (or disprove and revisit your hypothesis) by: modifying the benchmark harness to wait for a key press, loading up VisualVM (download Standalone), attaching to the program while it is waiting, and doing CPU profiling of the benchmark. Pay attention to what percentage of time is spent in each of put, get, and delete (and their helper methods). A method that takes a higher percentage of time than it is given as a percentage of the operational profile is a likely bottleneck. (Note, however, that a random number generator is involved here.)

    The operational profile that has highest throughput is Profile C where its Read only, but its not realistic. SO the next highest would be Profile B where its Read-Heavy. This is higher than Profile A (Logging Profile) because red-black trees inserts and deletes are more expensive than lookups. The loggin workload spends most of it's time doing put, which performs rotations and colors fixes. The read-heavy does mostly get, which has to traverse the tree and is cheaper per operation. Profile C (Read-Only) sows the highest raw througput, because it's only doing failed lookups on empty trees, which doesn't match the real world. 

5. Are your warmup times noticeably different from the “main” iteration times? Most likely yes, but either way, why might we expect a significant difference?

6. The numbers for operational profile (c) aren’t actually that useful. Look a the benchmarking code, and explain why those numbers, as reported, tell us nothing about the performance of using the BST in a real application for a read-only workload.

7. When I run the test for either of my operational profiles with instrumentation-based profiling enabled, my measurements slow down by a factor of 10 or more. Why?

8. Assume each run (each call to runOps) simulates the activity of one remote request. Based on the average execution time for each operational profile, what would the throughput be for each of your profiles? (i.e., requests/second)

9. Assuming each remote user makes 5 requests/minute, your program’s resource usage scales linearly, and we are only interested in CPU execution time, how many concurrent remote users could you support on your machine (again, do this for each operational profile) without degrading performance or overloading the system?