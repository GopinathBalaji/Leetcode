// Method 1: 3-way Quicksort with many duplicate elements
/*
Your quicksort logic is mostly correct, but it has one **major issue for LeetCode 912**:

## Main issue: bad performance with many duplicates

You are using Lomuto partition:

```cpp
if (nums[j] <= pivot) {
    swap(nums[i], nums[j]);
    i++;
}
```

This works, but when the array has many duplicate values, it becomes very unbalanced.

Example:

```cpp
nums = [5, 5, 5, 5, 5]
```

Every element satisfies:

```cpp
nums[j] <= pivot
```

So the pivot always ends up at the end:

```text
pivotIndex = high
```

Then you recursively sort:

```cpp
quickSort(nums, low, high - 1);
```

So the recursion becomes:

```text
n, n-1, n-2, n-3, ...
```

That gives:

```text
O(n^2)
```

Even with a random pivot, duplicates still cause this problem because all values are equal.

For LeetCode 912, this can cause **TLE** or deep recursion issues.


# Better fix: 3-way quicksort

For arrays with many duplicates, use **3-way partitioning**.

Instead of splitting into:

```text
<= pivot | pivot | > pivot
```

split into:

```text
< pivot | == pivot | > pivot
```

This handles duplicate-heavy arrays much better.


After partitioning:

```text
nums[low ... lt - 1]     < pivot
nums[lt ... gt]          == pivot
nums[gt + 1 ... high]    > pivot
```

So if the array is:

```cpp
[5, 5, 5, 5, 5]
```

the entire array becomes the `== pivot` section, and no unnecessary recursive sorting happens.

---

## Main takeaway

Your code is logically correct for basic quicksort, but for LeetCode 912 it can fail because:

```text
Random pivot does not fix the duplicate-element worst case.
```

Use **3-way quicksort**, or use **merge sort / heap sort** for guaranteed safer performance.
*/
class Solution {
public:
    vector<int> sortArray(vector<int>& nums) {
        quickSort(nums, 0, nums.size()-1);

        return nums;
    }

private:
    void quickSort(vector<int>& nums, int low, int high){
        if(low >= high){
            return;
        }

        int randomIndex = low + rand() % (high - low + 1);
        int pivot = nums[randomIndex];

        int lesserThan = low;
        int i = low;
        int greaterThan = high;

        while(i <= greaterThan){
            if(nums[i] < pivot){
                swap(nums[lesserThan], nums[i]);
                lesserThan++;
                i++;
            }else if(nums[i] > pivot){
                swap(nums[i], nums[greaterThan]);
                greaterThan--;
            }else{
                i++;
            }
        }

        quickSort(nums, low, lesserThan-1);
        quickSort(nums, greaterThan+1, high);
    }
};




// Method 1.5: (DOESN'T WORK) Traditional Quicksort with random pivot
/*
*/
// class Solution {
// public:
//     vector<int> sortArray(vector<int>& nums) {
//         quickSort(nums, 0, nums.size()-1);

//         return nums;
//     }

//     void quickSort(vector<int>& nums, int low, int high){
//         if(low >= high){
//             return;
//         }

//         int pivotIndex = partition(nums, low, high);

//         quickSort(nums, low, pivotIndex - 1);
//         quickSort(nums, pivotIndex + 1, high);
//     }

//     int partition(vector<int>& nums, int low, int high){
//         // Using Random Pivot instead of the traditional approach of simply choosing the
//         // last element as pivot
//         int randomIndex = low + rand() % (high - low + 1);
//         swap(nums[randomIndex], nums[high]);

//         int pivot = nums[high];
//         int i = low;

//         for(int j=low; j<high; j++){
//             if(nums[j] <= pivot){
//                 swap(nums[i], nums[j]);
//                 i++;
//             }
//         }

//         swap(nums[i], nums[high]);

//         return i;
//     }
// };