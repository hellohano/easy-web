//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.nd.hano.web.util;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Json2Std {
    private static final String TAG = "JSON2STR";
    private Map mResultMap = new HashMap();
    private List mResultArray = new ArrayList();
    private boolean mIsValid;
    private boolean mIsMap;
    private static ObjectMapper mObjectMapper = null;

    public static ObjectMapper getObectMapper() {
        if (mObjectMapper == null) {
            mObjectMapper = new ObjectMapper();
            mObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mObjectMapper.setSerializationInclusion(Include.NON_NULL);
        }

        return mObjectMapper;
    }

    public boolean isMap() {
        return this.mIsMap;
    }

    public Json2Std(String json) {
        if (!StringUtils.isEmpty(json)) {
            this.parseData(json);
        }
    }

    public boolean isValidJson() {
        return this.mIsValid;
    }

    private void parseData(String data) {
        Log.d(TAG, "parseData start");
        this.mIsMap = data.startsWith("{");

        try {
            if (this.mIsMap) {
                this.mResultMap = (Map) getObectMapper().readValue(data, Map.class);
            } else {
                this.mResultArray = (List) getObectMapper().readValue(data, new TypeReference() {
                });
            }

            this.mIsValid = true;
        } catch (IOException var3) {
            Log.w(TAG, "parseData end mIsValid = false, e=" + var3.getMessage());
            this.mIsValid = false;
        }

        Log.d(TAG, "parseData end");
    }

    public List<Object> getResultArray() {
        return this.mResultArray;
    }

    public Map<String, Object> getResultMap() {
        return this.mResultMap;
    }
}
