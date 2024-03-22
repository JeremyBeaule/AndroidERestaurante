package fr.isen.beaule.androiderestaurant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import fr.isen.beaule.androiderestaurant.ui.theme.AndroidERestauranteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate appelé")
        title = "Home"
        setContent {
            AndroidERestauranteTheme {
                HomePage(
                    onCategoryClicked = { category ->
                        goToCategory(category)
                    },
                    navigateToDishDetail = { dishName ->
                        val intent = Intent(this@MainActivity, DishDetailActivity::class.java)
                        intent.putExtra("dish_name", dishName)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun goToCategory(category: String) {
        Log.d("MainActivity", "Navigue vers CategoryActivity avec catégorie: $category")
        val intent = Intent(this, CategoryActivity::class.java).apply {
            putExtra("category_name", category)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "MainActivity est détruite")
    }
}

@Composable
fun HomePage(onCategoryClicked: (String) -> Unit, navigateToDishDetail: (String) -> Unit) {
    val context = LocalContext.current // Obtenez le contexte local pour le Toast

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Bienvenue chez\nAnDroidRestaurant",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            MenuCategory("Entrées", Color(0xFFFFA500)) {
                onCategoryClicked("Entrées")
            }
            MenuCategory("Plats", Color(0xFFFFA500)) {
                onCategoryClicked("Plats")
            }
            MenuCategory("Desserts", Color(0xFFFFA500)) {
                onCategoryClicked("Desserts")
            }
        }
        Image(
            painter = painterResource(id = R.drawable.mascot),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(128.dp)
        )
    }
}

@Composable
fun MenuCategory(name: String, backgroundColor: Color, onClick: () -> Unit) {
    Text(
        text = name,
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White
    )
    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 50.dp))
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AndroidERestauranteTheme {
        HomePage(
            onCategoryClicked = { category ->
                println("Clicked on category: $category")
            },
            navigateToDishDetail = { dishName ->
                println("Clicked on dish: $dishName")
            }
        )
    }
}
