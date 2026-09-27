class Solution {
private:
    class DSU {
    private:
        vector<int> parent;
        vector<int> rank;

    public:
        DSU(int n){
            parent.resize(n);
            rank.resize(n, 0);

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

        void unite(int a, int b){
            int rootA = find(a);
            int rootB = find(b);

            if(rootA == rootB){
                return;
            }

            if(rank[rootA] < rank[rootB]){
                parent[rootA] = rootB;
            }else if(rank[rootB] < rank[rootA]){
                parent[rootB] = rootA;
            }else{
                parent[rootA] = rootB;
                rank[rootB]++;
            }
        }
    };

public:
    bool canTraverseAllPairs(vector<int>& nums) {
        int n = nums.size();

        if(n == 1){
            return true;
        }

        for(int i=0; i<n; i++){
            if(nums[i] == 1){
                return false;
            }
        }

        DSU dsu(n);

        unordered_map<int, int> factorOwner;

        for(int i=0; i<n; i++){
            int x = nums[i];

            for(int p=2; p * p <= x; p++){
                if(x % p == 0){

                    if(factorOwner.count(p)){
                        dsu.unite(i, factorOwner[p]);
                    }else{
                        factorOwner[p] = i;
                    }

                    while(x % p == 0){
                        x /= p;
                    }
                }
            }

            if(x > 1){
                if(factorOwner.count(x)){
                    dsu.unite(i, factorOwner[x]);
                }else{
                    factorOwner[x] = i;
                }
            }
        }

        int root = dsu.find(0);

        for(int i=1; i<n; i++){
            if(dsu.find(i) != root){
                return false;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna