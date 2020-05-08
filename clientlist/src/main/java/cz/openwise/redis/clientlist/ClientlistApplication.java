package cz.openwise.redis.clientlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


@SpringBootApplication
public class ClientlistApplication {

	public static void main(String[] args) throws IOException {
        Assert.notNull(args, "args can't be null");

		SpringApplication.run(ClientlistApplication.class, args);

		if (args.length == 0) {
            System.out.println("ERR: there is no defined input file for parsing");
        } else {
            Map<String, List<Connection>> connections = fileParsing(new File(args[0]));

//            for (Map.Entry<String, List<Connection>> entry : connections.entrySet()) {
//                System.out.println(entry.getKey());
//
//                for (Connection conn : entry.getValue()) {
//                    System.out.println(conn);
//                }
//            }

            dataAnalytics(connections);
        }
	}

	private static void dataAnalytics(Map<String, List<Connection>> connections) {
	    // connection counts
        System.out.println("------------ Connection counts (TOP5)");
        Map<Integer, String> connCounts = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<String, List<Connection>> entry : connections.entrySet()) {
            int count = entry.getValue().size();

            connCounts.put(count, entry.getKey());
        }

        int topCount = 0;
        for (Map.Entry<Integer, String> entry : connCounts.entrySet()) {
            if (topCount == 5) {
                break;
            }

            System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
            topCount++;

            // output sorted by age for top connections
            List<Connection> topConnections = connections.get(entry.getValue());
            topConnections.sort(new Comparator<Connection>() {
                @Override
                public int compare(Connection c1, Connection c2) {
                    return Integer.valueOf(c1.age).compareTo(c2.age);
                }
            });

            for (Connection conn : topConnections) {
                System.out.println(conn);
            }

            System.out.println();
            System.out.println();
        }
    }

	private static Map<String, List<Connection>> fileParsing(File file) throws IOException {
        System.out.println("Starting parsing file: " + file);

        // [timestamp; list of connections]
        Map<String, List<Connection>> connections = new LinkedHashMap<>();

        BufferedReader reader = Files.newBufferedReader(file.toPath());
        String line = reader.readLine();
        String headerLine = null;
        int blockLinesCount = 0;
        while (line != null) {
            if (!StringUtils.isEmpty(line)) {
                if (line.startsWith("=")) {
                    // starting new data block
//                    if (blockLinesCount > 0) {
//                        System.out.println("Block '" + headerLine + "' ended, connection count: " + blockLinesCount);
//                    }

                    headerLine = StringUtils.replace(line, "=", "");
                    blockLinesCount = 0;
                    connections.put(headerLine, new ArrayList<>());
                } else {
                    connections.get(headerLine).add(parseConnection(line));
                    blockLinesCount++;
                }
            }

            line = reader.readLine();
        }

//        if (blockLinesCount > 0) {
//            System.out.println("Block '" + headerLine + "' ended, connection count: " + blockLinesCount);
//        }
        
        return connections;
    }

    private static Connection parseConnection(String connStr) {
	    Connection conn = new Connection();

	    // prerequisite: params are always in the same order
        // id=22199808 addr=80.95.99.142:38150 fd=120 name= age=3 idle=0 flags=N db=0 sub=0 psub=0 multi=-1
        //      qbuf=0 qbuf-free=0 obl=0 oll=0 omem=0 events=r cmd=del
        String[] params = StringUtils.delimitedListToStringArray(connStr, " ");
        conn.id = getConnStr(params[0]);
        conn.addr = getConnStr(params[1]);
        conn.fd = getConnStr(params[2]);
        conn.name = getConnStr(params[3]);
        conn.age = getConnInt(params[4]);
        conn.idle = getConnInt(params[5]);
        conn.flags = getConnStr(params[6]);
        conn.db = getConnStr(params[7]);
        conn.sub = getConnStr(params[8]);
        conn.psub = getConnStr(params[9]);
        conn.multi = getConnInt(params[10]);
        conn.qbuf = getConnInt(params[11]);
        conn.qbufFree = getConnInt(params[12]);
        conn.obl = getConnInt(params[13]);
        conn.oll = getConnInt(params[14]);
        conn.omem = getConnInt(params[15]);
        conn.events = getConnStr(params[16]);
        conn.cmd = getConnStr(params[17]);

	    return conn;
    }

    private static String getConnStr(String input) {
	    return StringUtils.delimitedListToStringArray(input, "=")[1].trim();
    }

    private static int getConnInt(String input) {
	    return Integer.valueOf(StringUtils.delimitedListToStringArray(input, "=")[1].trim());
    }
}
