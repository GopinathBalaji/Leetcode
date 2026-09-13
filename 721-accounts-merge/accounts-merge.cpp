// Method 1: DSU by rank
/*
This is a good **DSU / Union-Find** problem too.

The core idea is:

> Two accounts belong to the same person if they share at least one email.

### Hint 1: Treat each account index as a DSU node

If there are `n` accounts:

```cpp
DSU dsu(n);
```

Account `0`, account `1`, etc. are the nodes you union together.

### Hint 2: Map each email to the first account that owns it

Use:

```cpp
unordered_map<string, int> emailToAccount;
```

While scanning each account:

```text
["John", "a@mail.com", "b@mail.com"]
```

ignore the name for now and process the emails.

If an email has never been seen:

```cpp
emailToAccount[email] = accountIndex;
```

If it has been seen before, union the two account indices:

```cpp
dsu.unite(accountIndex, emailToAccount[email]);
```

That shared email proves those accounts belong together.

### Hint 3: After all unions, group emails by root

Now every account belongs to some DSU root.

Loop through:

```cpp
emailToAccount
```

For each:

```cpp
[email, accountIndex]
```

find:

```cpp
int root = dsu.find(accountIndex);
```

and group:

```cpp
groups[root].push_back(email);
```

A useful structure is:

```cpp
unordered_map<int, vector<string>> groups;
```

### Hint 4: Sort the emails

Each merged account must have sorted emails:

```cpp
sort(emails.begin(), emails.end());
```

Then prepend the account name.

You can get the name using the root account index:

```cpp
accounts[root][0]
```

So one result row becomes conceptually:

```text
[name, sortedEmail1, sortedEmail2, ...]
```

### Hint 5: Overall flow

```cpp
DSU dsu(accounts.size());

unordered_map<string, int> emailToAccount;

// Pass 1:
// map emails to account indices
// union accounts sharing an email

unordered_map<int, vector<string>> groups;

// Pass 2:
// group every email by DSU root

// Pass 3:
// sort emails
// prepend name
// add to answer
```

### Important mental model

Suppose:

```text
Account 0: John, a@mail, b@mail
Account 1: John, c@mail
Account 2: John, b@mail, d@mail
```

Because account `0` and `2` both contain `b@mail`, union them:

```text
0 <-> 2
```

Then their final group contains:

```text
a@mail
b@mail
d@mail
```

while account `1` stays separate.

The trickiest part is usually realizing that **emails identify connections, but account indices are what you union**.
*/
class Solution {
private:
    class DSU{
    private:
        vector<int> parent;
        vector<int> rank;

    public:
        DSU(int n){
            parent.resize(n);
            rank.resize(n, 1);

            for(int i=0; i<n; i++){
                parent[i] = i;
            }
        }

        int find(int x){
            if(parent[x] == x){
                return x;
            }

            parent[x] = find(parent[x]);

            return parent[x];
        }

        bool unite(int a, int b){
            int rootA = find(a);
            int rootB = find(b);

            if(rootA == rootB){
                return false;
            }

            if(rank[rootA] <  rank[rootB]){
                parent[rootA] = rootB;
            }else if(rank[rootB] < rank[rootA]){
                parent[rootB] = rootA;
            }else{
                parent[rootA] = rootB;
                rank[rootB]++;
            }

            return true;
        }
    };

public:
    vector<vector<string>> accountsMerge(vector<vector<string>>& accounts) {
        DSU dsu(accounts.size());

        unordered_map<string, int> emailToAccount;

        for(int i=0; i<accounts.size(); i++){
            for(int j=1; j<accounts[i].size(); j++){
                string email = accounts[i][j];

                if(!emailToAccount.count(email)){
                    emailToAccount[email] = i;
                }else{
                    dsu.unite(i, emailToAccount[email]);
                }
            }
        }

        unordered_map<int, vector<string>> groups;

        for(auto& [email, accountIndex] : emailToAccount){
            int root = dsu.find(accountIndex);

            groups[root].push_back(email);
        }

        vector<vector<string>> result;

        for(auto& [root, emails] : groups){
            std::sort(emails.begin(), emails.end());

            vector<string> mergedAccount;

            mergedAccount.push_back(accounts[root][0]);

            for(string& email : emails){
                mergedAccount.push_back(email);
            }

            result.push_back(mergedAccount);
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna