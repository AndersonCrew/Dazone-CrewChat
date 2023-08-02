package com.dazone.crewchatoff.interfaces

import com.dazone.crewchatoff.dto.ErrorDto

interface ICheckSSL {
    fun hasSSL(hasSSL: Boolean)
    fun checkSSLError(errorData: ErrorDto)
}

interface ICheckLogin {
    fun onSuccess(api: Boolean)
    fun onError(errorData: ErrorDto)
}

public interface WatingUpload {
    fun onFinish()
}