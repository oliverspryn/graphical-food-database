/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import graphicalfoodsearch.beans.ClickBean;
import graphicalfoodsearch.beans.FileBean;
import graphicalfoodsearch.beans.MouseMoveBean;
import graphicalfoodsearch.listeners.IFileListener;
import graphicalfoodsearch.listeners.IMouseListener;
import graphicalfoodsearch.listeners.IMouseMoveListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author GEARHARTJJ1
 */
public class GraphicalFoodSearch {
    
        static Window w;
        
        static BigOvenDB db = new BigOvenDB();
    
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
            w.SetExtension("bga");
			
            w.RegisterClickListener(new IMouseListener() {
                @Override
                public void ClickHandler(ClickBean bean) {
                    System.out.print(bean.GetX());
                    System.out.print(" x ");
                    System.out.println(bean.GetY());
                    
                    w.GetCanvas().handleClick(bean);
                }
            });
            
            w.RegisterRightClickListener(new IMouseListener() {
               @Override
               public void ClickHandler(ClickBean bean) {
                   w.GetCanvas().handleRightClick(bean);
               }
            });
            
            w.RegisterMouseMoveListener(new IMouseMoveListener() {
                @Override
                public void MouseMoveHandler(MouseMoveBean bean) {
                    w.GetCanvas().handleMouseMove(bean);
                }
            });
			
            w.RegisterFileListener(new IFileListener() {
                @Override
                public void NewHandler() {
                    initializeApp();
                }

                @Override
                public void OpenHandler(FileBean bean) {
                    // Clear the database
                    // (Not strictly necessary, I think, since FoodGraphData.loadData() is designed to not cause
                    // problems if Canvas tries to draw during loading; but it's good to be sure.)
                    FoodGraphData.firstIngredient = null; // This signals Canvas to stop drawing the tree
                    FoodGraphData.ingredients.clear();
                    FoodGraphData.recipes.clear();

                    // Update the screen so the old tree disappears
                    w.revalidate();
                    w.repaint();
                    
                    // Load the new data from file
                    boolean succeeded = FoodGraphData.loadData(bean.GetFilePath());
                    
                    if(!succeeded) {
                        JOptionPane.showMessageDialog(w.GetCanvas(),
                                "Could not load data from file! Maybe the file doesn't exist or is corrupted?");
                        return;
                    }
                    
                    // Update the screen again to reflect the new tree
                    w.revalidate();
                    w.repaint();
                }

                @Override
                public void SaveHandler(FileBean bean) {
                    // Right now, there's no difference between "Save" and "Save As".
                    SaveAsHandler(bean);
                }

                @Override
                public void SaveAsHandler(FileBean bean) {
                    boolean succeeded = FoodGraphData.serializeData(bean.GetFilePath());
                    
                    if(succeeded)
                        JOptionPane.showMessageDialog(w.GetCanvas(), "Successfully saved data to file.");
                    else
                        JOptionPane.showMessageDialog(w.GetCanvas(), "Could not save data to file! Maybe you don't have permissions to write to this location?");
                }
            });
            
            // Ask the user for a starting ingredient and build the DB/tree from it
            initializeApp();
	}
        
        // Uses the static DB (API) instance to search for recipes associated with an ingredient,
        // adding them to the global recipes set.
        // Triggers a window repaint to update the tree displayed on screen.
        public static Vector<Recipe> searchDBByIngredient(Ingredient ingredient) throws Exception {
            Vector<Recipe> recipes = db.searchByIngredient(ingredient.ingredientName);
            
            w.revalidate();
            w.repaint();
            
            return recipes;
        }
        
        // Uses the static DB (API) instance to fill in the ingredients field of a Recipe object
        // (when a search by ingredient initially returns a Recipe, it does not include any ingredients).
        // Returns the "filled in" Recipe object, and also updates the global recipe and ingredient sets.
        // Triggers a window repaint to update the tree displayed on screen.
        public static Recipe dbFillRecipeIngredients(Recipe recipe) throws Exception {
            Recipe populatedRecipe = db.getRecipeAndIngredientsById(recipe.id);
            
            w.revalidate();
            w.repaint();
            
            return populatedRecipe;
        }
        
        // Called when the app is first opened, and also when the "New" menu option is chosen.
        private static void initializeApp() {
            // Clear the database (if there's anythign there, i.e. this was called by the "New" menu option)
            FoodGraphData.firstIngredient = null; // This signals Canvas to stop drawing the tree
            FoodGraphData.ingredients.clear();
            FoodGraphData.recipes.clear();
            
            // Update the screen so the old tree disappears (we don't want it waiting around while the new one loads)
            w.revalidate();
            w.repaint();

            // Ask the user to enter a new starting ingredient
            String ingredientName = JOptionPane.showInputDialog(w.GetCanvas(),
                    "Enter a starting ingredient:");
            
            if(ingredientName == null) // The user clicked "Cancel"
                return;

            // Fetch the ingredient from BigOven and build the initial tree from it
            try {
                Vector<Recipe> recipes = db.searchByIngredient(ingredientName);
                for (Recipe r : recipes) {
                    db.getRecipeAndIngredientsById(r.id);
                }

                // Find the starting ingredient and draw the tree with it as the root
                if (recipes.size() > 0) {
                    for (Ingredient i : recipes.get(0).ingredients) {
                        if (ingredientName.equals(i.ingredientName)) {
                            FoodGraphData.firstIngredient = i;
                            w.revalidate();
                            w.repaint();
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(GraphicalFoodSearch.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(w.GetCanvas(),
                        "Could not retrieve data from BigOven. Please try again later!");
            }
        }
}