/*
 * Copyright (c) 2021 Leonard Sch�ngel
 * 
 * For licensing information see the included license (LICENSE.txt)
 */
package racecontrol.utility;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * A <code>Formatter</code> for <code>LogRecord</code>s that formats messages
 * for output on a single line (except when a <code>Throwable</code> is
 * associated with the <code>LogRecord</code>.
 */
public class SingleLineFormatter
        extends Formatter {

    /**
     * A <code>DateFormat</code> instance for formatting timestamps.
     */
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HH:mm:ss-SSS");
    /**
     * A <code>Date</code> instance for formatting timestamps.
     */
    private final Date date = new Date();
    /**
     * Line separator for wrapping lines at the end of log messages.
     */
    private final String lineSeparator = System.getProperty("line.separator");

    /**
     * Creates a new SingleLineFormatter.
     */
    public SingleLineFormatter() {
    }

    @Override
    public synchronized String format(LogRecord record) {
        date.setTime(record.getMillis());
        StringBuilder result = new StringBuilder();

        result.append('[')
                .append(DATE_FORMAT.format(date))
                .append("] ")
                .append(String.format("%1$-7.7s", record.getLevel().getName()))
                .append(' ')
                .append(String.format("%1$-20s", Thread.currentThread().getName()))
                .append(' ')
                .append(String.format("%1$-55s", source(record)))
                .append(": ")
                .append(formatMessage(record))
                .append(lineSeparator);

        if (record.getThrown() != null) {
            result.append(stackTrace(record.getThrown()));
            result.append(lineSeparator);
        }

        return result.toString();
    }

    private String source(LogRecord record) {
        return record.getSourceClassName() != null
                ? record.getSourceClassName().replaceAll("\\B\\w+(\\.[a-z])", "$1") + "." + record.getSourceMethodName() + "()"
                : record.getLoggerName();
    }

    private String stackTrace(Throwable thrown) {
        try ( StringWriter sWriter = new StringWriter();  PrintWriter pWriter = new PrintWriter(sWriter)) {
            thrown.printStackTrace(pWriter);
            pWriter.flush();
            return sWriter.toString();
        } catch (IOException exc) {
            throw new IllegalStateException("Could not print stack trace for log output", exc);
        }
    }
}
