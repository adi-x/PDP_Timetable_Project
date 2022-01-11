# Project
## Generate a school timetable through exhaustive search

Given an input file containing the courses, the program generates a school time table by trying to place each existing course in each available slot.
Additionally, the program can take an extra parameter which determines the number of courses that can take place in a day.
The problem is `NP-Complete`, so the program needs to explore all possible combinations to find the list of acceptable solutions. 
In the end, only one solution is given as output;

### Parallelized solution:
+ Implementation

The parallelized solution runs a thread for each course.
It places each of the input courses in the first slot available,
and then runs the algorithm in parallel for the remaining courses, with the reamining slots.
When a solution is found, the thread returns the timetable as computed.
If with a certain combination it would be impossible to yield a solution, the algorithm returns null.

+ Synchronization

To avoid clashing between the running threads, each one of them is running on a deep copy of the original timetable.
This way, since no resources are shared, no concurrency problems appear.
  
