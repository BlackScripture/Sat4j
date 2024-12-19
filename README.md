# Sat4j
Solver in Sat4j for solving an nxn EMP

sat4j is imported via maven dependencies

sat4j maven: [https://mvnrepository.com/artifact/org.sat4j](https://mvnrepository.com/artifact/org.ow2.sat4j)

sat4j maxsat maven: https://mvnrepository.com/artifact/org.ow2.sat4j/org.ow2.sat4j.maxsat

--------------------------------------

when cloning the repo maven has to be built and compiled, then OnceSolverExclusion should be runnable

![maven](https://github.com/user-attachments/assets/485bc5e1-874f-4e59-b314-8bae21563f96)

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

![1](https://github.com/user-attachments/assets/c2a83d5a-c2c9-4f10-ac9c-64722233be67)

the output is: Anzahl ungleicher Nachbarfarben waagerecht: 0

when changing the input to for example (input for dimension = 6, input for colors = 5)

![2](https://github.com/user-attachments/assets/13aa781f-51f3-4094-bca6-f16179989fcf)

the output is: Anzahl ungleicher Nachbarfarben waagerecht: 8 even though it should be 0, which can be checked by changing the softclause to a hardclause

but when changing the clause to a hardclause, for (input dimension = 6, input for colors = 5)

the output is: Anzahl ungleicher Nachbarfarben waagerecht: 0

so the solver somehow messes up the calculation with soft clauses for many inputs, but also gets the solution right for some inputs
