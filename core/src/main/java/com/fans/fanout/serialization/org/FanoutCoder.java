package com.fans.fanout.serialization.org;

import com.fans.fanout.net.Request;
import com.fans.fanout.net.Requests;
import com.fans.fanout.net.Response;
import com.fans.fanout.net.Responses;
import com.fans.fanout.serialization.Coder;

/**
 * 编码解码门面
 *
 * @author ：fsp
 * @date ：2020/5/19 14:42
 */
public class FanoutCoder implements Coder {

    private Responses responses = new FanoutResponses();

    private Requests requests = new FanoutRequests();

    @Override
    public byte[] encodeRequest(Request req) {
        FanoutRequest fanoutReq = (FanoutRequest) req;
        return fanoutReq.encode();
    }

    @Override
    public Request decodeRequest(byte[] orgReq) {
        FanoutRequest fanoutRequest = new FanoutRequest();
        fanoutRequest.setOrgData(orgReq);
        return fanoutRequest;
    }

    @Override
    public byte[] encodeResponse(Response rep) {
        FanoutResponse fanoutRep = (FanoutResponse) rep;
        return fanoutRep.encode();
    }

    @Override
    public Response decodeResponse(byte[] reqRep) {
        FanoutResponse fanoutRequest = new FanoutResponse();
        fanoutRequest.setOrgData(reqRep);
        return fanoutRequest;
    }

    @Override
    public Responses getResponses() {
        return responses;
    }

    @Override
    public Requests getRequests() {
        return requests;
    }
}
