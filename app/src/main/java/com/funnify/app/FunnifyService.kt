package com.funnify.app

import android.content.Context
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit

object FunnifyService {
    fun authenticate(context: Context, idToken: String, authProvider: Auth.AuthProvider, onSuccessListener: (Auth.AuthenticateResponse) -> Unit, onFailureListener: (Auth.AuthenticateResponse?) -> Unit) {
        val channel = ManagedChannelBuilder.forAddress(context.getString(R.string.funnify_grpc_server_host), context.getString(R.string.funnify_grpc_server_port).toInt()).usePlaintext().build()
        val stub = FunnifyServiceGrpc.newBlockingStub(channel)//.withDeadlineAfter(500, TimeUnit.MILLISECONDS)
        val grpcRequest = Auth.AuthenticateRequest.newBuilder().setIdToken(idToken).setProvider(authProvider).build()
        var grpcResponse: Auth.AuthenticateResponse? = null
        val thread = Thread { grpcResponse = stub.authenticate(grpcRequest) }
        thread.start()
        thread.join()
        val response = grpcResponse
        if (response != null && response.result.success)
            onSuccessListener(response)
        else
            onFailureListener(response)
    }
}