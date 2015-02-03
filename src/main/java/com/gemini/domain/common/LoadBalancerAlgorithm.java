package com.gemini.domain.common;

/**
 * @author t.varada.
 */
public enum LoadBalancerAlgorithm {
    ROUND_ROBIN("ROUND_ROBIN"),
    SOURCE_IP("SOURCE_IP"),
    LEAST_CONNECTIONS("LEAST_CONNECTIONS");
    private String name;
    LoadBalancerAlgorithm(String name){
        this.name = name;
    }
    public static LoadBalancerAlgorithm fromString(String algoValue){
        if(algoValue != null){
            for(LoadBalancerAlgorithm loadBalancerAlgorithm : LoadBalancerAlgorithm.values()){
                if(algoValue.equalsIgnoreCase(loadBalancerAlgorithm.name)){
                    return loadBalancerAlgorithm;
                }
            }
        }
        return null;
    }
}
