package com.gemini.domain.common;

/**
 * @author t.varada.
 */
public enum Protocol {
    HTTPS("https"),
    HTTP("http"),
    TCP("tcp"),
    PING("ping");
    private String name;
    Protocol(String name){
        this.name = name;
    }
    public static Protocol fromString(String protocolStr){
        if(protocolStr != null){
            for(Protocol protocol:Protocol.values()){
                if(protocolStr.equalsIgnoreCase(protocol.name)){
                    return protocol;
                }
            }
        }
        return null;
    }
}
