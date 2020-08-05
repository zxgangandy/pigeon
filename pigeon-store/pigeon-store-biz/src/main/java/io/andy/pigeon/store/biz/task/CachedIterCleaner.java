package io.andy.pigeon.store.biz.task;

import io.andy.pigeon.store.biz.replicate.PhysicalDB;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableScheduling
@Component
public class CachedIterCleaner {

    public static final int CACHE_IDLE_TIMEOUT_MS = 60 * 1000;

    private Map<String, PhysicalDB> dbs;


    @PostConstruct
    private void init() {
        dbs = new ConcurrentHashMap<>();
    }

    @Scheduled(fixedDelay = CACHE_IDLE_TIMEOUT_MS)
    @Async
    public void scheduleCleanup() {
        for (Map.Entry<String, PhysicalDB> entry : dbs.entrySet()) {
            entry.getValue().cleanIdleCachedIterators();
        }
    }

    public void addDB(PhysicalDB replicatedDB) {
        dbs.put(replicatedDB.getDbName(), replicatedDB);
    }

    public void removeDB(String dbName) {
        dbs.remove(dbName);
    }

}
