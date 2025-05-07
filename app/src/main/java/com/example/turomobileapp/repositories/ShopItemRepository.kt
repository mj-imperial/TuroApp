package com.example.turomobileapp.repositories

import com.example.turomobileapp.interfaces.ShopItemApiService
import com.example.turomobileapp.models.ShopItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShopItemRepository @Inject constructor(private val shopItemApiService: ShopItemApiService) {

    fun getAllShopItems(): Flow<Result<List<ShopItem>>> = flow {
        handleApiResponse(
            call = { shopItemApiService.getAllShopItems() },
            errorMessage = "Failed to get all shop items"
        )
    }

    fun getShopItemById(itemId: String): Flow<Result<ShopItem>> = flow {
        handleApiResponse(
            call = { shopItemApiService.getShopItemById(itemId) },
            errorMessage = "Failed to get shop item $itemId"
        )
    }
}