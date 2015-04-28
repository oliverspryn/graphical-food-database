/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicalfoodsearch;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    
    public Recipe getRecipeById(String id) throws Exception {
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
        return getRecipeFromNode(recipeNode);
    }
    
    //limitation of this is that the Recipes returned from search results do not include ingredients
    //the ingredients can be obtained by requerying the recipe using the recipe id
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
        String url = START_URL+"/recipes?pg=1&rpp=25&any_kw="+ingredientString+"&api_key="+API_STRING;
 
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
        
        return recipes;
    }
    
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
                for(int i = 0; i < ingredientNodes.getLength(); i++){
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
