class Solution {
public:
    int longestPalindrome(string s) {
//         Method 1: Greedy Approach
// For each letter, say it occurs v times. We know we have v / 2 * 2 letters that can be 
// partnered for sure. For example, if we have 'aaaaa', then we could have 'aaaa' 
// partnered, which is 5 / 2 * 2 = 4 letters partnered.

// At the end, if there was any v % 2 == 1, then that letter could have been a unique 
// center. Otherwise, every letter was partnered. To perform this check, we will check
// for v % 2 == 1 and ans % 2 == 0, the latter meaning we haven't yet added a unique
// center to  the answer.
        
        int count = 0;
        bool flag = true;
        unordered_map<char,int> m1;
        for(int i=0;i<s.length();i++){
            m1[s[i]]++;
        }
        for(auto it: m1){
            count += it.second / 2 * 2;
            if(count%2==0 && it.second%2==1){
                count++;
            }
        }
        return count;
    }
};

// Method 2: Alternate Greedy Approach

   /*
    int longestPalindrome(string s) {

    map<char,int> mp;
    int n=s.size();
    //int maxodd=0;
    int count=0;
    for(int i=0;i<n;i++)
    {
        mp[s[i]]++;
    }
    for(auto x:mp)
    {
        if((x.second)%2==0)
            count+=x.second;   //adding the even frequency numbers
        else
        {
           count+=x.second-1;   //taking one less than odd frequencies e.g. from 5 we will take 4
        }
    }
    for(auto x:mp)
    {
        if(x.second%2==1) 
		//here we are are adding 1 for odd frequency once as we already added possible even numbers
		//( odd numbers greater than 2(e.g out of 7 we took 6) and all the even numbers) and adding one for any character with 
		//odd frequency as that could be middle element
        {
            count++;
            break;
        }
    }
    return count;
}
   */