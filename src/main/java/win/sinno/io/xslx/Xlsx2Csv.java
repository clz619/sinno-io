package win.sinno.io.xslx;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import win.sinno.io.csv.CsvWriter;

/**
 * TODO
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 14:05.
 */
public class Xlsx2Csv {

  private class SheetToCSV implements SheetContentsHandler {

    private boolean firstCellOfRow = false;
    private int currentRow = -1;
    private int currentCol = -1;

    private void outputMissingRows(int number) throws IOException {
      for (int i = 0; i < number; i++) {
        for (int j = 0; j < minColumns; j++) {
//                    output.append(',');
          csvWriter.write(",");
        }
//                output.append('\n');
        csvWriter.newLine();
      }
    }

    @Override
    public void startRow(int rowNum) {
      // If there were gaps, output the missing rows
      try {
        outputMissingRows(rowNum - currentRow - 1);
      } catch (IOException e) {
        e.printStackTrace();
      }
      // Prepare for this row
      firstCellOfRow = true;
      currentRow = rowNum;
      currentCol = -1;
    }

    @Override
    public void endRow(int rowNum) {
      // Ensure the minimum number of columns
      for (int i = currentCol; i < minColumns; i++) {
//                output.append(',');
        try {
          csvWriter.write(",");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
//            output.append('\n');
      try {
        csvWriter.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void cell(String cellReference, String formattedValue,
        XSSFComment comment) {
      if (firstCellOfRow) {
        firstCellOfRow = false;
      } else {
//        output.append(',');
        try {
          csvWriter.write(",");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      // gracefully handle missing CellRef here in a similar way as XSSFCell does
      if (cellReference == null) {
        cellReference = new CellAddress(currentRow, currentCol).formatAsString();
      }

      // Did we miss any cells?
      int thisCol = (new CellReference(cellReference)).getCol();
      int missedCols = thisCol - currentCol - 1;
      for (int i = 0; i < missedCols; i++) {
//        output.append(',');
        try {
          csvWriter.write(",");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      currentCol = thisCol;

//      // Number or string?
//      try {
//        //noinspection ResultOfMethodCallIgnored
//        Double.parseDouble(formattedValue);
//        output.append(formattedValue);
//      } catch (NumberFormatException e) {
//        output.append('"');
//        output.append(formattedValue);
//        output.append('"');
//      }
      try {
        csvWriter.write(formattedValue);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
      // Skip, no headers or footers in CSV
    }
  }

  ///////////////////////////////////////

  private final OPCPackage xlsxPackage;

  /**
   * Number of columns to read starting with leftmost
   */
  private final int minColumns;

  /**
   * Destination for data
   */
//    private final PrintStream output;

  private final CsvWriter csvWriter;

  /**
   * Creates a new XLSX -> CSV converter
   *
   * @param pkg The XLSX package to process
   * @param csvWriter The PrintStream to output the CSV to
   * @param minColumns The minimum number of columns to output, or -1 for no minimum
   */
  public Xlsx2Csv(OPCPackage pkg, CsvWriter csvWriter, int minColumns) {
    this.xlsxPackage = pkg;
    this.csvWriter = csvWriter;
    this.minColumns = minColumns;
  }

  /**
   * Parses and shows the content of one sheet using the specified styles and shared-strings
   * tables.
   *
   * @param styles The table of styles that may be referenced by cells in the sheet
   * @param strings The table of strings that may be referenced by cells in the sheet
   * @param sheetInputStream The stream to read the sheet-data from.
   * @throws java.io.IOException An IO exception from the parser, possibly from a byte stream or
   * character stream supplied by the application.
   * @throws SAXException if parsing the XML data fails.
   */
  public void processSheet(
      StylesTable styles,
      ReadOnlySharedStringsTable strings,
      SheetContentsHandler sheetHandler,
      InputStream sheetInputStream) throws IOException, SAXException {
    DataFormatter formatter = new DataFormatter();
    InputSource sheetSource = new InputSource(sheetInputStream);
    try {
      XMLReader sheetParser = SAXHelper.newXMLReader();
      ContentHandler handler = new XSSFSheetXMLHandler(
          styles, null, strings, sheetHandler, formatter, false);
      sheetParser.setContentHandler(handler);
      sheetParser.parse(sheetSource);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
    }
  }

  /**
   * Initiates the processing of the XLS workbook file to CSV.
   *
   * @throws IOException If reading the data from the package fails.
   * @throws SAXException if parsing the XML data fails.
   */
  public void process() throws IOException, OpenXML4JException, SAXException {
    ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
    XSSFReader xssfReader = new XSSFReader(this.xlsxPackage);
    StylesTable styles = xssfReader.getStylesTable();
    XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
    int index = 0;
    while (iter.hasNext()) {
      InputStream stream = iter.next();
      String sheetName = iter.getSheetName();
      processSheet(styles, strings, new SheetToCSV(), stream);
      stream.close();
      ++index;
    }
  }
}
