package com.gy.listener;

import com.gy.listener.db.DataConverter;
import com.gy.listener.myLists.models.CheckedRecord;
import com.gy.listener.myLists.models.ListType;
import com.gy.listener.myLists.models.Record;
import com.gy.listener.myLists.models.RecordsList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String RECORD_SEPARATOR = "\\x7D\\x2C\\x7B"; // },{
    private static final int PRE_POST_FIX_LEN = 2; // [{ or }]

    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);

        String str = "[{text: value, isChecked: true},{text: val, isChecked: true},{text: val, isChecked: true}]";
        String expectedStr = "text: value, isChecked: true},{text: val, isChecked: true},{text: val, isChecked: true";
        assertEquals(expectedStr, str.substring(PRE_POST_FIX_LEN, str.length() - PRE_POST_FIX_LEN));

        String[] expectedSplit = {
                "text: value, isChecked: true", "text: val, isChecked: true", "text: val, isChecked: true"
        };

        assertArrayEquals(expectedSplit, expectedStr.split(RECORD_SEPARATOR));

        DataConverter converter = new DataConverter();
        List<Record> records = new ArrayList<>();
        records.add(new CheckedRecord("val1", "path", true));
        records.add(new CheckedRecord("val2", "path2", false));

        String converted = converter.fromRecordsList(records);

        List<Record> postConversion = converter.toRecordsList(converted);
    }

    @Test
    public void playground() {
//        assertTrue(Boolean.parseBoolean("true"));
//        assertFalse(Boolean.parseBoolean((String) "false"));
//
//        List<Record> records = new ArrayList<>();
//        records.add(new CheckedRecord("1", false));
//        records.add(new CheckedRecord("2", false));
//
//        RecordsList recordsList = new RecordsList("id", "name", null, ListType.GROCERIES, records);
//        RecordsList copyRecordsList = new RecordsList(recordsList.getId(),
//                recordsList.getName(),
//                null,
//                recordsList.getListType(),
//                new ArrayList<>(recordsList.getRecords()));
//
//        recordsList.setName("new name");
//        recordsList.setListType(ListType.TODO);
//        records.add(new CheckedRecord("3", false));
//        ((CheckedRecord) recordsList.getRecords().get(0)).setIsChecked(true);
//
//        assertNotEquals(recordsList.getName(), copyRecordsList.getName());
//        assertNotEquals(recordsList.getListType(), copyRecordsList.getListType());
//        assertNotEquals(recordsList.getRecords().size(), copyRecordsList.getRecords().size());
//        assertTrue(((CheckedRecord) recordsList.getRecords().get(0)).getIsChecked());
//        assertFalse(((CheckedRecord) copyRecordsList.getRecords().get(0)).getIsChecked());
//
//        System.out.println(copyRecordsList.getListType());
//        System.out.println(copyRecordsList.getName());
//        System.out.println(copyRecordsList.getRecords().size());

        CheckedRecord checkedRecord = new CheckedRecord("as", false);
        Record record = checkedRecord;

        Record copy = record.getClone();

        assertFalse(((CheckedRecord) copy).getIsChecked());
    }
}