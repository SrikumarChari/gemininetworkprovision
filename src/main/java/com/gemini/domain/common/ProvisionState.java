package com.gemini.domain.common;

/**
 * @author t.varada.
 */
public enum ProvisionState {
    PENDING_CREATE("PENDING_CREATE"),
    ACTIVE("PENDING_CREATE"),
    DOWN("DOWN");
    private String name;
    ProvisionState(String name){
        this.name = name;
    }

    public static ProvisionState fromString(String state){
        if(state != null){
            for(ProvisionState provisionState : ProvisionState.values()){
                if(state.equalsIgnoreCase(provisionState.name)){
                    return provisionState;
                }
            }
        }
        return null;
    }

}
