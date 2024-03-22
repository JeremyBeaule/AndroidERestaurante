// Définition des modèles de données basés sur la structure JSON.
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.File

data class Ingredient(
    @SerializedName("name_fr") val nameFr: String
)
data class Price(
    @SerializedName("price") val price: Double
)

data class Dish(
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("images") val images: List<String>,
    @SerializedName("ingredients") val ingredients: List<Ingredient>,
    @SerializedName("prices") val prices: List<Price>
)

data class Category(
    @SerializedName("name_fr") val nameFr: String,
    @SerializedName("items") val dishes: List<Dish>
)

data class MenuResponse(
    @SerializedName("data") val data: List<Category>
)

data class CartItem(
    val dishName: String,
    var quantity: Int,
    val price: Double
)

data class ShoppingCart(
    val items: MutableList<CartItem> = mutableListOf(),private val file: File? = null
) {
    fun addItem(item: CartItem) {
        items.add(item)
    }

    fun toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    fun totalQuantity(): Int {
        var total = 0
        for (item in items) {
            total += item.quantity
        }
        return total
    }
    fun FromJson(json: String): ShoppingCart {
        val gson = Gson()
        return gson.fromJson(json, ShoppingCart::class.java)
    }
    fun saveToFile(file: File?) {
        file?.writeText(toJson())
    }
}

