package com.cleartrip.sortorder.main;

import java.util.List;
import java.util.Map;

import com.cleartrip.sortorder.data.CitySortData;
import com.cleartrip.sortorder.data.HotelSortData;
import com.cleartrip.sortorder.util.SortOrderUtil;

/**
 * Generates hotel sort order based on PRM-14603
 * 
 * @author Sundaram
 *
 */
public class AutomatedSortOrderController {

    public static void main(String args[]) throws Exception {

        // Get MIS File location
        // String[] fileLocArr = SortOrderUtil.getMISFileLocations();
        String[] fileLocArr = new String[2];
        fileLocArr[0] = "/home/sundaramtiwari/Documents/Work/Automated_Sort_Order/HOTEL_MIS_BOOK_FOR_MONTH_UPTO__31-01-2016.csv";
        fileLocArr[1] = "/home/sundaramtiwari/Documents/Work/Automated_Sort_Order/HOTEL_MIS_BOOK_FOR_MONTH_UPTO__31-12-2015.csv";
        for (String fileLoc : fileLocArr) {
            System.out.println(fileLoc);
        }

        if (fileLocArr != null && fileLocArr.length > 0) {
            // Map of cityId and list of corresponding hotel sort data
            Map<String, Map<String, HotelSortData>> cityHotelsDataMap = SortOrderUtil.readCsv(fileLocArr);

            /** Loop through cityHotelsDataMap and calculate cityAvg details */
            Map<String, CitySortData> cityDataMap = SortOrderUtil.getCityDataMap(cityHotelsDataMap);

            /** Create 4 quadrants for each city and store hotelId in them */
            // Map of cityId and List of quadrants. Each quadrant will hold a list of hotels
            Map<String, List<List<HotelSortData>>> cityQuadrantMap = SortOrderUtil.getCityQuadrantMap(cityHotelsDataMap, cityDataMap);

            // Map of city and List of groups. Each group will hold a list of hotels
            Map<String, List<List<HotelSortData>>> cityGroupMap = SortOrderUtil.getCityGroupMap(cityHotelsDataMap, cityQuadrantMap);

            SortOrderUtil.generateCSVReport(cityGroupMap, cityDataMap);
        }
    }
}
