class Solution {
public:
    int trailingZeroes(int n) {
    int result = 0;
    for(long long i=5; n/i>0; i*=5){
        result += (n/i);
    }
    return result;
}
};

// Method 2: 
// The ZERO comes from 10.
// The 10 comes from 2 x 5
// And we need to account for all the products of 5 and 2. likes 4×5 = 20 ...
// So, if we take all the numbers with 5 as a factor, we'll have way more than enough
// even numbers to pair with them to get factors of 10

// How many multiples of 5 are there in the numbers from 1 to 100?
// because 100 ÷ 5 = 20, so, there are twenty multiples of 5 between 1 and 100.
// but wait, actually 25 is 5×5, so each multiple of 25 has an extra factor of 5, e.g.
// 25 × 4 = 100，which introduces extra of zero.

// So, we need know how many multiples of 25 are between 1 and 100? Since 100 ÷ 25 = 4,
// there are four multiples of 25 between 1 and 100.
// Finally, we get 20 + 4 = 24 trailing zeroes in 100!

// The above example tell us, we need care about 5, 5×5, 5×5×5, 5×5×5×5 ....
   /*
  int trailingZeroes(int n) {
    int result = 0;
    for(long long i=5; n/i>0; i*=5){
        result += (n/i);
    }
    return result;
}
    
        OR TO AVOID LONG LONG INT DATA TYPE
        
 int trailingZeroes(int n) {
    int sum=0;
	int tmp=0;
	while(n/5>0)
	{
		tmp=n/5;
		sum+=tmp;
		n=tmp;
    }
    return sum;
 }     
   */

//    Method 1: Brute Force (Integer Overflow)
        /*
         int trailingZeroes(int n) {
         
        long long int ans = 1;
        for(int i=1;i<=n;i++){
            ans = ans*i;
        }
        int a = 0;
       long long int count = 0;
        while(ans>0){
            a = ans%10;
            if(a==0){
                count++;
                ans = ans/10;
            }else{
                break;
            }
        }
        return count;
    }
    */