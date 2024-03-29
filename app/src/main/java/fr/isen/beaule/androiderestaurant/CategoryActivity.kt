package fr.isen.beaule.androiderestaurant

import Dish
import MenuResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.beaule.androiderestaurant.ui.theme.AndroidERestauranteTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
class CategoryActivity : ComponentActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestQueue = Volley.newRequestQueue(this)
        categoryName = intent.getStringExtra("category_name") ?: "Catégorie Inconnue"
        title = categoryName
        fetchMenu()
    }

    private fun fetchMenu() {
        val url = "http://test.api.catering.bluecodegames.com/menu"
        val postData = JSONObject().apply { put("id_shop", "1") }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData,
            { response ->
                val menuResponse = Gson().fromJson(response.toString(), MenuResponse::class.java)
                val selectedCategory = menuResponse.data.firstOrNull { it.nameFr == categoryName }
                val dishes = selectedCategory?.dishes ?: listOf()
                updateUI(dishes)
                Log.d("VolleyResponse", "Response: $response")
            },
            { error ->
                Log.e("VolleyError", "Error: ${error.message}")
                Toast.makeText(this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun updateUI(dishes: List<Dish>) {
        runOnUiThread {
            setContent {
                AndroidERestauranteTheme {
                    CategoryScreen(categoryName, dishes) { selectedDish ->
                        val dishIntent =
                            Intent(this@CategoryActivity, DishDetailActivity::class.java).apply {
                                putExtra("dish_name", selectedDish.nameFr)
                                putExtra("ingredients", Gson().toJson(selectedDish.ingredients))
                                // Assurez-vous que la liste d'images n'est pas vide avant d'essayer d'accéder à son contenu.
                                putExtra("images", Gson().toJson(selectedDish.images))
                                putExtra("prices", Gson().toJson(selectedDish.prices))

                                Log.d("CategoryActivity", "priceeee: ${selectedDish.prices}")
                                Log.d("CategoryActivity", "selectedDish: ${selectedDish.images}")


                            }
                        startActivity(dishIntent)
                    }
                }
            }
        }
    }@Composable
    fun CategoryScreen(
        categoryName: String,
        dishes: List<Dish>,
        onDishClicked: (Dish) -> Unit
    ) {
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize() ,

            contentScale = ContentScale.FillBounds  // Add this line

        )
        Column(modifier = Modifier.padding(16.dp)) {
            LazyColumn {
                items(dishes) { dish ->
                    Card(
                        modifier = Modifier // Modifier ici pour le jaune, changez `onSecondary` par votre couleur désirée

                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .clickable { onDishClicked(dish) },
                        shape = RoundedCornerShape(8.dp), // Vous pouvez ajuster le rayon de l'arrondi ici
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {

                            val imageUrl = when {
                                dish.images.size > 1 -> dish.images[1]
                                dish.images.isNotEmpty() -> dish.images.first()
                                else -> "https://res.cloudinary.com/teepublic/image/private/s--lJJYqwRw--/c_crop,x_10,y_10/c_fit,w_1109/c_crop,g_north_west,h_1260,w_1260,x_-76,y_-135/co_rgb:ffffff,e_colorize,u_Misc:One%20Pixel%20Gray/c_scale,g_north_west,h_1260,w_1260/fl_layer_apply,g_north_west,x_-76,y_-135/bo_0px_solid_white/t_Resized%20Artwork/c_fit,g_north_west,h_1054,w_1054/co_ffffff,e_outline:53/co_ffffff,e_outline:inner_fill:53/co_bbbbbb,e_outline:3:1000/c_mpad,g_center,h_1260,w_1260/b_rgb:eeeeee/c_limit,f_auto,h_630,q_auto:good:420,w_630/v1606803363/production/designs/16724317_0.jpg" // URL par défaut ou aucun URL
                            }
                            DishImage(imageUrl = imageUrl)
                            Text(
                                text = dish.nameFr,
                                modifier = Modifier.background(Color.Yellow)
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun DishImage(imageUrl: String) {
        val imageModifier = if (imageUrl.isNotEmpty()) {
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)) // Appliquer l'arrondi ici aussi pour que l'image s'aligne avec les coins de la carte
        } else {
            Modifier
                .fillMaxWidth()
                .height(200.dp)
        }

        val painter = rememberImagePainter(
            data = imageUrl,
            builder = {
                crossfade(true)
                placeholder(R.drawable.mascot)
                error(R.drawable.mascot)
            }
        )

        Image(
            painter = painter,
            contentDescription = "Dish Image",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        )
    }

}