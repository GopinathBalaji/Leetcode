class Solution {
public:
//     Method 1: Hash Map
   vector<int> twoSum(vector<int>& numbers, int target) {
        unordered_map<int, int> mp;
        for(int i = 0; i < numbers.size(); i++) {
            if(mp.find(numbers[i]) != mp.end()) return { mp.find(numbers[i])->second + 1, i + 1 };
            else mp[target-numbers[i]] = i;
        }
        return { };
   }
};

// Method 2: Two Pointer
   /*
     vector<int> twoSum(vector<int>& numbers, int target) {
        int l = 0;
        int r = numbers.size() - 1;
        while(l < r) {
            if(numbers[l] + numbers[r] == target) return { l + 1, r + 1 };
            else if(numbers[l] + numbers[r] < target) l++;
            else r--;
        }
        return { };
    }
   */

// Method 3: Binary Search
   /*
    public int[] twoSum(int[] numbers, int target) {
        for(int i = 0; i < numbers.length - 1; i++){
            int low = i + 1, high = numbers.length - 1, mid = low + (high - low)/2;
            while(low <= high){
                if(numbers[mid] == target - numbers[i]) return new int[]{i+1, mid+1};
                else if(numbers[mid] < target - numbers[i]) low = mid + 1;
                else high = mid - 1;
                mid = low + (high - low)/2;
            }
        }
        throw null;
    }
   */