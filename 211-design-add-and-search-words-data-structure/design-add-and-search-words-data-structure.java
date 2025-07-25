// Using Trie
// Adding words is the same. But to handle wildcard characters in search, do DFS on
// all characters.
/*
DFS Search on Trie Explanation

Base Case:
If we've reached the end of the word (index == word.length), we check if the current node marks the end of a valid word.

Regular Character:
Check if that character exists as a child. If not, return false.

Recurse to the next character from the matched child.

Wildcard '.':
Try all children of the current node.

If any child path returns true, return true.

If none match, return false.
*/ 
class WordDictionary {

    HashMap<Character, WordDictionary> children;
    boolean isEndOfWord;

    public WordDictionary() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
    
    public void addWord(String word) {
        WordDictionary node = this;
        for(char c: word.toCharArray()){
            node.children.putIfAbsent(c, new WordDictionary());
            node = node.children.get(c);
        }

        node.isEndOfWord = true;
    }
    
    public boolean search(String word) {
        WordDictionary root = this;
        return dfs(word, 0, root);
    }

    private boolean dfs(String word, int index, WordDictionary node){
        if(index == word.length()){
            return node.isEndOfWord;
        }

        char letter = word.charAt(index);

        if(letter != '.'){
            if(!node.children.containsKey(letter)){
                return false;
            }else{
                return dfs(word, index + 1, node.children.get(letter));
            }
        }else{
            for(WordDictionary child: node.children.values()){
                if(dfs(word, index + 1, child) == true){
                    return true;
                }
            }

            return false;
        }
    }
}

/**
 * Your WordDictionary object will be instantiated and called as such:
 * WordDictionary obj = new WordDictionary();
 * obj.addWord(word);
 * boolean param_2 = obj.search(word);
 */