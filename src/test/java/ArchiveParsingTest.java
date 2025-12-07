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

    public final ClassLoader cl = ArchiveParsingTest.class.getClassLoader();

    @Test
    @DisplayName("Чтение файлов из архива")
    void parseZipFileFromResourcesTest() throws Exception {
        URL resource = cl.getResource("files.zip");
        Assertions.assertNotNull(resource, "Файл files.zip не найден в resources!");
        File zipFileOnDisk = new File(resource.toURI());
        try (ZipFile zipFile = new ZipFile(zipFileOnDisk)) {

            ZipEntry pdfEntry = zipFile.getEntry("test.pdf");
            try (InputStream stream = zipFile.getInputStream(pdfEntry)) {
                PDF pdf = new PDF(stream);
                assertThat(pdf.text).contains("Text inside PDF");
            }

            ZipEntry csvEntry = zipFile.getEntry("test.csv");
            try (InputStream stream = zipFile.getInputStream(csvEntry);
                 CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                List<String[]> csvContent = reader.readAll();
                assertThat(csvContent).hasSizeGreaterThan(0);
                assertThat(csvContent.get(0)).contains("Header1");
                assertThat(csvContent.get(1)).contains("Value1");
            }

            ZipEntry xlsxEntry = zipFile.getEntry("test.xlsx");
            try (InputStream stream = zipFile.getInputStream(xlsxEntry)) {
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
