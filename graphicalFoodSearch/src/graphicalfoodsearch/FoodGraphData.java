/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;


/**
 *
 * @author GEARHARTJJ1
 */
public class FoodGraphData implements Serializable {
    public static HashSet<Ingredient> ingredients = new HashSet<>();
    public static HashSet<Recipe> recipes = new HashSet<>();    
    public static Ingredient firstIngredient = new Ingredient();
    
    public static Boolean serializeData(String fileName) {
        Boolean success = true;
        try{
            ObjectOutputStream ooStream = 
                new ObjectOutputStream(
                new BufferedOutputStream(
                new FileOutputStream(fileName)));
            ooStream.writeObject(FoodGraphData.firstIngredient);
            ooStream.writeObject(FoodGraphData.ingredients);
            ooStream.writeObject(FoodGraphData.recipes);
            ooStream.flush();
            ooStream.close();
        } catch(Exception ex){
            success = false;
        }
        return success;
    }
    
    public static Boolean loadData(String fileName){
        Boolean success = true;
        try{
            ObjectInputStream oiStream = 
                    new ObjectInputStream(
                    new BufferedInputStream(
                    new FileInputStream(fileName)));
            FoodGraphData.firstIngredient = (Ingredient)oiStream.readObject();
            FoodGraphData.ingredients = (HashSet<Ingredient>)oiStream.readObject();
            FoodGraphData.recipes = (HashSet<Recipe>)oiStream.readObject();
        } catch(Exception ex){
            success = false;
        }
        return success;
    }
}
