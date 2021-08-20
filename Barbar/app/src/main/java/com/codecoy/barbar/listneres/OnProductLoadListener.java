package com.codecoy.barbar.listneres;

import com.codecoy.barbar.dataModels.ProductDetailModel;


//Inteface OnDatesLoadListener is used to load data about ProductDetailModel data it has 3 abstract methods
public interface OnProductLoadListener {

    //onUserProductLoaded is used to load the data of ProductDetailModel
    void onUserProductLoaded(ProductDetailModel productDetailModel);


    //onFailure method is used when there is some error when loading the data
    void onFailure(String e);

    //onEmpty method is used when there is no data in the database
    void onEmpty(String message);
}
