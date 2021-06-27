class Solution {
public:
    void merge(vector<int>& nums1, int m, vector<int>& nums2, int n) {
//         Method 1: STL approach
        int a = n;
        while(a!=0){
            nums1.pop_back();
            a--;
        }
       nums1.insert(nums1.end(),nums2.begin(),nums2.end());
        sort(nums1.begin(),nums1.end());
    }
};

// Method 2: O(m+n) approach using two pointer
   /*
   int i = m - 1;
        int j = n - 1;
        int k = m + n - 1;
        
        while(i >= 0 && j >= 0){
            if(nums1[i] > nums2[j])
                nums1[k--] = nums1[i--];
            else
                nums1[k--] = nums2[j--];		
        }
         while(j >= 0)
			nums1[k--] = nums2[j--]; 
   */

// Method 3: Priority Queue
   /*
      priority_queue<int,vector<int> , greater<int>> pq;
        for(int i=0 ; i<m ; i++)
            pq.push(nums1[i]); // inserting nums1 array to priority_queue
        for(int j=0 ; j<n ; j++)
            pq.push(nums2[j]); // inserting nums2 array to priority_queue
			nums1.clear(); // clear the nums1 vector
        while(!pq.empty()){
            nums1.push_back(pq.top()); // inserting the values as its already sorted in the heap.
            pq.pop();
        }
        return ; 
   */