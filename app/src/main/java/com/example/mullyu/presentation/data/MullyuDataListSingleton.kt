package com.example.mullyu.presentation.data

import android.content.Context

object MullyuDataListSingleton {
    private var instance: MullyuDataList? = null
    private var lastSectorName: String? = null

    fun getInstance(viewModel: MullyuViewModel, context: Context, sectorName: String): MullyuDataList {
        if (instance == null || lastSectorName != sectorName) {
            instance?.disConnect() // 이전 연결 해제
            instance = MullyuDataList(viewModel, context, sectorName)
            lastSectorName = sectorName
        }
        return instance!!
    }

    fun clearInstance() {
        instance?.disConnect() // 연결 해제
        instance = null
        lastSectorName = null
    }
}