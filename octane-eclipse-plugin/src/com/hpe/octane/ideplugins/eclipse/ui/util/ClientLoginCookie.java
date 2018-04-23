/*******************************************************************************
 * © 2017 EntIT Software LLC, a Micro Focus company, L.P.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.hpe.octane.ideplugins.eclipse.ui.util;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.Optional;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.hpe.adm.nga.sdk.authentication.SimpleUserAuthentication;
import com.hpe.octane.ideplugins.eclipse.Activator;

public class ClientLoginCookie {

    private static HttpResponse httpResponse;
    private static HttpCookie lwssoCookie;

    public static HttpResponse loginClient(SimpleUserAuthentication user) {

        HttpRequest httpRequest;
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

        ByteArrayContent content = ByteArrayContent.fromString("application/json", user.getAuthenticationString());

        try {
            httpRequest = requestFactory.buildPostRequest(new GenericUrl(Activator.getConnectionSettings().getBaseUrl() + "/authentication/sign_in"),
                    content);
            httpResponse = httpRequest.execute();
        } catch (IOException e) {
            // bad luck, yo' ass can't login
            return null;
        }
        return httpResponse;

    }

    // public boolean isLoggedIn() {
    //
    // boolean isLogged = loginClient(username,password) ? true : false;
    // return isLogged;
    // }

    protected static HttpCookie setLwssoCookie() {
        HttpResponse httpResponse = getHttpResponse();
        List<String> strHPSSOCookieCsrf1 = httpResponse.getHeaders().getHeaderStringValues("Set-Cookie");

        for (String strCookie : strHPSSOCookieCsrf1) {
            List<HttpCookie> cookies = HttpCookie.parse(strCookie);
            Optional<HttpCookie> lwssoCookieOp = cookies.stream().filter(a -> a.getName().equals("LWSSO_COOKIE_KEY")).findFirst();
            if (lwssoCookieOp.isPresent()) {
                lwssoCookie = lwssoCookieOp.get();
                break;
            } else {
                return null;
            }
        }
        return lwssoCookie;
    }

    public static HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public static boolean isUserLoggedIn() {
        if (getHttpResponse() == null) {
            return false;
        }
        return true;
    }

    public static HttpResponse getDataForImage(String pictureLink) {

        HttpCookie lwssoCookieCopy;
        if (lwssoCookie == null) {
            lwssoCookieCopy = setLwssoCookie();
        } else {
            lwssoCookieCopy = lwssoCookie;
        }
        HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        try {
            HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(pictureLink));
            httpRequest.getHeaders().setCookie(lwssoCookieCopy.toString());
            httpResponse = httpRequest.execute();
        } catch (IOException e) {
            // again, bad luck
            return null;
        }
        return httpResponse;

    }
}
