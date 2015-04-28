/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//need to make it work so that the recipe ingredients will include the ingredients used to "search" for it

package graphicalfoodsearch;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author GEARHARTJJ1
 */
public class BigOvenDB {
    private final String USER_AGENT = "Mozilla/5.0";
    private final String START_URL = "http://api.bigoven.com";
    private final String API_STRING = "dvxOohB1k0rgliIz7TMZj4x6eGJ93GOD";
    public int numIngredientsPerRecipe = 5;
    
    //has side effect of adding the ingredients to the set of ingredients, and updating the recipeUsedIm vector for the ingredient
    //also updates the Recipe in the set so that it has the ingredients it is connected with
    public Recipe getRecipeAndIngredientsById(String id) throws Exception {
        String url = START_URL+"/recipe/"+id+"?api_key="+API_STRING;
 
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("ACCEPT", "application/xml");
        
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        InputStream xml = con.getInputStream();
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //InputSource source = new InputSource(new StringReader(response.toString()));
        Document doc = dBuilder.parse(xml);
        //String stuff = response.toString();
        doc.getDocumentElement().normalize();
        
        NodeList recipes = doc.getElementsByTagName("Recipe");
        //get just the first node since this function should get just one recipe
        Node recipeNode = recipes.item(0);
        Recipe r = getRecipeFromNode(recipeNode);
        //add the ingedients to the Recipe object in the set since bigOven doesn't give it with search results
        for(Recipe recipe : FoodGraphData.recipes){
            if(recipe.recipeName.equals(r.recipeName)){
                //add in the recipes existing ingredients
                for (Ingredient i : recipe.ingredients){
                    r.ingredients.add(i);
                }
                recipe.ingredients = r.ingredients;
                break;
            }
        }
        
        //add ingredients from this recipe to the current node
        for(Ingredient ingredient : r.ingredients) {
            ingredient.recipesUsedIn.add(r);
            if(!FoodGraphData.ingredients.add(ingredient)){
                //if the ingredient already exists in the set then it has to be found and then
                //the recipe that references it must be added to vector of referencing recipes
                for (Ingredient i : FoodGraphData.ingredients) {
                    if(ingredient.ingredientName.equals(i.ingredientName)){
                        i.recipesUsedIn.add(r);
                        break;
                    }
                }
            }
        }
        return r;
    }
    
    //limitation of this is that the Recipes returned from search results do not include ingredients
    //the ingredients can be obtained by requerying the recipe using the recipe id
    //has side effect of adding recipe to set of recipes
    public Vector<Recipe> searchByIngredient(String... ingredients) throws Exception{
        Vector<Recipe> recipes = new Vector<>();
        //build the search string
        String ingredientString = "";
        for(String ingredient : ingredients){
            ingredientString+=ingredient;
            ingredientString+="%20";
        }
        //remove extra "%20" from string
        if (ingredientString.endsWith("%20")) {
            ingredientString = ingredientString.substring(0, ingredientString.length() - 5);
        }
        
        //get the data
        String url = START_URL+"/recipes?pg=1&rpp=5&any_kw="+ingredientString+"&api_key="+API_STRING;
 
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        con.setRequestProperty("ACCEPT", "application/xml");
        
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        InputStream xml = con.getInputStream();
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //InputSource source = new InputSource(new StringReader(response.toString()));
        Document doc = dBuilder.parse(xml);
        //String stuff = response.toString();
        doc.getDocumentElement().normalize();
        
        try{
            NodeList recipesList = doc.getElementsByTagName("Results");
            //get just the first node since this function should get just one result
            Node searchNode = recipesList.item(0);

            recipes = getRecipesFromSearchNode(searchNode);
        } catch(Exception ex){}
        
        //for each of the recipes add in the search ingredients as ingedients
        //this is to help keep the search by ingredient system in check also       
        //add the recipes to the set of recipes
        for(Recipe recipe : recipes){
            for(String ingred : ingredients){
                Ingredient ingr = new Ingredient();
                ingr.ingredientName = ingred;
                recipe.ingredients.add(ingr);
            }
            FoodGraphData.recipes.add(recipe);
        }
        
        return recipes;
    }
    
    //this is modified to only get the first 5 ingredients for each recipe
    private Recipe getRecipeFromNode(Node recipeNode){
        Recipe recipe = new Recipe();
        if (recipeNode.getNodeType() == Node.ELEMENT_NODE) {
 
            Element eElement = (Element) recipeNode;
            //all html parsings need to be in try catch blocks in the event the html is missing data
            try {
                recipe.id = eElement.getElementsByTagName("RecipeID").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.id = "-1";
            }
            try{
                recipe.recipeName = eElement.getElementsByTagName("Title").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.recipeName = "";
            }
            try {
                recipe.instructions = eElement.getElementsByTagName("Instructions").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.instructions = "";
            }
            try {
                recipe.description = eElement.getElementsByTagName("Description").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.description = "";
            }
            try {
                recipe.foodType = eElement.getElementsByTagName("Cuisine").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.foodType = "";
            }
            try {
                recipe.estimatedMinutes = Double.parseDouble(eElement.getElementsByTagName("TotalMinutes").item(0).getTextContent());
            } catch (Exception ex) {
                recipe.estimatedMinutes = -1.0;
            }
            try {
                recipe.category = eElement.getElementsByTagName("Category").item(0).getTextContent();
            } catch (Exception ex) {
                recipe.category = "";
            }
            //get the ingredients for the recipe
            try {
                Node ingredientsNode = eElement.getElementsByTagName("Ingredients").item(0);
                NodeList ingredientNodes = ((Element) ingredientsNode).getElementsByTagName("Ingredient");
                int length = numIngredientsPerRecipe-1;
                if(ingredientNodes.getLength()<length){
                    length = ingredientNodes.getLength();
                }
                for(int i = 0; i < length; i++){
                    Node ingredientNode = ingredientNodes.item(i);
                    Ingredient ingredient = getIngredientFromNode(ingredientNode);
                    recipe.ingredients.add(ingredient);
                }
            } catch (Exception ex) {}
        }
        return recipe;
    }

    //limitation of this is that the Recipes returned from search results do not include ingredients
    //the ingredients can be obtained by requerying the recipe using the recipe id
    private Vector<Recipe> getRecipesFromSearchNode(Node searchNode){
        Vector<Recipe> result = new Vector<>();
        try{
            Element searchElement = (Element) searchNode;
            NodeList recipeNodes = searchElement.getElementsByTagName("RecipeInfo");
            for(int i = 0; i < recipeNodes.getLength(); i++) {
                result.add(getRecipeFromNode(recipeNodes.item(i)));
            }
        } catch(Exception ex){}
        return result;
    }
    
    private Ingredient getIngredientFromNode(Node ingredientNode) {
        Ingredient ingredient = new Ingredient();
        if (ingredientNode.getNodeType() == Node.ELEMENT_NODE) {
            Element iElement = (Element) ingredientNode;
            try {
                ingredient.ingredientName  = iElement.getElementsByTagName("Name").item(0).getTextContent();
            } catch (Exception ex) {
                ingredient.ingredientName = "";
            }
            try {
                ingredient.prepWork  = iElement.getElementsByTagName("PreparationNotes").item(0).getTextContent();
            } catch (Exception ex) {
                ingredient.prepWork = "";
            }
            try {
                ingredient.ingredientUnit  = iElement.getElementsByTagName("Unit").item(0).getTextContent();
            } catch (Exception ex) {
                ingredient.ingredientUnit = "";
            }
            try {
                ingredient.amountNeeded  = Double.parseDouble(iElement.getElementsByTagName("Quantity").item(0).getTextContent());
            } catch (Exception ex) {
                ingredient.amountNeeded = -1.0;
            }
        }
        return ingredient;
    }
}
