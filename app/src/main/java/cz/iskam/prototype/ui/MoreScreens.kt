package cz.iskam.prototype.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Bed
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Campaign
import androidx.compose.material.icons.rounded.Chair
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.RoomService
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.iskam.prototype.AppState
import cz.iskam.prototype.Defect
import cz.iskam.prototype.Kind
import cz.iskam.prototype.Reservation
import cz.iskam.prototype.Screen
import cz.iskam.prototype.Sheet
import cz.iskam.prototype.kc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/* ---------- Oznámení ---------- */

@Composable
fun NotifScreen(s: AppState) {
    val cs = MaterialTheme.colorScheme
    val gone = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    ScreenScaffold(withNav = true) {
        Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            H("Oznámení", modifier = Modifier.weight(1f))
            if (s.notifs.isNotEmpty()) IskamButton("Smazat vše", style = Btn.Text) {
                scope.launch {
                    s.notifs.map { it.id }.forEachIndexed { i, id -> launch { delay(i * 60L); gone += id } }
                    delay(380); s.notifs.clear(); gone.clear()
                }
            }
        }
        if (s.notifs.isEmpty()) {
            EmptyState(Icons.Rounded.NotificationsOff, Shapes.cookie9, "Žádná nová oznámení", "zatím 🙂")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                s.notifs.forEach { n ->
                    AnimatedVisibility(
                        visible = n.id !in gone,
                        exit = fadeOut() + slideOutHorizontally { it / 5 } + shrinkVertically(),
                    ) {
                        val (bg, fg) = when (n.kind) {
                            Kind.Tertiary -> cs.tertiaryContainer to cs.onTertiaryContainer
                            Kind.Ok -> cs.primaryContainer to cs.onPrimaryContainer
                            Kind.Default -> cs.secondaryContainer to cs.onSecondaryContainer
                        }
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(cs.surfaceContainer)
                                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Box(Modifier.size(40.dp).background(bg, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                                Icon(n.icon, null, Modifier.size(21.dp), tint = fg)
                            }
                            Column(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).clickable { s.openNotif(n.id) }) {
                                Text(
                                    buildAnnotatedString {
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(n.text) }
                                        if (n.unread) withStyle(SpanStyle(color = cs.error)) { append("  ●") }
                                    },
                                    fontSize = 14.5.sp, lineHeight = 19.5.sp,
                                )
                                Text(n.sub, fontSize = 12.5.sp, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                            }
                            RoundIconButton(Icons.Rounded.Close, "Zavřít", {
                                gone += n.id
                                scope.launch { delay(300); s.notifs.removeAll { it.id == n.id }; gone -= n.id }
                            }, iconSize = 20.dp)
                        }
                    }
                }
            }
            IskamCard(Modifier.padding(top = 6.dp), outlined = true) {
                CardTitle(Icons.Rounded.NotificationsActive, "Připomínky splatnosti", size = 15.sp)
                CardDesc("Tři dny před splatností ti přijde upozornění. ${if (s.remind) "Zapnuto." else "Vypnuto."}", cs.onSurfaceVariant)
            }
        }
    }
}

/* ---------- Více ---------- */

@Composable
fun MoreScreen(s: AppState) = ScreenScaffold(withNav = true) {
    H("Více", modifier = Modifier.padding(top = 10.dp))
    IskamCard(onClick = { s.go(Screen.Profil) }) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Avatar()
            Column(Modifier.weight(1f)) {
                TitleText("Jiří")
                Label("jiri_kara · Koleje na Káráku")
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
    Overline("Bydlení")
    SegmentedList(
        LRow(Icons.Rounded.Bed, "Ubytování", "Squat 01 · smlouva, jistina, ukončení") { s.go(Screen.Ubytovani) },
        LRow(Icons.Rounded.Draw, "Smlouva", if (s.signed) "Podepsáno" else "Čeká na podpis", lead = if (s.signed) Lead.Sec else Lead.Ter) { s.go(Screen.Smlouva) },
        LRow(Icons.Rounded.MeetingRoom, "Volné pokoje a přestěhování", "Koleje VŠE") { s.go(Screen.Pokoje) },
    )
    Overline("Na koleji")
    SegmentedList(
        LRow(Icons.Rounded.Build, "Závady", "${s.defects.count { it.st < 2 }} se řeší") { s.go(Screen.Zavady) },
        LRow(Icons.Rounded.Event, "Rezervace místností", "${s.res.size} aktivní") { s.go(Screen.Rezervace) },
    )
    Overline("Pomoc")
    SegmentedList(
        LRow(Icons.AutoMirrored.Rounded.Help, "Nápověda", "Jak na ISKAM, kolejní řád, termíny") { s.go(Screen.Napoveda) },
        LRow(Icons.Rounded.SupportAgent, "Kontakty", "Hospodářka, recepce, správa") { s.go(Screen.Kontakty) },
    )
    IskamButton("Odhlásit se", Modifier.padding(top = 6.dp), icon = Icons.AutoMirrored.Rounded.Logout, style = Btn.OutlinedRed, wide = true) { s.logout() }
}

@Composable
fun ProfilScreen(s: AppState, dark: Boolean) = ScreenScaffold {
    AppBar("Profil") { s.back() }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Avatar(112.dp, 5.dp)
        H("Jiří")
        Label("jiri_kara · student VŠE")
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatTile("Na Káráku", "kolej", Modifier.weight(1f))
        StatTile(if (s.paid) "0 minut" else "už 4 minuty", "pozdní zaplacení", Modifier.weight(1f))
    }
    SegmentedList(
        LRow(Icons.Rounded.NotificationsActive, "Připomínky splatnosti", "3 dny předem, push", trailing = {
            Switch(s.remind, null)
        }) {
            s.remind = !s.remind
            if (s.remind) s.toast("Připomínky zapnuté, 3 dny před splatností.", Icons.Rounded.NotificationsActive)
            else s.toast("Připomínky vypnuté.", Icons.Rounded.NotificationsOff)
        },
        LRow(Icons.Rounded.DarkMode, "Tmavý režim", "Podle systému, dokud nepřepneš", trailing = { Switch(dark, null) }) { s.dark = !dark },
        LRow(Icons.Rounded.Language, "Jazyk", "Čeština") { s.toast("English version is coming next semester.") },
    )
    IskamButton("Odhlásit se", icon = Icons.AutoMirrored.Rounded.Logout, style = Btn.OutlinedRed, wide = true) { s.logout() }
}

/* ---------- Ubytování & smlouva ---------- */

@Composable
fun UbytovaniScreen(s: AppState) {
    val cs = MaterialTheme.colorScheme
    ScreenScaffold {
        AppBar("Ubytování") { s.back() }
        IskamCard(container = cs.primaryContainer, contentColor = cs.onPrimaryContainer, padding = PaddingValues(20.dp), gap = 4.dp) {
            Text("Koleje na Káráku", style = T.label, color = cs.onPrimaryContainer.copy(alpha = .8f))
            Text("Squat 01", style = T.hxl)
            CardDesc("8 (0) spolubydlících")
        }
        ItemsBox(
            listOf(
                ItemRow("Typ pokoje", "Vícelůžkový"),
                ItemRow("Ubytování do", "30. 6. 2027"),
                ItemRow("Měsíčně", kc(3000)),
                ItemRow("Jistina", kc(5000), info = "jistina"),
                ItemRow("Smlouva", content = {
                    if (s.signed) Pill("Podepsáno", PillKind.Ok, Icons.Rounded.Check) else Pill("Čeká na podpis", PillKind.New)
                }),
            ),
            onInfo = { s.sheet = Sheet.Info(it) },
        )
        SegmentedList(
            if (!s.signed) LRow(Icons.Rounded.Draw, "Podepsat smlouvu", "Online, bez cesty na hospodářku", lead = Lead.Ter) { s.go(Screen.Smlouva) } else null,
            LRow(Icons.Rounded.SwapHoriz, "Přestěhovat se", "Volné pokoje na kolejích VŠE") { s.go(Screen.Pokoje) },
            LRow(Icons.Rounded.Description, "Kolejní řád", "Co platí na Káráku") { s.go(Screen.Napoveda) },
        )
        if (s.ending) {
            IskamCard(container = cs.errorContainer, contentColor = cs.onErrorContainer) {
                CardTitle(Icons.Rounded.HourglassTop, "Žádost o ukončení odeslána", tint = cs.error)
                CardDesc("Hospodářka se ozve s termínem předání pokoje.")
            }
        } else {
            IskamButton("Ukončit ubytování", icon = Icons.AutoMirrored.Rounded.Logout, style = Btn.OutlinedRed, wide = true) { s.sheet = Sheet.EndStay }
        }
    }
}

@Composable
fun SmlouvaScreen(s: AppState) {
    if (s.signed) {
        ScreenScaffold(center = true) {
            Box(Modifier.fillMaxWidth()) { AppBar("Smlouva") { s.back() } }
            Fill()
            Seal(Icons.Rounded.Draw, Shapes.softBurst)
            H("Podepsáno")
            Body("Smlouva na období 2026/27 je platná.\nKopii najdeš na školním e-mailu.", align = TextAlign.Center)
            Fill()
            IskamButton("Hotovo", big = true, wide = true) { s.back() }
        }
        return
    }
    var open by remember { mutableStateOf(false) }
    val rot by animateFloatAsState(if (open) 180f else 0f, label = "chev")
    ScreenScaffold {
        AppBar("Smlouva") { s.back() }
        H("Ubytovací smlouva 2026/27")
        ItemsBox(
            listOf(
                ItemRow("Kolej", "Koleje na Káráku"),
                ItemRow("Pokoj", "Squat 01"),
                ItemRow("Období", "1. 9. 2026 – 30. 6. 2027"),
                ItemRow("Měsíčně", kc(3000)),
            )
        )
        IskamCard(onClick = { open = !open }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Hlavní body smlouvy", Modifier.weight(1f), fontSize = 16.5.sp, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Rounded.ExpandMore, null, Modifier.rotate(rot), tint = MaterialTheme.colorScheme.outline)
            }
            AnimatedVisibility(open, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                Body(
                    "Kolejné se platí do 1. dne měsíce. Výpovědní lhůta je jeden měsíc. Jistina se vrací do 30 dnů po předání pokoje. Kolejní řád je součástí smlouvy.",
                    Modifier.padding(top = 6.dp),
                )
            }
        }
        CheckRow(s.signAgree, "Přečetl/a jsem smlouvu a kolejní řád") { s.signAgree = !s.signAgree }
        Fill()
        IskamButton("Podepsat online", icon = Icons.Rounded.Draw, big = true, wide = true, enabled = s.signAgree, loadMs = 1100) {
            s.signed = true
            s.notifs.removeAll { it.go == Screen.Smlouva }
        }
    }
}

/* ---------- Závady ---------- */

@Composable
fun ZavadyScreen(s: AppState) = ScreenScaffold(overlay = {
    Fab("Nahlásit závadu", Icons.Rounded.Add, Modifier.align(Alignment.BottomEnd).padding(18.dp).navigationBarsPadding()) {
        s.go(Screen.ZavadaNova)
    }
}) {
    AppBar("Závady") { s.back() }
    s.defects.forEach { d -> DefectCard(s, d) }
    Spacer(Modifier.heightIn(min = 70.dp))
}

@Composable
private fun DefectCard(s: AppState, d: Defect) {
    IskamCard(gap = 10.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LeadBox(d.icon)
            Column(Modifier.weight(1f)) {
                TitleText(d.title)
                Label("${d.cat} · nahlášeno ${d.date}")
            }
        }
        Steps(d.st)
        if (d.st < 2) {
            IskamButton(
                if (d.reminded) "Připomenuto" else "Pořád to nejde, připomenout",
                icon = if (d.reminded) Icons.Rounded.Done else Icons.Rounded.Campaign,
                style = if (d.reminded) Btn.Text else Btn.Tonal,
                enabled = !d.reminded,
            ) {
                val i = s.defects.indexOfFirst { it.id == d.id }
                s.defects[i] = d.copy(reminded = true)
                s.toast("Připomenuto správci budovy", Icons.Rounded.Campaign)
            }
        }
    }
}

private val CATS = listOf(
    "Voda" to Icons.Rounded.WaterDrop, "Elektřina" to Icons.Rounded.Bolt, "Topení" to Icons.Rounded.Thermostat,
    "Internet" to Icons.Rounded.Lan, "Nábytek" to Icons.Rounded.Chair, "Jiné" to Icons.Rounded.MoreHoriz,
)

@Composable
fun ZavadaNovaScreen(s: AppState) {
    val d = s.draft
    ScreenScaffold {
        AppBar("Nahlásit závadu") { s.back() }
        Crumbs("Závady", "Nová")
        TitleText("Co nefunguje?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            CATS.forEach { (c, ic) -> IskamChip(c, d.cat == c, icon = ic) { s.draft = d.copy(cat = c) } }
        }
        OutlinedTextField(
            d.text, { s.draft = s.draft.copy(text = it) }, Modifier.fillMaxWidth().heightIn(min = 120.dp),
            label = { Text("Popis") }, placeholder = { Text("Např. kape kohoutek v kuchyňce, nejde zavřít") },
            shape = RoundedCornerShape(14.dp), minLines = 4,
        )
        ItemsBox(listOf(ItemRow("Místo", "Squat 01, Koleje na Káráku")))
        IskamButton(
            if (d.photo) "Fotka přidána" else "Přidat fotku",
            icon = if (d.photo) Icons.Rounded.Check else Icons.Rounded.AddAPhoto,
            style = if (d.photo) Btn.Tonal else Btn.Outlined, wide = true,
        ) { s.draft = d.copy(photo = !d.photo) }
        Fill()
        IskamButton("Odeslat", big = true, wide = true, enabled = d.cat != null, loadMs = 900) {
            val cur = s.draft
            val ic = CATS.first { it.first == cur.cat }.second
            s.defects.add(0, Defect("d${System.nanoTime()}", cur.cat!!, ic, cur.text.trim().ifEmpty { "${cur.cat}: bez popisu" }, "30. 10.", 0))
            s.draft = cz.iskam.prototype.DefectDraft()
            s.replace(Screen.ZavadaOk)
        }
    }
}

@Composable
fun ZavadaOkScreen(s: AppState) = ScreenScaffold(center = true) {
    val cs = MaterialTheme.colorScheme
    Seal(Icons.Rounded.Build, Shapes.cookie6, cs.tertiaryContainer, cs.onTertiaryContainer)
    H("Nahlášeno")
    Body("Správce to uvidí ještě dnes. Stav sleduj v Závadách, o změně ti dáme vědět.", align = TextAlign.Center)
    Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IskamButton("Zobrazit závady", big = true, wide = true) { s.replace(Screen.Zavady) }
        IskamButton("Domů", style = Btn.Text) { s.tab(Screen.Home) }
    }
}

/* ---------- Rezervace ---------- */

@Composable
fun RezervaceScreen(s: AppState) = ScreenScaffold(overlay = {
    Fab("Nová rezervace", Icons.Rounded.Add, Modifier.align(Alignment.BottomEnd).padding(18.dp).navigationBarsPadding()) {
        s.go(Screen.RezervaceNova)
    }
}) {
    AppBar("Rezervace") { s.back() }
    if (s.res.isEmpty()) {
        EmptyState(Icons.Rounded.Event, Shapes.cookie6, "Nemáš žádnou rezervaci")
    } else {
        SegmentedList(*s.res.map { r ->
            LRow(r.icon, r.room, r.time, lead = if (r.icon == Icons.Rounded.Favorite) Lead.Ter else Lead.Sec, trailing = {
                RoundIconButton(Icons.Rounded.EventBusy, "Zrušit rezervaci", {
                    s.res.removeAll { it.id == r.id }
                    s.toast("Rezervace zrušena", Icons.Rounded.EventBusy)
                }, iconSize = 20.dp)
            })
        }.toTypedArray())
    }
    Spacer(Modifier.heightIn(min = 70.dp))
}

private val ROOMS = listOf(
    "Posilovna" to Icons.Rounded.FitnessCenter, "Studovna" to Icons.AutoMirrored.Rounded.MenuBook,
    "Prádelna" to Icons.Rounded.LocalLaundryService, "Obřadní sál" to Icons.Rounded.Favorite,
)
private val DAYS = listOf("Po 2.", "Út 3.", "St 4.", "Čt 5.", "Pá 6.")
private val SLOTS = listOf("8:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00")

@Composable
fun RezervaceNovaScreen(s: AppState) {
    val d = s.resDraft
    val busy = (d.room ?: "").length + (DAYS.indexOf(d.day) + 1)
    val ready = d.room != null && d.day != null && d.slot != null
    ScreenScaffold {
        AppBar("Nová rezervace") { s.back() }
        TitleText("Místnost")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ROOMS.forEach { (r, ic) -> IskamChip(r, d.room == r, icon = ic) { s.resDraft = d.copy(room = r, slot = null) } }
        }
        TitleText("Den")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DAYS.forEach { day -> IskamChip(day, d.day == day) { s.resDraft = d.copy(day = day, slot = null) } }
        }
        if (d.room != null && d.day != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TitleText("Čas ")
                Label("· obsazené jsou přeškrtnuté")
            }
            SLOTS.chunked(4).forEachIndexed { row, chunk ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chunk.forEachIndexed { k, slot ->
                        val i = row * 4 + k
                        val off = (i * 7 + busy) % 5 == 0
                        IskamChip(slot, d.slot == slot, Modifier.weight(1f), enabled = !off) { s.resDraft = d.copy(slot = slot) }
                    }
                }
            }
        } else {
            Body("Vyber místnost a den, ukážeme volné časy.")
        }
        Fill()
        IskamButton(if (ready) "Rezervovat · ${d.day} ${d.slot}" else "Rezervovat", big = true, wide = true, enabled = ready, loadMs = 800) {
            val cur = s.resDraft
            val ic = ROOMS.first { it.first == cur.room }.second
            val time = "${cur.day!!.split(" ")[0].lowercase()} ${cur.slot}"
            s.res.add(Reservation("r${System.nanoTime()}", cur.room!!, ic, time))
            s.notifs.add(0, cz.iskam.prototype.Notif("nr${System.nanoTime()}", ic, "Rezervace: ${cur.room}", "${cur.day} ${cur.slot}", go = Screen.Rezervace, unread = true))
            s.resDraft = cz.iskam.prototype.ResDraft()
            s.replace(Screen.Rezervace)
            s.toast(if (cur.room == "Obřadní sál") "Rezervováno. Přijď včas." else "Rezervováno", Icons.Rounded.EventAvailable)
        }
    }
}

/* ---------- Volné pokoje ---------- */

private data class Room(val id: String, val typ: String, val name: String, val od: String, val cena: Int, val feat: List<String>)

private val FREE_ROOMS = listOf(
    Room("p1", "1", "Jarov III · pokoj 412", "od 1. 12.", 4400, listOf("Vlastní koupelna", "Lednice")),
    Room("p2", "2", "Koleje na Káráku · Squat 02", "od 1. 11.", 3000, listOf("Stejná kolej", "Kuchyňka na patře")),
    Room("p3", "2", "Kolej Vltava · pokoj 218", "od 1. 1.", 3700, listOf("Blízko metra", "Sdílená koupelna")),
    Room("p4", "1", "Palachova kolej · pokoj 33", "od 1. 2.", 4600, listOf("Centrum", "Vlastní koupelna")),
)

@Composable
fun PokojeScreen(s: AppState) = ScreenScaffold {
    AppBar("Volné pokoje") { s.back() }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("vse" to "Vše", "1" to "Jednolůžkové", "2" to "Dvoulůžkové").forEach { (k, l) ->
            IskamChip(l, s.roomFilter == k) { s.roomFilter = k }
        }
    }
    FREE_ROOMS.filter { s.roomFilter == "vse" || it.typ == s.roomFilter }.forEach { r ->
        val int = s.moveInt[r.id] == true
        IskamCard(gap = 10.dp) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.weight(1f)) {
                    TitleText(r.name)
                    Label("${if (r.typ == "1") "Jednolůžkový" else "Dvoulůžkový"} · ${r.od}")
                }
                Pill(kc(r.cena), PillKind.Ok)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                r.feat.forEach { f ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Rounded.Check, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Label(f)
                    }
                }
            }
            IskamButton(
                if (int) "Zájem odeslán" else "Mám zájem o přestěhování",
                icon = if (int) Icons.Rounded.Check else Icons.Rounded.SwapHoriz,
                style = if (int) Btn.Tonal else Btn.Outlined,
            ) {
                s.moveInt[r.id] = !int
                if (!int) s.toast("Hospodářka dostala tvůj zájem o přestěhování", Icons.AutoMirrored.Rounded.Send)
            }
        }
    }
}

/* ---------- Kontakty ---------- */

@Composable
private fun ContactHead(icon: ImageVector, lead: Lead, title: String, sub: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LeadBox(icon, lead, size = 44.dp, corner = 16.dp)
        Column { TitleText(title); Label(sub) }
    }
}

@Composable
fun KontaktyScreen(s: AppState) = ScreenScaffold {
    AppBar("Kontakty") { s.back() }
    Body("Většinu věcí vyřešíš v appce. Když ne, tady je, kdo co řeší.")
    IskamCard(gap = 10.dp) {
        ContactHead(Icons.Rounded.Badge, Lead.Pri, "Hospodářka koleje", "Smlouvy, platby, stěhování")
        Body("Kancelář v přízemí · po–čt 9:00–11:00, 13:00–15:00", size = 14.sp)
        IskamButton("Napsat zprávu", icon = Icons.AutoMirrored.Rounded.Chat, style = Btn.Tonal) { s.sheet = Sheet.Msg }
    }
    IskamCard(gap = 8.dp) {
        ContactHead(Icons.Rounded.RoomService, Lead.Sec, "Recepce", "Klíče, návštěvy, balíky")
        Body("Vestibul · nonstop", size = 14.sp)
    }
    IskamCard(gap = 8.dp) {
        ContactHead(Icons.Rounded.Handyman, Lead.Ter, "Správce budovy", "Závady a opravy")
        IskamButton("Nahlásit závadu v appce", icon = Icons.Rounded.Build, style = Btn.Text) { s.go(Screen.ZavadaNova) }
    }
}

/* ---------- Nápověda ---------- */

private data class Faq(val q: String, val a: String, val go: Screen? = null, val cta: String? = null)

private val FAQ = listOf(
    Faq("Jak zaplatit kolejné?", "Na úvodní obrazovce klepni na Zaplatit vše a vyber způsob platby. Hotovo za pár vteřin.", Screen.Platba, "Zaplatit teď"),
    Faq("Jak poznám, že platba prošla?", "Hned po zaplacení uvidíš potvrzení, přijde ti oznámení a v Platbách › Provedené najdeš doklad."),
    Faq("Jak ukončit ubytování?", "V sekci Ubytování dole klepni na Ukončit ubytování a vyber datum. Hospodářka se ozve s termínem předání.", Screen.Ubytovani, "Otevřít Ubytování"),
    Faq("Jak nahlásit závadu?", "Domů › Závada, vyber kategorii a popiš, co nejde. Stav pak sleduješ v Závadách a můžeš připomenout.", Screen.ZavadaNova, "Nahlásit závadu"),
    Faq("Kdy jsou termíny nástupů?", "Zimní semestr od 15. 9., letní od 10. 2. Přesný termín máš ve smlouvě."),
    Faq("Kolejní řád v kostce", "Noční klid 22:00–6:00, návštěvy do 22:00 hlásit na recepci, kouření jen venku."),
    Faq("Co znamená ikona ⓘ?", "Vysvětlí položku, u které je, třeba co je jistina nebo za co platíš elektřinu."),
)

@Composable
private fun FaqItem(f: Faq, s: AppState) {
    var open by remember { mutableStateOf(false) }
    val cs = MaterialTheme.colorScheme
    val r by animateDpAsState(if (open) 28.dp else 20.dp, MaterialTheme.motionScheme.fastSpatialSpec(), label = "r")
    val bg by animateColorAsState(if (open) cs.surfaceContainerHigh else cs.surfaceContainer, label = "bg")
    val rot by animateFloatAsState(if (open) 180f else 0f, MaterialTheme.motionScheme.fastSpatialSpec(), label = "rot")
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(r)).background(bg)) {
        Row(
            Modifier.fillMaxWidth().clickable { open = !open }.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(f.q, Modifier.weight(1f), fontSize = 15.5.sp, fontWeight = FontWeight.SemiBold)
            Icon(Icons.Rounded.ExpandMore, null, Modifier.rotate(rot), tint = cs.onSurfaceVariant)
        }
        AnimatedVisibility(open, enter = expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()) + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                Body(f.a, size = 14.5.sp)
                if (f.go != null && f.cta != null) {
                    IskamButton("${f.cta} ›", Modifier.padding(top = 2.dp), style = Btn.Text) { s.go(f.go) }
                }
            }
        }
    }
}

@Composable
fun NapovedaScreen(s: AppState) = ScreenScaffold {
    AppBar("Nápověda") { s.back() }
    val cs = MaterialTheme.colorScheme
    TextField(
        s.faqQ, { s.faqQ = it }, Modifier.fillMaxWidth(),
        placeholder = { Text("Hledat, např. platba, závada") },
        leadingIcon = { Icon(Icons.Rounded.Search, null) },
        singleLine = true, shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = cs.surfaceContainerHigh, unfocusedContainerColor = cs.surfaceContainerHigh,
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
        ),
    )
    val q = s.faqQ.trim().lowercase()
    val list = FAQ.filter { q.isEmpty() || (it.q + it.a).lowercase().contains(q) }
    if (list.isEmpty()) {
        Column(Modifier.fillMaxWidth().padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ShapeIcon(Icons.Rounded.SearchOff, Shapes.flower)
            TitleText("srry, nic se nenašlo")
            IskamButton("Zeptat se hospodářky", style = Btn.Tonal) { s.go(Screen.Kontakty) }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { list.forEach { key(it.q) { FaqItem(it, s) } } }
    }
    IskamCard(outlined = true, onClick = { s.go(Screen.Kontakty) }) {
        CardTitle(Icons.Rounded.SupportAgent, "Nenašel/a jsi odpověď?", chevron = true, size = 15.sp)
        CardDesc("Napiš hospodářce přímo z appky.", cs.onSurfaceVariant)
    }
}
