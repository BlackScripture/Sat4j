# Sat4j
Solver in Sat4j for solving an nxn EMP

sat4j is imported via maven dependencies

sat4j maven: [https://mvnrepository.com/artifact/org.sat4j](https://mvnrepository.com/artifact/org.ow2.sat4j)

sat4j maxsat maven: https://mvnrepository.com/artifact/org.ow2.sat4j/org.ow2.sat4j.maxsat

---------------------------------------

PuzzleManager.java is for Puzzle creation

PlacementManager.java if for evaluating the solution

OneSolverExclusionNxN.java is for the clauses and solver

---------------

explanation for the output:

![output](https://github.com/user-attachments/assets/ba284700-f6b3-4605-8ec4-c07d83ea5882)

puzzle piece id: 29

edges: (outer ring of a piece) top, right, bottom, left (color expressed by number, 0 = grey and is to be placed on border) and have to be equal on neighbouring pieces
0,2,4,0
       
symbols: (inner ing of a piece) 1 and 2 are the two halfs of an arrow and have to be unequal on neighbouring pieces

a seed for the puzzle generation can be set in PuzzleManager.java in line 53

since seed is set, the same input generates  the same puzzle

--------------------------------

the problem:

softclause for horizontal color matching in line 186 in OneSolverExclusionNxN.java

when generating the puzzle with inputs: (set seed at 64L, input for dimension = 6, input for colors = 6)

the output is: Anzahl ungleicher Nachbarfarben waagerecht: 0

when changing the input to for example (input for dimension = 6, input for colors = 5)

the putput is: Anzahl ungleicher Nachbarfarben waagerecht: 8

but when changing the clause to a hradclause, for (input dimension = 6, input for colors = 5)

the output is: Anzahl ungleicher Nachbarfarben waagerecht: 0

so the solver somehow messes up the calculation with soft clauses for many inputs, but also gets the solution right for some inputs
