import com.example.kelompokandori.model.Destination
import com.example.kelompokandori.SupabaseClient
import io.github.jan.supabase.postgrest.from

class DestinationRepository {
    suspend fun getDestinations(): List<Destination> {
        return try {
            SupabaseClient.client.from("destinations")
                .select()
                .decodeList<Destination>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}