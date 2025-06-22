class RandomizedSet {
    private List<Integer> list = new ArrayList<>();
    private HashMap<Integer, Integer> idx = new HashMap<>();

    public RandomizedSet() {

    }
    
    public boolean insert(int val) {
        if(idx.containsKey(val)){
            return false;
        }
        idx.put(val, list.size());
        list.add(val);

        return true;
    }
    
    public boolean remove(int val) {
        if(idx.containsKey(val)){
            Integer i = idx.remove(val);
            int last = list.remove(list.size() - 1);
            if(i < list.size()){
                list.set(i, last);
                idx.put(last, i);
            }

            return true;
        }

        return false;
    }
    
    public int getRandom() {
        int min = 0;
        int max = list.size() - 1;
        int random = (int)(Math.random() * (max - min + 1)) + min;

        // Math.random() gives you a double in [0.0,1.0).
        // Multiply by (max - min + 1) to stretch to [0, max−min+1),
        // cast to int to get [0…max−min], then add min.

        return list.get(random);
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
//  ///////////////
//  Removing element in O(1) time logic
// list.remove() takes O(n) since it needs to shift all elements by one place, so 
// need to somehow ensure that we only remove the last element from the list.
// When you remove an arbitrary element from the middle of an ArrayList, every element to its right has to be shifted left one slot—an O(n) operation. To avoid that, we “cheat” a little:

// Grab and delete the index mapping for val

// Integer i = idx.remove(val);
// if (i == null) return false;
// Here i is the position where val sits in list. Removing it from the idx map at the start makes sure we won’t later mistake it for anything else.

// Pop the last element off the list

// int last = list.remove(list.size() - 1);
// Removing the tail of an ArrayList is O(1). Now list is one shorter, and we’ve “lost” that last element temporarily (saved in last).

// Did the removed value sit at the very end?

// Yes (i == oldListSize - 1): then we’ve already removed val by popping the last slot, and nothing else needs changing.

// No (i < newListSize): there’s now a “hole” at index i where val used to be. We fill that hole by moving our saved last element into it.

// Swap‐in the last element and update its index

// list.set(i, last);
// idx.put(last, i);
// list.set(i, last) writes last into the vacant slot

// idx.put(last, i) tells our map “last is now at position i”

// By doing this swap‐and‐pop trick, you never shift more than one element, and you keep your index map in sync. That way remove, insert, and getRandom all stay O(1).