class Solution {
public:
    bool isPalindrome(int x) {
//         Method 1: Converting to string
     if(x<0){
         return false;
     }   
         string a = to_string(x);
        int l1 = 0;
        int l2 = a.length()-1;
        while(l1<l2){
            if(a[l1]!=a[l2]){
                return false;
            }
            l1++;
            l2--;
        }
        return true;
    }
};

// Method 2: Similar approach but without string
    /*
    class Solution
{
public:
bool isPalindrome(int x)
{
vector a;
int q;
if (x < 0)
{
return false;
}
else
{
while (x != 0)
{
q = x % 10;
a.push_back(q);
x = x / 10;
}
int i = 0;
int j = a.size() - 1;
while (i < j)
{
if (a[i] == a[j])
{
i++;
j--;
}
else
{
return false;
}
}
}
return true;
}
};
    */