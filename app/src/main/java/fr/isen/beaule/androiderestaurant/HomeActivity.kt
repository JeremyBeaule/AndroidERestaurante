package fr.isen.beaule.androiderestaurant

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
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
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize() ,

                    contentScale = ContentScale.FillBounds  // Add this line

        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BIEN",
                    color = Color.Black,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = R.drawable.mascot),
                    contentDescription = "Mascot",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(shape = CircleShape)
                        .background(color = Color.White)
                        .shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            clip = true
                        )
                )
                Text(
                    text = "VENUE",
                    color = Color.Black,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(160.dp))
            MenuCategory("Entrées", Color(0xFFFFC107), onClick = { onCategoryClicked("Entrées") })
            MenuCategory("Plats", Color(0xFFFFC107), onClick = { onCategoryClicked("Plats") })
            MenuCategory("Desserts", Color(0xFFFFC107), onClick = { onCategoryClicked("Desserts") })
        }
    }
}

@Composable
fun MenuCategory(name: String, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
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
