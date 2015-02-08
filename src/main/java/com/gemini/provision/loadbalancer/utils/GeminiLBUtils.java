package com.gemini.provision.loadbalancer.utils;

import com.gemini.domain.common.AdminState;
import com.gemini.domain.common.LoadBalancerAlgorithm;
import com.gemini.domain.common.Protocol;
import com.gemini.domain.common.ProvisionState;
import com.gemini.domain.model.*;
import com.gemini.domain.tenant.GeminiTenant;
import org.openstack4j.api.Builders;
import org.openstack4j.model.network.ext.LbPool;
import org.openstack4j.model.network.ext.LbPoolUpdate;
import org.openstack4j.model.network.ext.Member;
import org.openstack4j.model.network.ext.MemberUpdate;
import org.pmw.tinylog.Logger;

import java.util.List;

/**
 * @author t.varada.
 */
public class GeminiLBUtils {

    public static GeminiPoolMember getAsGeminiPoolMember(Member member){
        GeminiPoolMember geminiPoolMember = new GeminiPoolMember();
        geminiPoolMember.setCloudID(member.getId());
        geminiPoolMember.setAdminState(member.isAdminStateUp() ? AdminState.ADMIN_UP : AdminState.ADMIN_DOWN);
        geminiPoolMember.setProvisionState(ProvisionState.fromString(member.getStatus()));
        geminiPoolMember.setIpAddress(member.getAddress());
        geminiPoolMember.setProtocolPort(member.getProtocolPort());
        geminiPoolMember.setWeight(member.getWeight());
        geminiPoolMember.setPoolId(member.getPoolId());
        return geminiPoolMember;
    }

    public static GeminiLoadBalancerPool getAsGeminiLoadBalancerPool(LbPool lbPool,GeminiEnvironment env){
        GeminiLoadBalancerPool geminiPool = new GeminiLoadBalancerPool();
        geminiPool.setCloudID(lbPool.getId());
        geminiPool.setName(lbPool.getName());
        geminiPool.setDescription(lbPool.getDescription());
        //TODO get the VpId from the pool
        geminiPool.setVipID(lbPool.getVipId());
        geminiPool.setProtocol(Protocol.fromString(lbPool.getProtocol()));
        geminiPool.setLoadBalancerAlgorithm(LoadBalancerAlgorithm.fromString(lbPool.getLbMethod()));
        //TODO set the pool member
        geminiPool.setAdminState(lbPool.isAdminStateUp() ? AdminState.ADMIN_UP : AdminState.ADMIN_DOWN);

        GeminiSubnet subnet = env.getApplications().stream()
                .map(GeminiApplication::getNetworks)
                .flatMap(List::stream)
                .map(GeminiNetwork::getSubnets)
                .flatMap(List::stream)
                .filter(s -> s.getCloudID().equals(lbPool.getId()))
                .findFirst().get();
        if (subnet == null) {
            Logger.info("Load Balancer cloud ID {} references a subnet not available in environment {}  Subnet ID: {}",
                    lbPool.getId(), env.getName(), lbPool.getSubnetId());
        } else {
            geminiPool.setGeminiSubnet(subnet);
        }
        return geminiPool;
    }

    public static MemberUpdate updatePoolMember(GeminiPoolMember geminiPoolMember){
        MemberUpdate update = Builders.memberUpdate().adminStateUp(geminiPoolMember.getAdminState() == AdminState.ADMIN_UP)
                .weight(geminiPoolMember.getWeight())
                .poolId(geminiPoolMember.getPoolId()).build();
        return update;
    }

    public static LbPoolUpdate updateLBPool(GeminiLoadBalancerPool geminiLoadBalancerPool){
        LbPoolUpdate update = Builders.lbPoolUpdate()
                .adminStateUp(geminiLoadBalancerPool.getAdminState() == AdminState.ADMIN_UP)
                .description(geminiLoadBalancerPool.getDescription())
                .lbMethod(geminiLoadBalancerPool.getLoadBalancerAlgorithm().name().toLowerCase())
                .name(geminiLoadBalancerPool.getName())
                .build();
        return update;

    }

    public static LbPool createLBPool(GeminiLoadBalancerPool geminiLoadBalancerPool,GeminiTenant tenant){
        LbPool lbPool = Builders.lbPool()
                .lbMethod(geminiLoadBalancerPool.getLoadBalancerAlgorithm().name().toLowerCase())
                .adminStateUp(geminiLoadBalancerPool.getAdminState() == AdminState.ADMIN_UP)
                .description(geminiLoadBalancerPool.getDescription())
                .name(geminiLoadBalancerPool.getName())
                .protocol(geminiLoadBalancerPool.getProtocol().name().toLowerCase())
                .subnetId(geminiLoadBalancerPool.getGeminiSubnet().getCloudID())
                .tenantId(tenant.getTenantID())
                .build();
        return lbPool;
    }

}
