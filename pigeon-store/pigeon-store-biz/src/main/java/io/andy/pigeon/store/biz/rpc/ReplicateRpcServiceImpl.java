package io.andy.pigeon.store.biz.rpc;

import io.andy.pigeon.rpc.core.server.annotation.RpcService;
import io.andy.pigeon.store.biz.bo.ReplicateRequest;
import io.andy.pigeon.store.biz.bo.ReplicateResponse;
import io.andy.pigeon.store.biz.replicate.PhysicalDB;
import io.andy.pigeon.store.biz.replicate.PhysicalDBReplicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RpcService
public class ReplicateRpcServiceImpl implements ReplicateRpcService {

    @Autowired
    private PhysicalDBReplicator storeReplicator;

    @Override
    public ReplicateResponse doRequest(ReplicateRequest request) {
        Map<String, PhysicalDB> mapDB = storeReplicator.getReplicatedDB();
        if (!mapDB.containsKey(request.getDbName())) {
            //TODO: add exception response
            return null;
        }

        PhysicalDB db = mapDB.get(request.getDbName());
        return db.handleReplicateRequest(request);
    }
}
