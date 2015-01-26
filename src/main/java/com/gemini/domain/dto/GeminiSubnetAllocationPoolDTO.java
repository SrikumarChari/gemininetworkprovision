/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiSubnetAllocationPoolDTO {

    private String start;
    private String end;
    private GeminiSubnetDTO parent;

    private List<GeminiServerDTO> servers = Collections.synchronizedList(new ArrayList());

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

    public List<GeminiServerDTO> getServers() {
        return servers;
    }

    public void setServers(List<GeminiServerDTO> servers) {
        this.servers = servers;
    }

    public boolean addServer(GeminiServerDTO server) {
        if (servers.stream().filter(s -> s.getName().equals(server.getName())).count() == 0) {
            return servers.add(server);
        } else {
            return false;
        }
    }

    public boolean deleteServer(GeminiServerDTO server) {
        return servers.removeIf(s -> s.getName().equals(server.getName()));
    }

    @Override
    public String toString() {
        return start + "," + end;
    }
}
