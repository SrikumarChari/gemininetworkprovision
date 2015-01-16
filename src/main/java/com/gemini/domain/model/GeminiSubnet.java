/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gemini.domain.model;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.net.InetAddress;
import java.util.List;

/**
 *
 * @author schari
 */
public class GeminiSubnet extends GeminiNetwork {
    //parent network that contains this subnet
    private GeminiNetwork parent;
    
    //address pool
    private InetAddress subnetStart;
    private InetAddress subnetEnd;

    //network string with mask
    private String cidr;

    public GeminiNetwork getParent() {
        return parent;
    }

    public void setParent(GeminiNetwork parent) {
        this.parent = parent;
    }

    public String getCidr() {
        return cidr;
    }

    private void setCidr(String cidr) {
        this.cidr = cidr;
    }
    
    public InetAddress getSubnetStart() {
        return subnetStart;
    }

    public void setSubnetStart(InetAddress start) {
        this.subnetStart = start;
        String sStart = subnetStart.getHostAddress();
        List<String> sStartList = Splitter.on(".").limit(3).splitToList(sStart);
        setCidr(Joiner.on(".").join(sStartList.get(0), sStartList.get(1), sStartList.get(2), "0") + "/24");
    }

    public InetAddress getSubnetEnd() {
        return subnetEnd;
    }

    public void setSubnetEnd(InetAddress end) {
        this.subnetEnd = end;
    }
}
