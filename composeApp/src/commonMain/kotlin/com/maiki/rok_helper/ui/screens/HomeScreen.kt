package com.maiki.rok_helper.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maiki.rok_helper.network.GovernorData
import com.maiki.rok_helper.network.RokStatsApi
import com.maiki.rok_helper.ui.theme.RoKColors
import com.maiki.rok_helper.util.ThousandsSeparatorTransformation
import com.maiki.rok_helper.util.createSettings
import com.maiki.rok_helper.util.openUrl
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToTool: (String) -> Unit) {
    val settings = remember { createSettings() }
    val json = remember { Json { ignoreUnknownKeys = true } }
    
    var playerId by remember { 
        mutableStateOf(settings.getString("saved_player_id", "")) 
    }
    var isIdSaved by remember { mutableStateOf(playerId.isNotEmpty()) }
    
    var governorData by remember { 
        mutableStateOf<GovernorData?>(
            settings.getString("cached_governor_data", "").let {
                if (it.isNotEmpty()) {
                    try { json.decodeFromString<GovernorData>(it) } catch (e: Exception) { null }
                } else null
            }
        )
    }
    
    var backupGovernorData by remember { mutableStateOf(governorData) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var lastUpdateTime by remember { 
        mutableLongStateOf(settings.getLong("last_update_time_${playerId}", 0L)) 
    }
    
    val scope = rememberCoroutineScope()
    // ВАЖНО: Ключ API пока пустой, мы добавим его позже через переменные окружения
    val api = remember { RokStatsApi.create(apiKey = "") }

    val gold = RoKColors.gold
    val cardBg = RoKColors.card
    val bgColor = RoKColors.bg

    fun fetchGovernorData(id: String, isManualRefresh: Boolean = false) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = api.getGovernor(id.toLong())
                if (response.success && response.data != null) {
                    governorData = response.data
                    isIdSaved = true
                    val currentTime = 0L // TODO: System.currentTimeMillis() alternative
                    settings.putString("saved_player_id", id)
                    settings.putString("cached_governor_data", json.encodeToString(response.data))
                    if (isManualRefresh) {
                        settings.putLong("last_update_time_${id}", currentTime)
                        lastUpdateTime = currentTime
                    }
                } else {
                    errorMessage = "Игрок не найден"
                }
            } catch (e: Exception) {
                errorMessage = "Не удалось загрузить данные (Проверьте API ключ)"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (isIdSaved && playerId.isNotEmpty() && governorData == null) {
            fetchGovernorData(playerId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isIdSaved) {
            Text(text = "ГЛАВНАЯ", color = gold, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = playerId,
                onValueChange = { if (it.length <= 9) playerId = it.filter { char -> char.isDigit() } },
                label = { Text("Введите Player ID (9 цифр)", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = ThousandsSeparatorTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = gold,
                    unfocusedBorderColor = Color.Gray.copy(0.4f),
                    cursorColor = gold,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = gold,
                    unfocusedLabelColor = Color.Gray
                ),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = gold) }
            )

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { if (playerId.isNotEmpty()) fetchGovernorData(playerId) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = gold),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("ПОИСК", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                if (backupGovernorData != null) {
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            governorData = backupGovernorData
                            playerId = backupGovernorData?.playerId?.toString() ?: ""
                            isIdSaved = true
                            errorMessage = null
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        Text("ОТМЕНА")
                    }
                }
            }
        }

        if (isLoading && isIdSaved) CircularProgressIndicator(color = gold, modifier = Modifier.padding(16.dp))

        errorMessage?.let { Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center) }

        governorData?.let { data ->
            Spacer(Modifier.height(24.dp))
            
            // Аватарка (заглушка для веба пока Coil не настроен)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(cardBg)
                    .border(1.dp, gold.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(data.playerName.take(1), color = gold, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            Text(text = if (!data.guildAbbreviation.isNullOrEmpty()) "[${data.guildAbbreviation}] ${data.playerName}" else data.playerName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "ID: ${data.playerId}", color = Color.Gray, fontSize = 14.sp)
                if (!data.guildName.isNullOrEmpty()) {
                    Text(text = "  ·  ", color = Color.Gray.copy(alpha = 0.5f), fontSize = 14.sp)
                    Text(text = data.guildName!!, color = gold.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = "СТАТИСТИКА", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    
                    StatRow(label = "Мощь", value = data.power.toString(), color = gold)
                    HorizontalDivider(color = Color.Gray.copy(0.1f), modifier = Modifier.padding(vertical = 8.dp))
                    StatRow(label = "Очки убийств", value = data.killPoints.toString(), color = Color(0xFFF85149))
                    HorizontalDivider(color = Color.Gray.copy(0.1f), modifier = Modifier.padding(vertical = 8.dp))
                    StatRow(label = "Королевство", value = "#${data.kingdom}", color = Color.White)
                }
            }

            if (isIdSaved) {
                Spacer(Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { backupGovernorData = governorData; isIdSaved = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    border = BorderStroke(1.dp, Color.Gray.copy(0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ИЗМЕНИТЬ ID")
                }
            }
        }

        // БЫСТРЫЙ ДОСТУП
        Spacer(Modifier.height(32.dp))
        Text(text = "БЫСТРЫЙ ДОСТУП", color = gold, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        Spacer(Modifier.height(16.dp))
        
        ToolsData.ALL_TOOLS.take(3).forEach { tool ->
            Card(
                onClick = { onNavigateToTool(tool.route) },
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(0.2f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(32.dp).background(gold.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Text(tool.title.take(1), color = gold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(text = tool.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = tool.description, color = Color.Gray, fontSize = 12.sp)
                    }
                    Text(text = "→", color = Color.Gray, fontSize = 18.sp)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
        Text(text = "СВЯЗЬ И ПОДДЕРЖКА", color = gold, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.Gray.copy(0.2f)), modifier = Modifier.fillMaxWidth()) {
            Column {
                SupportItem(title = "Telegram сообщество", onClick = { openUrl("https://t.me/RoK_Helper") })
                HorizontalDivider(color = Color.Gray.copy(0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                SupportItem(title = "Написать разработчику", onClick = { openUrl("https://t.me/Maiki_Tokyo") })
            }
        }
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun SupportItem(title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Text(text = "→", color = Color.Gray, fontSize = 18.sp)
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = color, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}
