/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author GEARHARTJJ1
 */
public class GraphicalFoodSearch {
    /**
     * @param args the command line arguments
     */
	public static void main(String[] args) {
            BigOvenDB db = new BigOvenDB();
            try {
                Vector<Recipe> recipes = db.searchByIngredient("potato");
                for(Recipe r : recipes){
                    db.getRecipeAndIngredientsById(r.id);
                }
                Set<Ingredient> testIngr = FoodGraphData.ingredients;
                Set<Recipe> testRecipe = FoodGraphData.recipes;
                int stuffs = 0;
            } catch (Exception ex) {
                Logger.getLogger(GraphicalFoodSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Window w;
            w = new Window("BigOven Graph Application");
            w.SetExtension(".bga");
	}
}