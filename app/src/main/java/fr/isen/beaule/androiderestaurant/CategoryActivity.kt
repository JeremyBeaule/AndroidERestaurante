package fr.isen.beaule.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.beaule.androiderestaurant.ui.theme.AndroidERestauranteTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categoryName = intent.getStringExtra("category_name") ?: "Catégorie Inconnue"

        val dishes = when (categoryName) {
            "Entrées" -> resources.getString(R.string.sample_entrees).split(", ")
            "Plats" -> resources.getString(R.string.sample_main_courses).split(", ")
            "Desserts" -> resources.getString(R.string.sample_desserts).split(", ")
            else -> listOf("Aucun plat disponible")
        }

        setContent {
            AndroidERestauranteTheme {
                CategoryScreen(categoryName, dishes) { dishName ->
                    val intent = Intent(this@CategoryActivity, DishDetailActivity::class.java)
                    intent.putExtra("dish_name", dishName)
                    startActivity(intent)
                }
            }
        }

    }
}
@Composable
fun CategoryScreen(categoryName: String, dishes: List<String>, onDishClicked: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn {
            items(dishes) { dish ->
                Text(
                    text = dish,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable { onDishClicked(dish) } // Gestion des clics sur chaque plat
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                Divider()
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview() {
    // Simuler les listes comme elles seraient décomposées à partir de votre strings.xml
    val sampleDishes = listOf("Test Dish 1", "Test Dish 2")

    // Utiliser les données simulées dans l'aperçu de CategoryScreen
    AndroidERestauranteTheme {
        CategoryScreen("Entrées", sampleDishes) { dish ->
            // Ici, vous pourriez afficher un message de débogage ou ne rien faire.
            println("Preview cliqué sur le plat: $dish")
        }
    }
}
