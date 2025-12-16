package com.anz.platform.common.tokenserv.model;

import java.util.ArrayList;
import java.util.List;


public class TokenizerRequest {

    private List<String> entity = new ArrayList<>();;

    public List<String> getEntity() {
        return entity;
    }

    public void setEntity(List<String> entity) {
        this.entity = entity;
    }
}
