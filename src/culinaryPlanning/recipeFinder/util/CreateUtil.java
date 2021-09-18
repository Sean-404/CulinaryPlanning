package culinaryPlanning.recipeFinder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import culinaryPlanning.recipeFinder.model.Fridge;
import culinaryPlanning.recipeFinder.model.Ingredients;
import culinaryPlanning.recipeFinder.model.Recipe;
import culinaryPlanning.recipeFinder.model.Unit;

public class CreateUtil {

	public static List<Fridge> fridgeList = new ArrayList<Fridge>();	
	public static List<Recipe> recipeList = new ArrayList<Recipe>();
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Get the Fridge file 
	 * @return
	 * @throws Exception 
	 */
	public static List<Fridge> getFridge(File fridgeFile) throws Exception{

		try(Scanner scanner = new Scanner(new FileInputStream(fridgeFile));
				){

			//Skip CSV Header
			scanner.nextLine();

			while (scanner.hasNextLine()) {
				fridgeList.add(convertToFridge(scanner.nextLine()));
			}
			

		}catch(Exception e){
			System.out.println("[ERROR] " + e.toString());
		}
		return fridgeList;
	}
	
	/**
	 * Get Recipe
	 * @return
	 */
	public static List<Recipe> getRecipes(File recipeFile)  throws FileNotFoundException{
		
		try{
			
			InputStream fis =  new FileInputStream(recipeFile);
			JSONParser jsonParser = new JSONParser();
			String recipeString = new Scanner(fis,"UTF-8").useDelimiter("\\A").next();
			
            JSONArray jsonRecipes = (JSONArray) jsonParser.parse(recipeString);
            
            for (int i=0; i < jsonRecipes.size(); i++){
            	
            	JSONObject jsonRecipeItem = (JSONObject)jsonRecipes.get(i);
            	recipeList.add(convertToRecipe(jsonRecipeItem));
            }
            
		}catch(Exception e){
			System.out.println("[ERROR] " + e.toString());
		}
		
		return recipeList;
	}
	
	/**
	 * Get the Fridge file from resource
	 * @return
	 */
	public static List<Fridge> getFridge(){

		try(InputStream fis =  CreateUtil.class.getResourceAsStream("/fridge.csv");
				Scanner scanner = new Scanner(fis);
				){

			//Skip CSV Header
			scanner.nextLine();

			while (scanner.hasNextLine()) {
				fridgeList.add(convertToFridge(scanner.nextLine()));
			}
			

		}catch(Exception e){
			System.out.println("[ERROR] " + e.toString());
			e.printStackTrace();
		}
		
		return fridgeList;
	}
	
	
	
	
	/**
	 * Get Recipe
	 * @return
	 */
	public static List<Recipe> getRecipes(){
		
		try{
			
			JSONParser jsonParser = new JSONParser();
			InputStream fis =  CreateUtil.class.getResourceAsStream("/recipes.json");
			String recipeString = new Scanner(fis,"UTF-8").useDelimiter("\\A").next();
			
            JSONArray jsonRecipes = (JSONArray) jsonParser.parse(recipeString);
            
            for (int i=0; i < jsonRecipes.size(); i++){
            	
            	JSONObject jsonRecipeItem = (JSONObject)jsonRecipes.get(i);
            	recipeList.add(convertToRecipe(jsonRecipeItem));
            }
            
		}catch(Exception e){
			System.out.println("[ERROR] " + e.toString());
			e.printStackTrace();
		}
		
		return recipeList;
	}


	/**
	 * Convert to Fridge Object.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Fridge convertToFridge(String data) throws Exception{

		String[] records=data.split(",");

		Fridge fridge = new Fridge();;

		fridge.setItem(records[0]);
		fridge.setAmount(Integer.parseInt(records[1]));
		fridge.setUnit(Unit.valueOf(records[2]));
		
		Date useBy = dateFormatter.parse(records[3]);
		
		fridge.setUseBy(useBy);

		return fridge;
	}
	
	
	/**
	 * Convert to Recipe Object.
	 * 
	 * @param jsonRecipeItem
	 * @return
	 * @throws Exception
	 */
	public static Recipe convertToRecipe(JSONObject jsonRecipeItem) throws Exception{
		
		Recipe recipe = new Recipe();
		recipe.setName((String) jsonRecipeItem.get("name"));
		JSONArray ingredientsArray = (JSONArray) jsonRecipeItem.get("ingredients");
	    
		List<Ingredients> ingList = new ArrayList<Ingredients>();
        for (int i=0; i < ingredientsArray.size(); i++){
        	ingList.add(convertToIngredient((JSONObject)ingredientsArray.get(i)));
        }
        
        recipe.setIngredients(ingList);
        
		return recipe;
	}
	
	
	/**
	 * Convert to Ingredient Object.
	 * 
	 * @param jsonIngredientItem
	 * @return
	 * @throws Exception
	 */
	public static Ingredients convertToIngredient(JSONObject jsonIngredientItem) throws Exception{
		
		Ingredients ingredient = new Ingredients();
		
		ingredient.setItem( (String) jsonIngredientItem.get("item"));
		ingredient.setAmount( Integer.parseInt((String) jsonIngredientItem.get("amount") ));
		ingredient.setUnit ( Unit.valueOf((String) jsonIngredientItem.get("unit")));
		
		return ingredient;
	}

}