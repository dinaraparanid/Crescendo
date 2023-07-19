package com.paranid5.mediastreamer.domain.video_cash_service

import android.os.Parcel
import android.os.Parcelable
import com.paranid5.mediastreamer.domain.utils.media.MediaFile
import io.ktor.http.HttpStatusCode
import kotlinx.parcelize.Parcelize

sealed interface CashingResult : Parcelable {
    sealed interface DownloadResult : CashingResult {
        @Parcelize
        data class Success(val file: MediaFile) : DownloadResult

        data class Error(val statusCode: HttpStatusCode) : DownloadResult {
            companion object CREATOR : Parcelable.Creator<Error> {
                override fun createFromParcel(parcel: Parcel) = Error(parcel)
                override fun newArray(size: Int): Array<Error?> = arrayOfNulls(size)
            }

            constructor(parcel: Parcel) : this(
                HttpStatusCode(
                    value = parcel.readInt(),
                    description = parcel.readString()!!
                )
            )

            override fun describeContents() = 0

            override fun writeToParcel(dest: Parcel, flags: Int) = dest.run {
                writeInt(statusCode.value)
                writeString(statusCode.description)
            }
        }

        @Parcelize
        object Canceled : DownloadResult

        @Parcelize
        object FileCreationError : DownloadResult

        @Parcelize
        object ConnectionLostError : DownloadResult
    }

    @Parcelize
    object ConversionError : CashingResult
}