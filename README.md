# RobinHood-Hashing
This is my implementation of the robinhood hashing algorithm for a linear hash map I worked on during my sophomore year.
The goal of the implementation was to test how much faster the robin hood algorithm is than the regular linear probing method.
An overall conclusion I found was that robin hood hashin allows for a hash table to push higher load factors, up towards 99% capacity,
without there being a significant effect on search times. Compared to the regular linear hash table which is not feasible for load factors
95% or above, the robin hood hashing algorithm is an efficient way to store and retrieve data without wasting much space in a list because 
of low load factors.
