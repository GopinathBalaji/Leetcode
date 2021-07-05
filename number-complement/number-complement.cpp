class Solution {
public:
    int findComplement(int num) {
      int binarylen = floor(log2(num)) + 1;   //find length of number in binary
        int mask = (long long int) (1<<binarylen) - 1;  // create a mask consisting of 1 of the same length
        return num^mask;   // now we can perform XOR which will flip the bits  
    }
};

