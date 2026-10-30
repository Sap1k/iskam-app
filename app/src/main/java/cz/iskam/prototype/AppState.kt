package cz.iskam.prototype

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

// Prototype only: everything below is hard-coded demo data, nothing talks to a backend.

enum class Screen(val tab: Boolean = false) {
    Login, Home(true), Platby(true), Notif(true), More(true),
    Platba, Karta, Prevod, Zaplaceno, Doklad, Profil, Ubytovani, Smlouva,
    Zavady, ZavadaNova, ZavadaOk, Rezervace, RezervaceNova, Pokoje, Kontakty, Napoveda
}

class Route(val screen: Screen, val arg: String? = null) {
    val id = nextId++

    private companion object {
        var nextId = 0L
    }
}

enum class NavDir { Fwd, Back, Fade }

data class Pending(val k: String, val v: Int, val info: String? = null)

val PENDING = listOf(
    Pending("Kolejné (říjen)", 3000),
    Pending("Elektřina", 1000, "elektrina"),
    Pending("Poplatky", 350, "poplatky"),
)
val TOTAL = PENDING.sumOf { it.v }

val INFO = mapOf(
    "elektrina" to ("Elektřina" to "Platí se paušál za každý měsíc. Na konci semestru se vyúčtuje podle skutečné spotřeby pokoje a přeplatek se vrací."),
    "poplatky" to ("Poplatky" to "Internet, praní a úklid společných prostor. Jedna částka za měsíc, nic se nedoplácí zvlášť."),
    "jistina" to ("Jistina" to "Vratná záloha, kterou skládáš při nástupu. Vrací se po ukončení ubytování, pokud je pokoj v pořádku a nic nedlužíš."),
    "splatnost" to ("Splatnost" to "Kolejné je splatné vždy k 1. dni měsíce. Tři dny předem ti appka pošle připomínku."),
)

data class HistoryItem(
    val id: String, val m: String, val sum: Int, val date: String, val how: String,
    val items: List<Pair<String, Int>>,
)

enum class Kind { Default, Tertiary, Ok }

data class Notif(
    val id: String, val icon: ImageVector, val text: String, val sub: String,
    val kind: Kind = Kind.Default, val go: Screen? = null, val unread: Boolean = false,
)

data class Defect(
    val id: String, val cat: String, val icon: ImageVector, val title: String,
    val date: String, val st: Int, val reminded: Boolean = false,
)

data class Reservation(val id: String, val room: String, val icon: ImageVector, val time: String)

data class DefectDraft(val cat: String? = null, val text: String = "", val photo: Boolean = false)
data class ResDraft(val room: String? = null, val day: String? = null, val slot: String? = null)

sealed interface Sheet {
    data class Info(val key: String) : Sheet
    data class Wallet(val name: String) : Sheet
    data object EndStay : Sheet
    data object Msg : Sheet
}

data class Snack(val text: String, val icon: ImageVector, val stamp: Long = System.nanoTime())

/** Czech number formatting like toLocaleString('cs-CZ'): 4 350 Kč */
fun kc(n: Int): String = n.toString().reversed().chunked(3).joinToString(" ").reversed() + " Kč"

class AppState {
    var paid by mutableStateOf(false)
    var paidAt by mutableStateOf<String?>(null)
    var method by mutableStateOf<String?>(null)

    val history = listOf(
        HistoryItem("zari", "Září 2026", 4200, "1. 9. 2026", "Kartou", listOf("Kolejné (září)" to 3000, "Elektřina" to 900, "Poplatky" to 300)),
        HistoryItem("srpen", "Srpen 2026", 3950, "31. 7. 2026", "Převodem", listOf("Kolejné (srpen)" to 3000, "Elektřina" to 650, "Poplatky" to 300)),
        HistoryItem("cerven", "Červen 2026", 4100, "2. 6. 2026", "Apple Pay", listOf("Kolejné (červen)" to 3000, "Elektřina" to 800, "Poplatky" to 300)),
    )

    val notifs = mutableStateListOf(
        Notif("n1", Icons.Rounded.WaterDrop, "Bude odstávka vody 1. 1. 1111.", "Správa koleje", unread = true),
        Notif("n2", Icons.Rounded.FitnessCenter, "Byla provedena rezervace posilovny", "čt 18:00", go = Screen.Rezervace, unread = true),
        Notif("n3", Icons.Rounded.Favorite, "Za dva dny máš naplánovanou svatbu – přijď včas", "Obřadní sál · út 12:00", Kind.Tertiary, Screen.Rezervace, true),
        Notif("n4", Icons.Rounded.Draw, "Smlouva na další období čeká na podpis", "Podepíšeš přímo v appce", Kind.Tertiary, Screen.Smlouva),
    )

    val defects = mutableStateListOf(
        Defect("d1", "Voda", Icons.Rounded.WaterDrop, "Kape kohoutek v kuchyňce", "12. 10.", 1),
        Defect("d2", "Internet", Icons.Rounded.Lan, "Nefunguje zásuvka LAN u postele", "3. 10.", 2),
    )

    val res = mutableStateListOf(
        Reservation("r1", "Obřadní sál", Icons.Rounded.Favorite, "út 12:00"),
        Reservation("r2", "Posilovna", Icons.Rounded.FitnessCenter, "čt 18:00"),
    )

    var signed by mutableStateOf(false)
    var signAgree by mutableStateOf(false)
    var ending by mutableStateOf(false)
    var remind by mutableStateOf(true)
    val moveInt = mutableStateMapOf<String, Boolean>()
    var segPay by mutableStateOf("cek")
    var draft by mutableStateOf(DefectDraft())
    var resDraft by mutableStateOf(ResDraft())
    var roomFilter by mutableStateOf("vse")
    var faqQ by mutableStateOf("")

    /** null = follow the system, like the prototype until you flip the switch. */
    var dark by mutableStateOf<Boolean?>(null)

    var stack by mutableStateOf(listOf(Route(Screen.Login)))
        private set
    var dir by mutableStateOf(NavDir.Fade)
        private set
    var sheet by mutableStateOf<Sheet?>(null)
    var snack by mutableStateOf<Snack?>(null)

    val current: Route get() = stack.last()
    val unread: Int get() = notifs.count { it.unread }

    fun go(screen: Screen, arg: String? = null) {
        sheet = null; dir = NavDir.Fwd; stack = stack + Route(screen, arg)
    }

    fun back() {
        sheet = null
        if (stack.size > 1) { dir = NavDir.Back; stack = stack.dropLast(1) }
    }

    fun tab(screen: Screen) {
        sheet = null; dir = NavDir.Fade; stack = listOf(Route(screen))
    }

    fun replace(screen: Screen) {
        dir = NavDir.Fwd; stack = stack.dropLast(1) + Route(screen)
    }

    fun toast(text: String, icon: ImageVector = Icons.Rounded.Info) {
        snack = Snack(text, icon)
    }

    fun login() = tab(Screen.Home)

    fun logout() {
        sheet = null; dir = NavDir.Back; stack = listOf(Route(Screen.Login))
    }

    fun finishPay(how: String) {
        paid = true; method = how; paidAt = "30. 10. 2026"
        notifs.add(0, Notif("np${System.nanoTime()}", Icons.Rounded.CheckCircle, "Platba proběhla úspěšně", "${kc(TOTAL)} · $how", Kind.Ok, Screen.Platby, true))
        sheet = null; dir = NavDir.Fwd
        stack = listOf(Route(Screen.Home), Route(Screen.Zaplaceno))
    }

    fun openNotif(id: String) {
        val i = notifs.indexOfFirst { it.id == id }
        if (i < 0) return
        val n = notifs[i]
        notifs[i] = n.copy(unread = false)
        when (n.go) {
            null -> toast("Označeno jako přečtené", Icons.Rounded.CheckCircle)
            Screen.Platby -> { segPay = "hist"; tab(Screen.Platby) }
            else -> go(n.go)
        }
    }
}
