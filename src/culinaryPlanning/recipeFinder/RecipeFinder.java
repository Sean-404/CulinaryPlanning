package culinaryPlanning.recipeFinder;

import java.io.File;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import culinaryPlanning.recipeFinder.model.Fridge;
import culinaryPlanning.recipeFinder.model.Ingredients;
import culinaryPlanning.recipeFinder.model.Recipe;
import culinaryPlanning.recipeFinder.util.CreateUtil;

/**
 * Recipe Finder
 *
 */

//store recipes into DB, mongoDB, couchDB (recommended) works with json?

public class RecipeFinder {

	private static Scanner scan = new Scanner(System.in);
	public static List<Fridge> fridgeList;
	public static List<Recipe> recipeList;
	private static List<Recipe> availableRecipesList = new ArrayList<Recipe>();
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	private static String fridgeFilePath = null;
	private static String recipeFilePath = null;

	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		showBanner();

		try{
			recipeFinder();
			
		}catch(FileNotFoundException fnf){
			System.out.println("[ERROR] " + fnf.toString());
		}catch (Exception e){
			System.out.println("[ERROR] " + e.toString());
		}

		end();
	}

	/**
	 * Get Files
	 * @throws Exception
	 */
	private static void getFiles() throws Exception {
		System.out.print("Enter the file path for the fridge.csv : ");
		fridgeFilePath = scan.next();

		fridgeList = CreateUtil.getFridge(new File(fridgeFilePath));
		
		System.out.print("Enter the file path for the recipes.json : ");
		recipeFilePath = scan.next();
		
		recipeList = CreateUtil.getRecipes(new File(recipeFilePath));
	}
	
	
	/**
	 * Recipe Finders
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void recipeFinder() throws FileNotFoundException, Exception {
		
		getFiles();
		
		if(fridgeList.size() == 0 || recipeList.size() == 0){
			System.out.println("\nPlease enter a correct filepath. ");
		}
		else{
			Recipe recommendedRecipe = process();
			System.out.println("\nBased on the items in the fridge and the available recipe/s, we recommend: \n\t > "
					+ recommendedRecipe.getName());
		}
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Recipe process() {
		
		System.out.println("\t > No. of items in the Fridge: " + fridgeList.size());
		System.out.println("\t > No. of Recipe/s found: " + recipeList.size());

		
		Date today = new Date();

		for (Recipe recipe : recipeList) {

			int numOfIngredients = recipe.getIngredients().size();
			int matchIngredients = 0;

			for (Ingredients ingredient : recipe.getIngredients()){

				for(Fridge f : fridgeList) {
					if (f.getItem().equals(ingredient.getItem()) && 
							f.getAmount() >= ingredient.getAmount()  &&
							f.getUnit() == ingredient.getUnit()){

						// Check Best Before Date
						if (f.getUseBy().after(today)) {
							matchIngredients++;
						} else{

						}
					}
				}
			}

			if (numOfIngredients == matchIngredients) {
				availableRecipesList.add(recipe);
			}
		}

		return recommendRecipe(availableRecipesList);
	}

	/**
	 * 
	 * @param availableRecipesList
	 * @return
	 */
	public static Recipe recommendRecipe(List<Recipe> availableRecipesList) {
		System.out.println("\t > No. of valid Recipes to cook: " + availableRecipesList.size());
		System.out.println("\t > List of valid Recipes to cook: ");
		for (Recipe recipe : availableRecipesList) {
			System.out.println("\t > " + recipe.getName());
		}
		
		Date today = new Date();
		Recipe recommendedRecipe = null;

		Date closestDate = null;
		String itemExpiringSoon = null;

		// Gather the items expiring soon
		for(Fridge f : fridgeList) {
			
			if (f.getUseBy().after(today)) {

				if (closestDate == null){
					closestDate = f.getUseBy();
					itemExpiringSoon = f.getItem();
				}else {
					if (f.getUseBy().before(closestDate)) {
						closestDate = f.getUseBy();
						itemExpiringSoon = f.getItem();
					}
				}
			}
		}

		if (itemExpiringSoon!=null) {
			System.out.println("\t > " + itemExpiringSoon + " is expiring soon at " + dateFormatter.format(closestDate));
		}
	
		for (Recipe recipe : availableRecipesList) {

			for (Ingredients ingredient : recipe.getIngredients()) {

				if(ingredient.getItem().equals(itemExpiringSoon)) {
					recommendedRecipe = recipe;
				}
			}
		}

		if(availableRecipesList.size() == 0 || recommendedRecipe == null) {
			recommendedRecipe =  new Recipe();
			recommendedRecipe.setName("Go to the shops!");
		}
		return recommendedRecipe;
	}

	/**
	 * Shows a banner
	 */
	public static void showBanner() {
		System.out.println("====================================");
		System.out.println("===        Recipe Finder         ===");
		System.out.println("====================================");
	}

	/**
	 * Shows a banner
	 */
	public static void end() {
		System.out.println("====================================");
	}


}
