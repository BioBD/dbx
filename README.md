# DBX 
	A tool to aid the creation of indexes and materialized views

## Intro

As databases grow in size and complexity, it is natural that it's queries 
start to become slower and slower. When that happens, database tuning comes in handy.
Some tuning techniques are creating indexes and materialized views.
It can be hard to find out what indexes and materialized views are going to be best for the workload.
The proposal of DBX is to help DBAs in that subject. Even further, another objective of DBX is to prove 
that a selected index is in fact useful for the database, and not only a guess made by the DBA.

## Technical details

The project is completely written in java, using the Netbeans IDE. 
It's architecture is based on software agents, which monitor the database,
suggest improvements and implement them. The tool is non-intrusive, meaning that
it does not alter the source code of the database and can be used in many DBs with 
small adaptations. It is currently compatible with PostgreSQL. 

## How do I try it?

COMING SOON

## Team 

[PUC-Rio](http://www.puc-rio.br/english/)'s [Database Self-Tuning Group](http://www.inf.puc-rio.br/~postgresql/index.php?lan=en)
