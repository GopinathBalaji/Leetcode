class Solution {
public:
    int hammingDistance(int x, int y) {
      int n = x^y;
        int res = 0;
        while(n){
            res += n&1;
            n >>= 1;
        }
        return res;
    }
}; 


// Method 2: Brian Kernighan’s Algorithm
// For example, consider number 52, which is 00110100 in binary, and has a total 3 bits set.

// 1st iteration of the loop: n = 52
 
// 00110100    &               (n)
// 00110011                    (n-1)
// ~~~~~~~~
// 00110000
 
 
// 2nd iteration of the loop: n = 48
 
// 00110000    &               (n)
// 00101111                    (n-1)
// ~~~~~~~~

// 00100000
 
 
// 3rd iteration of the loop: n = 32
 
// 00100000    &               (n)
// 00011111                    (n-1)
// ~~~~~~~~
// 00000000                    (n = 0)

   /*
     int hammingDistance(int x, int y) {
        int z = x^y, count = 0;
        while(z){
            z &= (z-1);
            count ++;
        }
        return count;
    }
   */

// Method 3: Using Bitset (C++)
   /*
    int hammingDistance(int x, int y) {
bitset<32> b(x^y);
return b.count();
}
   */

