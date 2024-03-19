package fr.isen.beaule.androiderestaurant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.beaule.androiderestaurant.ui.theme.AndroidERestauranteTheme

class DishDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DishDetailActivity", "onCreate")

        val dishName = intent.getStringExtra("dish_name") ?: "Nom inconnu"
        setContent {
            AndroidERestauranteTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DishDetailContent(dishName = dishName)
                }
            }
        }
    }
}

@Composable
fun DishDetailContent(dishName: String) {
    Text(text = "Nom du plat : $dishName", modifier = Modifier.fillMaxSize())
}

@Preview(showBackground = true)
@Composable
fun DishDetailPreview() {
    AndroidERestauranteTheme {
        DishDetailContent(dishName = "Plat de démonstration")
    }
}
