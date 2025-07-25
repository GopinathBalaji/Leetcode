// Backtracking
/*
Explanation of Backtraking Flow
index tracks which digit position you're processing.

At each position, get all letters mapped to that digit.

Loop over those letters:

Choose a letter.

Recurse to next digit (index + 1).

Undo the choice (backtrack by removing the last character from sb).
*/
class Solution {
    public List<String> letterCombinations(String digits) {
        List<String> ans = new ArrayList<>();
        if(digits.length() == 0){
            return ans;
        }

        HashMap<Character, String> map = new HashMap<>(){{
            put('2', "abc");
            put('3', "def");
            put('4', "ghi");
            put('5', "jkl");
            put('6', "mno");
            put('7', "pqrs");
            put('8', "tuv");
            put('9', "wxyz");
        }};

        StringBuilder sb = new StringBuilder();

        backtrack(digits, ans, 0, map, sb);

        return ans;
    }

    private void backtrack(String digits, List<String> ans, int index, HashMap<Character, String> map, StringBuilder sb){
        if(sb.length() == digits.length()){
            String combination = sb.toString();
            ans.add(combination);

            return;
        }

        char num = digits.charAt(index);
        String letters = map.get(num);

        for(char c: letters.toCharArray()){
            sb.append(c);
            backtrack(digits, ans, index + 1, map, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}

// function dfs(start_index, path):
//   if is_leaf(start_index):
//     report(path)
//     return
//   for edge in get_edges(start_index):
//     path.add(edge)
//     dfs(start_index + 1, path)
//     path.pop()