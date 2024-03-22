package fr.isen.beaule.androiderestaurant

import CartItem
import ShoppingCart
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.beaule.androiderestaurant.ui.theme.AndroidERestauranteTheme
import org.json.JSONArray
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

import com.google.gson.Gson
import java.io.File

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DishDetailLayout(
    modifier: Modifier = Modifier,
    dishName: String,
    ingredients: List<String>,
    images: List<String>,
    prices: List<Double>,
    onAddToCart: (Context, String, Int, Double) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    val totalPrice = remember(quantity) { prices.first() * quantity }
    val pagerState = rememberPagerState()
    val context = LocalContext.current


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (images.isNotEmpty()) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .height(200.dp) // Fixer la hauteur du pager
            ) { page ->
                DishImage(imageUrl = images[page])
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = dishName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center // Centrer le texte horizontalement
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ingrédients : ${ingredients.joinToString(", ")}",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center // Centrer le texte horizontalement
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (quantity > 1) quantity -= 1 }) {
                Text("-")
            }
            Text(
                "$quantity",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Button(onClick = { quantity += 1 }) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Prix total : ${totalPrice}€",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onAddToCart(context, dishName, quantity, totalPrice) }) {
            Text("Ajouter au panier")
        }
    }
}
class DishDetailActivity : ComponentActivity() {
    private val cartItemCount = mutableStateOf(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateCartItemCount(this, getCartSize(this))

        Log.d("DishDetailActivity", "onCreate")


        Log.d("DishDetailActivity", "onCreate")
        title = "Détail du plat"
        val dishName = intent.getStringExtra("dish_name") ?: "Nom inconnu"
        val ingredientsJson = intent.getStringExtra("ingredients") ?: "[]"
        val imagesJson = intent.getStringExtra("images") ?: "[]"

        val ingredientNames = JSONArray(ingredientsJson).let { jsonArray ->
            (0 until jsonArray.length()).map { i ->
                jsonArray.getJSONObject(i).getString("name_fr")
            }
        }
        Log.d("DishDetailActivity", "Ingredient names: $ingredientNames")

        val images = JSONArray(imagesJson).let { jsonArray ->
            (0 until jsonArray.length()).map { i ->
                jsonArray.getString(i)
            }
        }
        // Prendre la première image de la liste, si disponible
        val secondImageUrl = images.getOrNull(1) ?: ""
        //si secondeimageUrl vide prendre la premiere image

        val imageUrl =
            if (secondImageUrl.isNotEmpty()) secondImageUrl else images.firstOrNull() ?: ""
        Log.d("DishDetailActivity", "Selected Image URL: $imageUrl")

        val pricesJson = intent.getStringExtra("prices") ?: "[]"
        val pricesArray = JSONArray(pricesJson)
        val pricesList = mutableListOf<Double>() // Créer une liste pour stocker les prix extraits.

// Parcourir le tableau JSON et extraire les prix.
        for (i in 0 until pricesArray.length()) {
            val priceObject = pricesArray.getJSONObject(i)
            val price =
                priceObject.getDouble("price") // Assurez-vous que "price" est le nom correct de la clé dans votre JSON.
            pricesList.add(price)
        }

// Maintenant, `pricesList` contient tous les prix sous forme de doubles.
// Vous pouvez utiliser cette liste comme nécessaire, par exemple pour l'afficher dans l'interface utilisateur.

        Log.d("DishDetailActivity", "Price: $pricesList")

        setContent {
            AndroidERestauranteTheme {
                DishDetailScreen(
                    cartItemCount = cartItemCount.value,
                    dishName = dishName,
                    ingredients = ingredientNames,
                    images = images,
                    prices = pricesList,
                    onAddToCart = { context, name, quantity, price ->
                        addToCart(context, name, quantity, price)
                        // Optionnel : Mettre à jour l'interface utilisateur ici si nécessaire
                    },
                     onCartClicked = {
                        // Implémentez ce que vous voulez faire lorsque l'icône du panier est cliquée
                    }
                )
            }
        }
    }





    // Ajoutez ceci dans votre DishDetailActivity
    fun addToCart(context: Context, dishName: String, quantity: Int, price: Double) {
        val cartItem = CartItem(dishName, quantity, price)

        // Charger le panier existant à partir du fichier JSON
        val file = File(context.filesDir, "shopping.json")
        Log.d("FilePath", file.absolutePath)
        val shoppingCart = loadShoppingCart(file)

        // Ajouter l'article au panier
        shoppingCart.addItem(cartItem)

        // Mettre à jour le fichier JSON avec le panier mis à jour
        shoppingCart.saveToFile(file)

        // Mise à jour des préférences utilisateur pour le nombre d'articles dans le panier
        val sharedPreferences = context.getSharedPreferences("fr.isen.beaule.androiderestaurant", Context.MODE_PRIVATE)
        val totalQuantity = shoppingCart.totalQuantity() // calculer le total de la quantité d'articles dans le panier
        sharedPreferences.edit().putInt("cart_size", totalQuantity).apply()

        // Mettez à jour l'état pour refléter le changement dans l'interface utilisateur
        updateCartItemCount(context, totalQuantity)

        // Remplacer Snackbar par Toast
        Toast.makeText(context, "Article ajouté au panier", Toast.LENGTH_LONG).show()
        Log.d("DishDetailActivity", "Article ajouté au panier: $dishName, $quantity, $price")
    }

    // Fonction pour charger le panier existant à partir du fichier JSON
    fun loadShoppingCart(file: File): ShoppingCart {
        return if (file.exists()) {
            val json = file.readText()
            ShoppingCart().FromJson(json)
        } else {
            ShoppingCart()
        }
    }


    fun updateCartItemCount(context: Context, count: Int) {
        val sharedPreferences = context.getSharedPreferences("fr.isen.beaule.androiderestaurant", Context.MODE_PRIVATE)
        Log.d("DishDetailActivity", "Cart size: $count")
        cartItemCount.value = count // Mettre à jour l'état pour refléter le changement dans l'interface utilisateur
        Log.d("DishDetailActivity", "Cart size: ${cartItemCount.value}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    fun ShoppingCart.toJson(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
    fun getCartSize(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("fr.isen.beaule.androiderestaurant", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("cart_size", 0)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        val menuItem = menu?.findItem(R.id.action_cart)
        val actionView = menuItem?.actionView
        val cartBadge: TextView = actionView?.findViewById(R.id.cart_badge) as TextView

        val cartSize = getCartSize(this)
        if (cartSize > 0) {
            cartBadge.text = cartSize.toString()
            cartBadge.visibility = View.VISIBLE
        } else {
            cartBadge.visibility = View.GONE
        }

        actionView?.setOnClickListener { onOptionsItemSelected(menuItem!!) }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                // Récupérer le contenu du panier (le JSON) depuis votre fichier
                val file = File(filesDir, "shopping.json")
                val cartJson = file.readText()

                // Démarrer CartActivity et passer le contenu du panier en tant qu'extra
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("cart_json", cartJson)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    }

    @Composable
    fun DishImage(imageUrl: String) {
        val defaultImageUrl = "https://upload.wikimedia.org/wikipedia/en/e/ed/Nyan_cat_250px_frame.PNG"
        val finalImageUrl = if (imageUrl.isEmpty()) defaultImageUrl else imageUrl

        Image(
            painter = rememberImagePainter(data = finalImageUrl),
            contentDescription = "Dish Image",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun DishDetailPreview() {
        AndroidERestauranteTheme {
            DishDetailActivity().DishDetailLayout(
                dishName = "Nom du plat",
                ingredients = listOf("Ingrédient 1", "Ingrédient 2"),
                images = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
                prices = listOf(10.0, 15.0),
                onAddToCart = { context, dishName, quantity, price ->
                    Log.d("DishDetailPreview", "Added to cart: $dishName, $quantity, $price")
                }
            )
            // Il est 12h11 quand j'écris ça, tu ne devrais pas faire ça mon petit Jérémy ~ François
        }
    }
    @Composable
    fun DishDetailScreen(
        dishName: String,

        ingredients: List<String>,
        images: List<String>,
        prices: List<Double>,
        onAddToCart: (Context, String, Int, Double) -> Unit,
        cartItemCount: Int, // Ajouter le nombre d'articles dans le panier
        onCartClicked: () -> Unit // Ajouter un gestionnaire de clic pour l'icône du panier

    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = dishName) },
                    actions = {
                        BadgeIcon(
                            count = cartItemCount,
                            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                            onClick = onCartClicked
                        )
                    }
                )
            }
        ) { innerPadding ->
            DishDetailLayout(
                modifier = Modifier.padding(innerPadding),
                dishName = dishName,
                ingredients = ingredients,
                images = images,
                prices = prices,
                onAddToCart = onAddToCart
            )
        }
    }
    @Composable
    fun BadgeIcon(
        count: Int,
        icon: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Box(modifier = modifier.wrapContentSize(Alignment.TopEnd)) {
            IconButton(onClick = onClick) {
                icon()
            }
            if (count > 0) {
                Text(
                    text = count.toString(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 8.dp, y = -4.dp)
                        .size(16.dp)
                        .background(Color.Red, shape = CircleShape)
                        .wrapContentSize(Alignment.Center),
                    color = Color.White,
                    style = TextStyle(fontSize = 10.sp)
                )
            }
        }
    }








