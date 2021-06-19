/* 
DP explaination:
Given below is the binary equivalent representation of decimal numbers upto 16.
If we try to find a pattern here then we will notice that:
No. of 1s in binary representation of current number = No. of 1s in binary representation of half of the current number + current number%2.

vector<int> countBits(int n) {
	vector<int>dp(n+1);
    dp[0]=0;
	for(int i=1;i<n+1;i++) {
		dp[i]=dp[i/2]+i%2;
     }
     return dp;
}

OR

If the current number is even then just check for count of half of its value as right shift of the half of its value will result it into current number so count of both will be same as you are just right shifting the value.
If the current number is odd then check for count of half of its value and add 1 to that value will result count for this current number as after right shift of the half of its value you also have to flip the LSB so for this flip one count we will have to add.
class Solution {
public:
    vector<int> countBits(int n) {
        vector<int> v(n+1);
        v[0]=0;
        for(int i=1;i<=n;i++)
        {
            if(i%2==0)
                v[i]=v[i/2];
            else
                v[i]=v[i/2] + 1;
        }
        return v;
    }
};
*/


class Solution {
public:
    // DYNAMIC PROGRAMING SOLUTION
    vector<int> countBits(int n) {
   vector<int> vec(n+1);
     vec[0] = 0;
       for(int i=1;i<=n;i++){
           if(i%2==0){
               vec[i] = vec[i/2];
           }else{
               vec[i] = vec[i/2] + 1;
           }   
       } 
    return vec;
    }
    
};