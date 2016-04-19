package com.cleartrip.sortorder.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cleartrip.sortorder.data.CitySortData;
import com.cleartrip.sortorder.data.HotelSortData;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class SortOrderUtil {

    private static final int DATE = 19;

    private static final Log logger = LogFactory.getLog(SortOrderUtil.class);

    private static final int NUMER_OF_DAYS = 21;
    private static final int NUMBER_OF_CITIES = 700;
    private static final String CSV = ".csv";
    private static final String SLASH = "/";
    private static final String MIS_FILE_LOC = "/usr/local/storage/backup/mis_reports/";
    private static final String MIS_FILE_NAME = "HOTEL_MIS_BOOK_FOR_MONTH_UPTO__";
    private static final String OUTPUT_FILE = "/home/sundaramtiwari/Documents/Work/Automated_Sort_Order/"+ DATE +"Jan/NewAutoSortOrder_";
    private static final DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private static final DateFormat outformatter = new SimpleDateFormat("ddMMMyyyy");
    private static final DateFormat fileFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private static final DateFormat folderFormatter = new SimpleDateFormat("dd-MMM-yyyy");

    private static Set<String> citySet = new HashSet<String>(5);

    static {
        citySet.add("bangalore");
        /*citySet.add("new delhi");
        citySet.add("mumbai");
        citySet.add("mysore");
        citySet.add("jaipur");*/
    }

    public static Map<String, Map<String, HotelSortData>> readCsv(String[] fileLocArr) throws Exception {
        Map<String, Map<String, HotelSortData>> cityHotelsMap = new HashMap<String, Map<String, HotelSortData>>();

        Calendar endCal = getEndCal();
        Date endDate = endCal.getTime();

        Date startDate = getStartDate(endCal);

        System.out.println("Start Date (excluded): " + startDate);
        System.out.println("End Date (included): " + endDate);
        // long diff = endDate.getTime() - startDate.getTime();
        // System.out.println(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));

        CsvReader reader = null;
        for (String fileLocation : fileLocArr) {
            try {
                reader = new CsvReader(fileLocation);
                reader.readRecord();

                int hotelIdVal = -1, cityVal = -1, rnsVal = -1, markUpVal = -1, cbVal = -1, disVal = -1, totWalletCbVal = -1, disSupBFVal = -1, disSupTaxVal = -1, disSupMarkupVal = -1, bookingDateVal = -1, 
                        sourceTypeVal = -1, gateWayFeeVal = -1, hotelBaseFareVal = -1, hotelOtherChargesVal = -1, supplierTaxVal = -1, costBaseFareVal = -1, costHotelOtherChargesVal = -1, costSupplierTaxVal = -1,
                        serviceFeeVal = -1, serviceFeeTaxVal = -1;

                String[] values = reader.getValues();
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        String columnName = values[i];
                        if (columnName.equals("hotel_full_city")) {
                            cityVal = i;
                        } else if (columnName.contains("No of Room Nights")) {
                            rnsVal = i;
                        } else if (columnName.equals("markup")) {
                            markUpVal = i;
                        } else if (columnName.equals("discount")) {
                            disVal = i;
                        } else if (columnName.equals("cashback")) {
                            cbVal = i;
                        } else if (columnName.equals("hotel_id")) {
                            hotelIdVal = i;
                        } else if (columnName.equals("discount_sup_basefare")) {
                            disSupBFVal = i;
                        } else if (columnName.equals("discount_sup_tax")) {
                            disSupTaxVal = i;
                        } else if (columnName.equals("discount_sup_markup")) {
                            disSupMarkupVal = i;
                        } else if (columnName.equals("total_wallet_cashback")) {
                            totWalletCbVal = i;
                        } else if (columnName.equals("booking_date")) {
                            bookingDateVal = i;
                        } else if (columnName.equals("source_type")) {
                            sourceTypeVal = i;
                        } else if (columnName.equals("gateway_fee")) {
                            gateWayFeeVal = i;
                        } else if (columnName.equals("Hotel base_fare")) {
                            hotelBaseFareVal = i;
                        } else if (columnName.equals("hotel_other charges")) {
                            hotelOtherChargesVal = i;
                        } else if (columnName.equals("supplier_tax")) {
                            supplierTaxVal = i;
                        } else if (columnName.equals("cost_base_fare")) {
                            costBaseFareVal = i;
                        } else if (columnName.equals("cost_hotel_other_charges")) {
                            costHotelOtherChargesVal = i;
                        } else if (columnName.equals("cost_supplier_tax")) {
                            costSupplierTaxVal = i;
                        } else if (columnName.equals("service_fee")) {
                            serviceFeeVal = i;
                        } else if (columnName.equals("service_fee_stax")) {
                            serviceFeeTaxVal = i;
                        }
                    }
                }

                while (reader.readRecord()) {
                    String hotelId = null, city = null;
                    int rns = 0;
                    double discount = 0, markUp = 0;
                    Date bookingDate = null;
                    String sourceType = null;
                    try {
                        if (sourceTypeVal != -1 && StringUtils.isNotBlank(reader.get(sourceTypeVal))) {
                            sourceType = reader.get(sourceTypeVal);
                        }
                        if (bookingDateVal != -1 && StringUtils.isNotBlank(reader.get(bookingDateVal))) {
                            bookingDate = formatter.parse(reader.get(bookingDateVal));
                            if (bookingDate.compareTo(startDate) < 0 || bookingDate.compareTo(endDate) > 0) {
                                continue;
                            }
                        }
                        String cityStr = reader.get(cityVal);
                        if (StringUtils.isNotBlank(cityStr)) {
                            city = cityStr.split(", ")[0].toLowerCase();
                        }
                        if (hotelIdVal != -1 && StringUtils.isNotBlank(reader.get(hotelIdVal))) {
                            hotelId = reader.get(hotelIdVal);
                        }
                        if (rnsVal != -1 && StringUtils.isNotBlank(reader.get(rnsVal))) {
                            rns = Integer.parseInt(reader.get(rnsVal));
                        }
                        if (disVal != -1 && StringUtils.isNotBlank(reader.get(disVal))) {
                            discount += Math.round((Double.parseDouble(reader.get(disVal))));
                        }
                        if (cbVal != -1 && StringUtils.isNotBlank(reader.get(cbVal))) {
                            discount += Math.round(Double.parseDouble(reader.get(cbVal)));
                        }
                        if (disSupBFVal != -1 && StringUtils.isNotBlank(reader.get(disSupBFVal))) {
                            discount += Math.round(Double.parseDouble(reader.get(disSupBFVal)));
                        }
                        if (disSupTaxVal != -1 && StringUtils.isNotBlank(reader.get(disSupTaxVal))) {
                            discount += Math.round(Double.parseDouble(reader.get(disSupTaxVal)));
                        }
                        if (disSupMarkupVal != -1 && StringUtils.isNotBlank(reader.get(disSupMarkupVal))) {
                            discount += Math.round(Double.parseDouble(reader.get(disSupMarkupVal)));
                        }
                        if (totWalletCbVal != -1 && StringUtils.isNotBlank(reader.get(totWalletCbVal))) {
                            discount += Math.round(Double.parseDouble(reader.get(totWalletCbVal)));
                        }
                        if (markUpVal != -1 && StringUtils.isNotBlank(reader.get(markUpVal))) {
                            markUp += Math.round(Double.parseDouble(reader.get(markUpVal)));
                        }
                        if (gateWayFeeVal != -1 && StringUtils.isNotBlank(reader.get(gateWayFeeVal))) {
                            markUp += Math.round(Double.parseDouble(reader.get(gateWayFeeVal)));
                        }
                        if (hotelBaseFareVal != -1 && StringUtils.isNotBlank(reader.get(hotelBaseFareVal))) {
                            markUp += Math.round(Double.parseDouble(reader.get(hotelBaseFareVal)));
                        }
                        if (hotelOtherChargesVal != -1 && StringUtils.isNotBlank(reader.get(hotelOtherChargesVal))) {
                            markUp += Math.round(Double.parseDouble(reader.get(hotelOtherChargesVal)));
                        }
                        if (supplierTaxVal != -1 && StringUtils.isNotBlank(reader.get(supplierTaxVal))) {
                            markUp += Math.round(Double.parseDouble(reader.get(supplierTaxVal)));
                        }
                        if (costBaseFareVal != -1 && StringUtils.isNotBlank(reader.get(costBaseFareVal))) {
                            markUp -= Math.round(Double.parseDouble(reader.get(costBaseFareVal)));
                        }
                        if (costHotelOtherChargesVal != -1 && StringUtils.isNotBlank(reader.get(costHotelOtherChargesVal))) {
                            markUp -= Math.round(Double.parseDouble(reader.get(costHotelOtherChargesVal)));
                        }
                        if (costSupplierTaxVal != -1 && StringUtils.isNotBlank(reader.get(costSupplierTaxVal))) {
                            markUp -= Math.round(Double.parseDouble(reader.get(costSupplierTaxVal)));
                        }
                        if (serviceFeeVal != -1 && StringUtils.isNotBlank(reader.get(serviceFeeVal))) {
                            markUp -= Math.round(Double.parseDouble(reader.get(serviceFeeVal)));
                        }
                        if (serviceFeeTaxVal != -1 && StringUtils.isNotBlank(reader.get(serviceFeeTaxVal))) {
                            markUp -= Math.round(Double.parseDouble(reader.get(serviceFeeTaxVal)));
                        }
                    } catch (Exception e) {
                        logger.error("Error parsing MIS record from CSV", e);
                        System.err.println("Error parsing MIS record from CSV" + e);
                    }

                    if (sourceType == null || !sourceType.equalsIgnoreCase("ACCOUNT")) {
                        continue;
                    }
                    if (cityHotelsMap.containsKey(city)) {
                        // If cityHotelsMap contains this cityId, get the hotelsMap
                        Map<String, HotelSortData> hotelsMap = cityHotelsMap.get(city);
                        HotelSortData hotelSortData = hotelsMap.get(hotelId);

                        // If hotelsMap contains this hotelId, update existing hotelSortData
                        if (hotelSortData != null) {
                            hotelSortData.setNoOfBookings(hotelSortData.getNoOfBookings() + 1);
                            hotelSortData.setRoomNights(hotelSortData.getRoomNights() + rns);
                            hotelSortData.setMarkUp(hotelSortData.getMarkUp() + markUp);
                            hotelSortData.setDiscount((hotelSortData.getDiscount() + discount));
                            if (discount == 0) {
                                hotelSortData.setRoomNightsWithCB(hotelSortData.getRoomNightsWithCB() + rns);
                            }
                        } else {
                            // hotelsMap doesn't contains this hotelId, create a new hotelSortData and add it to hotelsMap
                            hotelSortData = new HotelSortData();
                            hotelSortData.setHotelId(hotelId);
                            hotelSortData.setCity(city);
                            hotelSortData.setBookingDate(bookingDate);
                            hotelSortData.setNoOfBookings(1);
                            hotelSortData.setRoomNights(rns);
                            hotelSortData.setMarkUp(markUp);
                            hotelSortData.setDiscount(discount);
                            if (discount == 0) {
                                hotelSortData.setRoomNightsWithCB(rns);
                            }
                        }
                        hotelsMap.put(hotelId, hotelSortData);
                        cityHotelsMap.put(city, hotelsMap);
                    } else if (city != null && !city.equals("") && hotelId != null && !hotelId.equals("0")) {
                        // cityHotelsMap doesn't contains this city. Add this city to the map and create & add new hotelsMap with new hotelSortData
                        Map<String, HotelSortData> hotelsMap = new HashMap<String, HotelSortData>();
                        HotelSortData hotelSortData = new HotelSortData();
                        hotelSortData.setHotelId(hotelId);
                        hotelSortData.setCity(city);
                        hotelSortData.setBookingDate(bookingDate);
                        hotelSortData.setNoOfBookings(1);
                        hotelSortData.setRoomNights(rns);
                        hotelSortData.setMarkUp(markUp);
                        hotelSortData.setDiscount(discount);
                        if (discount == 0) {
                            hotelSortData.setRoomNightsWithCB(rns);
                        }
                        hotelsMap.put(hotelId, hotelSortData);
                        cityHotelsMap.put(city, hotelsMap);
                    }
                }
            } catch (Exception e) {
                if (reader == null) {
                    logger.error("Error reading MIS CSV file: " + fileLocation + "\n" + e);
                    System.err.println("Error reading MIS CSV file: " + fileLocation + "\n" + e);
                }
                return cityHotelsMap;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        logger.error("Exception occured in closing reader: " + e);
                        System.err.println("Exception occured in closing reader: " + e);
                        return cityHotelsMap;
                    }
                }
            }
        }
        System.out.println("Completed reading!!!");
        return cityHotelsMap;
    }

    public static Calendar getEndCal() {
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, 0);
        endCal.set(Calendar.DATE, DATE);
        return endCal;
    }

    public static Map<String, CitySortData> getCityDataMap(Map<String, Map<String, HotelSortData>> cityHotelsDataMap) {
        Map<String, CitySortData> cityDataMap = new HashMap<String, CitySortData>(NUMBER_OF_CITIES);
        try {
            for (String city : cityHotelsDataMap.keySet()) {
                Map<String, HotelSortData> hotelsMap = cityHotelsDataMap.get(city);
                int totalRns = 0;
                double totMkp = 0, totDis = 0;
                for (String hotelId : hotelsMap.keySet()) {
                    HotelSortData hotelSortData = hotelsMap.get(hotelId);
                    totalRns += hotelSortData.getRoomNights();
                    totMkp += hotelSortData.getMarkUp();
                    totDis += hotelSortData.getDiscount();
                }
                CitySortData cityData = new CitySortData();
                int avgRns = totalRns / hotelsMap.size();
                cityData.setAvgRoomNights(avgRns);
                cityData.setRps((totMkp - totDis) / (hotelsMap.size() * avgRns));
                cityDataMap.put(city, cityData);
            }
        } catch (Exception e) {
            logger.error("Exception occured in getCityDataMap: " + e);
            System.err.println("Exception occured in getCityDataMap: " + e);
            return cityDataMap;
        }
        return cityDataMap;
    }

    public static Map<String, List<List<HotelSortData>>> getCityQuadrantMap(Map<String, Map<String, HotelSortData>> cityHotelsDataMap, Map<String, CitySortData> cityDataMap) {
        Map<String, List<List<HotelSortData>>> cityQuadrantMap = new HashMap<String, List<List<HotelSortData>>>(cityHotelsDataMap.size());

        for (String cityId : cityHotelsDataMap.keySet()) {
            List<HotelSortData> firstQuad = new ArrayList<HotelSortData>();
            List<HotelSortData> secondQuad = new ArrayList<HotelSortData>();
            List<HotelSortData> thirdQuad = new ArrayList<HotelSortData>();
            List<HotelSortData> fourthQuad = new ArrayList<HotelSortData>();
            List<HotelSortData> unIdentified = new ArrayList<HotelSortData>();

            Map<String, HotelSortData> hotelsMap = cityHotelsDataMap.get(cityId);
            for (String hotelId : hotelsMap.keySet()) {
                HotelSortData hotelData = hotelsMap.get(hotelId);
                int quad = getQuadForHotel(hotelData, cityDataMap.get(hotelData.getCity()));
                switch (quad) {
                case 1:
                    firstQuad.add(hotelData);
                    break;
                case 2:
                    secondQuad.add(hotelData);
                    break;
                case 3:
                    thirdQuad.add(hotelData);
                    break;
                case 4:
                    fourthQuad.add(hotelData);
                    break;
                default:
                    unIdentified.add(hotelData);
                }
            }

            List<List<HotelSortData>> cityQuadList = new ArrayList<List<HotelSortData>>(4);
            cityQuadList.add(firstQuad);
            cityQuadList.add(secondQuad);
            cityQuadList.add(thirdQuad);
            cityQuadList.add(fourthQuad);
            cityQuadList.add(unIdentified);

            cityQuadrantMap.put(cityId, cityQuadList);
        }
        return cityQuadrantMap;
    }

    private static int getQuadForHotel(HotelSortData hotelData, CitySortData citySortData) {
        int quad = 0;
        try {
            int rns = hotelData.getRoomNights();
            double markUp = hotelData.getMarkUp();
            double discount = hotelData.getDiscount();
            double rps = (markUp - discount) / rns;

            if (rns >= citySortData.getAvgRoomNights()) {
                if (rps >= citySortData.getRps()) {
                    quad = 1;
                } else if (rps < citySortData.getRps()) {
                    quad = 4;
                }
            } else if (rns < citySortData.getAvgRoomNights()) {
                if (rps >= citySortData.getRps()) {
                    quad = 2;
                } else if (rps < citySortData.getRps()) {
                    quad = 3;
                }
            }
        } catch (Exception e) {
            logger.error("Exception occured in getQuadForHotel: " + e);
            return quad;
        }
        return quad;
    }

    public static Map<String, List<List<HotelSortData>>> getCityGroupMap(Map<String, Map<String, HotelSortData>> cityHotelsDataMap, Map<String, List<List<HotelSortData>>> cityQuadrantMap) {
        Map<String, List<List<HotelSortData>>> cityGroupMap = new HashMap<String, List<List<HotelSortData>>>(cityHotelsDataMap.size());

        /** Create 3 groups for each city and add hotelIds according to logic of PRM-16403 */
        for (String city : cityQuadrantMap.keySet()) {
            List<List<HotelSortData>> list = cityQuadrantMap.get(city);
            List<HotelSortData> firstGroup = new ArrayList<HotelSortData>();
            firstGroup.addAll(list.get(0));
            firstGroup.sort(new GroupOneSortOrder());
            List<HotelSortData> secondGroup = new ArrayList<HotelSortData>();
            secondGroup.addAll(list.get(1));
            secondGroup.addAll(list.get(2));
            secondGroup.sort(new GroupTwoSortOrder());
            List<HotelSortData> thirdGroup = new ArrayList<HotelSortData>();
            thirdGroup.addAll(list.get(3));
            thirdGroup.sort(new GroupThreeSortOrder());

            List<List<HotelSortData>> groupList = new ArrayList<List<HotelSortData>>(20);
            groupList.add(firstGroup);
            groupList.add(secondGroup);
            groupList.add(thirdGroup);
            cityGroupMap.put(city, groupList);
        }
        return cityGroupMap;
    }

    public static void generateCSVReport(Map<String, List<List<HotelSortData>>> cityGroupMap, Map<String, CitySortData> cityDataMap) {
        for (String city : cityGroupMap.keySet()) {
            if (citySet.contains(city)) {
                Date date = getEndCal().getTime();
                String dateStr = outformatter.format(date);
                String outputFileLocation = OUTPUT_FILE + city + "_" + dateStr + CSV;
                boolean alreadyExists = new File(outputFileLocation).exists();

                try {
                    // use FileWriter constructor that specifies open for appending
                    CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFileLocation, true), ',');
                    int rank = 1;
                    List<List<HotelSortData>> list = cityGroupMap.get(city);

                    // if the file didn't already exist then we need to write out the header line
                    if (!alreadyExists) {
                        CitySortData citySortData = cityDataMap.get(list.get(0).get(0).getCity());
                        csvOutput.write("City:");
                        //csvOutput.write(city);
                        csvOutput.write("Avg RNs");
                        csvOutput.write("Avg RPS");
                        csvOutput.endRecord();
                        
                        csvOutput.write(city);
                        csvOutput.write(String.valueOf(citySortData.getAvgRoomNights()));
                        csvOutput.write(String.valueOf(citySortData.getRps()));
                        csvOutput.endRecord();

                        csvOutput.write("HotelId");
                        csvOutput.write("Rank");
                        csvOutput.write("Group");
                        csvOutput.write("No Of RNs"); csvOutput.write("Total Markup"); csvOutput.write("Total C+D"); csvOutput.write("Booking Date");
                         
                        csvOutput.endRecord();
                    }

                    // for (List<HotelSortData> list1 : list) {
                    for (int i = 0; i < list.size(); i++) {
                        List<HotelSortData> list1 = list.get(i);
                        int group = i + 1;
                        for (HotelSortData hotel : list1) {
                            csvOutput.write(hotel.getHotelId());
                            csvOutput.write(Integer.toString(rank++));
                            csvOutput.write(String.valueOf(group));
                            
                            csvOutput.write(String.valueOf(hotel.getRoomNights())); csvOutput.write(String.valueOf(hotel.getMarkUp())); csvOutput.write(String.valueOf(hotel.getDiscount()));
                            csvOutput.write(String.valueOf(hotel.getBookingDate()));
                             
                            csvOutput.endRecord();
                        }
                    }
                    csvOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Completed writing for: " + city);
                }
            }
        }
    }

    public static String[] getMISFileLocations() {
        String[] fileLocArr = null;
        try {
            Calendar endCal = Calendar.getInstance();
            Calendar startCal = getStartCalendar();

            int diffYear = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
            fileLocArr = new String[++diffMonth];

            for (int i = 0; i < diffMonth; i++) {
                // If its not current month, update the startCal date to last date of month
                if (endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH) > 0 || endCal.get(Calendar.YEAR) > startCal.get(Calendar.YEAR)) {
                    startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
                // If its current month, set date to yesterday
                else if (startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH) && endCal.get(Calendar.YEAR) == startCal.get(Calendar.YEAR)) {
                    endCal.add(Calendar.DATE, -1);
                    startCal = endCal;
                }

                String folderName = folderFormatter.format(startCal.getTime());
                String fileFolderLocation = MIS_FILE_LOC + folderName + SLASH;
                String fileNameForThisMonth = MIS_FILE_NAME + fileFormatter.format(startCal.getTime()) + CSV;
                String fileLocation = fileFolderLocation + fileNameForThisMonth;
                fileLocArr[i] = fileLocation;

                startCal.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
            logger.error("Exception occured in generating MIS file locations: " + e);
            return fileLocArr;
        }
        return fileLocArr;
    }

    public static Calendar getStartCalendar() {
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.DATE, -NUMER_OF_DAYS);
        return startCal;
    }

    private static Date getStartDate(Calendar endCal) {
        endCal.add(Calendar.DATE, -NUMER_OF_DAYS);
        return endCal.getTime();
    }
}

class GroupOneSortOrder implements Comparator<HotelSortData> {

    @Override
    public int compare(HotelSortData hotel1, HotelSortData hotel2) {
        int result = 0;

        // Base Criteria: Preference to higher rns
        if (hotel1.getRoomNights() > hotel2.getRoomNights()) {
            result = -1;
        } else if (hotel1.getRoomNights() < hotel2.getRoomNights()) {
            result = 1;
        } else {
            // Criteria 2: Revenue/RNs for the hotel
            /*double avgrps1 = hotel1.getMarkUp() / hotel1.getRoomNights();
            double avgrps2 = hotel2.getMarkUp() / hotel2.getRoomNights();
            if (avgrps1 > avgrps2) {
                result = -1;
            } else if (avgrps1 < avgrps2) {
                result = 1;
            } else {
                // Add logic for sorting based on previous/last computed sort order
                // Criteria 3: For now, sorting based on hotelId
                result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
            }*/

            // Conflict Criteria 1: Sort based on MarkUp - Discount
            double profitOfHotel1 = hotel1.getMarkUp() - hotel1.getDiscount();
            double profitOfHotel2 = hotel2.getMarkUp() - hotel2.getDiscount();

            if (profitOfHotel1 > profitOfHotel2) {
                result = -1;
            } else if (profitOfHotel1 < profitOfHotel2) {
                result = 1;
            } else {
                // Add logic for sorting based on relative ranking from previous/last computed sort order
                // Conflict Criteria 2: Sorting based on hotelId
                result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
            }
        }
        return result;
    }
}

class GroupTwoSortOrder implements Comparator<HotelSortData> {

    @Override
    public int compare(HotelSortData hotel1, HotelSortData hotel2) {
        int result = 0;
        /*double rps1 = hotel1.getMarkUp() / hotel1.getNoOfBookings();
        double rps2 = hotel2.getMarkUp() / hotel2.getNoOfBookings();
        double cps1 = hotel1.getDiscount() / hotel1.getNoOfBookings();
        double cps2 = hotel2.getDiscount() / hotel2.getNoOfBookings();
        int rns1 = hotel1.getRoomNights();
        int rns2 = hotel2.getRoomNights();

        double bcv1 = (rps1 - cps1) * rns1;
        double bcv2 = (rps2 - cps2) * rns2;
        // Base Criteria: Sort based on RPS-CPS*RNS
        if (bcv1 > bcv2) {
            result = -1;
        } else if (bcv1 < bcv2) {
            result = 1;
        } else {
            // Add logic for sorting based on relative ranking from previous/last computed sort order
            // Criteria 2: Sorting based on hotelId
            result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
        }*/

        // Base Criteria: Sort based on MarkUp - Discount
        double profitOfHotel1 = hotel1.getMarkUp() - hotel1.getDiscount();
        double profitOfHotel2 = hotel2.getMarkUp() - hotel2.getDiscount();

        if (profitOfHotel1 > profitOfHotel2) {
            result = -1;
        } else if (profitOfHotel1 < profitOfHotel2) {
            result = 1;
        } else {
            // Add logic for sorting based on relative ranking from previous/last computed sort order
            // Conflict Criteria: Sorting based on hotelId
            result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
        }

        return result;
    }
}

class GroupThreeSortOrder implements Comparator<HotelSortData> {

    @Override
    public int compare(HotelSortData hotel1, HotelSortData hotel2) {
        int result = 0;
        // Base criteria: Revenue/RNs for the hotel
        /*double avgrps1 = hotel1.getMarkUp() / hotel1.getRoomNights();
        double avgrps2 = hotel2.getMarkUp() / hotel2.getRoomNights();
        if (avgrps1 > avgrps2) {
            result = -1;
        } else if (avgrps1 < avgrps2) {
            result = 1;
        } else {
            // Add logic for sorting based on previous/last computed sort order
            // Criteria 3: For now, sorting based on hotelId
            result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
        }*/

        // Base Criteria: Sort based on (Markup - Discount)/RN 
        double profitOfHotel1PerRN = (hotel1.getMarkUp() - hotel1.getDiscount()) / hotel1.getRoomNights();
        double profitOfHotel2PerRN = (hotel2.getMarkUp() - hotel2.getDiscount()) / hotel2.getRoomNights();

        if (profitOfHotel1PerRN > profitOfHotel2PerRN) {
            result = -1;
        } else if (profitOfHotel1PerRN < profitOfHotel2PerRN) {
            result = 1;
        } else {
            // Add logic for sorting based on relative ranking from previous/last computed sort order
            // Conflict Criteria: Sorting based on hotelId
            result = Integer.parseInt(hotel1.getHotelId()) > Integer.parseInt(hotel2.getHotelId()) ? -1 : 1;
        }

        return result;
    }
}
