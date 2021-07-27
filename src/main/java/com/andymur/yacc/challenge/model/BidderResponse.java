package com.andymur.yacc.challenge.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BidderResponse {

    public static final BidderResponse FAILED_RESPONSE = new BidderResponse(0l, 0, "");

    private final long id;
    private final int bid;
    private final String content;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BidderResponse(@JsonProperty(value = "id", required = true) long id,
                          @JsonProperty(value = "bid", required = true) int bid,
                          @JsonProperty(value = "content", required = true) String content) {
        this.id = id;
        this.bid = bid;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public int getBid() {
        return bid;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "BidderResponse{" +
                "id=" + id +
                ", bid=" + bid +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidderResponse that = (BidderResponse) o;
        return id == that.id &&
                bid == that.bid &&
                Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bid, content);
    }
}
