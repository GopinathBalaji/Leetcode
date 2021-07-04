class Solution {
public:
    bool repeatedSubstringPattern(string s) {
//         Method 1: Naive Approach
       string temp = "";
        string temp2 = "";
        int j=1;
        
        while(j<s.length()){
           temp2.append(s,0,j);
            int a = s.size()/temp2.size();
           while(a!=0){
               temp.append(temp2,0,temp2.size());
               a--;
           }
            if(temp==s){
                return true;
            }
            j++;
            temp.erase();
            temp2.erase();
        }
        return false;
    }
};

// Method 2: KMP String Matching Algorithm
   /*
      void constructLPS(int lps[],string pattern){
        int m=pattern.length();
        lps[0]=0;
        int len=0;
        int i=1;
        while(i<m){
            if(pattern[i]==pattern[len]){
                len++;
                lps[i]=len;
                i++;
            }
            else{
                if(len!=0){
                    len=lps[len-1];
                }
                else{
                    lps[i]=0;
                    i+=1;
                }
            }
        }
    }
    bool KMP(string s,string pattern){
        int m=s.length();
        int n=pattern.length();
        if(n>m){
            return false;
        }
        int lps[n];
        constructLPS(lps,pattern);
        int i=0,j=0;
        while(i<m){
            if(s[i]==pattern[j]){
               i++;
                j++;
            }
            if(j==n){
                return true;
            }
            else if(i<m&&s[i]!=pattern[j]){
                if(j!=0){
                    j=lps[j-1];
                }
                else{
                    i++;
                }
            }
        }
        return false;
    }
    bool repeatedSubstringPattern(string s) {
        string str=s+s;
        int m=s.length();
        string t=str.substr(1,2*m-2);
        return KMP(t,s);
    }
   */

// Method 3: Builtin Method (Java)
// This works because if there is string abcabc then repeated substring is abc. So there are two 
// abc substrings in the string. If we concatenate this string on itself and remove first and 
// last characters [a]bc(abc+abc)ab[c] (quadratic squares are removed characters) we still have 
// second abc from the last part of a first string and second abc from the first part of a second 
// string. I hope it makes sense
   
   /*
     public boolean repeatedSubstringPattern(String s) {
        return s.concat(s).substring(1, 2 * s.length() - 1).contains(s);
    }
   */

// Method 4: Rabin Karp Algorithm which uses Rolling Hash (Not for this question but use for 
// string matching)
   
   /*
     #include <bits/stdc++.h>
using namespace std;
 
// d is the number of characters in the input alphabet

  #define d 256
 
 // pat -> pattern
 //    txt -> text
 //    q -> A prime number

void search(char pat[], char txt[], int q)
{
    int M = strlen(pat);
    int N = strlen(txt);
    int i, j;
    int p = 0; // hash value for pattern
    int t = 0; // hash value for txt
    int h = 1;
 
    // The value of h would be "pow(d, M-1)%q"
    for (i = 0; i < M - 1; i++)
        h = (h * d) % q;
 
    // Calculate the hash value of pattern and first
    // window of text
    for (i = 0; i < M; i++)
    {
        p = (d * p + pat[i]) % q;
        t = (d * t + txt[i]) % q;
    }
 
    // Slide the pattern over text one by one
    for (i = 0; i <= N - M; i++)
    {
 
        // Check the hash values of current window of text
        // and pattern. If the hash values match then only
        // check for characters on by one
        if ( p == t )
        {
             // Check for characters one by one 
            for (j = 0; j < M; j++)
            {
                if (txt[i+j] != pat[j])
                    break;
            }
 
            // if p == t and pat[0...M-1] = txt[i, i+1, ...i+M-1]
            if (j == M)
                cout<<"Pattern found at index "<< i<<endl;
        }
 
        // Calculate hash value for next window of text: Remove
        // leading digit, add trailing digit
        if ( i < N-M )
        {
            t = (d*(t - txt[i]*h) + txt[i+M])%q;
 
            // We might get negative value of t, converting it
            // to positive
            if (t < 0)
            t = (t + q);
        }
    }
}
 
 // Driver code 
int main()
{
    char txt[] = "GEEKS FOR GEEKS";
    char pat[] = "GEEK";
       
      // A prime number
    int q = 101;
     
      // Function Call
      search(pat, txt, q);
    return 0;
}
   */

