package culinaryPlanning.recipeFinder.model;

import java.util.List;

public class Recipe {

	String name;
	List<Ingredients> ingredients;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Ingredients> getIngredients() {
		return ingredients;
	}
	
	public void setIngredients(List<Ingredients> ingredients) {
		this.ingredients = ingredients;
	}
	
}