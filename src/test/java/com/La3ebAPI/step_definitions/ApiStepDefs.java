package com.La3ebAPI.step_definitions;

import com.La3ebAPI.utilities.ConfigurationReader;
import com.La3ebAPI.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class ApiStepDefs {

    Response response;
    Response response3;
    Map<String,Object> handhsakeMap = new HashMap<>();

    @Given("User adds a product to cart")
    public void user_adds_a_product_to_cart() {

        handhsakeMap.put("Content-Type", "application/json");
        handhsakeMap.put("x-app-version","1.4.1");

        Map <String, Object> bodyMap = new HashMap<>();
        bodyMap.put("store", "la3eben");

        response = given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .and().headers(handhsakeMap)
                .and().body(bodyMap)
                .when().post("/handshake");
        handhsakeMap.put("x-access-token", response.body().path("token"));
    }
    @When("User places an order")
    public void user_places_an_order() {

        given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .headers(handhsakeMap).
                when().post("/cart/empty-cart");

        System.out.println(response.statusCode());
    }
    @When("Shipping Info entered")
    public void shipping_Info_entered() {
        Map <String, Object> cartMap = new HashMap<>();
        Map <String, Object> itemsMap = new HashMap<>();
        itemsMap.put("sku",ConfigurationReader.get("sku"));
        itemsMap.put("qty", 1);

        Map [] items = {itemsMap};
        cartMap.put("items", items);
        cartMap.put("cart_source", "wallet");

        Response response2 = given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .headers(handhsakeMap).
                        and().body(cartMap).
                        when().put("/cart");
        String body = response2.body().prettyPrint();
        System.out.println(response2.statusCode());
    }
    @Then("status should be success")
    public void status_should_be_success() {
        Map<String, Object> shippingMap = new HashMap<>();
        shippingMap.put("useAsBilling", true);
        shippingMap.put("firstName", "KadirB");
        shippingMap.put("lastName","AUTOMATION");
        shippingMap.put("email", "kadirbalikci@hotmail.com");
        shippingMap.put("telephone","+96651234578");
        shippingMap.put("city","Riyadh");
        shippingMap.put("postCode", "12345");
        shippingMap.put("countryId","SA");
        shippingMap.put("street","23, O11raby Street");
        shippingMap.put("shippingCarrierCode", "flatrate");

        Response response1 =  given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .headers(handhsakeMap).
                        and().body(shippingMap).
                        when().put("/cart/shipping-information");

        int statusCode = response1.statusCode();
        System.out.println(statusCode);
    }

    @When("Get Token from CKO")
    public void get_Token_from_CKO() {
       Map <String, Object> CKO_Headers = new HashMap<>();
       CKO_Headers.put("Content-Type","application/json");
       CKO_Headers.put("Authorization","pk_test_5698ab73-00ff-420a-be2e-ba100c2dd5ab");

       Map<String, Object> CKO_Body = new HashMap<>();
       CKO_Body.put("number","4485040371536584");
       CKO_Body.put("expiryMonth",12);
       CKO_Body.put("expiryYear",2021);
       CKO_Body.put("cvv","100");
       CKO_Body.put("type","card");
       CKO_Body.put("phone","+966512345678");

        response3 = given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .and().headers(CKO_Headers)
                .and().body(CKO_Body)
                .when().post("https://sandbox.checkout.com/api2/v2/tokens/card");
        handhsakeMap.put("x-access-token", response.body().path("token"));
        System.out.println(response3.statusCode());

    }
    @Then("Complete Order")
    public void complete_Order() {
        Map<String, Map<String, Object>> placeOrderBody = new HashMap<>();
        Map<String, Object> paymentMethod = new HashMap<>();
        String CardToken = response3.body().path("id");

        paymentMethod.put("method","checkoutcom_card_payment");
        paymentMethod.put("token", CardToken);
        paymentMethod.put("cardBin","448504");
        paymentMethod.put("save",false);
        paymentMethod.put("success_url","http://la3eb.com/success");
        paymentMethod.put("failure_url","http://la3eb.com/failure");

        placeOrderBody.put("paymentMethod", paymentMethod);

      Response response4 =  given().contentType(ContentType.JSON)
                .and().accept(ContentType.JSON)
                .headers(handhsakeMap).
                and().body(placeOrderBody).
                when().put("/cart/place-order");

        Driver.get().get(response4.body().path("threeDS.redirectUrl"));

        System.out.println(response4.body().prettyPrint());
        System.out.println(response4.statusCode());

        }
    }