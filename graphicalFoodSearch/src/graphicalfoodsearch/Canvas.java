/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author JOHNSONEJ1
 */
public class Canvas extends JPanel {

    public Ingredient startingIngredient;
    
    // Utility function used by paint() to draw an individual text node
    private void drawNode(Graphics g, int x, int y, String content) {
        FontMetrics metrics = g.getFontMetrics();
        float width = metrics.stringWidth(content) + 10;
        float height = 20;
       
        Graphics2D g2 = (Graphics2D) g;

        //draw a rectangle
        g2.draw(new Rectangle2D.Float(x, y, width, height));
        //draw the text
        g2.drawString(content, x + 5, y + height * 3/4);
    }

    // Draw the visualization tree of Recipe and Ingredient nodes based on stored data
    public void paint(Graphics g) {
        // If the starting ingredient hasn't been set, leave the panel blank.
        if (startingIngredient == null) {
            return;
        }

        // Determine the maximum depth of the new tree (for spacing on screen)
        int totalNodeCount = FoodGraphData.ingredients.size() + FoodGraphData.recipes.size();
        int maxDepth = (int) (Math.log(totalNodeCount) / Math.log(2));

        // Draw the root node (starting ingredient) at the top-center of the canvas
        drawNode(g, this.getWidth() / 2, 5, startingIngredient.ingredientName);

        // Build the "tree" based on a breadth-first traversal of the Ingredients and Recipes
        // starting from startingIngredient
        Set traversed = new HashSet();
        LinkedList queue = new LinkedList();
        traversed.add(startingIngredient);
        queue.push(startingIngredient);
        while (!queue.isEmpty()) {
            Object ingredientOrRecipe = queue.pop();

            if (ingredientOrRecipe instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) ingredientOrRecipe;

                for (int i = 0; i < ingredient.recipesUsedIn.size(); i++) {
                    Recipe recipe = ingredient.recipesUsedIn.get(i);
                    if (!traversed.contains(recipe)) {
                        traversed.add(recipe);

                            // Create a Node representing this ingredient in the tree.
                        // Each level in the tree will be 40 pixels tall.
                        // Each node in this level will be spaced evenly across the width of the panel.
                        int nodeCount = traversed.size();
                        int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                        int yCoord = currentDepth * 40 + 5;
                        int xSpacing = this.getWidth() / (ingredient.recipesUsedIn.size() + 1);
                        int xCoord = (i + 1) * xSpacing;

                        drawNode(g, xCoord, yCoord, recipe.recipeName);

                        queue.add(recipe);
                    }
                }
            } else if (ingredientOrRecipe instanceof Recipe) {
                Recipe recipe = (Recipe) ingredientOrRecipe;

                for (int i = 0; i < recipe.ingredients.size(); i++) {
                    Ingredient ingredient = recipe.ingredients.get(i);
                    if (!traversed.contains(ingredient)) {
                        traversed.add(ingredient);

                        // As above, create a Node representing this recipe in the tree.
                        int nodeCount = traversed.size();
                        int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                        int yCoord = currentDepth * 40 + 5;
                        int xSpacing = this.getWidth() / (recipe.ingredients.size() + 1);
                        int xCoord = (i + 1) * xSpacing;

                        drawNode(g, xCoord, yCoord, ingredient.ingredientName);

                        queue.add(ingredient);
                    }
                }
            }
        }
    }
}
