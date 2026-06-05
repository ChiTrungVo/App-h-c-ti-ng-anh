package com.example.mobile_project.feature.vocabulary.data

import com.example.mobile_project.data.model.VocabularyWord
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

data class VocabularyFileWord(
    val word: String,
    val meaning: String,
    val pronunciation: String = "",
    val definition: String = "",
    val example: String = "",
    val collocations: List<String> = emptyList(),
    val relatedWords: List<String> = emptyList(),
    val note: String = ""
)

data class VocabularyImportParseResult(
    val words: List<VocabularyFileWord>,
    val skippedRows: Int
)

object VocabularyImportExportCodec {
    val headers = listOf(
        "word",
        "meaning",
        "pronunciation",
        "definition",
        "example",
        "collocations",
        "relatedWords",
        "note"
    )

    fun parseCsv(content: String): VocabularyImportParseResult {
        val rows = parseCsvRows(content.removePrefix("\uFEFF"))
            .filter { row -> row.any { it.isNotBlank() } }

        if (rows.isEmpty()) return VocabularyImportParseResult(emptyList(), 0)

        val headerMap = rows.first().mapIndexed { index, value ->
            normalizeHeader(value) to index
        }.toMap()

        var skippedRows = 0
        val words = rows.drop(1).mapNotNull { row ->
            val parsed = row.toVocabularyFileWord(headerMap)
            if (parsed == null) skippedRows += 1
            parsed
        }

        return VocabularyImportParseResult(words, skippedRows)
    }

    fun toCsv(words: List<VocabularyWord>): String {
        val lines = mutableListOf(headers.joinToString(","))
        words.forEach { word ->
            lines.add(
                listOf(
                    word.word,
                    word.meaning,
                    word.pronunciation,
                    word.definition,
                    word.example,
                    word.collocations.joinToString("; "),
                    word.relatedWords.joinToString("; "),
                    word.note
                ).joinToString(",") { it.escapeCsvCell() }
            )
        }
        return lines.joinToString("\n")
    }

    fun parseXlsx(bytes: ByteArray): VocabularyImportParseResult {
        val entries = unzip(bytes)
        val sheetPath = findFirstWorksheetPath(entries) ?: "xl/worksheets/sheet1.xml"
        val sheetBytes = entries[sheetPath] ?: return VocabularyImportParseResult(emptyList(), 0)
        val sharedStrings = parseSharedStrings(entries["xl/sharedStrings.xml"])
        val rows = parseSheetRows(sheetBytes, sharedStrings)
            .filter { row -> row.any { it.isNotBlank() } }

        if (rows.isEmpty()) return VocabularyImportParseResult(emptyList(), 0)

        val headerMap = rows.first().mapIndexed { index, value ->
            normalizeHeader(value) to index
        }.toMap()

        var skippedRows = 0
        val words = rows.drop(1).mapNotNull { row ->
            val parsed = row.toVocabularyFileWord(headerMap)
            if (parsed == null) skippedRows += 1
            parsed
        }

        return VocabularyImportParseResult(words, skippedRows)
    }

    fun toXlsx(words: List<VocabularyWord>): ByteArray {
        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            zip.putXml("[Content_Types].xml", contentTypesXml())
            zip.putXml("_rels/.rels", rootRelsXml())
            zip.putXml("docProps/core.xml", coreXml())
            zip.putXml("docProps/app.xml", appXml())
            zip.putXml("xl/workbook.xml", workbookXml())
            zip.putXml("xl/_rels/workbook.xml.rels", workbookRelsXml())
            zip.putXml("xl/worksheets/sheet1.xml", sheetXml(words))
        }
        return output.toByteArray()
    }

    private fun List<String>.toVocabularyFileWord(headerMap: Map<String, Int>): VocabularyFileWord? {
        fun value(name: String): String {
            val index = headerMap[name] ?: return ""
            return getOrNull(index)?.trim().orEmpty()
        }

        val word = value("word")
        val meaning = value("meaning")
        if (word.isBlank() || meaning.isBlank()) return null

        return VocabularyFileWord(
            word = word,
            meaning = meaning,
            pronunciation = value("pronunciation"),
            definition = value("definition"),
            example = value("example"),
            collocations = splitList(value("collocations")),
            relatedWords = splitList(value("relatedWords")),
            note = value("note")
        )
    }

    private fun parseCsvRows(content: String): List<List<String>> {
        val rows = mutableListOf<List<String>>()
        val row = mutableListOf<String>()
        val cell = StringBuilder()
        var index = 0
        var inQuotes = false

        while (index < content.length) {
            val char = content[index]
            when {
                char == '"' && inQuotes && index + 1 < content.length && content[index + 1] == '"' -> {
                    cell.append('"')
                    index += 1
                }

                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    row.add(cell.toString())
                    cell.clear()
                }

                (char == '\n' || char == '\r') && !inQuotes -> {
                    row.add(cell.toString())
                    cell.clear()
                    rows.add(row.toList())
                    row.clear()
                    if (char == '\r' && index + 1 < content.length && content[index + 1] == '\n') {
                        index += 1
                    }
                }

                else -> cell.append(char)
            }
            index += 1
        }

        if (cell.isNotEmpty() || row.isNotEmpty()) {
            row.add(cell.toString())
            rows.add(row.toList())
        }

        return rows
    }

    private fun unzip(bytes: ByteArray): Map<String, ByteArray> {
        val entries = mutableMapOf<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    entries[entry.name] = zip.readBytes()
                }
                entry = zip.nextEntry
            }
        }
        return entries
    }

    private fun findFirstWorksheetPath(entries: Map<String, ByteArray>): String? {
        val workbookBytes = entries["xl/workbook.xml"] ?: return null
        val relsBytes = entries["xl/_rels/workbook.xml.rels"] ?: return null
        val workbook = xmlDocument(workbookBytes)
        val firstSheet = workbook.documentElement.elementsByLocalName("sheet").firstOrNull() ?: return null
        val relationshipId = firstSheet.getAttributeNS(
            "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
            "id"
        ).ifBlank { firstSheet.getAttribute("r:id") }
        if (relationshipId.isBlank()) return null

        val rels = xmlDocument(relsBytes)
        val target = rels.documentElement.elementsByLocalName("Relationship")
            .firstOrNull { it.getAttribute("Id") == relationshipId }
            ?.getAttribute("Target")
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return when {
            target.startsWith("/") -> target.removePrefix("/")
            target.startsWith("xl/") -> target
            else -> "xl/$target"
        }
    }

    private fun parseSharedStrings(bytes: ByteArray?): List<String> {
        if (bytes == null) return emptyList()
        val document = xmlDocument(bytes)
        return document.documentElement
            .elementsByLocalName("si")
            .map { si ->
                si.elementsByLocalName("t").joinToString("") { it.textContent.orEmpty() }
            }
    }

    private fun parseSheetRows(sheetBytes: ByteArray, sharedStrings: List<String>): List<List<String>> {
        val document = xmlDocument(sheetBytes)
        return document.documentElement.elementsByLocalName("row").map { rowElement ->
            val cells = mutableMapOf<Int, String>()
            var fallbackIndex = 0
            rowElement.childElements("c").forEach { cell ->
                val cellIndex = columnIndexFromReference(cell.getAttribute("r")) ?: fallbackIndex
                cells[cellIndex] = readCellValue(cell, sharedStrings)
                fallbackIndex = cellIndex + 1
            }
            val maxIndex = cells.keys.maxOrNull() ?: -1
            (0..maxIndex).map { cells[it].orEmpty() }
        }
    }

    private fun readCellValue(cell: Element, sharedStrings: List<String>): String {
        return when (cell.getAttribute("t")) {
            "s" -> {
                val index = cell.firstChildText("v").toIntOrNull()
                index?.let { sharedStrings.getOrNull(it) }.orEmpty()
            }

            "inlineStr" -> cell.elementsByLocalName("t").joinToString("") { it.textContent.orEmpty() }
            else -> cell.firstChildText("v")
        }
    }

    private fun xmlDocument(bytes: ByteArray) =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        }.newDocumentBuilder().parse(ByteArrayInputStream(bytes))

    private fun Element.elementsByLocalName(localName: String): List<Element> {
        val result = mutableListOf<Element>()
        val nodes = getElementsByTagNameNS("*", localName)
        for (i in 0 until nodes.length) {
            (nodes.item(i) as? Element)?.let(result::add)
        }
        return result
    }

    private fun Element.childElements(localName: String): List<Element> {
        val result = mutableListOf<Element>()
        val nodes = childNodes
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as? Element ?: continue
            if (element.localName == localName || element.nodeName == localName) {
                result.add(element)
            }
        }
        return result
    }

    private fun Element.firstChildText(localName: String): String {
        return childElements(localName).firstOrNull()?.textContent.orEmpty()
    }

    private fun columnIndexFromReference(reference: String): Int? {
        val letters = reference.takeWhile { it.isLetter() }
        if (letters.isBlank()) return null
        return letters.uppercase(Locale.US).fold(0) { acc, char ->
            acc * 26 + (char - 'A' + 1)
        } - 1
    }

    private fun splitList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        val separator = if (value.contains(";")) ";" else ","
        return value.split(separator)
            .map { it.trim() }
            .filter { it.isNotBlank() }
    }

    private fun normalizeHeader(value: String): String = value.trim()
        .lowercase(Locale.US)
        .replace(" ", "")
        .replace("_", "")
        .let { normalized ->
            when (normalized) {
                "term", "vocabulary", "tu", "tuvung" -> "word"
                "translation", "nghia" -> "meaning"
                "pronounce", "phienam" -> "pronunciation"
                "define", "dinhnghia" -> "definition"
                "vidu" -> "example"
                "collocation", "cumtu" -> "collocations"
                "relatedword", "relatedwords", "tulienquan" -> "relatedWords"
                "ghichu" -> "note"
                else -> normalized
            }
        }

    private fun String.escapeCsvCell(): String {
        val needsQuote = any { it == ',' || it == '"' || it == '\n' || it == '\r' }
        val escaped = replace("\"", "\"\"")
        return if (needsQuote) "\"$escaped\"" else escaped
    }

    private fun ZipOutputStream.putXml(name: String, xml: String) {
        putNextEntry(ZipEntry(name))
        write(xml.toByteArray(StandardCharsets.UTF_8))
        closeEntry()
    }

    private fun sheetXml(words: List<VocabularyWord>): String {
        val rows = buildList {
            add(headers)
            words.forEach { word ->
                add(
                    listOf(
                        word.word,
                        word.meaning,
                        word.pronunciation,
                        word.definition,
                        word.example,
                        word.collocations.joinToString("; "),
                        word.note
                    )
                )
            }
        }

        val rowXml = rows.mapIndexed { rowIndex, values ->
            val rowNumber = rowIndex + 1
            val cells = values.mapIndexed { cellIndex, value ->
                val reference = "${columnName(cellIndex)}$rowNumber"
                """<c r="$reference" t="inlineStr"><is><t>${value.escapeXml()}</t></is></c>"""
            }.joinToString("")
            """<row r="$rowNumber">$cells</row>"""
        }.joinToString("")

        return """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <sheetData>$rowXml</sheetData>
</worksheet>"""
    }

    private fun columnName(index: Int): String {
        var value = index + 1
        val name = StringBuilder()
        while (value > 0) {
            val remainder = (value - 1) % 26
            name.insert(0, ('A'.code + remainder).toChar())
            value = (value - 1) / 26
        }
        return name.toString()
    }

    private fun String.escapeXml(): String = replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&apos;")

    private fun contentTypesXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/docProps/core.xml" ContentType="application/vnd.openxmlformats-package.core-properties+xml"/>
  <Override PartName="/docProps/app.xml" ContentType="application/vnd.openxmlformats-officedocument.extended-properties+xml"/>
</Types>"""

    private fun rootRelsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties" Target="docProps/core.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties" Target="docProps/app.xml"/>
</Relationships>"""

    private fun workbookXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets><sheet name="Vocabulary" sheetId="1" r:id="rId1"/></sheets>
</workbook>"""

    private fun workbookRelsXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
</Relationships>"""

    private fun coreXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<cp:coreProperties xmlns:cp="http://schemas.openxmlformats.org/package/2006/metadata/core-properties" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <dc:creator>MinLish</dc:creator>
  <cp:lastModifiedBy>MinLish</cp:lastModifiedBy>
  <dcterms:created xsi:type="dcterms:W3CDTF">${nowIso()}</dcterms:created>
  <dcterms:modified xsi:type="dcterms:W3CDTF">${nowIso()}</dcterms:modified>
</cp:coreProperties>"""

    private fun appXml() = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Properties xmlns="http://schemas.openxmlformats.org/officeDocument/2006/extended-properties" xmlns:vt="http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes">
  <Application>MinLish</Application>
</Properties>"""

    private fun nowIso(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(Date())
}
