package io.andy.pigeon.store.biz.logic;

import io.andy.pigeon.store.biz.constant.DBRole;
import io.andy.pigeon.store.biz.constant.ReturnCode;
import io.andy.pigeon.store.biz.replicate.PhysicalDB;
import io.andy.pigeon.store.biz.replicate.PhysicalDBReplicator;
import org.rocksdb.*;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class LogicDB {
    private String dbName;
    private DBRole role;

    private PhysicalDB physicalDB;

    private RocksDB db;

    private PhysicalDBReplicator replicator;

    public LogicDB(ApplicationContext context, String dbName, DBRole role, RocksDB db) {
        this.role = role;
        this.db = db;
        this.replicator = context.getBean(PhysicalDBReplicator.class);
        replicator.addDB(dbName, db, role);
        physicalDB = replicator.getReplicatedDB().get(dbName);
    }

    public ReturnCode write(WriteOptions options, WriteBatch updates) {
        if (physicalDB != null) {
            return physicalDB.write(options, updates);
        } else {
            try {
                db.write(options, updates);
                return ReturnCode.OK;
            } catch (RocksDBException e) {
                e.printStackTrace();
                return ReturnCode.WRITE_ERROR;
            }
        }
    }

    public byte[] get(ReadOptions options, byte[] key) {
        try {
            return db.get(options, key);
        } catch (RocksDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<byte[]> multiGet(ReadOptions options, List<byte[]> keys) {
        try {
            return db.multiGetAsList(options, keys);
        } catch (RocksDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public RocksIterator newInterator(ReadOptions options) {
        return db.newIterator(options);
    }

}
