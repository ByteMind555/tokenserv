package com.anz.platform.common.tokenserv.model;

import java.util.ArrayList;
import java.util.List;


public class TokenizerRequest {

    private final List<String> entity = new ArrayList<>();;


    public TokenizerRequest(List<String> plainValue) {
        this.entity.addAll(plainValue);
    }

    public List<String> getEntity() {
        return entity;
    }
}
