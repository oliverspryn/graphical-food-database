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
public class Recipe implements Serializable {
    // The GUI node associated with this Recipe
    public Node node;
    
    public Double estimatedMinutes;
    public String foodType;
    public String description;
    //could add in recipe picture
    public Vector<Ingredient> ingredients = new Vector<>();
    public String instructions;
    public String recipeName;
    public String category;
    //stored as a string because that is how it will always be used in queries/responses
    public String id;
    @Override
    public boolean equals(Object obj) {
        return this.recipeName.equals(((Recipe)obj).recipeName);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.recipeName);
        return hash;
    }
}
