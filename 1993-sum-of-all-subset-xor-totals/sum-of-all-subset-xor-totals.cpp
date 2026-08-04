// Method 1: Recursive approach
/*
### Hint 1: Build subsets recursively

At each index, you have two choices:

* Exclude `nums[i]` → XOR stays the same.
* Include `nums[i]` → XOR becomes `currentXor ^ nums[i]`.

When you reach the end of the array, add the current XOR to the total.

### Hint 2: You do not need to store subsets

Your recursive state only needs:

```text
(index, currentXor)
```

This gives an `O(2^n)` solution, which is sufficient for the problem’s small constraints.

### Hint 3: There is also a mathematical shortcut

Think about each binary bit independently. If a bit appears in **at least one** number, exactly half of all subsets will have that bit set in their XOR.

The bits that appear anywhere are represented by:

```text
nums[0] | nums[1] | ... | nums[n - 1]
```

Use that observation to derive an `O(n)` formula involving `2^(n - 1)`.
*/
class Solution {
private:
    int subsetXOR(vector<int>& nums, int index, int currentXor){
        if(index >= nums.size()){
            return currentXor;
        }

        int exclude = subsetXOR(nums, index + 1, currentXor);
        int include = subsetXOR(nums, index + 1, currentXor ^ nums[index]);

        return exclude + include;
    }

public:
    int subsetXORSum(vector<int>& nums) {
        return subsetXOR(nums, 0, 0);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna