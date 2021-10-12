class Solution {
public:
//     Method 1: Sliding Window using While loop and 2 pointers
    
    double findMaxAverage(vector<int>& nums, int k) {
        int n = nums.size();
        if(n<k){
            return 0.0;
        }
        
        double sum = 0;
        int i=0,j=0;
        while(j < k-1){   //getting sum till k-1
            sum += nums[j];
            j++;
        }
        
        double max = INT_MIN;
        while(j < n){
            sum += nums[j];   //adding kth element in sum
            
            double avg = sum/k;   //finding average of k elements
            if(avg > max){
                max = avg;   //if avg is greater than prev update it
            }
            sum -= nums[i];    //subtract the nums[j] value from sum
            i++;      //increment i pointer
            j++;     //increment j pointer
        } 
        return max;
    }
};

// Method 2: Simpler Sliding Window
   /*
    double findMaxAverage(vector<int>& nums, int k) {
        double sum=0, res=INT_MIN;
        for(int i=0;i<nums.size();i++) {
            if(i<k) sum+=nums[i];
            else {
                res=max(sum, res);
                sum+=nums[i]-nums[i-k];
            }
        }
        res=max(sum, res);
        return res/k;
    }
   */