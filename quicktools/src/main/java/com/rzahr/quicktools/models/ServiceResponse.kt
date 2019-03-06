package com.rzahr.quicktools.models

import android.os.Parcel
import android.os.Parcelable

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused")
class ServiceResponse(parcel: Parcel) : Parcelable {

    private var responseDetails = ""
    private var errorDetails = ""
    private var responseCode = 500

    init {
        responseDetails = parcel.readString()
        errorDetails = parcel.readString()
        responseCode = parcel.readInt()
    }

    fun getResponseDetails(): String? {
        return responseDetails
    }

    fun getErrorDetails(): String? {
        return errorDetails
    }

    fun getResponseCode(): Int {
        return responseCode
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(responseDetails)
        dest.writeString(errorDetails)
        dest.writeInt(responseCode)
    }

    companion object CREATOR : Parcelable.Creator<ServiceResponse> {
        override fun createFromParcel(parcel: Parcel): ServiceResponse {
            return ServiceResponse(parcel)
        }

        override fun newArray(size: Int): Array<ServiceResponse?> {
            return arrayOfNulls(size)
        }
    }
}
