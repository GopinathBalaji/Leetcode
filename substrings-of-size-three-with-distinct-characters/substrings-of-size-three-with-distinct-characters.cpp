class Solution {
public:
//     Method 1: Using Set (Time complexity greater than O(n) )
    int countGoodSubstrings(string s) {
        if(s.length()<3){
            return 0;
        }
        
        int count = 0;
        unordered_set<char> s1;
        for(int i=0;i<s.length()-2;i++){
            s1.clear();
            for(int j=i;j<i+3;j++){
                s1.insert(s[j]);
            }
            if(s1.size()==3){
                count++;
            }
        }
        return count;
       
    }
};

// Method 2: Sliding Window without Set O(n)
// Here we are using sliding window technique with a window size of 3
// With every iteration new element is pushed into the window and the last element of window is pushed out.
// With in every window we check if there is any repetition of elements using if loop
            /*
                 int countGoodSubstrings(string s) {
                    if(s.size()<3)return 0;
                    
                    int count = 0;
                    char a = s[0],b = s[1],c = s[2];
                    for(int i=3;i<=s.length()-1;i++){
                        if(a!=b && b!=c && a!=c){
                            count++;
                        }
                        a=b;
                        b=c;
                        c=s[i];
                    }
                    return count;
                }
            */

// Method 3: Sliding Window using HashMap
   /*
       int countGoodSubstrings(string s) {
	
	 // * This is classic sliding window problem with fixed window length 3 here 
	 // * we will always maintain the window len 3(fixed length)  by adjusting i and j 
	 // * in all similar problems
	 
         // i is LHS of the window 
        int i = 0;
       // j is RHS of window 
        int j = 0;
        int n = s.size();
		
		 // Map to keep track of occurance of each char 
        unordered_map<char, int>mp;
		
		 // Variable keeping track of answer/result 
        int ans = 0;
        
        while (i < n && j < n) {
             // Increment the count in map each time you iterate through any character 
            mp[s[j]]++;
            
           
            if (j - i + 1 < 3) {
             // Case 1:
             // *  Keep incrementing the RHS till you  make substring of exactly length 3(fixed length). 
             
                j++;
            } else if (mp.size() == 3) {
               // Case 2: 
			   // * If the map size is exact 3 we have found the sub string with 3 unique 
               // * chars(here window len 3 we are mainting always) ,
			   // * increment the answer, Also shift the LHS and RHS of the current window for which result is calulated
			   // * and remove the entry from map for the LHS(Note the RHS will still be part of new shifted window).
			   
                ans++;
                mp.erase(s[i]);
                i++;
                j++;
            } else {
               
		 * Case 3: 
		// * If map size is not equal to 3 (sliding window len here will always be 3)then we surely 
            // have some repeating chars so we need to slide the window again.
		// * For LHS Decrement the count from map and if its zero then erase it as we are using 
	    // * size of map to calucate number of unique elements present(Note the RHS will  still be
          // part of new shifted window) 
			    
                mp[s[i]]--;
                if (mp[s[i]] == 0) {
                    mp.erase(s[i]);
                }
                i++;
                j++;
                
            }
        }
        
        
        return ans;
        
    }
   */

// Method 3: Just check every 3 letter combination
   /*
    int countGoodSubstrings(string s) 
{
	int result=0,n=s.length();
	for(int i=0;i<n-2;i++)
		if(s[i]!=s[i+1]&&s[i]!=s[i+2]&&s[i+1]!=s[i+2]) result++;

	return result;
}
   */










