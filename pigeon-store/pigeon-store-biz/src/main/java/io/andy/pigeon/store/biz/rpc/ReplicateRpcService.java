package io.andy.pigeon.store.biz.rpc;

import io.andy.pigeon.store.biz.bo.ReplicateRequest;
import io.andy.pigeon.store.biz.bo.ReplicateResponse;

public interface ReplicateRpcService {
    ReplicateResponse doRequest(ReplicateRequest request);
}
