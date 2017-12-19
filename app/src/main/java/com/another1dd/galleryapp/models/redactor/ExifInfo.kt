package com.another1dd.galleryapp.models.redactor

data class ExifInfo(
        var mExifOrientation: Int = 0,
        var mExifDegrees: Int = 0,
        var mExifTranslation: Int = 0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val exifInfo = other as ExifInfo

        if (mExifOrientation != exifInfo.mExifOrientation) return false
        return if (mExifDegrees != exifInfo.mExifDegrees) false else mExifTranslation == exifInfo.mExifTranslation

    }

    override fun hashCode(): Int {
        var result = mExifOrientation
        result = 31 * result + mExifDegrees
        result = 31 * result + mExifTranslation
        return result
    }
}
