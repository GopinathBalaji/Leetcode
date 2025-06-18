class Solution {
    public List<String> subdomainVisits(String[] cpdomains) {
        HashMap<String, Integer> map = new HashMap<>();
        List<String> ans = new ArrayList<>();

        for(String s : cpdomains){
            String[] parts = s.split("\\s+");
            // String[] parts = input.split(" ");  (Same as this line)

            int count = Integer.parseInt(parts[0]);
            String domain = parts[1];
            map.put(domain, map.getOrDefault(domain, 0) + count);

            for(int i=0;i<domain.length();i++){
                if(domain.charAt(i) == '.'){
                    String subDom = domain.substring(i+1, domain.length());
                    map.put(subDom, map.getOrDefault(subDom, 0) + count);
                }
            }
        }

        for(Map.Entry<String, Integer> e : map.entrySet()){
            String temp = e.getKey();
            int val = e.getValue();
            String temp2 = val + " " + temp;
            ans.add(temp2);
        }

        return ans;   
    }
}