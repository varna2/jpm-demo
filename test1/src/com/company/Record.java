package com.company;

import java.time.LocalDate;

public class Record {
    private String entity;
    private String action;
    private double agreedFx;
    private String currency;
    private LocalDate instructionDate;
    private LocalDate settlementDate;
    private int units;
    private double pricePerUnit;


    public Record(String line) throws Exception {

        try {
            String [] parts = line.split(";");
            entity = parts[0].trim();
            action = parts[1].trim().toUpperCase();
            agreedFx = Double.parseDouble(parts[2].trim());
            currency = parts[3].trim().toUpperCase();
            instructionDate = LocalDate.parse(parts[4].trim(), config.dateFmt);
            settlementDate = correctDate(currency, LocalDate.parse(parts[5].trim(), config.dateFmt));
            units = Integer.parseInt(parts[6].trim());
            pricePerUnit = Double.parseDouble(parts[7].trim());
        }
        catch (Exception ex) {
            debug("failed record initialization: " + ex);
            throw new Exception("Failed record initialization: " + ex);
        }
    }

    private void debug(String msg) {
        System.out.println("record> " + msg);
    }

    // get value of the whole record as string - for internal debugging
    public String toString(){
        return String.format("%s, %s, AgreedFx: %f on %s, instructed: %s, settled: %s (%s), units: %d, pricePerUnit: %f, amount: %f",
                entity, action, agreedFx, currency, instructionDate.format(config.dateFmt), settlementDate.format(config.dateFmt),
                settlementDate.format(config.weekdayFmt), units, pricePerUnit, getAmount());
    }

    // for comparator
    public LocalDate getSDate() {
        return settlementDate;
    }

    // for comparator
    public double getAmount() {
        return pricePerUnit * units * agreedFx;
    }

    // getter
    public String getAction() {
        return action;
    }

    // getter
    public String getEntity() {
        return entity;
    }


    // correction of settlement dates from weekend to next working day/date
    private LocalDate correctDate(String cur, LocalDate inDate) {
        String wd = inDate.format(config.weekdayFmt);
        // working days are 7-4
        if (cur.equals("AED") || (cur.equals("SAR"))) {
            if (wd.equals("Fri")) {
                return inDate.plusDays(2);
            }
            if (wd.equals("Sat")) {
                return inDate.plusDays(1);
            }
        }
        else {      // working days are 1-5
            if (wd.equals("Sat")) {
                return inDate.plusDays(2);
            }
            if (wd.equals("Sun")) {
                return inDate.plusDays(1);
            }
        }

        // rest days - no change
        return inDate;
    }

}
