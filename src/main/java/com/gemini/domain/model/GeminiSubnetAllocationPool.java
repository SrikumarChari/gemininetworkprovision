/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    //the servers on this network
    @Reference
    List<GeminiServer> servers = Collections.synchronizedList(new ArrayList());

    @Reference
    private GeminiSubnet parent;

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

    public boolean addServer(GeminiServer srv) {
        if (servers.stream().filter(s -> s.getName().equals(srv.getName())).count() == 0) {
            return servers.add(srv);
        } else {
            return false;
        }
    }

    public boolean deleteServer(GeminiServer srv) {
        return servers.removeIf(s -> s.getName().equals(srv.getName()));
    }

    public List<GeminiServer> getServers() {
        return servers;
    }

    public void setServer(List<GeminiServer> servers) {
        this.servers = servers;
    }

    @Override
    public String toString() {
        return start.getHostAddress() + "," + end.getHostAddress();
    }

}
