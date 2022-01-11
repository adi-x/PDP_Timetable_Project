# Project
## Generate a school timetable through exhaustive search

Given an input file containing the courses, the program generates a school time table by trying to place each existing course in each available slot.
Additionally, the program can take an extra parameter which determines the number of courses that can take place in a day.
The problem is `NP-Complete`, so the program needs to explore all possible combinations to find the list of acceptable solutions. 

### Parallelized solution:
+ Implementation

The parallelized solution runs a thread for each course.
It places each of the input courses in the first slot available,
and then runs the algorithm in parallel for the remaining courses, with the reamining slots.
When a solution is found, the thread returns the timetable as computed.
If with a certain combination it would be impossible to yield a solution, the algorithm returns null.

For the MPI version, we schedule requests on the available processes such that the final computation is computed with the help of all of the processes. In the end each process will have a near equal contribution to the final result. 

+ Synchronization

To avoid clashing between the running threads, each one of them is running on a deep copy of the original timetable.
This way, since no resources are shared, no concurrency problems appear.
  

### Performance:
|            	| groups 	| subjects 	| processors 	| time 	|
|------------	|--------	|----------	|------------	|------	|
| no clashes 	| 25     	| 1        	| 3          	| 952  	|
| no clashes 	| 3      	| 3        	| 3          	| 365  	|
| no clashes 	| 3      	| 4        	| 3          	| 492  	|
