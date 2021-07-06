class Solution {
public:
    int rotatedDigits(int n) {
//         Method 1: Bottom Up Dynamic Programming
        unordered_map<int,int> m1{{0,0},{1,0},{2,1},{3,-1},{4,-1},{5,1},{6,1},{7,-1},{8,0},{9,1},{10,0}};
        int count = 0;
        if(n<=10){
           for(int i=0;i<n+1;i++){
               if(m1[i]==1){
                   count++;
               }
           }
            return count;
        }
        
        else{
            
            int count = 4;
            for(int i=11;i<n+1;i++){
                int temp = 0;
                int b = i;
                while(b){
                    int a = b%10;
                    if(a==3 || a==4 || a==7){
                        temp=0;
                        break;
                    }
                   temp += m1[a];
                    b = b/10;
                }
                if(temp>0){
                    count++;
                }
            }
            return count;
        }
        
        
    }
};

// Method 2: Simple Logic
// Basically, if you your number contains any digits that cannot be rotated, it becomes invalid 
// (3,4, 7)
// If it contains any of the digits that can be rotated that creates a new digit (2, 5, 6, 9),
// then rotating it will always create a new overall number.
// The ones that can rotate and do not create a new digit (0, 1, 8) don't do anything to the 
// overall number. 808 flipped is still 808. The only way a new number can be created is if the 
// number contains a 2, 5, 6, or 9.

   /*
     public int rotatedDigits(int N) {
      int count = 0;
      for(int i = 0; i <= N; i++) {
        String s = String.valueOf(i);
        if(s.contains("3")) continue;
        if(s.contains("4")) continue;
        if(s.contains("7")) continue;
        
        if(s.contains("2") || s.contains("5") || s.contains("6") || s.contains("9")) count++;
        
      }
      return count;
    }
   */