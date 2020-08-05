package io.andy.pigeon.store.biz.replicate;

import com.google.common.primitives.Longs;
import lombok.Data;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatch;


@Data
public class LogDataExtractor extends WriteBatch.Handler {
    private long ms;

    @Override
    public void put(int i, byte[] bytes, byte[] bytes1) throws RocksDBException {

    }

    @Override
    public void put(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void merge(int i, byte[] bytes, byte[] bytes1) throws RocksDBException {

    }

    @Override
    public void merge(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void delete(int i, byte[] bytes) throws RocksDBException {

    }

    @Override
    public void delete(byte[] bytes) {

    }

    @Override
    public void singleDelete(int i, byte[] bytes) throws RocksDBException {

    }

    @Override
    public void singleDelete(byte[] bytes) {

    }

    @Override
    public void deleteRange(int i, byte[] bytes, byte[] bytes1) throws RocksDBException {

    }

    @Override
    public void deleteRange(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void logData(byte[] bytes) {
        ms = Longs.fromByteArray(bytes);
    }

    @Override
    public void putBlobIndex(int i, byte[] bytes, byte[] bytes1) throws RocksDBException {

    }

    @Override
    public void markBeginPrepare() throws RocksDBException {

    }

    @Override
    public void markEndPrepare(byte[] bytes) throws RocksDBException {

    }

    @Override
    public void markNoop(boolean b) throws RocksDBException {

    }

    @Override
    public void markRollback(byte[] bytes) throws RocksDBException {

    }

    @Override
    public void markCommit(byte[] bytes) throws RocksDBException {

    }
}
