package br.com.raveline.reciperx.utils

object Constants {
    const val baseUrl = "https://raw.githubusercontent.com/DevTides/"

    // DATASTORE
    const val dataStoreDogKey = "dataStoreDogKey"
    const val timeStoreDogKey = "timeStoreDog_Key"

    // NOTIFICATIONS
    const val notificationIdKey = 58005
    const val channelIdKey = "br.com.raveline.reciperx.channel.id"

    // CAMERA
    const val cameraIdKey = 2151
    const val galleryIdKey = 222
    const val cameraNameKey = "RecipeRx_Images"

    // DISHES CONSTANTS
    const val DISH_TYPE = "DishType"
    const val DISH_CATEGORY = "DishCategory"
    const val DISH_COOKING_TYPE = "DishCookingType"

    fun dishTypes():ArrayList<String>{

        val list = arrayListOf<String>()
        list.add("Breakfast")
        list.add("Lunch")
        list.add("Snacks")
        list.add("Dinner")
        list.add("Salad")
        list.add("Side Dish")
        list.add("Dessert")
        list.add("Others")

        return list
    }

    fun dishCategories():ArrayList<String>{
        val list = arrayListOf<String>()

        list.add("Pizza")
        list.add("BBQ")
        list.add("Bakery")
        list.add("Burger")
        list.add("Cafe")
        list.add("Chicken")
        list.add("Dessert")
        list.add("Drinks")
        list.add("Hot Dogs")
        list.add("Juices")
        list.add("Sandwich")
        list.add("Tea & Coffee")
        list.add("Wraps")
        list.add("Others")

        return list
    }

    fun dishCookingTime():ArrayList<String>{
        val list = arrayListOf<String>()

        list.add("5")
        list.add("10")
        list.add("20")
        list.add("30")
        list.add("40")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")
        list.add("More...")

        return list
    }





}