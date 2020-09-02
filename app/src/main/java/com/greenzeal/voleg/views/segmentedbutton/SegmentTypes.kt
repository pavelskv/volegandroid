package com.greenzeal.voleg.views.segmentedbutton

internal enum class SegmentType {
    FIRST, CENTER, LAST, ONLY
}

internal enum class SegmentSpreadType(val value: Int) {
    EVENLY(0), WRAP(1)
}