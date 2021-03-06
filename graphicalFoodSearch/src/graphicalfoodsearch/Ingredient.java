/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import java.io.Serializable;
import java.util.Objects;
import java.util.Vector;

/**
 *
 * @author GEARHARTJJ1
 */
public class Ingredient implements Serializable {
    public int centerX, centerY;
    
    public Recipe parentNode = null;
    public int depth = 0; // depth in the tree of Ingredients and Recipes
    public double widthAllocated = 1.0; // fraction of screen width allocated to this node and its children
    public int firstXAllocated = 0; // left-hand x coord. of the width allocated to this node and its children

    public String ingredientName = "";
    public Double amountNeeded = -1.0;
    public String prepWork = "";
    //used in correlation with the amount, like grams, bottles, etc.
    public String ingredientUnit = "";
    public Vector<Recipe> recipesUsedIn = new Vector<>();
    @Override
    public boolean equals(Object obj) {
        return this.ingredientName.equals(((Ingredient)obj).ingredientName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.ingredientName);
        return hash;
    }
}
