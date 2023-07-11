package ru.leti.wise.task.task.helper;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        GrpcServerCall grpcServerCall = new GrpcServerCall(call);
        ServerCall.Listener listener = next.startCall(grpcServerCall, headers);
        return new GrpcForwardingServerCallListener<ReqT>(call.getMethodDescriptor(), listener) {
            @Override
            public void onMessage(ReqT message) {
                log.info("Method: {}, Message: {}", methodName, message);
                super.onMessage(message);
            }
        };
    }

    private class GrpcServerCall<ReqT, RespT> extends ServerCall<ReqT, RespT> {

        ServerCall<ReqT, RespT> serverCall;

        protected GrpcServerCall(ServerCall<ReqT, RespT> serverCall) {
            this.serverCall = serverCall;
        }

        @Override
        public void request(int numMessages) {
            serverCall.request(numMessages);
        }

        @Override
        public void sendHeaders(Metadata headers) {
            serverCall.sendHeaders(headers);
        }

        @Override
        public void sendMessage(RespT message) {
            log.info("Method: {}, Response: {}", serverCall.getMethodDescriptor().getFullMethodName(), message);
            serverCall.sendMessage(message);
        }

        @Override
        public void close(Status status, Metadata trailers) {
            serverCall.close(status, trailers);
        }

        @Override
        public boolean isCancelled() {
            return serverCall.isCancelled();
        }

        @Override
        public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
            return serverCall.getMethodDescriptor();
        }
    }

    private class GrpcForwardingServerCallListener<ReqT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

        String methodName;

        protected GrpcForwardingServerCallListener(MethodDescriptor method, ServerCall.Listener<ReqT> listener) {
            super(listener);
            methodName = method.getFullMethodName();
        }
    }
}
