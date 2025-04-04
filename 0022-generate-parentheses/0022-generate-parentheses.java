class Solution {

    public void dfs(List<String> res, int n, StringBuilder path, int openCount, int closeCount){
        if(path.length() == 2*n){
            res.add(path.toString());
            return;
        }

        if(openCount < n){
            path.append('(');
            dfs(res, n, path, openCount + 1, closeCount);
            path.deleteCharAt(path.length() - 1);
        }

        if(closeCount < openCount){
            path.append(')');
            dfs(res, n, path, openCount, closeCount + 1);
            path.deleteCharAt(path.length() - 1);
        }
    }


    public List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();

        dfs(res, n, new StringBuilder(), 0, 0);

        return res;
    }
}