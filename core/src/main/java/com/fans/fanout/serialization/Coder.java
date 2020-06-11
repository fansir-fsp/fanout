package com.fans.fanout.serialization;

import com.fans.fanout.net.Request;
import com.fans.fanout.net.Requests;
import com.fans.fanout.net.Response;
import com.fans.fanout.net.Responses;

/**
 * @author ：fsp
 * @date ：2020/5/19 14:42
 */
public interface Coder {

    byte[] encodeRequest(Request req);

    Request decodeRequest(byte[] orgReq);

    byte[] encodeResponse(Response rep);

    Response decodeResponse(byte[] reqRep);

    Responses getResponses();

    Requests getRequests();
}
