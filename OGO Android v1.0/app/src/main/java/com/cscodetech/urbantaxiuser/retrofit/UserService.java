package com.customerogo.app.retrofit;


import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST(APIClient.APPEND_URL + "u_reg_user.php")
    Call<JsonObject> createUser(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_login_users.php")
    Call<JsonObject> userLogin(@Body RequestBody requestBody);



    @POST(APIClient.APPEND_URL + "mobile_check.php")
    Call<JsonObject> mobileCheck(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "country_code.php")
    Call<JsonObject> getCodelist(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_vehicle_list.php")
    Call<JsonObject> vehicleList(@Body RequestBody requestBody);



    @POST(APIClient.APPEND_URL + "u_order_nows.php")
    Call<JsonObject> getOrderNow(@Body RequestBody requestBody);



    @POST(APIClient.APPEND_URL + "u_map_info.php")
    Call<JsonObject> getOrderMaps(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_order_cancle.php")
    Call<JsonObject> orderCancle(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_tripe_historys.php")
    Call<JsonObject> tripeHistory(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_tripe_details.php")
    Call<JsonObject> tripeDetails(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_rate_update.php")
    Call<JsonObject> rateUpdate(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_couponlist.php")
    Call<JsonObject> getCouponList(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_check_coupon.php")
    Call<JsonObject> checkCoupon(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_forget_password.php")
    Call<JsonObject> getForgot(@Body RequestBody requestBody);


    @POST(APIClient.APPEND_URL + "u_getdata.php")
    Call<JsonObject> getData(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_pagelists.php")
    Call<JsonObject> pagelist(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_wallet_report.php")
    Call<JsonObject> walletReport(@Body RequestBody object);

    @POST(APIClient.APPEND_URL + "u_wallet_up.php")
    Call<JsonObject> walletUpdate(@Body RequestBody object);

    @POST(APIClient.APPEND_URL + "u_paymentgateway.php")
    Call<JsonObject> paymentlist(@Body RequestBody object);

    @POST(APIClient.APPEND_URL + "u_home_data.php")
    Call<JsonObject> homeData(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "notification.php")
    Call<JsonObject> getNote(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_profile_edit.php")
    Call<JsonObject> getUpdate(@Body RequestBody requestBody);

    @POST(APIClient.APPEND_URL + "u_faq.php")
    Call<JsonObject> getFaq(@Body RequestBody requestBody);




}
