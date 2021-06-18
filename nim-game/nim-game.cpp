class Solution {
public:
    bool canWinNim(int n) {
       return n%4!=0;
    }
};

/*
// DP Solution JAVA
public boolean canWinNim(int n) {
List list = new LinkedList();
for(int i=1; i <= n; i++){
if (i <= 3) list.add(true);
else {
boolean res = false;
res |= !list.get(0);
res |= !list.get(1);
res |= !list.get(2);
list.remove(0);
list.add(res);
}
}
return list.remove(2);
}
*/