/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

/**
 *
 * @author JOHNSONEJ1
 */
public class RecipeNode extends Node {
    
    public RecipeNode(float xi, float yi, Recipe recipe) {
        super(xi, yi, recipe.recipeName);
    }
}
