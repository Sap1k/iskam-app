package cz.iskam.prototype.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.LocalLaundryService
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.iskam.prototype.AppState
import cz.iskam.prototype.Notif
import cz.iskam.prototype.ResDraft
import cz.iskam.prototype.Reservation
import cz.iskam.prototype.Screen

private data class ResRoom(val name: String, val icon: ImageVector, val sub: String)

private val ROOMS = listOf(
    ResRoom("Posilovna", Icons.Rounded.FitnessCenter, "2. patro · max 90 min"),
    ResRoom("Studovna", Icons.AutoMirrored.Rounded.MenuBook, "Tichá zóna · 8 míst"),
    ResRoom("Prádelna", Icons.Rounded.LocalLaundryService, "2 pračky, sušička"),
    ResRoom("Obřadní sál", Icons.Rounded.Favorite, "Svatby a jiné katastrofy"),
)
private val DAYS = listOf("Po 2.", "Út 3.", "St 4.", "Čt 5.", "Pá 6.")
private val SLOTS = listOf("8:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00")

/** Per-room accent: container colours + an M3 Expressive shape, so each room is recognisable at a glance. */
@Composable
private fun roomStyle(name: String): Triple<Color, Color, Shape> {
    val cs = MaterialTheme.colorScheme
    return when (ROOMS.indexOfFirst { it.name == name }) {
        0 -> Triple(cs.primaryContainer, cs.onPrimaryContainer, Shapes.cookie9)
        1 -> Triple(cs.secondaryContainer, cs.onSecondaryContainer, Shapes.clover8)
        2 -> Triple(cs.surfaceContainerHighest, cs.onSurface, Shapes.sunny)
        else -> Triple(cs.tertiaryContainer, cs.onTertiaryContainer, Shapes.flower)
    }
}

/* ---------- seznam rezervací ---------- */

@Composable
fun RezervaceScreen(s: AppState) = ScreenScaffold(overlay = {
    Fab("Nová rezervace", Icons.Rounded.Add, Modifier.align(Alignment.BottomEnd).navigationBarsPadding().padding(18.dp)) {
        s.go(Screen.RezervaceNova)
    }
}) {
    AppBar("Rezervace") { s.back() }
    if (s.res.isEmpty()) {
        EmptyState(Icons.Rounded.Event, Shapes.cookie6, "Nemáš žádnou rezervaci", "Posilovna, studovna, prádelna… klepni na Nová rezervace.")
    } else {
        Overline("Nadcházející")
        s.res.forEach { r -> ReservationCard(s, r) }
    }
    Spacer(Modifier.heightIn(min = 80.dp))
}

@Composable
private fun ReservationCard(s: AppState, r: Reservation) {
    val (bg, fg, _) = roomStyle(r.room)
    val parts = r.time.split(" ")
    val sub = ROOMS.firstOrNull { it.name == r.room }?.sub ?: ""
    IskamCard(padding = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                Modifier.size(68.dp).background(bg, RoundedCornerShape(20.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(parts.getOrElse(0) { "" }.uppercase(), color = fg, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(parts.getOrElse(1) { "" }, color = fg, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(r.icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    TitleText(r.room)
                }
                Label(sub)
            }
            RoundIconButton(Icons.Rounded.EventBusy, "Zrušit rezervaci", {
                s.res.removeAll { it.id == r.id }
                s.toast("Rezervace zrušena", Icons.Rounded.EventBusy)
            }, iconSize = 22.dp, tint = MaterialTheme.colorScheme.error)
        }
    }
}

/* ---------- nová rezervace ---------- */

@Composable
private fun StepHeader(n: Int, title: String, done: Boolean, hint: String? = null) {
    val cs = MaterialTheme.colorScheme
    val bg by animateColorAsState(if (done) cs.primary else cs.surfaceContainerHighest, label = "step")
    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(Modifier.size(28.dp).background(bg, CircleShape), contentAlignment = Alignment.Center) {
            if (done) Icon(Icons.Rounded.Check, null, Modifier.size(18.dp), tint = cs.onPrimary)
            else Text("$n", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = cs.onSurfaceVariant)
        }
        TitleText(title)
        if (hint != null) Label("· $hint")
    }
}

@Composable
private fun RoomTile(room: ResRoom, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val (bg, fg, shape) = roomStyle(room.name)
    val container by animateColorAsState(if (selected) cs.secondaryContainer else cs.surfaceContainer, label = "tile")
    val border by animateColorAsState(if (selected) cs.primary else Color.Transparent, label = "border")
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(if (selected) 28.dp else 24.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(2.dp, border),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                ShapeIcon(room.icon, shape, size = 52.dp, iconSize = 24.dp, bg = bg, fg = fg)
                Spacer(Modifier.weight(1f))
                AnimatedVisibility(selected, enter = fadeIn(), exit = fadeOut()) {
                    Box(Modifier.size(24.dp).background(cs.primary, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Check, null, Modifier.size(16.dp), tint = cs.onPrimary)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            TitleText(room.name)
            Label(room.sub)
        }
    }
}

@Composable
fun RezervaceNovaScreen(s: AppState) {
    val d = s.resDraft
    val busy = (d.room ?: "").length + (DAYS.indexOf(d.day) + 1)
    val ready = d.room != null && d.day != null && d.slot != null
    val cs = MaterialTheme.colorScheme
    ScreenScaffold {
        AppBar("Nová rezervace") { s.back() }

        StepHeader(1, "Místnost", d.room != null)
        ROOMS.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { r ->
                    RoomTile(r, d.room == r.name, Modifier.weight(1f)) { s.resDraft = s.resDraft.copy(room = r.name, slot = null) }
                }
            }
        }

        StepHeader(2, "Den", d.day != null, "listopad")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DAYS.forEach { day ->
                val (name, num) = day.split(" ").let { it[0] to it[1] }
                ToggleButton(
                    checked = d.day == day,
                    onCheckedChange = { s.resDraft = s.resDraft.copy(day = day, slot = null) },
                    modifier = Modifier.weight(1f).height(76.dp),
                    shapes = ToggleButtonDefaults.shapesFor(76.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(name.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(num.trimEnd('.'), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        StepHeader(3, "Čas", d.slot != null, if (d.room != null && d.day != null) "obsazené jsou přeškrtnuté" else null)
        if (d.room != null && d.day != null) {
            SLOTS.chunked(4).forEachIndexed { row, chunk ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    chunk.forEachIndexed { k, slot ->
                        val off = ((row * 4 + k) * 7 + busy) % 5 == 0
                        ToggleButton(
                            checked = d.slot == slot,
                            onCheckedChange = { s.resDraft = s.resDraft.copy(slot = slot) },
                            enabled = !off,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shapes = ToggleButtonDefaults.shapesFor(48.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp),
                        ) {
                            Text(
                                slot, fontWeight = FontWeight.SemiBold, maxLines = 1,
                                textDecoration = if (off) TextDecoration.LineThrough else null,
                            )
                        }
                    }
                }
            }
        } else {
            Body("Vyber místnost a den, ukážeme volné časy.")
        }

        Fill()
        AnimatedVisibility(ready, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            val room = ROOMS.firstOrNull { it.name == d.room }
            if (room != null) {
                val (bg, fg, shape) = roomStyle(room.name)
                IskamCard(container = cs.surfaceContainerHigh, padding = PaddingValues(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        ShapeIcon(room.icon, shape, size = 48.dp, iconSize = 22.dp, bg = bg, fg = fg)
                        Column(Modifier.weight(1f)) {
                            TitleText(room.name)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Rounded.Place, null, Modifier.size(14.dp), tint = cs.onSurfaceVariant)
                                Label("${d.day} listopadu · ${d.slot}")
                            }
                        }
                    }
                }
            }
        }
        IskamButton(if (ready) "Rezervovat" else "Vyber místnost, den a čas", icon = if (ready) Icons.Rounded.EventAvailable else null, big = true, wide = true, enabled = ready, loadMs = 800) {
            val cur = s.resDraft
            val ic = ROOMS.first { it.name == cur.room }.icon
            val time = "${cur.day!!.split(" ")[0].lowercase()} ${cur.slot}"
            s.res.add(Reservation("r${System.nanoTime()}", cur.room!!, ic, time))
            s.notifs.add(0, Notif("nr${System.nanoTime()}", ic, "Rezervace: ${cur.room}", "${cur.day} ${cur.slot}", go = Screen.Rezervace, unread = true))
            s.resDraft = ResDraft()
            s.replace(Screen.Rezervace)
            s.toast(if (cur.room == "Obřadní sál") "Rezervováno. Přijď včas." else "Rezervováno", Icons.Rounded.EventAvailable)
        }
    }
}
