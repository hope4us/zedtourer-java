package com.appbuildersworld.zedtourerjava.interfaces;



import static com.appbuildersworld.zedtourerjava.connectivity.Constant.authenticate_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.confirm_cashier_account_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.get_business_cashiers_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.get_business_products_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.get_product_categories_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.register_business_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.register_cashier_url;
import static com.appbuildersworld.zedtourerjava.connectivity.Constant.register_customer_url;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET(get_product_categories_url)
    Call<ResponseBody> getProductCategories();

    @GET(get_business_products_url)
    Call<ResponseBody> getBusinessProducts(@Query("products") String json, @Query("businessId") int businessId);

    @GET(get_business_cashiers_url)
    Call<ResponseBody> getBusinessCashiers(@Query("businessId") int businessId);

    @POST(register_business_url)
    @FormUrlEncoded
    Call<ResponseBody> registerBusiness(
            @Field("business") String json);

    @POST(register_customer_url)
    @FormUrlEncoded
    Call<ResponseBody> registerCustomer(
            @Field("customer") String json);

    @POST(register_cashier_url)
    @FormUrlEncoded
    Call<ResponseBody> registerCashier(
            @Field("cashier") String json);

    @POST(authenticate_url)
    @FormUrlEncoded
    Call<ResponseBody> authenticate(
            @Field("credentials") String json);

    @POST(confirm_cashier_account_url)
    @FormUrlEncoded
    Call<ResponseBody> confirmCashierAccount(
            @Field("cashierDetails") String json);
}
