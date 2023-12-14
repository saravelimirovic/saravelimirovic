import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.ProductCard

class ProductAdapter(private val context: Context, private val products: List<ProductCard>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.grid_one_product, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productCard = products[position]

        // Postavljanje podataka proizvoda u prikaz
        val resourceId = context.resources.getIdentifier("food", "drawable", context.packageName)
        val resourceId2 = context.resources.getIdentifier("favorite_fill0_wght400_grad0_opsz24", "drawable", context.packageName)

        holder.productImage.setImageResource(resourceId)
        holder.like.setImageResource(resourceId2)
        holder.rating.rating = 3.5F
        holder.name.text = productCard.name
//        holder.price.text = productCard.price.toString() + " RSD"
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.grid_image_product)
        val name: TextView = itemView.findViewById(R.id.grid_name_product)
        val like: ImageView = itemView.findViewById(R.id.grid_like_product)
        val rating: RatingBar = itemView.findViewById(R.id.grid_ratingBar_product)
    }
}
