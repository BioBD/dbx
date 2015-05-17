# DBX 
	A tool to aid the creation of indexes and materialized views in relational databases

## Intro

As databases grow in size and complexity, it is natural that it's queries 
start to become slower and slower. When that happens, database tuning comes in handy.
Some tuning techniques are creating indexes and materialized views.
It can be hard to find out what indexes and materialized views are going to be best for the workload.
The proposal of DBX is to help DBAs in that subject.

## Technical details

The project is completely written in java, using the Netbeans IDE. 
It's architecture is based on software agents, which monitor the database,
suggest improvements and implement them. The tool is non-intrusive, meaning that
it does not alter the source code of the database and can be used in many DBs with 
small adaptations. It is currently compatible with PostgreSQL. 

## How to use it
1. Clone or download the repository
2. Alter the file *src/base/template.database.properties.* to the configuration of your own database.
3. Open the project with Netbeans, and press the 'run' button to start it.
4. DBX will then start to capture statistics of the queries made to your database.
5. The solution will then create the materialized views it found to be best based on the statistics gathered. 


## To-Do
* Distribute the solution without Netbeans
* Create an informal example of the program in use 

## Team 

[PUC-Rio](http://www.puc-rio.br/english/)'s [Database Self-Tuning Group](http://www.inf.puc-rio.br/~postgresql/index.php?lan=en)
