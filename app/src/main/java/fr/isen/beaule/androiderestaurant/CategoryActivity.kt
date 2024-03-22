package fr.isen.beaule.androiderestaurant

import Dish
import MenuResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
                        val dishIntent = Intent(this@CategoryActivity, DishDetailActivity::class.java).apply {
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
    }

    @Composable
    fun CategoryScreen(
        categoryName: String,
        dishes: List<Dish>,
        onDishClicked: (Dish) -> Unit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            LazyColumn {
                items(dishes) { dish ->
                    Text(
                        text = dish.nameFr,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { onDishClicked(dish) }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Divider()
                }
            }
        }
    }



}