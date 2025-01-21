package com.example.roomy.ui.Composables

import com.example.roomy.R

object ItemSuggestionMap {

    val nameToIconId: HashMap<String, Int> = hashMapOf(


        "Bananas" to R.drawable.banana,
        "Cheese" to R.drawable.cheese,
        "Cherries" to R.drawable.cherry,
        "Cola" to R.drawable.cola,
        "Eggs" to R.drawable.eggs,
        "Milk" to R.drawable.milk,
        "Steak" to R.drawable.steak,

        // Fruits
        "Apples" to R.drawable.apples,
        "Oranges" to R.drawable.oranges,
        "Grapes" to R.drawable.grapes,
        "Lemons" to R.drawable.lemons,
        "Pineapple" to R.drawable.pineapple,
        "Strawberries" to R.drawable.strawberries,
        "Peaches" to R.drawable.peaches,
        "Watermelon" to R.drawable.watermelon,
        "Avocados" to R.drawable.avocados,
        "Pears" to R.drawable.pears,

        // Vegetables
        "Carrots" to R.drawable.carrots,
        "Broccoli" to R.drawable.broccoli,
        "Spinach" to R.drawable.spinach,
        "Potatoes" to R.drawable.potatoes,
        "Tomatoes" to R.drawable.tomatoes,
        "Cucumbers" to R.drawable.cucumbers,
        "Lettuce" to R.drawable.lettuce,
        "Bell Peppers" to R.drawable.bell_peppers,
        "Zucchini" to R.drawable.zucchini,
        "Onions" to R.drawable.onions,

        // Dairy
        "Butter" to R.drawable.butter,
        "Yogurt" to R.drawable.yogurt,
        "Cream Cheese" to R.drawable.cream_cheese,
        "Sour Cream" to R.drawable.sour_cream,
        "Ice Cream" to R.drawable.ice_cream,

        // Meat & Seafood
        "Chicken" to R.drawable.chicken,
        "Beef" to R.drawable.beef,
        "Ham" to R.drawable.beef,
        "Bacon" to R.drawable.bacon,
        "Salmon" to R.drawable.salmon,
        "Shrimp" to R.drawable.shrimp,

        // Grains & Bakery
        "Bread" to R.drawable.bread,
        "Croissants" to R.drawable.croissant,
        "Bagels" to R.drawable.bagel,
        "Pita Bread" to R.drawable.pita,
        "Tortillas" to R.drawable.tortilla,
        "Pasta" to R.drawable.pasta,
        "Rice" to R.drawable.rice,
        "Cereal" to R.drawable.cereal,
        "Oats" to R.drawable.oats,

        // Snacks
        "Chips" to R.drawable.chips,
        "Popcorn" to R.drawable.popcorn,
        "Nuts" to R.drawable.nuts,
        "Chocolate" to R.drawable.chocolate,
        "Cookies" to R.drawable.cookie,
        "Candy" to R.drawable.candy,
        "Pretzels" to R.drawable.pretzel,

        // Beverages
        "Coffee" to R.drawable.coffee,
        "Tea" to R.drawable.tea,
        "Juice" to R.drawable.juice,
        "Soda" to R.drawable.soda,
        "Water" to R.drawable.water,
        "Wine" to R.drawable.wine,
        "Beer" to R.drawable.beer,


        // Spices & Herbs
        "Salt" to R.drawable.salt,
        "Pepper" to R.drawable.pepper,
        "Garlic" to R.drawable.garlic,
        "Basil" to R.drawable.basil,
        "Thyme" to R.drawable.thyme,
        "Cinnamon" to R.drawable.cinnamon,
        "Paprika" to R.drawable.paprika,
        "Chillis" to R.drawable.chillis,

        // Household
        "Toilet Paper" to R.drawable.toilet_paper,
        "Dish Soap" to R.drawable.dish_soap,
        "Laundry Detergent" to R.drawable.laundry_detergent,
        "Trash Bags" to R.drawable.trash_bags,
        "Sponges" to R.drawable.sponge,

        // Pet Food
        "Dog Food" to R.drawable.dog_food,
        "Cat Food" to R.drawable.cat_food,

        // Home & Garden
        "Plant Pots" to R.drawable.soil,
        "Soil" to R.drawable.soil,
        "Fertilizer" to R.drawable.fertilizer,

        // Other Essentials
        "Toothpaste" to R.drawable.toothpaste,
        "Toothbrush" to R.drawable.toothbrush,
        "Shampoo" to R.drawable.shampoo,
        "Conditioner" to R.drawable.shampoo,
        "Soap" to R.drawable.soap,
        "Deodorant" to R.drawable.deodorant,
        "Razors" to R.drawable.razor,
        "Tissues" to R.drawable.tissues
    )
}

val suggestedItems =
    listOf(
        "Bananas", "Cheese", "Cherries", "Cola", "Eggs", "Milk", "Steak",
        "Apples", "Oranges", "Grapes", "Lemons", "Pineapple", "Strawberries", "Peaches",
        "Watermelon", "Avocados", "Pears", "Carrots", "Broccoli", "Spinach", "Potatoes",
        "Tomatoes", "Cucumbers", "Lettuce", "Bell Peppers", "Zucchini", "Onions", "Butter",
        "Yogurt", "Cream Cheese", "Sour Cream", "Ice Cream", "Chicken", "Beef", "Ham", "Bacon",
        "Salmon", "Shrimp", "Bread", "Croissants", "Bagels", "Pita Bread", "Tortillas", "Pasta",
        "Rice", "Cereal", "Oats", "Chips", "Popcorn", "Nuts", "Chocolate", "Cookies", "Candy",
        "Pretzels", "Coffee", "Tea", "Juice", "Soda", "Water", "Wine", "Beer", "Salt", "Pepper",
        "Garlic", "Basil", "Thyme", "Cinnamon", "Paprika", "Chillis", "Toilet Paper", "Dish Soap",
        "Laundry Detergent", "Trash Bags", "Sponges", "Dog Food", "Cat Food", "Plant Pots", "Soil",
        "Fertilizer", "Toothpaste", "Toothbrush", "Shampoo", "Conditioner", "Soap", "Deodorant",
        "Razors", "Tissues"
    )

