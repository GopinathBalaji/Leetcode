class Solution {
public:
//     Method 1: Brute Force
    int minTaps(int n, vector<int>& ranges) {
        int  min = 0;
        int max = 0;
        int count = 0;
        
//         when max is >= n that means we have covered the entire garden
        while(max<n){
            
            //Choose the tap with maximum range it can reach to right
            //For a given min value (to left)
            for(int i=0;i<ranges.size();i++){
                if(i-ranges[i]<=min && i+ranges[i]>max){
                    max = i+ranges[i];
                }
            }
            
            //It means we couldn't expand our range to right
            if(max==min){
                return -1;
            }
            
             //Now, we have reached till max, next we want to cover more than this max
            //starting from min
            min = max;
            count++;
        }
        return count;
    }
};

// Method 2: Bottom Up Dynamic Programming
   /*
     int minTaps(int n, vector<int>& ranges) {
         vector<int> dp(ranges.size(),ranges.size()+1);
         
        //t[i] = minimum taps needed to cover 0 to ith of Garden
         dp[0] = 0;  //I need 0 tap to cover 0 to 0
         
         //Why start from i = 0
        //Because value at ranges[0] also impacts the range {i-ranges[i], i+ranges[i]}
        //We will miss that impact if we skip i = 0
         for(int i=0;i<ranges.size();i++){
             int left = max(0,i-ranges[i]);
             int right = min(n, i+ranges[i]);
             
             
        //     0 to l is watered
        //    We now need to find minimum taps to cover from (l+1) to r
             for(int j=left+1;j<=right;j++){  
             
             //Check if this range from(left+1..right) can
            //  be watered using less number of taps than previous
                 dp[j] = min(dp[j],dp[j]+1);
             }
         }
         
         //if min taps needed to water ground is greater than (n+1) we return -1
        //Because we only had (n+1) taps
         return dp[n] > n+1 ? -1 : dp[n];
     }
   */

// Method 3: Similar to question "Jump Game 2"
   /*
    ublic:
    int minTaps(int n, vector<int>& ranges) {
        vector<int> jumps(n+1, 0);
        
        for(int i = 0; i<n+1; i++) {
            int l = max(0, i-ranges[i]);
            int r = min(n, i+ranges[i]);
            
            jumps[l] = max(jumps[l], r-l); //from l, that's farthest I can jump to right
        }
        
        //Now this questions has turned to Jump Game II
        //Where you have maximum jump you can take form an index i and
        //you have to reach end
        
        //This is just Jump Game - II code
        int currEnd  = 0;
        int maxReach = 0;
        int count    = 0;
        for(int i = 0; i<n; i++) {
            maxReach = max(maxReach, jumps[i]+i);
            
            if(i == currEnd) {
                count++;
                currEnd = maxReach;
            }
        }
        
        if(currEnd >= n)
            return count;
        return -1;
    }
   */