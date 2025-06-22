// Using extra space to maintain Prefix and Suffix products
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int[] prefix = new int[nums.length];
        int[] suffix = new int[nums.length];

        int prefixProduct = nums[0];
        prefix[0] = nums[0];
        
        for(int i=1;i<nums.length;i++){
            prefixProduct = prefixProduct * nums[i];
            prefix[i] = prefixProduct;
        }

        int suffixProduct = nums[nums.length -1];
        suffix[nums.length -1] = nums[nums.length -1];

        for(int i=nums.length-2;i>=0;i--){
            suffixProduct = suffixProduct * nums[i];
            suffix[i] = suffixProduct;
        }

        for(int i=0;i<nums.length;i++){
            if(i == 0){
                nums[i] = suffix[i+1];
            }else if(i == nums.length - 1){
                nums[i] = prefix[i-1];
            }else{
                int temp = prefix[i-1] * suffix[i+1];
                nums[i] = temp;
            }
        }

        return nums;
    }
}

// Using O(1) space
/*
The left product we call the prefix product.

The right product we call the suffix product.

First pass (left prefixes)

Initialize res[0] = 1 because there are no elements before index 0.

For i = 1…n-1:


res[i] = res[i-1] * nums[i-1];
After this loop, res[i] equals :

​
product of j=0 to j=i-1 (nums[j]).

Second pass (right suffixes)

Keep a scalar suffix = 1 to represent: 

​
Product of j=n to j=n-1 (nums[j]), i.e. empty.

Iterate i from n-1 down to 0:

Multiply the accumulated suffix into res[i]:


res[i] *= suffix;
Now res[i] = (left product) × (right product so far).

Update the suffix to include nums[i] for the next step left:


suffix *= nums[i];
By the time you finish, each res[i] has been multiplied by the product of all elements to its right.

Space complexity

We only ever use:

the input array nums (given),

the output array res (which doesn’t count extra, since it’s required),

two integers (i in the loops and suffix).

That’s O(1) auxiliary space.

Time complexity

Two simple scans of the array → O(n) overall.
*/

// class Solution {
//     public int[] productExceptSelf(int[] nums) {
//         int n = nums.length;
//         int[] res = new int[n];
        
//         // 1) First pass: compute prefix products into res[]
//         //    res[i] will hold the product of all nums[0..i-1].
//         res[0] = 1;
//         for (int i = 1; i < n; i++) {
//             res[i] = res[i - 1] * nums[i - 1];
//         }
        
//         // 2) Second pass: sweep from the right, maintaining a running suffix product.
//         //    At each i, multiply res[i] (which is “product of left side”) by suffix
//         int suffix = 1;
//         for (int i = n - 1; i >= 0; i--) {
//             res[i] = res[i] * suffix;
//             suffix = suffix * nums[i];
//         }
        
//         return res;
//     }
// }