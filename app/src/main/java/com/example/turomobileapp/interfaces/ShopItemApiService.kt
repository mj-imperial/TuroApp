package com.example.turomobileapp.interfaces

import com.example.turomobileapp.models.ShopItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ShopItemApiService {
    @GET("/shop/items")
    suspend fun getAllShopItems(): Response<List<ShopItem>>

    @GET("/shop/items/{itemId}")
    suspend fun getShopItemById(
        @Path("itemId") itemId: String
    ): Response<ShopItem>
}