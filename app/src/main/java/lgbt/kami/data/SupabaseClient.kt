package lgbt.kami.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth

import lgbt.kami.BuildConfig

object SupabaseClient {
    val client = createSupabaseClient(BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_ANON_KEY) {
        install(Auth)
    }
}
