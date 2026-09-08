package com.maiki.rok_helper.ui.screens

data class ToolInfo(
    val id: String,
    val title: String,
    val description: String,
    val iconPath: String,
    val route: String
)

object ToolsData {
    val ALL_TOOLS = listOf(
        ToolInfo("lyceum", "Лицей знаний (Ответы)", "Вопросы и ответы лицея знаний", "lyceum_icon.webp", "lyceum"),
        ToolInfo("build", "Калькулятор зданий", "Расчет строительства зданий", "building_icon.webp", "build"),
        ToolInfo("train", "Калькулятор обучения", "Расчет обучения и улучшения войск", "training_icon.webp", "train"),
        ToolInfo("heal", "Калькулятор исцеления", "Расчет исцеления воинов", "healing_icon.webp", "heal"),
        ToolInfo("vip", "Калькулятор VIP", "Расчет цены vip уровней", "VIP_icon.webp", "vip"),
        ToolInfo("tech", "Калькулятор технологий", "Расчет изучения технологий", "technologies_icon.webp", "tech"),
        ToolInfo("gear", "Калькулятор снаряжения", "Расчет материалов и снаряжения", "equipment_icon.webp", "gear")
    )
}
