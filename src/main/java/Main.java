import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {
    //private Predicate<JSONObject> jsonObjectPredicate;

    //read in JSON file containing info about papers, give program a keyword to search for an an integer, n

    //read the JSON file. Look for anything that says title. Check if the thing after title matches the keyword. If so, we'll keep that somewhere


    List<JSONObject> findLinesWithReferences(String file){

        //        for(JSONObject j: jsonLines){
//            if(j.has("references")) {
//                System.out.println(j.getJSONArray("references"));
//                JSONArray refs = j.getJSONArray("references");
//                for(int i = 0; i < refs.length(); i++){
//
//                }
//                //if(j.getJSONArray("references"))
//            }
//        }


        //find the papers that cited the papers we got returned from the keyword search
        //I need to go into the larger file and check the ids within each object's references against the ids for the papers I got from my search
        List<JSONObject> withReferences = new ArrayList<>();
        try(Stream<String> secondStream = Files.lines(Paths.get(file))){
            withReferences = secondStream
                    .map(JSONObject::new)
                    .filter(jsonObject -> jsonObject.has("references"))
                    .collect(Collectors.toList());
            }
        catch(IOException e){
            e.printStackTrace();
        }
        return withReferences;
    }

    List<JSONObject> findTier2(String file, String id){
        List<JSONObject> tier2 = new ArrayList<>();
        try(Stream<String> secondStream = Files.lines(Paths.get(file))){
            tier2 = secondStream
                    .map(JSONObject::new)
                    .filter(jsonObject -> jsonObject.getString("id").contains(id))
                    .collect(Collectors.toList());
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return tier2;
    }



    public static void main(String[] args) throws FileNotFoundException, IOException {

        String file = "dblp_papers_v11.txt";
        //String file = "json_hw5.txt";
        List<JSONObject> jsonLines;
        List<JSONObject> tier1Papers = new ArrayList<>();
        List<String> ids = new ArrayList<>();//will contain all the ids from the jsonLines list
        List<JSONArray> refsForTier2 = new ArrayList<>();
        List<JSONArray> tier2 = new ArrayList<>();


        try(Stream<String> stream = Files.lines(Paths.get(file))){

            jsonLines = stream
                    .map(JSONObject::new)//convert to JSONObject to take advantage of the methods there
                    .filter(jsonObject -> jsonObject.getString("title").contains("Predict"))//filter according to keyword
                    .collect(Collectors.toList());//put the results in our list



            for(JSONObject j: jsonLines){
                ids.add(j.getString("id"));
                if(j.has("references"))
                {
                    JSONArray refs = j.getJSONArray("references");
                    refsForTier2.add(refs);
                }

            }

        }
        catch(IOException e){
            e.printStackTrace();
        }

//        System.out.println("First stream complete");
        Main m = new Main();
        List<JSONObject> objsWithReferences = m.findLinesWithReferences(file);
        System.out.println("Found articles WITH references listed");

//        for(int i = 0; i < ids.size(); i++){
//            System.out.println(ids.get(i));
//        }


        for(JSONObject j : objsWithReferences){
            JSONArray refs = j.getJSONArray("references");
            for(int i = 0; i < ids.size(); i++)
            {
                for(int k = 0; k < refs.length(); k++){

                    if(refs.getString(k).equals(ids.get(i))){
                        System.out.println("ID: " + ids.get(i) + " matches " + refs.getString(k));
                        tier1Papers.add(j);
                        break;
                    }
                }
            }
        }
        if(tier1Papers.isEmpty()){
            System.out.println("No matches!");
        }
        else{

            for(JSONArray j: refsForTier2)
            {
                for(int i = 0; i < j.length(); i++)
                {
                    String newId = j.get(i).toString();
                    m.findTier2(file, newId);
                }

            }
        }
//        System.out.println(tier1Papers.size());


    }
}


    //program will first search for articles w/ titles containing keyword, then finds the papers these articles cited. These are tier-1 papers

    //Tier-2 papers are the ones that were cited by tier-1 papers

    //Output should be info of papers in all tiers up to level n ranked in order of importance.
        //you decide how importance is defined.


