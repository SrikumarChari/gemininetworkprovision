/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import java.net.InetAddress;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author schari
 */
@Entity
public class GeminiSubnetAllocationPool {

    private InetAddress start;
    private InetAddress end;
    
    @Reference
    private GeminiSubnet parent;

    public GeminiSubnetAllocationPool() {
    }

    public GeminiSubnetAllocationPool(InetAddress start, InetAddress end) {
        this.start = start;
        this.end = end;
    }

    public InetAddress getStart() {
        return start;
    }

    public void setStart(InetAddress start) {
        this.start = start;
    }

    public InetAddress getEnd() {
        return end;
    }

    public void setEnd(InetAddress end) {
        this.end = end;
    }

    public GeminiSubnet getParent() {
        return parent;
    }

    public void setParent(GeminiSubnet parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return start.getHostAddress() + "," + end.getHostAddress();
    }

}
