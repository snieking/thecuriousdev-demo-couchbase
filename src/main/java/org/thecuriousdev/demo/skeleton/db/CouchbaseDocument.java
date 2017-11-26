package org.thecuriousdev.demo.skeleton.db;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface CouchbaseDocument {

    String getType();
    void setType(String type);

    @JsonIgnore String getId();
    @JsonIgnore void setId(String id);

    @JsonIgnore long getCas();
    @JsonIgnore void setCas(long cas);
}
