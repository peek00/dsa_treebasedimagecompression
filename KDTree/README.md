# KD - Trees Implementation

### Implementation

### Compression (KD Tree Inspired)
1. Treat RGB layers and separate and compress them separately.
For each layer, 
2. Store the size of the entire array as an integer. 
3. Need to find a way to identify their pixel position. When decompress, must maintain.
4. Need to find a way to get unique values of all pixel positions, aka do a set. (Compression)
5. Sort the unique pixel range.
6. Pick the median of the pixel range to partition data into two halves.
7. Create a Kd-Tree node for current spliting axis and splitting value.
8. Continue until desired. (stoppping condition can be number of nodes)  
    (Compression, this will be a hyper parameter, I am thinking of making it a hyper parameter)
9. Can rebalance tree. 
10. Need to save the pixel values and the original array position

> Clarify that we just need to save the original pixel position and the decompress algo will do the rest.

### Decompression
1. We first construct an array of desired length from the information so we can insert via index. 
2. We traverse the tree from the top down and insert the pixels accordingly. 