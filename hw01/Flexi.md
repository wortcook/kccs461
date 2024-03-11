# Flex Algorithm
This was an experiment around the idea of a search harness where different implementations
could be swapped out. The entire notion is based on an idea to be able to easily 
illustrate the fundamental variations on how the frontier is handled for each of the different
search algorithms.

For example, see the key difference between Breadth First and Depth First is
simply the choice of backing data structure. Or that any Best First algorithm likely
involves a list sorted by a cost function (a priority queue).

See [FlexiSearch.java](https://github.com/wortcook/kccs461/blob/main/hw01/src/main/java/edu/umkc/cs461/hw1/algorithms/FlexiSearch.java)

