class Solution {
    public boolean isPalindrome(int x) {
        if(x<0){
            return false;
        }
        
       String str = Integer.toString(x);
       // Alternate method to convert int to String is to use: String.valueOf(int_value);
        for(int i=0;i<str.length()/2;i++){
           if(str.charAt(i) != str.charAt(str.length()-i-1)){
               return false;
           }
        }
        
        return true;
    }
}

/*
PYTHON
------
Rever half of the number:

def isPalindrome(self, x: int) -> bool:
    if x<0 or (x>0 and x%10==0):
        return False
    
    result = 0
    while(x > result):
        result = result*10 + x%10
        x = x//10
    
    return True if (x==result or x==(result//10)) else False
*/
