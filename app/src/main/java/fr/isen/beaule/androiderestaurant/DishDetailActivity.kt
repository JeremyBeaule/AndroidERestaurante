package fr.isen.beaule.androiderestaurant

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter

import com.google.gson.Gson
import org.json.JSONObject
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
    Image(
        painter = painterResource(id = R.drawable.back),
        contentDescription = "Background",
        modifier = Modifier.fillMaxSize() ,

        contentScale = ContentScale.FillBounds  // Add this line

    )
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dishName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center // Center text horizontally
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (images.isNotEmpty()) {
            HorizontalPager(
                count = images.size,
                state = pagerState,
                modifier = Modifier
                    .height(200.dp) // Set the pager height
                //arrondir les coins
                    .clip(RoundedCornerShape(14.dp)) // Appliquer l'arrondi ici aussi pour que l'image s'aligne avec les coins de la carte

            ) { page ->
                DishImage(imageUrl = images[page])
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Here is where you call IngredientsButtons
        IngredientsButtons(ingredients = ingredients)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            //marquer quantité en police 30
            text = "Quantité",
            style = TextStyle(
                fontSize = 30.sp, // Set the font size to 30
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )


        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { if (quantity > 1) quantity -= 1 }) {
                Text("-")
            }
            Text(
                "$quantity",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
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
            textAlign = TextAlign.Center,
            color = Color.Yellow
            //mettre en gras

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onAddToCart(context, dishName, quantity, totalPrice) }) {
            Text("Ajouter au panier")
        }
    }
}

@Composable
fun IngredientsButtons(ingredients: List<String>) {
    // Organiser les ingrédients en lignes de 5
    val rows = ingredients.chunked(3)

    Column {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), // Ajouter un peu d'espace entre les lignes
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                row.forEach { ingredient ->
                    Button(
                        onClick = { /* Définissez ici votre action */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp), // Ajouter un peu d'espace entre les boutons
                        contentPadding = PaddingValues(8.dp) // Réduire le remplissage à l'intérieur du bouton pour économiser de l'espace
                    ) {
                        Text(
                            text = ingredient,
                            style = TextStyle(fontSize = 17.sp), // Réduire la taille du texte si nécessaire
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis // Ajoutez cela pour éviter que le texte ne dépasse du bouton
                        )
                    }
                }
                // S'il y a moins de 5 éléments dans la dernière rangée, ajoutez des boutons invisibles pour garder l'alignement
                if (row.size < 3) {
                    for (i in 0 until (3 - row.size)) {
                        Spacer(modifier = Modifier.weight(1f).padding(horizontal = 2.dp))
                    }
                }
            }
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
            val context = LocalContext.current
            AndroidERestauranteTheme {
                DishDetailScreen(
                    context = context,
                    cartItemCount = cartItemCount.value,
                    dishName = dishName,
                    ingredients = ingredientNames,
                    images = images,
                    prices = pricesList,
                    onAddToCart = { ctx, name, quantity, price ->
                        addToCart(ctx, name, quantity, price)
                    },
                    onCartClicked = {
                        val intent = Intent(context, CartActivity::class.java)
                        context.startActivity(intent)
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
        // Chemin vers le fichier JSON du panier.
        val file = File(context.filesDir, "shopping.json")

        // Calculer le nombre total d'articles en lisant le fichier JSON.
        val totalCount = if (file.exists()) {
            try {
                val json = file.readText()
                val jsonObject = JSONObject(json)
                val itemsArray = jsonObject.getJSONArray("items")
                var totalQuantity = 0
                for (i in 0 until itemsArray.length()) {
                    val item = itemsArray.getJSONObject(i)
                    totalQuantity += item.getInt("quantity")
                }
                totalQuantity // Retourner le total des quantités.
            } catch (e: Exception) {
                Log.e("getCartSize", "Error reading cart size", e)
                0
            }
        } else {
            0
        }

        // Mise à jour des SharedPreferences avec le nouveau total.
        val sharedPreferences = context.getSharedPreferences("fr.isen.beaule.androiderestaurant", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("cart_size", totalCount).apply()

        return totalCount // Retourner le total calculé.
    }

    override fun onResume() {
        super.onResume()
        updateCartSize()
    }
    private fun updateCartSize() {
        val cartSize = getCartSize(this)
        updateCartItemCount(this, cartSize) // Mettre à jour l'interface utilisateur avec la nouvelle valeur de `cartSize`.

        // Mettre à jour votre interface utilisateur avec la nouvelle valeur de `cartSize`.
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
    var finalImageUrl by remember { mutableStateOf(defaultImageUrl) }

    LaunchedEffect(key1 = imageUrl) {
        if (imageUrl.isNotEmpty()) {
            val isValid = isValidImageUrl(imageUrl)
            finalImageUrl = if (isValid) imageUrl else defaultImageUrl
        }
    }

    Image(
        painter = rememberImagePainter(data = finalImageUrl),
        contentDescription = "Dish Image",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun DishDetailScreen(
    context: Context, // Ajoutez cette ligne
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
                title = { Text(text = "Details") },
                //ajoutez de la couleur #60E0DB
                backgroundColor = Color(0xFF60E0DB),

                actions = {
                    BadgeIcon(
                        count = cartItemCount,
                        icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart") },
                        onClick = { onCartClicked() } // Ici, on appelle la fonction onCartClicked passée en paramètre
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
            onAddToCart = { _, name, quantity, price ->
                onAddToCart(context, name, quantity, price) // Passer le contexte actuel lors de l'ajout au panier
            }
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


suspend fun isValidImageUrl(imageUrl: String): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000 // Temps d'attente de connexion de 5 secondes
            connection.readTimeout = 5000 // Temps d'attente de lecture de 5 secondes
            connection.requestMethod = "GET"
            connection.connect()

            val responseCode = connection.responseCode
            connection.disconnect()

            responseCode == HttpURLConnection.HTTP_OK // Vérifie si la réponse est 200 (OK)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}








