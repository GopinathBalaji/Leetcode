// To mark the end of the work use a boolean flag set to true.
// This will help us identify if the word is just a prefix or a full 
// proper word, because only the last character will have that set to true.   
class Trie {

    HashMap<Character, Trie> children;
    boolean isEndOfWord;

    public Trie() {
        children = new HashMap<>();
        isEndOfWord = false;
    }
    
    public void insert(String word) {
        Trie node = this;
        for(char c: word.toCharArray()){
            node.children.putIfAbsent(c, new Trie());
            node = node.children.get(c);
        }
        node.isEndOfWord = true; // Mark the end of a full word
    }
    
    public boolean search(String word) {
        Trie node = this;
        for(char c: word.toCharArray()){
            if(!node.children.containsKey(c)){
                return false;
            }else{
                node = node.children.get(c);
            }
        }
        return node.isEndOfWord; // Only true if this is an actual word
    }
    
    public boolean startsWith(String prefix) {
        Trie node = this;
        for(char c: prefix.toCharArray()){
            if(!node.children.containsKey(c)){
                return false;
            }else{
                node = node.children.get(c);
            }
        }
        return true;
    }
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */