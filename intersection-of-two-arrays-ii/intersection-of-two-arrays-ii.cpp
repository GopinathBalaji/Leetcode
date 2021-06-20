class Solution {
public:  
    // Method 1: Hash Map approach
    vector<int> intersect(vector<int>& nums1, vector<int>& nums2) {
        vector<int> ans;
       unordered_map<int,int> m1;
        for(int i=0;i<nums1.size();i++){
            m1[nums1[i]]++;
        }
        for(int j=0;j<nums2.size();j++){
            if(--m1[nums2[j]]>=0){
                // m1[nums2[j]]--;
                ans.push_back(nums2[j]);
            }
        }
        return ans; 
        
        // Method 2: Two pointer approach
        
//            vector<int> intersect(vector<int>& nums1, vector<int>& nums2) {
//         vector<int> vec;
//         sort(nums1.begin(),nums1.end());
//         sort(nums2.begin(),nums2.end());
//         int i=0,j=0;
//         int n,m;
//         n=nums1.size();
//         m=nums2.size();
//         while(i<n&&j<m)
//         {
//             if(nums1[i]<nums2[j])
//                 i++;
//             else if(nums2[j]<nums1[i])
//                 j++;
//             else
//             {
//                 int a=nums1[i];
//                 vec.push_back(a);
//               i++;
//                 j++;
//             }
//         }
//         return vec;
//         }
       
        
    }
};