class Solution {
    public List<String> alertNames(String[] keyName, String[] keyTime) {
        HashMap<String, List<String>> map = new HashMap<>();
        List<String> ans= new ArrayList<>();

        for(int i=0;i<keyName.length;i++){
            map.putIfAbsent(keyName[i], new ArrayList<>());

            map.get(keyName[i]).add(keyTime[i]);
        }
        // Another way to write this using lamda function
        // for (int i = 0; i < keyName.length; i++) {
        //     map.computeIfAbsent(keyName[i], k -> new ArrayList<>())
        //        .add(keyTime[i]);
        // }


        for(String key: map.keySet()){
            
            if(map.get(key).size() < 3){
                continue;
            }
            List<String> times = map.get(key);
            Collections.sort(times);

            List<Integer> mins = new ArrayList<>(times.size());
            for(String time:times){
                int h = Integer.parseInt(time.substring(0,2));
                int m = Integer.parseInt(time.substring(3, 5));
                mins.add(h * 60 + m);
            }

            for(int i=0;i+2<mins.size();i++){
                if(mins.get(i+2) - mins.get(i) <= 60){
                    ans.add(key);
                    break;
                }
            }
        }
        Collections.sort(ans);

        return ans;
    }
}