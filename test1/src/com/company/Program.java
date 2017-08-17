package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.time.LocalDate;
import static java.lang.Math.max;


public class Program {
    List<Record> records;


    // for report printing
    public void print(String msg) {
        System.out.println(msg);
    }

    // internal - debugging
    public void debug(String msg) {
        System.out.println(msg);
    }

    // internal - error indication
    public void error(String msg) {
        System.out.println("ERROR: " + msg);
    }

    // internal - dump all records
    public void debug_records() {
        for (Record rec : records) {
            debug(rec.toString());
        }
    }

    // we have to get data somehow. It was not defined "how" in test requirements, thus I created text file with data
    // this function reads the data file and creates in-memory data structure for further processing
    public void readData() {
        try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
            String line = br.readLine();    // skip header and initialize line

            while (line != null) {
                line = br.readLine();
                // debug("line read: " + line);

                if (line != null && !line.isEmpty()) {
                    try {
                        Record rec = new Record(line);
                        records.add(rec);
                    }
                    catch (Exception ex) {
                        error("new record exception: " + ex + "on data line: " + line);
                        // do nothing, just skip the record
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // prints report, Records sorted by settlementDate
    public void printReportUSDSettled(){
        print("\n\tREPORT OF USD AMOUNT SETTLED\n");
        print(String.format("%-15s\t%-20s\t%-20s", "Date", "Incoming USD (sold)", "Outgoing USD (bought)"));

        records.sort(Comparator.comparing(Record::getSDate));

        LocalDate xdate = records.get(0).getSDate();
        double incoming = 0;
        double outgoing = 0;
        for (Record rec : records) {
            // end of "today" sequence
            if (rec.getSDate().compareTo(xdate) != 0) {
                print(String.format("%-15s\t% 20f\t% 20f", xdate.format(config.dateFmt), outgoing, incoming));      // ** +last line below

                xdate = rec.getSDate();
                incoming = 0;
                outgoing = 0;
            }

            if (rec.getAction().equals("B")) {
                incoming += rec.getAmount();
            }
            if (rec.getAction().equals("S")) {
                outgoing += rec.getAmount();
            }
        }
        // last record processing
        if ((incoming > 0) || (outgoing > 0)){
            print(String.format("%-15s\t% 20f\t% 20f", xdate.format(config.dateFmt), outgoing, incoming));
        }

        print("\n");
    }


    public void printReportEntitiesRanks(){
        print("\n\tREPORT OF ENTITIES RANKED\n");

        List<String> incoming_rank = new ArrayList<>();     // buy
        List<String> outgoing_rank = new ArrayList<>();     // sell

        // NOTICE: it is not clear if ranking is based on amount of 1 instruction,
        //          or of ALL instructions (sum(amount))
        //          I assume 1 instruction approach, word "total" or something like it was missing in the task

        records.sort(Comparator.comparing(Record::getAmount));
        Collections.reverse(records);

        for (Record rec : records) {
            if (rec.getAction().equals("S")) {
                if (incoming_rank.contains(rec.getEntity()) == false) {
                    incoming_rank.add(rec.getEntity());
                }
            }
            if (rec.getAction().equals("B")) {
                if (outgoing_rank.contains(rec.getEntity()) == false) {
                    outgoing_rank.add(rec.getEntity());
                }
            }
        }

        //
        int N = max(incoming_rank.size(), outgoing_rank.size());
        String inX, outX;
        print(String.format("%-10s\t%-20s\t%-20s", "Rank", "Incoming (amt.sold, dsc)", "Outgoing (amt.bought, dsc)"));
        for (int i=0; i < N; i++){
            if (i < incoming_rank.size()) {
                inX = incoming_rank.get(i);
            }
            else {
                inX = "\t";
            }
            if (i < outgoing_rank.size()) {
                outX = outgoing_rank.get(i);
            }
            else {
                outX = "\t";
            }
            print(String.format("%-10d\t%-20s\t%-20s", i+1, inX, outX));
        }

    }

    public void start() {
        System.out.println("JP Morgan Java Technical Test solution");

        records = new ArrayList<>();

        readData();
        //debug("List of records:");
        //debug_records();

        printReportUSDSettled();
        printReportEntitiesRanks();
    }

}
