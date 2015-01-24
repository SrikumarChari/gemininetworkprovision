/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

/**
 *
 * @author schari
 */
public class GeminiSubnetAllocationPoolDTO {

    private String start;
    private String end;
    private GeminiSubnetDTO parent;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public GeminiSubnetDTO getParent() {
        return parent;
    }

    public void setParent(GeminiSubnetDTO parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return start + "," + end;
    }
}
