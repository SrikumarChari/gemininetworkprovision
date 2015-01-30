package com.gemini.domain.common;

/**
 * @author t.varada.
 */
public enum LoadBalancerAlgorithm {
    ROUND_ROBIN,
    SOURCE_IP,
    LEAST_CONNECTIONS;
}
