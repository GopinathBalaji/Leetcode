// Computing Prefix and Suffix Product Arrays (Uses extra space)
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int prefixProduct = 1;
        int suffixProduct = 1;

        int[] prefixPro = new int[nums.length];
        int[] suffixPro = new int[nums.length];
        int[] ans = new int[nums.length];

        for(int i=0; i<nums.length; i++){
            prefixProduct = prefixProduct * nums[i];
            prefixPro[i] = prefixProduct; 
        }

        for(int i=nums.length-1; i>=0; i--){
            suffixProduct = suffixProduct * nums[i];
            suffixPro[i] = suffixProduct;
        }

        for(int i=0; i<nums.length; i++){
            if(i == 0){
                ans[i] = suffixPro[i+1];
            }else if(i == nums.length - 1){
                ans[i] = prefixPro[i-1];
            }else{
                ans[i] = prefixPro[i-1] * suffixPro[i+1];
            }
        }

        return ans;
    }
}


// No Extra Space
/*
We only need the prefix up to i-1 and the suffix from i+1, which can be folded into two passes using just the output array and one running suffix.

How it works:
* After the first pass, `ans[i]` holds the product of all elements **to the left** of `i`.
* The second pass walks from right to left keeping a running `suffix` = product of elements **to the right** of `i`. Multiply it into `ans[i]`.
* This naturally handles **zeros**:

  * One zero → only that index gets the non-zero product; others become 0.
  * Two or more zeros → all results are 0.


### Walkthrough

`nums = [1, 2, 3, 4]`

* Pass 1 (prefix): `ans = [1, 1, 2, 6]`
* Pass 2 (suffix running):

  * i=3: ans\[3] \*= 1 → 6; suffix \*= 4 → 4
  * i=2: ans\[2] \*= 4 → 8; suffix \*= 3 → 12
  * i=1: ans\[1] \*= 12 → 12; suffix \*= 2 → 24
  * i=0: ans\[0] \*= 24 → 24
    Result: `[24, 12, 8, 6]`.

`nums = [1, 2, 0, 4]`

* Pass 1: `ans = [1, 1, 2, 0]`
* Pass 2: suffix=1

  * i=3: ans\[3]=0\*1=0; suffix=4
  * i=2: ans\[2]=2\*4=8; suffix=0
  * i=1: ans\[1]=1\*0=0
  * i=0: ans\[0]=1\*0=0
    Result: `[0, 0, 8, 0]`.

This meets O(n) time and O(1) extra space (excluding the output array), avoids out-of-bounds, and handles zeros correctly.
*/
// class Solution {
//     public int[] productExceptSelf(int[] nums) {
//         int n = nums.length;
//         int[] ans = new int[n];
//         if (n == 0) return ans;
        
//         // 1) ans[i] = product of nums[0..i-1]  (prefix before i)
//         ans[0] = 1;
//         for (int i = 1; i < n; i++) {
//             ans[i] = ans[i - 1] * nums[i - 1];
//         }

//         // 2) Multiply by suffix product of nums[i+1..n-1]
//         int suffix = 1;
//         for (int i = n - 1; i >= 0; i--) {
//             ans[i] *= suffix;
//             suffix *= nums[i];
//         }
//         return ans;
//     }
// }