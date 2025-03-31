class Solution {
    public List<String> letterCombinations(String digits) {
        if(digits.length() == 0){
            return new ArrayList<>();
        }
        
        List<String> res = new ArrayList<>();
        Map<Character, String> phone = Map.of('2', "abc", 
                                              '3', "def", 
                                              '4', "ghi", 
                                              '5', "jkl",
                                              '6', "mno", 
                                              '7', "pqrs", 
                                              '8', "tuv", 
                                              '9', "wxyz");

        dfs(digits, phone, digits.length(), res, 0, new StringBuilder());

        return res;
    }

    public static void dfs(String digits, Map<Character, String> phone, Integer max, List<String> res, int currsize, StringBuilder path){
        if(currsize == max){
            res.add(path.toString());
            return;
        }

        String letters = phone.get(digits.charAt(currsize));

        for(char letter: letters.toCharArray()){
            path.append(letter);
            dfs(digits, phone, max, res, currsize + 1, path);
            path.deleteCharAt(path.length() - 1);
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