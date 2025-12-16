package com.anz.platform.common.tokenserv.model;

import java.util.ArrayList;
import java.util.List;

public class TokenizerResponse {

    private List<String> entity = new ArrayList<>();

    public TokenizerResponse(List<String> entity) {
        this.entity = entity;
    }

    public List<String> getEntity() {
        return entity;
    }

}
