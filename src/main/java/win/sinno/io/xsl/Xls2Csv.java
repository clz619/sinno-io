package win.sinno.io.xsl;

import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * xls 2 Csv
 *
 * @author : admin@chenlizhong.cn
 * @version : 1.0
 * @since : 2017-05-27 11:08.
 */
public class Xls2Csv implements HSSFListener {

    private POIFSFileSystem fs;
    private PrintStream output;

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


    /**
     * @param fs
     * @param output
     * @param minColumns the min number of columns to output , or -1 for no minimun
     */
    public Xls2Csv(POIFSFileSystem fs, PrintStream output, int minColumns) {
        this.fs = fs;
        this.output = output;
        this.minColumns = minColumns;
    }

    public Xls2Csv(String filename, int minColumns) throws IOException {
        this(new POIFSFileSystem(new FileInputStream(filename)), System.out, minColumns);
    }

    public void process() throws IOException {
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatTrackingHSSFListener = new FormatTrackingHSSFListener(listener);

        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();

        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatTrackingHSSFListener);
        } else {
            sheetRecordCollectingListener = new EventWorkbookBuilder.SheetRecordCollectingListener(formatTrackingHSSFListener);
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

                    output.println();
                    output.println(boundSheetRecords[sheetIndex].getSheetname() + " [" + (sheetIndex + 1) + "]:");
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
                    thisStr = '"' + HSSFFormulaParser.toFormulaString(hssfWorkbook, frec.getParsedExpression()) + '"';
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
                output.print(",");
            }
            output.print(thisStr);
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
                    output.print(',');
                }
            }

            lastColumnNumber = -1;
            output.println();
        }


    }

}
