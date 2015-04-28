/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import graphicalfoodsearch.beans.FileBean;
import graphicalfoodsearch.listeners.IFileListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author GEARHARTJJ1
 */
public class GraphicalFoodSearch {
    
        static Window w;
    
    /**
     * @param args the command line arguments
     */
	public static void main(String[] args) {
            /*BigOvenDB db = new BigOvenDB();
            try {
                Vector<Recipe> recipes = db.searchByIngredient("potato");
                for(Recipe r : recipes){
                    db.getRecipeAndIngredientsById(r.id);
                }
                Set<Ingredient> testIngr = FoodGraphData.ingredients;
                for(Ingredient i : testIngr){
                    System.out.println(i.ingredientName);
                    for(Recipe r : i.recipesUsedIn){
                        System.out.print(r.recipeName+"   ");
                    }
                    System.out.println();
                }
                Set<Recipe> testRecipe = FoodGraphData.recipes;
                FoodGraphData.serializeData("testStuffs.txt");
                int stuffs = 0;
            } catch (Exception ex) {
                Logger.getLogger(GraphicalFoodSearch.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            w = new Window("BigOven Graph Application");
            w.SetExtension(".bga");
			
			w.RegisterListener(new IFileListener() {
				@Override
				public void NewHandler() {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void OpenHandler(FileBean bean) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void SaveHandler(FileBean bean) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void SaveAsHandler(FileBean bean) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}
			});
	}
        
        // Clears and re-builds the visualization tree based on currently stored data
        public static void RefreshTree(Ingredient startingIngredient) {
            // Loop through all the Recipes and Ingredients, and set their associated Nodes to null.
            // We'll be clearing these from the screen and building new ones at appropriate coordinates.
            for(Ingredient ingredient : FoodGraphData.ingredients)
                ingredient.node = null;
            for(Recipe recipe : FoodGraphData.recipes)
                recipe.node = null;
            
            JPanel Canvas = w.GetPanel();
            
            // Clear all the Nodes from the screen
            Canvas.removeAll();
            
            // Determine the maximum depth of the new tree (for spacing on screen)
            int totalNodeCount = FoodGraphData.ingredients.size() + FoodGraphData.recipes.size();
            int maxDepth = (int) (Math.log(totalNodeCount) / Math.log(2));
            
            // Draw the root node (starting ingredient) at the top-center of the canvas
            Node root = new Node(Canvas.getWidth() / 2, 5, startingIngredient.ingredientName);
            startingIngredient.node = root;
            Canvas.add(root);
            
            // Build a fresh tree of Nodes based on a breadth-first traversal of the Ingredients and Recipes
            // starting from startingIngredient
            Set traversed = new HashSet();
            LinkedList queue = new LinkedList();
            traversed.add(startingIngredient);
            queue.push(startingIngredient);
            while(!queue.isEmpty()) {
                Object ingredientOrRecipe = queue.pop();
                
                if(ingredientOrRecipe instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient)ingredientOrRecipe;
                    
                    for(int i = 0; i < ingredient.recipesUsedIn.size(); i++) {
                        Recipe recipe = ingredient.recipesUsedIn.get(i);
                        if(!traversed.contains(recipe)) {
                            traversed.add(recipe);
                            
                            // Create a Node representing this ingredient in the tree.
                            // Each level in the tree will be 40 pixels tall.
                            // Each node in this level will be spaced evenly across the width of the panel.
                            int nodeCount = traversed.size();
                            int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                            int yCoord = currentDepth * 40 + 5;
                            int xSpacing = Canvas.getWidth() / (ingredient.recipesUsedIn.size() + 1);
                            int xCoord = (i + 1) * xSpacing;
                            
                            Node n = new Node(xCoord, yCoord, recipe.recipeName);
                            recipe.node = n;
                            Canvas.add(n);
                            
                            queue.add(recipe);
                        }
                    }
                }
                else if(ingredientOrRecipe instanceof Recipe) {
                    Recipe recipe = (Recipe)ingredientOrRecipe;
                    
                    for(int i = 0; i < recipe.ingredients.size(); i++) {
                        Ingredient ingredient = recipe.ingredients.get(i);
                        if(!traversed.contains(ingredient)) {
                            traversed.add(ingredient);
                            
                            // As above, create a Node representing this recipe in the tree.
                            int nodeCount = traversed.size();
                            int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                            int yCoord = currentDepth * 40 + 5;
                            int xSpacing = Canvas.getWidth() / (recipe.ingredients.size() + 1);
                            int xCoord = (i + 1) * xSpacing;
                            
                            Node n = new Node(xCoord, yCoord, ingredient.ingredientName);
                            ingredient.node = n;
                            Canvas.add(n);
                            
                            queue.add(ingredient);
                        }
                    }
                }
            }
        }
}