import com.codeborne.pdftest.PDF;
import com.opencsv.CSVReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveParsingTest {

    private final ClassLoader cl = ArchiveParsingTest.class.getClassLoader();
    private static final String ZIP_NAME = "files.zip";

    private File getZipFileFromResources() throws Exception {
        URL resource = cl.getResource(ZIP_NAME);
        Assertions.assertNotNull(resource, "Файл " + ZIP_NAME + " не найден в resources!");
        return new File(resource.toURI());
    }

    @Test
    @DisplayName("Проверка контента PDF файла внутри архива")
    void parsePdfFromZipTest() throws Exception {
        try (ZipFile zipFile = new ZipFile(getZipFileFromResources())) {
            String fileName = "test.pdf";
            ZipEntry entry = zipFile.getEntry(fileName);

            Assertions.assertNotNull(entry, "Файл " + fileName + " не найден в архиве " + ZIP_NAME);

            try (InputStream stream = zipFile.getInputStream(entry)) {
                PDF pdf = new PDF(stream);
                assertThat(pdf.text).contains("Text inside PDF");
            }
        }
    }

    @Test
    @DisplayName("Проверка контента CSV файла внутри архива")
    void parseCsvFromZipTest() throws Exception {
        try (ZipFile zipFile = new ZipFile(getZipFileFromResources())) {
            String fileName = "test.csv";
            ZipEntry entry = zipFile.getEntry(fileName);

            Assertions.assertNotNull(entry, "Файл " + fileName + " не найден в архиве " + ZIP_NAME);

            try (InputStream stream = zipFile.getInputStream(entry);
                 CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                List<String[]> csvContent = reader.readAll();
                assertThat(csvContent).hasSizeGreaterThan(0);
                assertThat(csvContent.get(0)).contains("Header1");
                assertThat(csvContent.get(1)).contains("Value1");
            }
        }
    }

    @Test
    @DisplayName("Проверка контента XLSX файла внутри архива")
    void parseXlsxFromZipTest() throws Exception {
        try (ZipFile zipFile = new ZipFile(getZipFileFromResources())) {
            String fileName = "test.xlsx";
            ZipEntry entry = zipFile.getEntry(fileName);

            Assertions.assertNotNull(entry, "Файл " + fileName + " не найден в архиве " + ZIP_NAME);

            try (InputStream stream = zipFile.getInputStream(entry)) {
                XSSFWorkbook workbook = new XSSFWorkbook(stream);
                String cellValue = workbook.getSheetAt(0)
                        .getRow(1)
                        .getCell(0)
                        .getStringCellValue();

                assertThat(cellValue).isEqualTo("ExcelValue");
            }
        }
    }
}