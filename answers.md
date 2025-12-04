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

Based on the average execution times, Profile (C) (100% reads) shows the highest raw throughput, but this result is not meaningful because the tree is rebuilt empty on every run and no operations ever modify it its just for reads. For realistic performance, the important comparison is between Profile (A) (logging: 10/85/5) and Profile (B) (read-heavy: 90/5/5). Profile (B) clearly achieves higher throughput (≈13.82 req/s) than Profile (A) (≈7.70 req/s). This difference is explained by how a red-black tree handles different operations: get is cheap and simply walks the tree, while put and delete both perform tree rebalancing operations such as rotations and color flips. These structural changes make inserts and deletes significantly more expensive per operation. So To confirm this, I modified the benchmark harness to pause before starting the timed iterations, attached VisualVM, enabled CPU profiling, and then resumed execution. In the logging profile (A), VisualVM showed that put and its helper methods (rotateLeft, rotateRight, color-fix routines) consumed the majority of CPU time, even more than their 85% share of operations would suggest. Deletes also appeared as expensive operations, while get consumed very little CPU. In the read-heavy profile (B), get dominated the operation count (~11 million calls) and used minimal CPU per call, while put and delete still stood out as the main hotspots despite being only 5% each of the operations. This profiling behavior matches the throughput results and confirms that expensive update operations are the primary bottleneck, and workloads dominated by lookups naturally achieve higher throughput. 

5. Are your warmup times noticeably different from the “main” iteration times? Most likely yes, but either way, why might we expect a significant difference?

Yes, the warmup times in all of my profiles were noticeably different from the main iteration times. This is expected because the JVM behaves very differently during the first execution of the code. The warmup run includes several one-time costs:
Class Loading and Initialization
The first call loads the Benchmark class, the RedBlackBST class, and all of their inner classes. None of these classes are loaded during compilation, so the first execution pays that cost.
JIT Compilation (Just-In-Time Optimization)
The JVM initially interprets bytecode, then detects “hot” methods—such as put, get, and delete—and compiles them into optimized native machine code. This compilation occurs during or shortly after the warmup, making later iterations faster.
Cache Warming
CPU instruction cache
Branch predictor state
Data caches (L1, L2, L3)
Memory pages being pulled into RAM
These caches are cold during the warmup and become optimized as the benchmark runs.
Random JVM Runtime Overhead
The first run may also trigger initial heap allocations or an early minor garbage collection cycle.
Because all of these costs occur during the warmup phase but not during the later runs, the warmup iteration is significantly slower than the main benchmark iterations. The main runs, by contrast, execute optimized code with warm caches, so their timing is more stable and representative of actual performance in a steady-state system.

6. The numbers for operational profile (c) aren’t actually that useful. Look a the benchmarking code, and explain why those numbers, as reported, tell us nothing about the performance of using the BST in a real application for a read-only workload.

The numbers for operational profile (c) (100% reads, 0 inserts, 0 deletes) are not useful for understanding real read-only BST performance because the benchmark never creates a persistent tree. Each call to runOps starts with a completely empty red-black tree, and because the read-only workload performs no inserts, every get operation simply traverses an empty structure and immediately returns null. This means the benchmark is not measuring real lookup cost in a populated tree; it is only measuring the overhead of a failed lookup in an empty tree.

In a real application, a read-only workload would be reading from a tree that already contains data, and lookups would require traversing a path of height O(log n). In the benchmark, however, the tree never grows and always stays empty, so the measured times are artificially small and do not reflect real-world behavior. Therefore, the reported numbers for profile (c) tell us nothing meaningful about performance in a true read-only scenario, because the benchmark is not modeling the presence of stored data.

7. When I run the test for either of my operational profiles with instrumentation-based profiling enabled, my measurements slow down by a factor of 10 or more. Why?

The numbers for operational profile (c) (100% reads, 0 inserts, 0 deletes) are not useful for understanding real read-only BST performance because the benchmark never creates a persistent tree. Each call to runOps starts with a completely empty red-black tree, and because the read-only workload performs no inserts, every get operation simply traverses an empty structure and immediately returns null. This means the benchmark is not measuring real lookup cost in a populated tree; it is only measuring the overhead of a failed lookup in an empty tree.

In a real application, a read-only workload would be reading from a tree that already contains data, and lookups would require traversing a path of height O(log n). In the benchmark, however, the tree never grows and always stays empty, so the measured times are artificially small and do not reflect real-world behavior. Therefore, the reported numbers for profile (c) tell us nothing meaningful about performance in a true read-only scenario, because the benchmark is not modeling the presence of stored data.

8. Assume each run (each call to runOps) simulates the activity of one remote request. Based on the average execution time for each operational profile, what would the throughput be for each of your profiles? (i.e., requests/second)

Based on the average execution time for each operational profile, I would each invocation of runOps as one request. The throughput formula requests/second => 10^9 / AvgTime (nano seconds). So doing this using the average iteration times for the answer to Question 3.

For Profile A (Logging 10/85/5) The average time was 129,807,567 ns. So the throughput would be
10^9/129,807,567 => 7.70 requests per second.

For Profile B (Read Heavy 90/5/5) The average time was 72,380,980 ns. So the throughput would be
10^9/72,380,980 => 13.82 requests per second.

For Profile C (Read Only 100/0/0) The average time was 8,242,793 ns. So the throughput would be
10^9/8,242,793 => 121.32 request/second. 

Profile C is only relaly hihg because the tree is never popualted and every get returns immediately from an empty sturcture. 

9. Assuming each remote user makes 5 requests/minute, your program’s resource usage scales linearly, and we are only interested in CPU execution time, how many concurrent remote users could you support on your machine (again, do this for each operational profile) without degrading performance or overloading the system?

Assuming each remote user makes 5 requests/minute, that means its 5/60 => 0.0833 requests/second per user. So if the system's resource usage scale linearly and we only consider CPU time, the maximum number of conccurent users we can support will be:

Max Users = Throughput / (5/60) => Throughput * 12.

So based on this using our throughputs from the reposne to qeusetion 8:

For Profile A (Logging 10/85/5) the throughput was 7.70 requests/second so the Max users are
7.70*12 => 92.4 => 92 concurrent users.

For Profile B (Read-Heavy 90/5/5) the throughput was 13.82 requests/second so the Max users are
13.82 * 12 => 165.8 => 166 concurrent users.

For Profile C (Read-Only 100/0/0) the throughput was 121.32 requests/second so the Max users are 
121.32 * 12 => 1455.8 => 1,456 concurrent users.

As stated in earlier questions, the value for profile C isn't very meaningful because it doesn't represent the real-world since the tree never gets popualted and all lookups are empty.