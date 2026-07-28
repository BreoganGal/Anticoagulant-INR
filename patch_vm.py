import re

with open("app/src/main/java/com/example/ui/MainViewModel.kt", "r") as f:
    content = f.read()

# Fix 1: change todayDateStr to dynamic
content = content.replace("val todayDateStr = LocalDate.now().toString()", 
                          "val todayDateStr: String\n        get() = LocalDate.now().toString()")

# Fix 2: map recent14Days and recent7Logs from allDoses
content = content.replace("import kotlinx.coroutines.flow.combine", "import kotlinx.coroutines.flow.combine\nimport kotlinx.coroutines.flow.map")

recent14_pattern = r'recent14Days = repository\.getRecent14Days\(todayDateStr\)\.stateIn\([\s\S]*?initialValue = emptyList\(\)\n        \)'
recent14_replacement = """recent14Days = allDoses.map { list ->
            list.filter { it.date <= todayDateStr }.sortedByDescending { it.date }.take(14)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )"""

content = re.sub(recent14_pattern, recent14_replacement, content)

recent7_pattern = r'recent7Logs = repository\.getRecent7Logs\(\)\.stateIn\([\s\S]*?initialValue = emptyList\(\)\n        \)'
recent7_replacement = """recent7Logs = allDoses.map { list ->
            list.filter { it.isTaken || it.punctuality == "MISSED" }.sortedByDescending { it.date }.take(7)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )"""

content = re.sub(recent7_pattern, recent7_replacement, content)

with open("app/src/main/java/com/example/ui/MainViewModel.kt", "w") as f:
    f.write(content)

