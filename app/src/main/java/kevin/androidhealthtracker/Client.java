package kevin.androidhealthtracker;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;

/**
 * Created by kevin on 22/03/18.
 */

public class Client {
    //Todo create a client jar in the server project and then user that as a dependency in here.
    //TODO use restTemplate.exchange as a method for talking to api  return restTemplate.exchange(requestEntity, returnType).getBody();
    //Types of RequestEntity and ParameterizedTypeReference<> in method.

    private final String accountJson;

    public Client(String a) {
        accountJson = a;
    }

    public Boolean authenticateUser(){
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.12.140.122:8080/users",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error);
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return accountJson == null ? null : accountJson.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", accountJson, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public String getSessionToken(){
        return null;
    }
}
