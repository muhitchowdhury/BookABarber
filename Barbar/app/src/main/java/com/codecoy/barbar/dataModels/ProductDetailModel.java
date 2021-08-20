package com.codecoy.barbar.dataModels;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;




//ProductDetailModel Class used to store data about Product Detail
public class ProductDetailModel {


    private ArrayList<String> product_images; //ArrayList to store product images link

    //Constructor ProductDetailModel to assign Values
    public ProductDetailModel(ArrayList<String> product_images)
    {
        this.product_images = product_images;
    }

    //Empty Constructo
    public ProductDetailModel() {
    }


    //GETTER AND SETTER METHOD


    //getProduct_images to get list of product images
    public ArrayList<String> getProduct_images() {
        return product_images;
    }


    //setProduct_images to set the product images
    public void setProduct_images(ArrayList<String> product_images) {
        this.product_images = product_images;
    }
}
