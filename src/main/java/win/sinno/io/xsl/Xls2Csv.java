package win.sinno.io.xsl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import win.sinno.io.csv.CsvWriter;

/**
 * xls 2 Csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 11:08.
 */
public class Xls2Csv implements HSSFListener {

  private POIFSFileSystem fs;
  private final CsvWriter csvWriter;

  private int minColumns;
  private int lastRowNumber;
  private int lastColumnNumber;

  /**
   * should we output the formula , or the value it has
   */
  private boolean outputFormulaValues = true;

  /**
   * for parsing Formulas
   */
  private EventWorkbookBuilder.SheetRecordCollectingListener sheetRecordCollectingListener;
  private HSSFWorkbook hssfWorkbook;

  /**
   * records we pick up as we process
   */
  private SSTRecord sstRecord;
  private FormatTrackingHSSFListener formatTrackingHSSFListener;

  /**
   * which sheet we're on
   *
   * @param record
   */
  private int sheetIndex = -1;
  private BoundSheetRecord[] boundSheetRecords;
  private List<BoundSheetRecord> boundSheetRecordList = new ArrayList<BoundSheetRecord>();

  /**
   * for handling Formulas with String results
   */
  private int nextRow;
  private int nextColumn;
  private boolean outputNextStringRecord;


  public Xls2Csv(String filename, CsvWriter csvWriter, int minColumns)
      throws IOException {
    this(new POIFSFileSystem(new FileInputStream(filename)), csvWriter, minColumns);
  }

  /**
   * @param minColumns the min number of columns to output , or -1 for no minimun
   */
  public Xls2Csv(POIFSFileSystem fs, CsvWriter csvWriter, int minColumns) {
    this.fs = fs;
    this.csvWriter = csvWriter;
    this.minColumns = minColumns;
  }

//  public Xls2Csv(String filename, int minColumns) throws IOException {
//    this(new POIFSFileSystem(new FileInputStream(filename)), System.out, minColumns);
//  }

  public void process() throws IOException {
    MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
    formatTrackingHSSFListener = new FormatTrackingHSSFListener(listener);

    HSSFEventFactory factory = new HSSFEventFactory();
    HSSFRequest request = new HSSFRequest();

    if (outputFormulaValues) {
      request.addListenerForAllRecords(formatTrackingHSSFListener);
    } else {
      sheetRecordCollectingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(
          formatTrackingHSSFListener);
      request.addListenerForAllRecords(sheetRecordCollectingListener);
    }

    factory.processWorkbookEvents(request, fs);
  }

  @Override
  public void processRecord(Record record) {
    int thisRow = -1;
    int thisColumn = -1;
    String thisStr = null;

    switch (record.getSid()) {
      case BoundSheetRecord.sid:
        boundSheetRecordList.add((BoundSheetRecord) record);
        break;

      case BOFRecord.sid:
        BOFRecord br = (BOFRecord) record;

        if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
          if (sheetRecordCollectingListener != null && hssfWorkbook == null) {
            hssfWorkbook = sheetRecordCollectingListener.getStubHSSFWorkbook();
          }

          sheetIndex++;

          if (boundSheetRecords == null) {
            boundSheetRecords = BoundSheetRecord.orderByBofPosition(boundSheetRecordList);
          }

//          output.println();
//          output.println(
//              boundSheetRecords[sheetIndex].getSheetname() + " [" + (sheetIndex + 1) + "]:");
        }

        break;

      case SSTRecord.sid:
        sstRecord = (SSTRecord) record;
        break;

      case BlankRecord.sid:
        BlankRecord brec = (BlankRecord) record;

        thisRow = brec.getRow();
        thisColumn = brec.getColumn();
        thisStr = "";
        break;

      case BoolErrRecord.sid:
        BoolErrRecord berec = (BoolErrRecord) record;

        thisRow = berec.getRow();
        thisColumn = berec.getColumn();
        thisStr = "";
        break;

      case FormulaRecord.sid:
        FormulaRecord frec = (FormulaRecord) record;

        thisRow = frec.getRow();
        thisColumn = frec.getColumn();

        if (outputFormulaValues) {
          if (Double.isNaN(frec.getValue())) {
            outputNextStringRecord = true;
            nextRow = frec.getRow();
            nextColumn = frec.getColumn();
          } else {
            thisStr = formatTrackingHSSFListener.formatNumberDateCell(frec);
          }
        } else {
          thisStr =
              '"' + HSSFFormulaParser.toFormulaString(hssfWorkbook, frec.getParsedExpression())
                  + '"';
        }
        break;

      case StringRecord.sid:
        if (outputNextStringRecord) {
          StringRecord srec = (StringRecord) record;
          thisStr = srec.getString();
          thisRow = nextRow;
          thisColumn = nextColumn;
          outputNextStringRecord = false;
        }
        break;

      case LabelRecord.sid:
        LabelRecord lrec = (LabelRecord) record;

        thisRow = lrec.getRow();
        thisColumn = lrec.getColumn();
        thisStr = '"' + lrec.getValue() + '"';
        break;

      case LabelSSTRecord.sid:
        LabelSSTRecord lsrec = (LabelSSTRecord) record;

        thisRow = lsrec.getRow();
        thisColumn = lsrec.getColumn();
        if (sstRecord == null) {
          thisStr = '"' + "(No SST Record, can't identify string)" + '"';
        } else {
          thisStr = '"' + sstRecord.getString(lsrec.getSSTIndex()).toString() + '"';
        }
        break;

      case NoteRecord.sid:
        NoteRecord nrec = (NoteRecord) record;

        thisRow = nrec.getRow();
        thisColumn = nrec.getColumn();
        thisStr = '"' + "(TODO)" + '"';
        break;

      case NumberRecord.sid:
        NumberRecord numrec = (NumberRecord) record;

        thisRow = numrec.getRow();
        thisColumn = numrec.getColumn();

        // Format
        thisStr = formatTrackingHSSFListener.formatNumberDateCell(numrec);
        break;

      case RKRecord.sid:
        RKRecord rkrec = (RKRecord) record;

        thisRow = rkrec.getRow();
        thisColumn = rkrec.getColumn();
        thisStr = '"' + "(TODO)" + '"';
        break;

      default:
        break;
    }

    //handler new row
    if (thisRow != -1 && thisRow != lastRowNumber) {
      lastColumnNumber = -1;
    }

    if (record instanceof MissingCellDummyRecord) {
      MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
      thisRow = mc.getRow();
      thisColumn = mc.getColumn();
      thisStr = "";
    }

    if (thisStr != null) {
      if (thisColumn > 0) {
//        output.print(",");
        try {
          csvWriter.write(",");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
//      output.print(thisStr);
      try {
        csvWriter.write(thisStr);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if (thisRow > -1) {
      lastRowNumber = thisRow;
    }

    if (thisColumn > -1) {
      lastColumnNumber = thisColumn;
    }

    if (record instanceof LastCellOfRowDummyRecord) {
      if (minColumns > 0) {
        if (lastColumnNumber == -1) {
          lastColumnNumber = 0;
        }
        for (int i = lastColumnNumber; i < minColumns; i++) {
//          output.print(',');
          try {
            csvWriter.write(",");
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      lastColumnNumber = -1;
//      output.println();
      try {
        csvWriter.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


  }

}
