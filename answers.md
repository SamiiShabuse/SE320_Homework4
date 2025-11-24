1. A short (one paragraph) explanation of why the benchmark harness is structured as it is. Why is there an initial run that isn’t counted in the min/max/average? Why are we measuring multiple runs?

2. Reasonable operational profiles for use of the RedBlackBST in the following use cases. Each of these profiles, for our purposes, is simply a percentage of insert/delete/lookup operations (3 numbers that sum to 100 in each case).
    1. A logging data structure, which mostly records new data, with occasional queries and edits (deletions)
    2. A read-heavy database, with occasional updates and deletions.
    3. A read-only database, with no modifications performed.

3. For each of your operational profiles, report the following, which should be produced by Benchmark.main() once you update the hard-coded operational profile.
    1. Warmup time
    2. Minimum iteration time
    3. Maximum iteration time
    4. Average iteration time
    
4. Which of your operational profiles has higher throughput (i.e., performs work work per unit time)? What about the red-black tree might explain this outcome? Confirm (or disprove and revisit your hypothesis) by: modifying the benchmark harness to wait for a key press, loading up VisualVM (download Standalone), attaching to the program while it is waiting, and doing CPU profiling of the benchmark. Pay attention to what percentage of time is spent in each of put, get, and delete (and their helper methods). A method that takes a higher percentage of time than it is given as a percentage of the operational profile is a likely bottleneck. (Note, however, that a random number generator is involved here.)

5. Are your warmup times noticeably different from the “main” iteration times? Most likely yes, but either way, why might we expect a significant difference?

6. The numbers for operational profile (c) aren’t actually that useful. Look a the benchmarking code, and explain why those numbers, as reported, tell us nothing about the performance of using the BST in a real application for a read-only workload.

7. When I run the test for either of my operational profiles with instrumentation-based profiling enabled, my measurements slow down by a factor of 10 or more. Why?

8. Assume each run (each call to runOps) simulates the activity of one remote request. Based on the average execution time for each operational profile, what would the throughput be for each of your profiles? (i.e., requests/second)

9. Assuming each remote user makes 5 requests/minute, your program’s resource usage scales linearly, and we are only interested in CPU execution time, how many concurrent remote users could you support on your machine (again, do this for each operational profile) without degrading performance or overloading the system?