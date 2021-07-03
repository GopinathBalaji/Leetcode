class Solution {
public:
    int arrangeCoins(int n) {
//         Method 1: O(n) time
        int count = 0;
       for(int i=1;i<=n;i++){
           if(n-i>=0){
                n = n-i;
               count++;
           }else{
               break;
           }
       }
        return count;
    }
};

// Method 2: Binary Search
// Assume that the answer is kk, i.e. we've managed to complete kk rows of coins. These completed 
// rows contain in total 1 + 2 + ... + k =(k (k + 1))/2
// We could now reformulate the problem as follows:
// Find the maximum kk such that {k (k + 1)}/{2} ≤N.
// The problem seems to be one of those search problems.
   /*
     public int arrangeCoins(int n) {
    long left = 0, right = n;
    long k, curr;
    while (left <= right) {
      k = left + (right - left) / 2;
      curr = k * (k + 1) / 2;

      if (curr == n) return (int)k;

      if (n < curr) {
        right = k - 1;
      } else {
        left = k + 1;
      }
    }
    return (int)right;
  }
   */

// Method 2: Math
// (k(k+1))/2 <= N
// k(k+1)≤2N
// By Completing the squares technique:
// (k+1/2)^2 -1/4 = <= 2N
// Therefore we can find the value of k from this equation

   /*
      public int arrangeCoins(int n) {

        // 1 + 2 + 3 + ... + (n - 1) + n = n(n + 1)/2
        // eg: 1 + 2 + 3 + 4 = n(n + 1)/2
        // therefore, n(n + 1)/2 = 10;
        // n^2 + n = 10 * 2
        // n^2 + n + (-2)*10 = 0
        // generalizing => n^2 + n + (-2)*c = 0;
        // quadratic equations=> [-1 (+/-) Sq.Root(b^2 - 4ac)] / 2a
        // Test #1 given 10:: 
        // for 1 + 2 + 3 + 4 = 10 => n^2 + n + (-2)*10 = 0;
        // root #1 = (-1 + root(1 - 4*(-2)*10)) / 2
        //         = (-1 + root(1 + 8a)) / 2 => ** pattern **
        //         = (-1 + root(81)) / 2
        //         = (-1 + 9) / 2
        //         = 4
        // Test #2 given 8:: 
        // n^2 + n + (-2)*8 = 0;
        // root #1 = (-1 + root(1 - 4*(-2)*8)) / 2
        //         = (-1 + root(1 + 8a)) / 2 => ** pattern **
        //         = (-1 + root(65)) / 2
        //         = (-1 + 8.06) / 2
        //         = (int) 7.06 / 2
        //         = 3
        
        // return (int) (-1 + Math.sqrt(1 + 8 * n)) / 2;
        // We need a long conversion to prevent overflow
        
        return (int) (-1 + Math.sqrt(1 + 8L * n)) / 2;
    }
   */


  