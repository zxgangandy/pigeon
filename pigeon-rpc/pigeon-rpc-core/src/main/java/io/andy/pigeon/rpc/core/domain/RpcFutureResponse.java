package io.andy.pigeon.rpc.core.domain;


import io.andy.pigeon.rpc.core.client.RpcInvokerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 异步结果
 *
 */
public class RpcFutureResponse implements Future<RpcResponse> {

    private RpcInvokerFactory rpcInvokerFactory;

    private RpcRequest request;
    private RpcResponse response;

    // future lock
    private boolean done = false;
    private Object lock = new Object();

    public RpcFutureResponse(final RpcInvokerFactory rpcInvokerFactory, RpcRequest request) {
        this.rpcInvokerFactory = rpcInvokerFactory;
        this.request = request;

        // set-InvokerFuture
        setInvokerFuture();
    }

    public void setInvokerFuture() {
        this.rpcInvokerFactory.setInvokerFuture(request.getRequestId(), this);
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
        synchronized (lock) {
            done = true;// response有值,设置为true
            lock.notifyAll();// 解除阻塞
        }
    }


    // ---------------------- for invoke ----------------------

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // TODO
        return false;
    }

    @Override
    public boolean isCancelled() {
        // TODO
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        try {
            return get(-1, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!done) {
            synchronized (lock) {
                try {
                    if (timeout < 0) {
                        lock.wait();// 一直阻塞, 直到 lock.notifyAll()
                    } else {
                        long timeoutMillis = (TimeUnit.MILLISECONDS == unit) ? timeout : TimeUnit.MILLISECONDS.convert(timeout, unit);
                        lock.wait(timeoutMillis);// 阻塞, 直到 lock.notifyAll() 或超时
                    }
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }
        if (!done) {
            throw new RuntimeException("request timeout at:" + System.currentTimeMillis() + ", request:" + request.toString());
        }
        return response;
    }
}
