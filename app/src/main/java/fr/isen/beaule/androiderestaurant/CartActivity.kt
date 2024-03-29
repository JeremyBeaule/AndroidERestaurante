package fr.isen.beaule.androiderestaurant

import Cart
import CartItem
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val file = File(filesDir, "shopping.json")
        val cartJson = if (file.exists()) file.readText() else ""
        android.util.Log.d("CartActivity", "Cart JSON: $cartJson")

        setContent {
            CartScreen(cartJson, file)
        }
    }
}
@Composable
fun CartScreen(cartJson: String, file: File) {
    val gson = Gson()
    val cartType = object : TypeToken<Cart>() {}.type
    var items by remember { mutableStateOf(listOf<CartItem>()) }
    var totalAmount by remember { mutableStateOf(0.0) }

    LaunchedEffect(cartJson) {
        items = try {
            val cart = gson.fromJson(cartJson, cartType) as Cart
            val itemList = cart.items.groupBy { it.dishName }.map { (name, itemList) ->      CartItem(name, itemList.sumOf { it.quantity }, itemList.first().price)
            }
            totalAmount = itemList.sumOf { it.quantity * it.price }
            itemList
        } catch (e: Exception) {
            Log.e("CartActivity", "Error parsing cart JSON", e)
            emptyList()
        }
    }

    fun updateItems() {
        val newCartJson = gson.toJson(Cart(items))
        file.writeText(newCartJson)
        Log.d("CartActivity", "Updated Cart JSON: $newCartJson")
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxHeight(), // Assurez-vous que la colonne utilise toute la hauteur disponible
            verticalArrangement = Arrangement.SpaceBetween // Espacer les éléments du haut vers le bas
        ) {
            Column {
                Text(text = "Votre Panier", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, item ->
                    CartItemView(
                        item = item,
                        onAdd = {
                            items = items.map {
                                if (it.dishName == item.dishName) it.copy(quantity = it.quantity + 1) else it
                            }
                            updateItems()
                        },
                        onRemove = {
                            items = items.mapNotNull {
                                if (it.dishName == item.dishName) it.takeIf { it.quantity > 1 }?.copy(quantity = it.quantity - 1) else it
                            }
                            updateItems()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Text("Montant total: $totalAmount €", style = MaterialTheme.typography.bodyLarge)

            Button(
                onClick = {
                    // Logique de paiement ici
                },
                modifier = Modifier.align(Alignment.CenterHorizontally) // Centre le bouton horizontalement
            )
            {
                Text("Payer")
            }
        }
    }
}



@Composable
fun CartItemView(item: CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Utilisation de CardDefaults pour l'élévation
    ){
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = 8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Plat : ${item.dishName}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Prix : ${item.price} €", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Quantité : ${item.quantity}", style = MaterialTheme.typography.bodyLarge)

            }
            Button(onClick = { onAdd() }) {
                Text(text = "+")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onRemove() }) {
                Text(text = "-")
            }
        }
    }
}
