package com.mobileoid2.celltone.network.jsonparsing;

import java.util.List;

/**
 * Created by Sourav on 23/03/18.
 */

public class JsonResponse<T> {
    private String response;
    private String  errorString ;

    public String getObject() {
        return response;
    }

    public void setObject(String response) {
        this.response = response;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }
}
