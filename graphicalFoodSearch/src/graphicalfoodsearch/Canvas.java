/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import graphicalfoodsearch.beans.ClickBean;
import graphicalfoodsearch.beans.MouseMoveBean;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author JOHNSONEJ1
 */
public class Canvas extends JPanel implements ActionListener {
    
    private JPopupMenu ingredientPopupMenu;
    private JPopupMenu recipePopupMenu;
    private JMenuItem addIngredientMenuItem;
    private JMenuItem addRecipeMenuItem;
    private Object lastRightClickedNode; // so right-click menu handlers know which node was clicked on
    
    private Window w;

    //public Ingredient startingIngredient;
    
    // Maps bounding rectangles of GUI nodes (as drawn by last call to paint()) to Ingredient/Recipe objects.
    // This is used to detect and respond to mouse events on these nodes.
    private HashMap<Rectangle,Object> nodesByLocation = new HashMap<>();
    
    public Canvas(Window w) {
        this.w = w;
        
        ingredientPopupMenu = new JPopupMenu();
        recipePopupMenu = new JPopupMenu();
        
        // Note that the recipe popup menu as the "Add Ingredient" option, and the ingredient popup menu has
        // "Add Recipe". This is because recipes are children of ingredients, and vice versa.
        addIngredientMenuItem = recipePopupMenu.add("Add Ingredient");
        addRecipeMenuItem = ingredientPopupMenu.add("Add Recipe");
        
        addIngredientMenuItem.addActionListener(this);
        addRecipeMenuItem.addActionListener(this);
        
        revalidate();
        repaint();
    }
    
    // Utility function used by paint() to draw an individual text node
    private void drawNode(Graphics g, int x, int y, Object ingredientOrRecipe) {
        String content = "";
        Color color = Color.ORANGE;
        if(ingredientOrRecipe instanceof Ingredient) {
            content = ((Ingredient)ingredientOrRecipe).ingredientName;
            color = Color.ORANGE;
            //System.out.println("ingredient");
        }
        else if(ingredientOrRecipe instanceof Recipe) {
            content = ((Recipe)ingredientOrRecipe).recipeName;
            color = Color.YELLOW;
            //System.out.println("recipe");
        }
        // Note that if ingredientOrRecipe is neither of these types, we'll just draw an empty box.
        // Ideally we'd throw an exception here, but this will suffice for our purposes.
        
        // TODO: don't truncate strings, use different colors and overlapping boxes instead
        if(content.length() > 25)
            content = content.substring(0, 25) + "...";
        
        FontMetrics metrics = g.getFontMetrics();
        float width = metrics.stringWidth(content) + 10;
        float height = 20;
       
        Graphics2D g2 = (Graphics2D) g;

        //draw a rectangle
        Rectangle2D.Float bounds = new Rectangle2D.Float(x, y, width, height);
        g2.setColor(color);
        g2.fill(bounds);
        g2.setColor(Color.black);
        g2.draw(bounds);
        
        //draw the text
        g2.drawString(content, x + 5, y + height * 3/4);
        
        // Add the newly-drawn node to nodesByLocation so we can identify mouse events within it
        nodesByLocation.put(new Rectangle((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height),
                ingredientOrRecipe);
    }

    // Draw the visualization tree of Recipe and Ingredient nodes based on stored data
    public void paint(Graphics g) {
        // If the starting ingredient hasn't been set, leave the panel blank.
        if (FoodGraphData.firstIngredient == null) {
            return;
        }
        
        // Clear nodesByLocation, since we're rebuilding the node tree from scratch
        nodesByLocation.clear();
        
        // To draw the tree, we will run a breadth-first traversal twice: the first time to set each node's
        // parent pointer, depth, and share of the screen width; and the second time to actually draw the tree
        // based on this information.
        
        // Draw the root node (starting ingredient) at the top-center of the canvas
        drawNode(g, this.getWidth() / 2, 5, FoodGraphData.firstIngredient);

        // Build the "tree" based on a breadth-first traversal of the Ingredients and Recipes
        Set traversed = new HashSet();
        LinkedList queue = new LinkedList();
        
        FoodGraphData.firstIngredient.parentNode = null;
        FoodGraphData.firstIngredient.depth = 0;
        FoodGraphData.firstIngredient.widthAllocated = 1.0;
        traversed.add(FoodGraphData.firstIngredient);
        queue.push(FoodGraphData.firstIngredient);
        
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
                        //int nodeCount = traversed.size();
                        //int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                        //int yCoord = currentDepth * 40 + 5;
                        //int xSpacing = (int) (this.getWidth() / (Math.pow(2, currentDepth) + 1));
                       //int xCoord = (i + 1) * xSpacing;

                        recipe.parentNode = ingredient;
                        recipe.depth = ingredient.depth + 1;
                        recipe.widthAllocated = ingredient.widthAllocated / ingredient.recipesUsedIn.size();
                        recipe.firstXAllocated = (int) (ingredient.firstXAllocated + (i * recipe.widthAllocated * this.getWidth()));
                        
                        System.out.println(i + " of " + ingredient.recipesUsedIn.size() + ": width = " +
                                recipe.widthAllocated + "; first x = " + recipe.firstXAllocated);
                        
                        // Draw a node representing this ingredient on the screen.
                        // Each level in the tree will have height 40, starting from y=5.
                        int yCoord = recipe.depth * 40 + 5;
                        // Horizontally, we will position this node in the middle of its allocated space.
                        int xCoord = (int) (recipe.firstXAllocated + (recipe.widthAllocated / 2 * this.getWidth()));

                        //Set center coordinates - to be used for drawing a line to parent
                        FontMetrics metrics = g.getFontMetrics();
                        int centX = xCoord + metrics.stringWidth(recipe.recipeName)/2 + 5;
                        int centY = yCoord + 10;
                        recipe.centerX = centX;
                        recipe.centerY = centY;
                        
                        drawNode(g, xCoord, yCoord, recipe);

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
                       // int nodeCount = traversed.size();
                       // int currentDepth = (int) Math.floor(Math.log(nodeCount) / Math.log(2));
                       // int yCoord = currentDepth * 40 + 5;
                       // int xSpacing = (int) (this.getWidth() / (Math.pow(2, currentDepth) + 1));
                       // int xCoord = (i + 1) * xSpacing;

                        ingredient.parentNode = recipe;
                        ingredient.depth = recipe.depth + 1;
                        ingredient.widthAllocated = recipe.widthAllocated / recipe.ingredients.size();
                        ingredient.firstXAllocated = (int) (recipe.firstXAllocated + (i * ingredient.widthAllocated * this.getWidth()));
                        
                        // Draw a node representing this recipe on the screen.
                        // Each level in the tree will have height 40, starting from y=5.
                        int yCoord = ingredient.depth * 40 + 5;
                        // Horizontally, we will position this node in the middle of its allocated space.
                        int xCoord = (int) (ingredient.firstXAllocated + (ingredient.widthAllocated / 2 * this.getWidth()));
                        
                        //Set center coordinates - to be used for drawing a line to parent
                        FontMetrics metrics = g.getFontMetrics();
                        int centX = xCoord + metrics.stringWidth(recipe.recipeName)/2 + 5;
                        int centY = yCoord + 10;
                        recipe.centerX = centX;
                        recipe.centerY = centY;

                        drawNode(g, xCoord, yCoord, ingredient);

                        queue.add(ingredient);
                    }
                }
            }
        }
    }
    
    public void handleClick(ClickBean click) {
        // Detect if the click was inside a node
        for(Map.Entry<Rectangle,Object> node : nodesByLocation.entrySet()) {
            Point clickLoc = new Point(click.GetX(), click.GetY());
            if(node.getKey().contains(clickLoc)) {
                String labelTitle = "";
                if(node.getValue() instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient)node.getValue();
                    labelTitle = ingredient.ingredientName;
                    
                    try {
                        // The user clicked on an Ingredient, so we want to search for recipes associated with that
                        // ingredient and add them to the tree.
                        Vector<Recipe> recipes = GraphicalFoodSearch.searchDBByIngredient(ingredient);
                        
                        for(Recipe r : recipes) {
                            GraphicalFoodSearch.dbFillRecipeIngredients(r);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(Canvas.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                else if(node.getValue() instanceof Recipe) {
                    labelTitle = ((Recipe)node.getValue()).recipeName;
                    // At the moment, I don't think there's really anything to do in response to a click on a
                    // Recipe. Since Recipes always have ingredients associated with them, the user will never
                    // have to click on a Recipe to expand its ingredients - they'll automatically be expanded.
                    // The "click to search" functionality, thus, is only needed for Ingredient nodes.
                    
                    // NOTE: we can, in fact, get "orphaned", ingredient-less recipe nodes due to interactive
                    // additions by the user. At the moment, there isn't really a good way to populate their
                    // ingredients, since we don't have BigOven ID's for them. This is OK - it just means that
                    // if the user thinks he can click on a recipe leaf node to expand it, he'll be sorely
                    // disappointed because nothing will happen. It's a feature, not a bug!
                }
                
                System.out.println("Clicked on '" + labelTitle + "'");
                
                break;
            }
        }
    }
    
    public void handleRightClick(ClickBean click) {
        // Detect if the click was inside a node
        for(Map.Entry<Rectangle,Object> node : nodesByLocation.entrySet()) {
            Point clickLoc = new Point(click.GetX(), click.GetY());
            if(node.getKey().contains(clickLoc)) {
                // Save the node so that the right-click menu item handlers can know what was clicked on
                lastRightClickedNode = node.getValue();
                
                String labelTitle = "";
                if(node.getValue() instanceof Ingredient) {
                    labelTitle = ((Ingredient)node.getValue()).ingredientName;
                    ingredientPopupMenu.show(this, click.GetX(), click.GetY());
                }
                else if(node.getValue() instanceof Recipe) {
                    labelTitle = ((Recipe)node.getValue()).recipeName;
                    recipePopupMenu.show(this, click.GetX(), click.GetY());
                }
                
                System.out.println("Right-clicked on '" + labelTitle + "'");
                
                break;
            }
        }
    }
    
    public void handleMouseMove(MouseMoveBean move) {
        // Detect if the mouse position was inside a node
        for(Map.Entry<Rectangle,Object> node : nodesByLocation.entrySet()) {
            Point mouseLoc = new Point(move.GetX(), move.GetY());
            if(node.getKey().contains(mouseLoc)) {
                String labelTitle = "";
                if(node.getValue() instanceof Ingredient)
                    labelTitle = ((Ingredient)node.getValue()).ingredientName;
                else if(node.getValue() instanceof Recipe)
                    labelTitle = ((Recipe)node.getValue()).recipeName;
                
                //System.out.println("Mouse hovered over '" + labelTitle + "'");
                
                break;
            }
        }
    }

    // Event handler for right-click context menu items on Recipe/Ingredient GUI nodes
    @Override
    public void actionPerformed(ActionEvent e) {
        if(lastRightClickedNode == null) // This should never happen. Just being safe.
            return;
        
        if(e.getSource() == addIngredientMenuItem) { // User clicked "Add Ingredient" from a Recipe node
            if(lastRightClickedNode instanceof Recipe) { // Should always be true - again, just being safe.
                Recipe recipe = (Recipe)lastRightClickedNode;
                
                // Ask the user to input a new ingredient by name
                String ingredientName = JOptionPane.showInputDialog(recipePopupMenu, "Enter ingredient name:");
                
                // Create an Ingredient from the string entered, and add it to the tree
                Ingredient newIngredient = new Ingredient();
                newIngredient.ingredientName = ingredientName;
                recipe.ingredients.add(newIngredient);
            }
        }
        else if(e.getSource() == addRecipeMenuItem) { // User clicked "Add REcipe" from an Ingredient node
            if(lastRightClickedNode instanceof Ingredient) { // As above, should always be true.
                Ingredient ingredient = (Ingredient)lastRightClickedNode;
                
                // Ask the user to input a new recipe by name
                String recipeName = JOptionPane.showInputDialog(ingredientPopupMenu, "Enter recipe name:");
                
                // Create a Recipe from the string entered, and add it to the tree
                Recipe newRecipe = new Recipe();
                newRecipe.recipeName = recipeName;
                ingredient.recipesUsedIn.add(newRecipe);
                
                // NOTE: this is the only way to get an "orphaned" Recipe node, i.e. with no ingredients associated.
                // At the moment, I know of no way to get the ingredients, since we'd need to search by BigOven ID,
                // which isn't possible since the user entered the recipe by name.
                // If this doens't work well, we MAY need to comment out the "add recipe" functionality.
            }
        }
        
        // Re-draw the window to update the tree
        w.revalidate();
        w.repaint();
    }
}
