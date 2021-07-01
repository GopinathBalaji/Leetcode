class Solution {
public:
    int majorityElement(vector<int>& nums) {
//         Method 1: Using HashMap (Not constant space)
         int count = nums.size()/2;
        unordered_map<int,int> m1;
        for(int i=0;i<nums.size();i++){
            m1[nums[i]]++;
            if(m1[nums[i]]>count){
                return nums[i];
            }
        }
       return 0;
    }
};

// Method 2: Sorting
// If the elements are sorted in monotonically increasing (or decreasing) order, the 
// majority element can be found at index n/2 or n/2 + 1 if n is even.
   /*
     public int majorityElement(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length/2];
    }
   */

// Method 3: Randomization
// Because a given index is likely to have the majority element, we can just select a 
// random index, check whether its value is the majority element, return if it is, and
// repeat if it is not.
    /*
     private int randRange(Random rand, int min, int max) {
        return rand.nextInt(max - min) + min;
    }

    private int countOccurences(int[] nums, int num) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == num) {
                count++;
            }
        }
        return count;
    }

    public int majorityElement(int[] nums) {
        Random rand = new Random();

        int majorityCount = nums.length/2;

        while (true) {
            int candidate = nums[randRange(rand, 0, nums.length)];
            if (countOccurences(nums, candidate) > majorityCount) {
                return candidate;
            }
        }
    }
    */

// Method 4: Divide and Conquer
// Here, we apply a classical divide & conquer approach that recurses on the left and right
// halves of an array until an answer can be trivially achieved for a length-1 array. Note 
// that because actually passing copies of subarrays costs time and space, we instead pass
// lo and hi indices that describe the relevant slice of the overall array. In this case, 
// the majority element for a length-1 slice is trivially its only element, so the 
// recursion stops there. If the current slice is longer than length-1, we must combine the 
// answers for the slice's left and right halves. If they agree on the majority element,
// then the majority element for the overall slice is obviously the same[1]. If they 
// disagree, only one of them can be "right", so we need to count the occurrences of 
// the left and right majority elements to determine which subslice's answer is 
// globally correct. The overall answer for the array is thus the majority element
// between indices 0 and nn.

   /*
   private int countInRange(int[] nums, int num, int lo, int hi) {
        int count = 0;
        for (int i = lo; i <= hi; i++) {
            if (nums[i] == num) {
                count++;
            }
        }
        return count;
    }

    private int majorityElementRec(int[] nums, int lo, int hi) {
        // base case; the only element in an array of size 1 is the majority
        // element.
        if (lo == hi) {
            return nums[lo];
        }

        // recurse on left and right halves of this slice.
        int mid = (hi-lo)/2 + lo;
        int left = majorityElementRec(nums, lo, mid);
        int right = majorityElementRec(nums, mid+1, hi);

        // if the two halves agree on the majority element, return it.
        if (left == right) {
            return left;
        }

        // otherwise, count each element and return the "winner".
        int leftCount = countInRange(nums, left, lo, hi);
        int rightCount = countInRange(nums, right, lo, hi);

        return leftCount > rightCount ? left : right;
    }

    public int majorityElement(int[] nums) {
        return majorityElementRec(nums, 0, nums.length-1);
    }

   */

// Method 5: Boyer Moore Voting Algo
// Eg) [7, 7, 5, 7, 5, 1 | 5, 7 | 5, 5, 7, 7 | 7, 7, 7, 7]
// Here, the 7 at index 0 is selected to be the first candidate for majority element. count
// will eventually reach 0 after index 5 is processed, so the 5 at index 6 will be the next 
// candidate. In this case, 7 is the true majority element, so by disregarding this prefix,
// we are ignoring an equal number of majority and minority elements - therefore, 7 will 
// still be the majority element in the suffix formed by throwing away the first prefix.

   /*
    public int majorityElement(int[] nums) {
        int count = 0;
        Integer candidate = null;

        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }
   */

// Method 6: Bit Masking
   /*
    int majorityElement(vector<int>& nums) {
        int candidate=0;
        int n=nums.size();
        for(int i=0;i<32;++i)
        {
            int ones=0;
            
            for(int j=0;j<n;++j)
                if(nums[j] & 1<<i)
                    ones++;
            
            if(ones>(n-ones))
                candidate|=1<<i;
        }
        
        return candidate;
        
    }
   */