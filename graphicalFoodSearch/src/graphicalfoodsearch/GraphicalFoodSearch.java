/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import graphicalfoodsearch.beans.ClickBean;
import graphicalfoodsearch.beans.FileBean;
import graphicalfoodsearch.listeners.IFileListener;
import graphicalfoodsearch.listeners.IMouseListener;
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
            w.SetExtension("bga");
			
			w.RegisterClickListener(new IMouseListener() {
				@Override
				public void ClickHandler(ClickBean bean) {
					System.out.print(bean.GetX());
					System.out.print(" x ");
					System.out.println(bean.GetY());
				}
			});
			
            w.RegisterFileListener(new IFileListener() {
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
            
            //// TEST CODE FOR TREE DRAWING
            BigOvenDB db = new BigOvenDB();
            try {
                Vector<Recipe> recipes = db.searchByIngredient("potato");
                for(Recipe r : recipes){
                    db.getRecipeAndIngredientsById(r.id);
                }
                
                // Find the "potato" Ingredient and draw the tree with it as the root
                if(recipes.size() > 0) {
                    for(Ingredient i : recipes.get(0).ingredients) {
                        if("potato".equals(i.ingredientName)) {
                            w.GetCanvas().startingIngredient = i;
                            w.revalidate();
                            w.repaint();
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(GraphicalFoodSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
}