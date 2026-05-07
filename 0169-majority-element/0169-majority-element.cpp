// Method 1: Boyer-Moore Voting Algorithm (O(n) time and in O(1) space)
/*
################### WHAT WAS I DOING WRONG ##################
Your approach is trying to implement **Boyer-Moore Voting Algorithm**, but the candidate update is in the wrong place.

The problem is here:

```cpp
if(count == 0){
    majorEle = nums[i];
}
```

You update `majorEle` after already using `nums[i]` to cancel the count, but you leave:

```cpp
count = 0;
```

So the new candidate has no active vote.

---

Example where your code fails:

```cpp
nums = [3, 2, 3]
```

Your code does:

```text
majorEle = 3, count = 0

i = 0, nums[i] = 3
count++ → count = 1

i = 1, nums[i] = 2
count-- → count = 0
majorEle = 2

i = 2, nums[i] = 3
majorEle != nums[i]
count-- → count = -1

return 2
```

But the correct answer is:

```cpp
3
```

---

The fix is: when `count == 0`, choose the current element as the new candidate **before comparing**, then give it one vote.
*/
class Solution {
public:
    int majorityElement(vector<int>& nums) {
        int majorEle = nums[0];
        int count = 0;

        for(int i=0; i<nums.size(); i++){
            if(count == 0){
                majorEle = nums[i];
            }

            if(majorEle == nums[i]){
                count++;
            }else{
                count--;
            }
        }

        return majorEle;
    }
};