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
public class IngredientNode extends Node {
    
    public IngredientNode(float xi, float yi, Ingredient ingredient) {
        super(xi, yi, ingredient.ingredientName);
    }
}
