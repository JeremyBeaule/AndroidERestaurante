package fr.isen.beaule.androiderestaurant

import ShoppingCart
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.gson.Gson

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Récupérer le JSON du panier depuis l'intent
        val cartJson = intent.getStringExtra("cart_json")

        // Analyser le JSON et afficher les éléments du panier
        val gson = Gson()
        val cart = gson.fromJson(cartJson, ShoppingCart::class.java)
        // Utilisez les données du panier pour afficher dans votre vue
    }
}
