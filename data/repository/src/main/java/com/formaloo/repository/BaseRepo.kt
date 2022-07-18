package com.formaloo.repository

import android.util.Log
import com.formaloo.common.exception.Failure
import com.formaloo.common.exception.ViewFailure
import com.formaloo.common.functional.Either
import org.json.JSONObject
import retrofit2.Call
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.formaloo.model.Result

open class BaseRepo {

    fun <T, R> request(call: Call<T>, transform: (T) -> R, default: T): Either<Failure, R> {
        return try {
            val response = call.execute()

            var jObjError: JSONObject? = null
            Log.e(TAG, "request: " + response.raw())
//            Log.e(TAG, "request: " + response.body())
            try {
                jObjError = JSONObject(response.errorBody()?.string())
                Log.e("request", "Repo responseErrorBody jObjError-> $jObjError")

            } catch (e: Exception) {

                Log.e(TAG, "request: ${response.raw()}")
            }

            when (response.code()) {
                200 -> Either.Right(transform((response.body() ?: default)))
                201 -> Either.Right(transform((response.body() ?: default)))
                400 -> Either.Left(ViewFailure.responseError("$jObjError"))
                401 -> Either.Left(Failure.UNAUTHORIZED_Error)
                500 -> Either.Left(Failure.ServerError)
                else -> Either.Left(ViewFailure.responseError("$jObjError"))
            }

        } catch (exception: IOException) {
            Either.Left(ViewFailure.responseError("IOException++>  $exception"))

        } catch (exception: SocketException) {
            Either.Left(Failure.NetworkConnection)

        }  catch (exception: SocketTimeoutException) {
            Either.Left(Failure.NetworkConnection)

        }  catch (exception: UnknownHostException) {
            Either.Left(Failure.NetworkConnection)

        } catch (exception: Throwable) {
            Log.e(TAG, "request: " + exception)
            Either.Left(ViewFailure.responseError("exception++>  $exception"))
        }

    }

    fun <T, R> callRequest(call: Call<T>, transform: (T) -> R, default: T):Result<R> {
        return try {
            val response = call.execute()

            var jObjError: JSONObject? = null
            Log.e(TAG, "request: " + response.raw())
//            Log.e(TAG, "request: " + response.body())
            try {
                jObjError = JSONObject(response.errorBody()?.string())
                Log.e("request", "Repo responseErrorBody jObjError-> $jObjError")

            } catch (e: Exception) {

                Log.e(TAG, "request: ${response.raw()}")
            }

            when (response.code()) {
                200 -> Result.Success(transform((response.body() ?: default)))
                201 -> Result.Success(transform((response.body() ?: default)))
//                400 -> Result.ErrorStr("$jObjError")
//                401 -> Result.ErrorStr("unauthorized")
//                500 -> Result.ErrorStr("ServerError")
                else -> Result.Error(null)
            }

        } catch (exception: IOException) {
            Result.Error(exception)

        } catch (exception: SocketException) {
            Result.Error(exception)

        }  catch (exception: SocketTimeoutException) {
            Result.Error(exception)

        }  catch (exception: UnknownHostException) {
            Result.Error(exception)

        } catch (exception: Throwable) {
            Log.e(TAG, "request: " + exception)
            Result.Error(null)
        }

    }

}
